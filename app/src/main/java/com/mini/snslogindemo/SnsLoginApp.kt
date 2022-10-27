package com.mini.snslogindemo

import android.app.Application
import com.mini.snslogin.SnsLogin

class SnsLoginApp : Application() {

    override fun onCreate() {
        super.onCreate()
        SnsLogin
            .withGoogle(this)
            .withTwitter(this)
            .withKakao(this, appKey = "dad5433de792bb67c1217f65c5df43f0")
            .withNaver(this, clientId = "", clientSecret = "", clientName = "")
    }
}