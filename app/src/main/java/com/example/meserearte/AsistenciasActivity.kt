package com.example.meserearte

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar
import java.util.Locale

class AsistenciasActivity : AppCompatActivity() {
    private data class Asistencia(var nombre: String, var estado: String = "Pendiente", var hora: String = "Sin registrar")
    private val lista = mutableListOf(
        Asistencia("María López"), Asistencia("Juan Pérez"),
        Asistencia("Ana Torres"), Asistencia("Luis Gómez")
    )
    private lateinit var barra: ProgressBar
    private lateinit var resumen: TextView
    private val opciones = listOf("Pendiente", "Asistencia", "Retardo", "Inasistencia")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_asistencias)
        findViewById<View>(R.id.asistenciasRoot).ajustarBarrasDelSistema()
        configurarBarraMesero(0)
        barra = findViewById(R.id.progresoAsistencias)
        resumen = findViewById(R.id.tvResumenAsistencias)
        val contenedor = findViewById<LinearLayout>(R.id.contenedorAsistencias)
        lista.forEach { asistencia ->
            val tarjeta = layoutInflater.inflate(R.layout.item_asistencia, contenedor, false)
            tarjeta.findViewById<TextView>(R.id.tvAsistenciaNombre).text = asistencia.nombre
            val hora = tarjeta.findViewById<TextView>(R.id.tvAsistenciaHora)
            hora.text = asistencia.hora
            val spinner = tarjeta.findViewById<Spinner>(R.id.spEstadoAsistencia)
            spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, opciones)
            spinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: android.widget.AdapterView<*>?) = Unit
                override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                    asistencia.estado = opciones[position]
                    actualizarResumen()
                }
            }
            tarjeta.findViewById<Button>(R.id.btnCambiarHora).setOnClickListener {
                val cal = Calendar.getInstance()
                TimePickerDialog(this, { _, h, m ->
                    asistencia.hora = String.format(Locale.getDefault(), "%02d:%02d", h, m)
                    hora.text = "Llegada: ${asistencia.hora}"
                    actualizarResumen()
                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
            }
            contenedor.addView(tarjeta)
        }
        actualizarResumen()
    }

    private fun actualizarResumen() {
        val completos = lista.count { it.estado != "Pendiente" }
        barra.max = lista.size
        barra.progress = completos
        resumen.text = "$completos de ${lista.size} registrados · " +
            "${lista.count { it.estado == "Asistencia" }} asistencias · " +
            "${lista.count { it.estado == "Retardo" }} retardos · " +
            "${lista.count { it.estado == "Inasistencia" }} inasistencias"
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_mesero, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        manejarMenuMesero(item) || super.onOptionsItemSelected(item)
}
