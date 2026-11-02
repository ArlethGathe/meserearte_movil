package com.example.meserearte

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class EventosAsignadosActivity : AppCompatActivity() {

    private var nombre = ""
    private var fecha = ""
    private var lugar = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eventos_asignados)
        findViewById<View>(R.id.eventosAsignadosRoot).ajustarBarrasDelSistema()

        // Barra superior, menú y pestañas del mesero
        configurarBarraMesero(R.id.tabMisEventos)

        // Datos recibidos por el intent (si falta alguno se busca por nombre,
        // que es lo único que envía la notificación)
        nombre = intent.getStringExtra(EXTRA_EVENTO) ?: "Boda Ramírez"
        val conocido = DatosEventos.buscar(nombre)
        fecha = intent.getStringExtra(EXTRA_FECHA)
            ?: conocido?.fecha ?: "Fecha por confirmar"
        lugar = intent.getStringExtra(EXTRA_LUGAR)
            ?: conocido?.lugar ?: "Lugar por confirmar"

        findViewById<TextView>(R.id.tvEventoNombre).text = nombre
        findViewById<TextView>(R.id.tvEventoFecha).text = fecha
        findViewById<TextView>(R.id.tvEventoLugar).text = lugar
        findViewById<TextView>(R.id.tvEventoEstado)
            .aplicarEstado(EstadoEvento.CONFIRMADA)

        findViewById<Button>(R.id.btnComoLlegar).setOnClickListener {
            comoLlegar()
        }
        findViewById<Button>(R.id.btnCompartir).setOnClickListener {
            compartirEvento()
        }
    }

    // Intent implícito: abrir una aplicación de mapas con la ubicación
    private fun comoLlegar() {
        val consulta = Uri.encode(lugar)

        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$consulta")))
        } catch (e: ActivityNotFoundException) {
            // Sin app de mapas: intentar con el navegador
            try {
                val web = Uri.parse(
                    "https://www.google.com/maps/search/?api=1&query=$consulta"
                )
                startActivity(Intent(Intent.ACTION_VIEW, web))
            } catch (e2: ActivityNotFoundException) {
                mostrarSnackbar("No hay una aplicación de mapas instalada")
            }
        }
    }

    // Intent implícito: compartir el resumen del evento como texto plano
    private fun compartirEvento() {
        val resumen = "Evento: $nombre\n" +
                "Fecha: $fecha\n" +
                "Lugar: $lugar\n" +
                "Uniforme: ${getString(R.string.detalle_uniforme)}\n" +
                "Indicaciones: ${getString(R.string.detalle_indicaciones)}\n" +
                "Capitán: ${getString(R.string.detalle_capitan)}"

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_SUBJECT, "Evento: $nombre")
        intent.putExtra(Intent.EXTRA_TEXT, resumen)

        try {
            startActivity(Intent.createChooser(intent, "Compartir evento"))
        } catch (e: ActivityNotFoundException) {
            mostrarSnackbar("No hay una aplicación para compartir el evento")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_mesero, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (manejarMenuMesero(item)) true
        else super.onOptionsItemSelected(item)
    }
}
