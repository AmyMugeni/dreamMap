package com.dreammap.app


import android.app.Application
import com.google.firebase.FirebaseApp

/**
 * The Application class is the first component instantiated when the app process is created.
 * We use it here to perform one-time initializations required for the entire app,
 * such as setting up the connection to Firebase.
 */
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initializes the FirebaseApp instance using the configuration from the
        // google-services.json file included in your project. This single line
        // sets up the connection for all Firebase services (Firestore, Auth, etc.).
        FirebaseApp.initializeApp(this)
    }
}
