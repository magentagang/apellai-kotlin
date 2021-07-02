package com.magentagang.apellai

import android.app.Application
import timber.log.Timber

class LoggingApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}