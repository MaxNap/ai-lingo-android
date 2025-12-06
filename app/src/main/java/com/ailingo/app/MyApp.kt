package com.ailingo.app

import android.app.Application
import com.google.firebase.FirebaseApp

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase normally (no emulators)
        try {
            FirebaseApp.initializeApp(this)
        } catch (_: Throwable) {
            // Ignore if already initialized
        }
    }
}
