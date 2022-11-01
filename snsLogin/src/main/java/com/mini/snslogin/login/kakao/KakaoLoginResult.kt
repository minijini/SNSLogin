package com.mini.snslogin.login.kakao

data class KakaoLoginResult(
    val accessToken: String,
    val id: Long,
    val email: String,
    val nickname: String,
    val thumbnailImageUrl: String
)