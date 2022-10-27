package com.mini.snslogin.login.naver

import android.content.Context
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback

internal object NaverLogin {

    fun login(
        context: Context,
        onSuccess: (NaverLoginResult) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val oauthLoginCallback = object : OAuthLoginCallback {
            override fun onSuccess() {
                kotlin.runCatching {
                    val accessToken = checkNotNull(NaverIdLoginSDK.getAccessToken())
                    val refreshToken = checkNotNull(NaverIdLoginSDK.getRefreshToken())
                    val tokenType = checkNotNull(NaverIdLoginSDK.getTokenType())
                    val expire = checkNotNull(NaverIdLoginSDK.getExpiresAt().toString())
                    val status = checkNotNull(NaverIdLoginSDK.getState().toString())
                    NaverLoginResult(
                        accessToken = accessToken,
                        refreshToken = refreshToken,
                        tokenType = tokenType,
                        expire = expire,
                        status = status
                    ).also(onSuccess)
                }.onFailure {
                    onFailure("[OAuthLoginCallback onSuccess()] NaverLoginResult is null")
                }
            }

            override fun onFailure(httpStatus: Int, message: String) {
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                onFailure("[code $errorCode] $errorDescription")
            }

            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }
        }

        NaverIdLoginSDK.authenticate(context, oauthLoginCallback)
    }

    fun logout(){
        NaverIdLoginSDK.logout()
    }
}