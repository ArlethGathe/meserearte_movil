package com.example.meserearte

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class InicioMeseroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio_mesero)
        findViewById<View>(R.id.inicioMeseroRoot).ajustarBarrasDelSistema()
        configurarBarraMesero(R.id.tabInicio)

        val evento = DatosEventos.buscar("Boda Ramírez") ?: DatosEventos.lista.first()
        findViewById<TextView>(R.id.tvInicioProximo).text = evento.nombre
        findViewById<TextView>(R.id.tvInicioFecha).text = evento.fecha
        findViewById<TextView>(R.id.tvInicioLugar).text = evento.lugar
        // Datos de muestra compartidos con el widget del escritorio.
        ProximoEventoPrefs.guardar(this, evento)

        findViewById<Button>(R.id.btnInicioDisponibilidad).setOnClickListener {
            startActivity(Intent(this, DisponibilidadActivity::class.java).apply {
                putExtra(EXTRA_CORREO, intent.getStringExtra(EXTRA_CORREO) ?: "")
            })
        }
        findViewById<Button>(R.id.btnInicioEventos).setOnClickListener {
            abrirMisEventos()
        }
    }

    override fun onResume() {
        super.onResume()
        val total = DatosEventos.lista.size
        val registradas = DatosEventos.lista.count {
            it.estado == EstadoEvento.CONFIRMADA || it.estado == EstadoEvento.NO_DISPONIBLE
        }
        findViewById<ProgressBar>(R.id.progresoConfirmaciones).apply {
            max = total
            progress = registradas
        }
        findViewById<TextView>(R.id.tvConfirmaciones).text =
            "$registradas de $total confirmaciones registradas"
        findViewById<TextView>(R.id.tvConteoEventos).text =
            "$total eventos asignados · ${total - registradas} pendientes"
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_mesero, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        manejarMenuMesero(item) || super.onOptionsItemSelected(item)
}
