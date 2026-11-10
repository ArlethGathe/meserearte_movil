package com.example.meserearte

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DisponibilidadActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disponibilidad)
        findViewById<View>(R.id.disponibilidadRoot).ajustarBarrasDelSistema()

        // Barra superior, pestañas y correo del mesero
        configurarBarraMesero(R.id.tabDisponibilidad)

        val correo = intent.getStringExtra(EXTRA_CORREO) ?: ""
        if (correo.isNotEmpty()) {
            findViewById<TextView>(R.id.tvBienvenidaMesero).text =
                "Hola, $correo. Indica si puedes asistir a cada evento"
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
        estado.aplicarEstado(EstadoEvento.PENDIENTE)

        // Disponible: cambia la etiqueta y permite deshacer
        btnDisponible.setOnClickListener {
            val anterior = estado.tag as? EstadoEvento ?: EstadoEvento.PENDIENTE
            estado.aplicarEstado(EstadoEvento.CONFIRMADA)
            DatosEventos.buscar(nombre)?.estado = EstadoEvento.CONFIRMADA
            ProximoEventoWidget.actualizarTodos(this)

            mostrarSnackbar("Disponibilidad confirmada: $nombre", "Deshacer") {
                estado.aplicarEstado(anterior)
                DatosEventos.buscar(nombre)?.estado = anterior
                ProximoEventoWidget.actualizarTodos(this)
            }
        }

        // No disponible: pide confirmación con una alerta
        btnNoDisponible.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("No disponible")
                .setMessage("¿Confirmas que no estarás disponible para este evento?")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Confirmar") { _, _ ->
                    val anterior =
                        estado.tag as? EstadoEvento ?: EstadoEvento.PENDIENTE
                    estado.aplicarEstado(EstadoEvento.NO_DISPONIBLE)
                    DatosEventos.buscar(nombre)?.estado = EstadoEvento.NO_DISPONIBLE
                    ProximoEventoWidget.actualizarTodos(this)

                    mostrarSnackbar("Marcaste no disponible: $nombre", "Deshacer") {
                        estado.aplicarEstado(anterior)
                DatosEventos.buscar(nombre)?.estado = anterior
                ProximoEventoWidget.actualizarTodos(this)
                    }
                }
                .show()
        }
    }

    // Inflar el menú del mesero
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_mesero, menu)
        return true
    }

    // Atender las opciones del menú
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (manejarMenuMesero(item)) true
        else super.onOptionsItemSelected(item)
    }
}
