package com.mini.snslogin.login.kakao

import android.content.Context
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient

typealias KakaoAccessToken = String

internal object KakaoLogin {

    fun login(
        context: Context,
        onSuccess: (token: KakaoAccessToken) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                onFailure(error)
            } else if (token != null) {
                onSuccess(token.accessToken)
            }
        }

        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                if (error != null) {
                    onFailure(error)

                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }

                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
                } else if (token != null) {
                    onSuccess(token.accessToken)
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
        }
    }


    fun logOut(onSuccess: () -> Unit, onFailure: (Throwable) -> Unit) {
        UserApiClient.instance.logout {
            if (it != null) {
                onFailure(it)
            } else {
                onSuccess()
            }
        }
    }


    fun hashToken(context: Context,onHashToken: (hash: String) -> Unit){
        onHashToken(Utility.getKeyHash(context))
    }



}