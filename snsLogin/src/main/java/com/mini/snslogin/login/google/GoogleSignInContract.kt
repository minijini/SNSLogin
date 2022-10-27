package com.mini.snslogin.login.google

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.mini.snslogin.util.Logger
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

internal class GoogleSignInContract : ActivityResultContract<String, GoogleSignInAccount?>() {
    override fun createIntent(context: Context, input: String): Intent {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(input)
            .requestEmail()
            .build()
            .let {
                GoogleSignIn.getClient(context, it)
            }.signInIntent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): GoogleSignInAccount? {
        return runCatching {
            GoogleSignIn.getSignedInAccountFromIntent(intent)
                .getResult(ApiException::class.java)
        }.onFailure {
            Logger.e(it)
        }.getOrNull()
    }
}