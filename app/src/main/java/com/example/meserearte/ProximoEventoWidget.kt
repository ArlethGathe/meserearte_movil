package com.example.meserearte

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import java.util.Calendar
import java.util.Locale

object ProximoEventoPrefs {
    private const val PREFERENCIAS = "widget_proximo_evento"

    fun guardar(context: Context, evento: Evento) {
        context.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE).edit()
            .putString("nombre", evento.nombre)
            .putString("fecha", evento.fecha)
            .putString("lugar", evento.lugar)
            .apply()
        ProximoEventoWidget.actualizarTodos(context)
    }

    fun leer(context: Context): Triple<String, String, String> {
        val pref = context.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE)
        return Triple(
            pref.getString("nombre", "Abre MesereArte para ver tus eventos") ?: "",
            pref.getString("fecha", "") ?: "",
            pref.getString("lugar", "") ?: ""
        )
    }

    // Parseo sencillo de las fechas de los eventos de ejemplo (dd MMM yyyy · HH:mm).
    fun diasRestantes(fecha: String): String {
        val partes = Regex("(\\d{1,2})\\s+(\\p{L}+)\\s+(\\d{4})").find(fecha)
            ?.destructured ?: return ""
        val meses = listOf("ene", "feb", "mar", "abr", "may", "jun", "jul", "ago", "sep", "oct", "nov", "dic")
        val mes = meses.indexOf(partes.component2().take(3).lowercase(Locale.ROOT))
        if (mes < 0) return ""
        val inicio = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }
        val destino = Calendar.getInstance().apply {
            set(partes.component3().toInt(), mes, partes.component1().toInt(), 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val dias = (destino.timeInMillis - inicio.timeInMillis) / 86_400_000L
        return when {
            dias < 0 -> "Evento pasado"
            dias == 0L -> "¡Hoy!"
            dias == 1L -> "Mañana"
            else -> "En $dias días"
        }
    }
}

class ProximoEventoWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        actualizar(context, manager, ids)
    }

    companion object {
        fun actualizarTodos(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, ProximoEventoWidget::class.java))
            actualizar(context, manager, ids)
        }

        private fun actualizar(context: Context, manager: AppWidgetManager, ids: IntArray) {
            val (nombre, fecha, lugar) = ProximoEventoPrefs.leer(context)
            ids.forEach { id ->
                val vistas = RemoteViews(context.packageName, R.layout.widget_proximo_evento)
                vistas.setTextViewText(R.id.widgetNombre, nombre)
                vistas.setTextViewText(R.id.widgetFecha, fecha)
                vistas.setTextViewText(R.id.widgetLugar, lugar)
                vistas.setTextViewText(R.id.widgetDias, ProximoEventoPrefs.diasRestantes(fecha))
                val abrir = Intent(context, EventosAsignadosActivity::class.java).apply {
                    putExtra(EXTRA_EVENTO, nombre)
                    putExtra(EXTRA_FECHA, fecha)
                    putExtra(EXTRA_LUGAR, lugar)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                val pendiente = PendingIntent.getActivity(
                    context, id, abrir,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                vistas.setOnClickPendingIntent(R.id.widgetRoot, pendiente)
                manager.updateAppWidget(id, vistas)
            }
        }
    }
}
