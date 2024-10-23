package br.com.angelica.comprainteligente

import android.app.Application
import br.com.angelica.comprainteligente.di.appModule
import com.google.android.libraries.places.api.Places
import com.google.firebase.FirebaseApp
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Inicialize o Firebase
        FirebaseApp.initializeApp(this)

        // Inicialize o Google Places
        Places.initialize(applicationContext, "AIzaSyBdExxtobm1IW4hfSbmJbLWlmfPa40skQk")

        // Inicialize o Koin
        startKoin {
            androidContext(this@MainApplication)
            modules(appModule)
        }
    }
}