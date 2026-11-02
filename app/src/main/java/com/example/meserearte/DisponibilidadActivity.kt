package com.example.meserearte

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class DisponibilidadActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disponibilidad)
        findViewById<View>(R.id.disponibilidadRoot).ajustarBarrasDelSistema()

        // Barra superior con menú de opciones
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbarMesero)
        setSupportActionBar(toolbar)

        // Mostrar el correo recibido desde el inicio de sesión
        val correo = intent.getStringExtra(EXTRA_CORREO) ?: ""
        if (correo.isNotEmpty()) {
            toolbar.subtitle = correo
            findViewById<TextView>(R.id.tvBienvenidaMesero).text =
                "Hola, $correo. Indica si puedes asistir a cada evento"
        }

        // Pestañas de navegación (sus vistas se desarrollarán después)
        val pestanas = mapOf(
            R.id.tabInicio to "Inicio",
            R.id.tabMisEventos to "Mis eventos",
            R.id.tabDisponibilidad to "Disponibilidad",
            R.id.tabNotificaciones to "Notificaciones"
        )
        for ((id, nombre) in pestanas) {
            findViewById<TextView>(id).setOnClickListener {
                if (id != R.id.tabDisponibilidad) avisar(nombre)
            }
        }

        // Eventos pendientes de confirmar
        configurarEvento(
            "Boda Ramírez",
            findViewById(R.id.tvEstadoBoda),
            findViewById(R.id.btnDisponibleBoda),
            findViewById(R.id.btnNoDisponibleBoda)
        )
        configurarEvento(
            "Evento Corporativo ABC",
            findViewById(R.id.tvEstadoCorp),
            findViewById(R.id.btnDisponibleCorp),
            findViewById(R.id.btnNoDisponibleCorp)
        )
    }

    // Eventos de los botones Disponible / No disponible de una tarjeta
    private fun configurarEvento(
        nombre: String,
        estado: TextView,
        btnDisponible: Button,
        btnNoDisponible: Button
    ) {
        btnDisponible.setOnClickListener {
            estado.text = "Confirmada"
            estado.setBackgroundResource(R.drawable.bg_chip_confirmed)
            estado.setTextColor(getColor(R.color.ok_text))
            Toast.makeText(
                this,
                "Confirmaste tu disponibilidad: $nombre",
                Toast.LENGTH_SHORT
            ).show()
        }

        btnNoDisponible.setOnClickListener {
            estado.text = "No disponible"
            estado.setBackgroundResource(R.drawable.bg_chip_cancelled)
            estado.setTextColor(getColor(R.color.bad_text))
            Toast.makeText(
                this,
                "Marcaste que no estás disponible: $nombre",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Inflar el menú del mesero
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_mesero, menu)
        return true
    }

    // Atender las opciones del menú
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_mesero_cerrar_sesion -> {
                cerrarSesion()
                true
            }
            R.id.menu_mesero_inicio,
            R.id.menu_mesero_mis_eventos,
            R.id.menu_mesero_notificaciones,
            R.id.menu_mesero_perfil -> {
                avisar(item.title.toString())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun avisar(seccion: String) {
        Toast.makeText(
            this,
            "$seccion: vista en desarrollo",
            Toast.LENGTH_SHORT
        ).show()
    }
}
