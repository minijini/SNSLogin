package com.mini.snslogin.login.twitter

import android.app.Activity
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.mini.snslogin.FirebaseUserData
import com.mini.snslogin.util.toSafe


internal object TwitterLogin {

    fun login(
        activity: Activity,
        onSuccess: (firebaseUserData: FirebaseUserData) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val provider = OAuthProvider.newBuilder("twitter.com")
        val firebaseAuth = FirebaseAuth.getInstance()
        val pendingResultTask: Task<AuthResult>? = firebaseAuth.pendingAuthResult
        if (pendingResultTask != null) {
            pendingResultTask
                .addOnSuccessListener(
                    OnSuccessListener { authResult ->
                        handleAuthResult(authResult, onSuccess, onFailure)
                    })
                .addOnFailureListener {
                    onFailure(it.message.toSafe())
                }
        } else {
            firebaseAuth
                .startActivityForSignInWithProvider(activity, provider.build())
                .addOnSuccessListener { authResult ->
                    handleAuthResult(authResult, onSuccess, onFailure)
                }
                .addOnFailureListener {
                    onFailure(it.message.toSafe())
                }
        }
    }

    fun logOut(){
        FirebaseAuth.getInstance().signOut()
    }

    private fun handleAuthResult(authResult : AuthResult, onSuccess: (firebaseUserData: FirebaseUserData) -> Unit, onFailure: (String) -> Unit){
        val user = authResult.user ?: run {
            onFailure("Fail to sign in with firebase")
            return
        }

        user.getIdToken(true).addOnSuccessListener { result ->
            if (result.token != null && result.token.toSafe().isNotBlank()) {
                FirebaseUserData(
                    email = user.email.toSafe(),
                    idToken = result.token.toSafe(),
                    displayName = user.displayName.toSafe(),
                    phoneNumber = user.phoneNumber.toSafe(),
                    photoUrl = user.photoUrl.toString(),
                    isEmailVerified = user.isEmailVerified.toSafe(),
                    isAnonymous = user.isAnonymous.toSafe(),
                    providerId = user.providerId,
                    uid = user.uid
                ).also {
                    onSuccess(it)
                }
            } else {
                onFailure("invalid firebase token")
            }
        }
    }

}