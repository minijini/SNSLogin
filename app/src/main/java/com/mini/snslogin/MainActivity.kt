package com.mini.snslogin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.mini.snslogin.login.google.GoogleLoginLauncher
import com.mini.snslogin.R

/**
 * this demo will not work properly.
 * Because it needs to be set all configuration on auth provider dashboard.
 * For example, you need to create Firebase project to use [SnsLogin.googleLogin], and configure all setting it needs
 */
class MainActivity : AppCompatActivity() {

    private val googleLoginLauncher: GoogleLoginLauncher by lazy {
        SnsLogin.googleLogin("getString(R.string.default_web_client_id)")
            .onSuccess { googleUser ->
                onSuccess(googleUser.toString())
            }.onFailure {
                onFailure(it)
                SnsLogin.googleLogout(this)
            }.build(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        loginWithNaver()
        loginWithKakao()
//        hashTokenWithKakao()
    }



    private fun loginWithGoogle() {
        googleLoginLauncher.launch()
    }

    private fun loginWithKakao() {
        SnsLogin.kakaoLogin(this, onSuccess = {
            onSuccess(it.toString())
        }, onFailure = { throwable, msg ->
            onFailure(msg)
            SnsLogin.kakaoLogout()

        })
    }

    private fun hashTokenWithKakao(){
        SnsLogin.kakaoHashToken(this, onHashToken = {
            onSuccess(it)
        })
    }

    private fun loginWithNaver() {
        SnsLogin.naverLogin(this, onSuccess = {
            onSuccess(it.toString())
        }, onFailure = {
            onFailure(it)
            SnsLogin.naverLogout()
        })
    }

    private fun loginWithTwitter() {
        SnsLogin.twitterLogin(this, onSuccess = {
            onSuccess(it.toString())
        }, onFailure = {
            onFailure(it)
            SnsLogin.twitterLogout()
        })
    }

    private fun onSuccess(data: String) {
        Toast.makeText(this, "Login Succeed! data : $data", Toast.LENGTH_LONG).show()
        Log.d(javaClass.simpleName, "Login Succeed! data : $data")
    }

    private fun onFailure(error: String) {
        Toast.makeText(this, "Login Failed! error: $error", Toast.LENGTH_LONG).show()
        Log.d(javaClass.simpleName, "Login Failed! error: $error")
    }
}