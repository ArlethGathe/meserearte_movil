package com.example.meserearte

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

object Notificaciones {

    const val CANAL_RECORDATORIOS = "recordatorios_eventos"

    // Desde Android 8.0 toda notificación debe pertenecer a un canal
    fun crearCanal(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CANAL_RECORDATORIOS,
                "Recordatorios de eventos",
                NotificationManager.IMPORTANCE_HIGH
            )
            canal.description =
                "Recordatorios, cambios de horario y avisos de uniforme"

            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(canal)
        }
    }

    // Construye y muestra la notificación del sistema.
    // El permiso se verifica antes de llamar a este método.
    @SuppressLint("MissingPermission")
    fun enviar(
        context: Context,
        tipo: String,
        evento: Evento,
        titulo: String,
        mensaje: String
    ) {
        val id = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()

        // Al tocar el aviso se abre Eventos asignados con el nombre del evento
        val intent = Intent(context, EventosAsignadosActivity::class.java)
        intent.putExtra(EXTRA_EVENTO, evento.nombre)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val pendingIntent = PendingIntent.getActivity(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificacion = NotificationCompat.Builder(context, CANAL_RECORDATORIOS)
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(ContextCompat.getColor(context, R.color.burgundy))
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setSubText("$tipo · ${evento.nombre}")
            .setStyle(NotificationCompat.BigTextStyle().bigText(mensaje))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(id, notificacion)
    }
}
