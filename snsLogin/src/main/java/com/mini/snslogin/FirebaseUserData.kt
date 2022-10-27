package com.mini.snslogin


data class FirebaseUserData(
    val idToken : String,
    val email : String,
    val displayName: String,
    val phoneNumber: String,
    val photoUrl : String,
    val isEmailVerified : Boolean,
    val isAnonymous : Boolean,
    val providerId : String,
    val uid: String
)
