package com.minecraft.im

import android.app.Application
import com.minecraft.im.data.SessionManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MinecraftImApp : Application() {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate() {
        super.onCreate()
        sessionManager.bootstrap()
    }
}
