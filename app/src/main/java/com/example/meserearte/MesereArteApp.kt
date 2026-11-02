package com.example.meserearte

import android.app.Application

class MesereArteApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // El canal de notificaciones se registra al iniciar la aplicación
        Notificaciones.crearCanal(this)
    }
}
