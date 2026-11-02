package com.example.meserearte

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

// Clave del Intent para enviar el correo capturado en el inicio de sesión
const val EXTRA_CORREO = "correo_usuario"

// Regresa a la pantalla de acceso y limpia la pila de pantallas
fun Activity.cerrarSesion() {
    val intent = Intent(this, LoginActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    startActivity(intent)
    finish()
}

// Evita que el contenido quede debajo de las barras del sistema
// (Android 15 en adelante dibuja la app de borde a borde)
fun View.ajustarBarrasDelSistema() {
    val izquierda = paddingLeft
    val arriba = paddingTop
    val derecha = paddingRight
    val abajo = paddingBottom

    ViewCompat.setOnApplyWindowInsetsListener(this) { vista, insets ->
        val barras = insets.getInsets(
            WindowInsetsCompat.Type.systemBars() or
                    WindowInsetsCompat.Type.displayCutout()
        )
        vista.setPadding(
            izquierda + barras.left,
            arriba + barras.top,
            derecha + barras.right,
            abajo + barras.bottom
        )
        insets
    }
    ViewCompat.requestApplyInsets(this)
}
