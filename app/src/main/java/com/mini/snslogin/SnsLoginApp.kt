package com.mini.snslogin

import android.app.Application

class SnsLoginApp : Application() {

    override fun onCreate() {
        super.onCreate()
        SnsLogin
            .withGoogle(this)
            .withTwitter(this)
            .withKakao(this, appKey = "0123eaa81cb8ff3ec79acbf922e50f69")
            .withNaver(this, clientId = "", clientSecret = "", clientName = "")
    }
}