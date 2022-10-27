package com.mini.snslogin

import android.app.Activity
import android.content.Context
import com.mini.snslogin.login.google.GoogleLoginLauncher
import com.google.firebase.FirebaseApp
import com.kakao.sdk.common.KakaoSdk
import com.mini.snslogin.login.google.GoogleAuthUtil
import com.mini.snslogin.login.kakao.KakaoAccessToken
import com.mini.snslogin.login.kakao.KakaoLogin
import com.mini.snslogin.login.naver.NaverLogin
import com.mini.snslogin.login.naver.NaverLoginResult
import com.mini.snslogin.login.twitter.TwitterLogin
import com.navercorp.nid.NaverIdLoginSDK


class NotInitializedError(authProvider: String) : Throwable("$authProvider SDK not been initialized!")

object SnsLogin {

    internal enum class AuthProvider {
        Google {
            override fun logout(activity: Activity) {
                googleLogout(activity)
            }
        },
        Twitter {
            override fun logout(activity: Activity) {
                twitterLogout()
            }
        },
        Kakao {
            override fun logout(activity: Activity) {
                kakaoLogout()
            }
        },
        Naver {
            override fun logout(activity: Activity) {
                naverLogout()
            }
        };

        abstract fun logout(activity: Activity)
    }

    private val authProviders = mutableListOf<AuthProvider>()


    fun withGoogle(context: Context) = apply {
        FirebaseApp.initializeApp(context)
        authProviders.add(AuthProvider.Google)
    }


    fun withTwitter(context: Context) = apply {
        FirebaseApp.initializeApp(context)
        authProviders.add(AuthProvider.Twitter)
    }

    fun withKakao(context: Context, appKey: String) = apply {
        KakaoSdk.init(context, appKey)
        authProviders.add(AuthProvider.Kakao)
    }

    fun withNaver(
        context: Context,
        clientId: String,
        clientSecret: String,
        clientName: String
    ) = apply {
        NaverIdLoginSDK.initialize(
            context,
            clientId,
            clientSecret,
            clientName
        )
        authProviders.add(AuthProvider.Naver)
    }

    fun googleLogin(defaultWebClientId: String): GoogleLoginLauncher.Builder {
        checkSdkInitialized(AuthProvider.Google)
        return GoogleLoginLauncher.Builder(defaultWebClientId)
    }

    fun googleLogout(activity: Activity) {
        checkSdkInitialized(AuthProvider.Google)
        GoogleAuthUtil.signOut(activity)
    }

    fun logoutAll(activity: Activity) {
        authProviders.forEach { it.logout(activity) }
    }

    private fun checkSdkInitialized(authProvider: AuthProvider) {
        if (!authProviders.contains(authProvider)) {
            throw NotInitializedError(authProvider.name)
        }
    }


    fun kakaoLogin(
        context: Context,
        onSuccess: (token: KakaoAccessToken) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        checkSdkInitialized(AuthProvider.Kakao)
        KakaoLogin.login(context, onSuccess, onFailure)
    }

    fun kakaoLogout(onSuccess: () -> Unit = {}, onFailure: (Throwable) -> Unit = {}) {
        checkSdkInitialized(AuthProvider.Kakao)
        KakaoLogin.logOut(onSuccess, onFailure)
    }

    fun kakaoHashToken(context: Context,onHashToken: (hash: String) -> Unit){
        KakaoLogin.hashToken(context,onHashToken)
    }

    fun naverLogin(
        context: Context,
        onSuccess: (NaverLoginResult) -> Unit,
        onFailure: (String) -> Unit
    ) {
        checkSdkInitialized(AuthProvider.Naver)
        NaverLogin.login(context, onSuccess, onFailure)
    }


    fun naverLogout() {
        checkSdkInitialized(AuthProvider.Naver)
        NaverLogin.logout()
    }

    fun twitterLogin(
        activity: Activity,
        onSuccess: (firebaseUserData: FirebaseUserData) -> Unit,
        onFailure: (String) -> Unit
    ) {
        checkSdkInitialized(AuthProvider.Twitter)
        TwitterLogin.login(activity, onSuccess, onFailure)
    }

    fun twitterLogout() {
        checkSdkInitialized(AuthProvider.Twitter)
        TwitterLogin.logOut()
    }

}