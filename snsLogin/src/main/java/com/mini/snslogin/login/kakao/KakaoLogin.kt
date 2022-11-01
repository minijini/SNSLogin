package com.mini.snslogin.login.kakao

import android.content.Context
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import com.mini.snslogin.util.toSafe
import java.lang.Error

typealias KakaoAccessToken = String

internal object KakaoLogin {

    fun login(
        context: Context,
        onSuccess: (kakaoresult: KakaoLoginResult) -> Unit,
        onFailure: (Throwable, msg : String) -> Unit
    ) {
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                var errormsg  = errormsg(error)

                onFailure(error,errormsg)
            } else if (token != null) {
                UserApiClient.instance.me { user, error ->
                    if (error != null) {
                        var errormsg  = errormsg(error)
                        onFailure(error , errormsg)
                    }
                    else if (user != null) {
                        KakaoLoginResult(
                            accessToken = token.accessToken.toSafe(),
                            id = user.id.toSafe(),
                            email = user.kakaoAccount?.email.toSafe(),
                            nickname = user.kakaoAccount?.profile?.nickname.toSafe(),
                            thumbnailImageUrl = user.kakaoAccount?.profile?.thumbnailImageUrl.toSafe()
                        ).also{
                            onSuccess(it)
                        }
                    }
                }

            }
        }

        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                if (error != null) {
                    var errormsg  = errormsg(error)
                    onFailure(error , errormsg)

                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }

                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
                } else if (token != null) {
                    UserApiClient.instance.me { user, error ->
                        if (error != null) {
                            var errormsg  = errormsg(error)
                            onFailure(error,errormsg)
                        }
                        else if (user != null) {
                            KakaoLoginResult(
                                accessToken = token.accessToken.toSafe(),
                                id = user.id.toSafe(),
                                email = user.kakaoAccount?.email.toSafe(),
                                nickname = user.kakaoAccount?.profile?.nickname.toSafe(),
                                thumbnailImageUrl = user.kakaoAccount?.profile?.thumbnailImageUrl.toSafe()
                            ).also{
                                onSuccess(it)
                            }
                        }
                    }


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


    fun errormsg(error:  Throwable?) : String{
        when {
            error.toString() == AuthErrorCause.AccessDenied.toString() -> {
                return "접근이 거부 됨(동의 취소)"
            }
            error.toString() == AuthErrorCause.InvalidClient.toString() -> {
                return  "유효하지 않은 앱"
            }
            error.toString() == AuthErrorCause.InvalidGrant.toString() -> {
                return "인증 수단이 유효하지 않아 인증할 수 없는 상태"
            }
            error.toString() == AuthErrorCause.InvalidRequest.toString() -> {
                return "요청 파라미터 오류"
            }
            error.toString() == AuthErrorCause.InvalidScope.toString() -> {
                return  "유효하지 않은 scope ID"
            }
            error.toString() == AuthErrorCause.Misconfigured.toString() -> {
                return  "설정이 올바르지 않음(android key hash)"
            }
            error.toString() == AuthErrorCause.ServerError.toString() -> {
                return  "서버 내부 에러"
            }
            error.toString() == AuthErrorCause.Unauthorized.toString() -> {
                return  "앱이 요청 권한이 없음"
            }
            else -> { // Unknown
                return  "기타 에러"
            }
        }
    }


}