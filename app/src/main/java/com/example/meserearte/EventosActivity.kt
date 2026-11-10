package com.example.meserearte

import android.os.Bundle
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import java.util.Calendar
import java.util.Locale
import java.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class EventosActivity : AppCompatActivity() {

    private lateinit var contenedor: LinearLayout
    private lateinit var tvSinResultados: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eventos)
        findViewById<View>(R.id.eventosRoot).ajustarBarrasDelSistema()

        // Barra superior, menú y pestañas
        configurarBarraAdmin(R.id.tabEventos)

        contenedor = findViewById(R.id.llEventos)
        tvSinResultados = findViewById(R.id.tvSinResultados)

        // Crear una tarjeta por cada evento
        for (evento in DatosEventos.gestion) {
            agregarTarjeta(evento)
        }

        // La búsqueda filtra las tarjetas mientras se escribe
        findViewById<EditText>(R.id.etBuscar).doAfterTextChanged { texto ->
            filtrar(texto?.toString() ?: "")
        }

        findViewById<Button>(R.id.btnNuevoEvento).setOnClickListener {
            mostrarDialogoNuevoEvento()
        }
    }

    // Formulario con los widgets DatePicker y TimePicker de la práctica.
    private fun mostrarDialogoNuevoEvento() {
        val vista = LayoutInflater.from(this).inflate(R.layout.dialogo_nuevo_evento, null)
        val campoNombre = vista.findViewById<EditText>(R.id.etNuevoEventoNombre)
        val campoLugar = vista.findViewById<EditText>(R.id.etNuevoEventoLugar)
        val botonFecha = vista.findViewById<Button>(R.id.btnElegirFecha)
        val botonHora = vista.findViewById<Button>(R.id.btnElegirHora)
        var fecha = ""
        var hora = ""
        botonFecha.setOnClickListener {
            val hoy = Calendar.getInstance()
            DatePickerDialog(this, { _, anio, mes, dia ->
                val elegido = Calendar.getInstance().apply { set(anio, mes, dia) }
                fecha = SimpleDateFormat("dd MMM yyyy", Locale("es", "MX")).format(elegido.time)
                botonFecha.text = fecha
            }, hoy.get(Calendar.YEAR), hoy.get(Calendar.MONTH), hoy.get(Calendar.DAY_OF_MONTH)).show()
        }
        botonHora.setOnClickListener {
            val ahora = Calendar.getInstance()
            TimePickerDialog(this, { _, h, m ->
                hora = String.format(Locale.getDefault(), "%02d:%02d", h, m)
                botonHora.text = hora
            }, ahora.get(Calendar.HOUR_OF_DAY), ahora.get(Calendar.MINUTE), true).show()
        }
        val dialogo = MaterialAlertDialogBuilder(this)
            .setTitle("Nuevo evento")
            .setView(vista)
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Guardar", null)
            .create()
        dialogo.setOnShowListener {
            dialogo.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val nombre = campoNombre.text.toString().trim()
                val lugar = campoLugar.text.toString().trim()
                if (nombre.isBlank()) { campoNombre.error = "Ingresa un nombre"; return@setOnClickListener }
                if (lugar.isBlank()) { campoLugar.error = "Ingresa un lugar"; return@setOnClickListener }
                if (fecha.isBlank() || hora.isBlank()) {
                    mostrarSnackbar("Selecciona la fecha y la hora")
                    return@setOnClickListener
                }
                val nuevo = Evento(nombre, "$fecha · $hora", lugar, 0, EstadoEvento.PROGRAMADO)
                DatosEventos.lista.add(nuevo)
                agregarTarjeta(nuevo)
                filtrar("")
                findViewById<EditText>(R.id.etBuscar).setText("")
                dialogo.dismiss()
                mostrarSnackbar("Evento programado correctamente")
            }
        }
        dialogo.show()
    }

    // Infla la tarjeta de un evento y le asigna sus datos y acciones
    private fun agregarTarjeta(evento: Evento) {
        val tarjeta = LayoutInflater.from(this)
            .inflate(R.layout.item_evento, contenedor, false)

        tarjeta.tag = evento.nombre

        tarjeta.findViewById<TextView>(R.id.tvNombre).text = evento.nombre
        tarjeta.findViewById<TextView>(R.id.tvFecha).text = evento.fecha
        tarjeta.findViewById<TextView>(R.id.tvLugar).text = evento.lugar
        tarjeta.findViewById<TextView>(R.id.tvMeseros).text =
            "${evento.meseros} meseros asignados"
        tarjeta.findViewById<TextView>(R.id.tvEstado).aplicarEstado(evento.estado)

        tarjeta.findViewById<Button>(R.id.btnVer).setOnClickListener {
            avisarEnDesarrollo("Ver ${evento.nombre}")
        }
        tarjeta.findViewById<Button>(R.id.btnEditar).setOnClickListener {
            avisarEnDesarrollo("Editar ${evento.nombre}")
        }
        tarjeta.findViewById<Button>(R.id.btnEliminar).setOnClickListener {
            confirmarEliminar(tarjeta)
        }

        contenedor.addView(tarjeta)
    }

    // Alerta de confirmación antes de eliminar
    private fun confirmarEliminar(tarjeta: View) {
        MaterialAlertDialogBuilder(this)
            .setTitle("¿Eliminar evento?")
            .setMessage("Se eliminará el evento seleccionado.")
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Eliminar") { _, _ -> eliminar(tarjeta) }
            .show()
    }

    // Retira la tarjeta y ofrece deshacer con un Snackbar
    private fun eliminar(tarjeta: View) {
        val posicion = contenedor.indexOfChild(tarjeta)
        contenedor.removeView(tarjeta)
        actualizarMensajeVacio()

        mostrarSnackbar("Evento eliminado", "Deshacer") {
            contenedor.addView(tarjeta, minOf(posicion, contenedor.childCount))
            actualizarMensajeVacio()
        }
    }

    // Muestra solo las tarjetas cuyo nombre contiene el texto buscado
    private fun filtrar(texto: String) {
        val busqueda = texto.trim()
        for (i in 0 until contenedor.childCount) {
            val tarjeta = contenedor.getChildAt(i)
            val nombre = tarjeta.tag as? String ?: ""
            tarjeta.visibility =
                if (nombre.contains(busqueda, ignoreCase = true)) View.VISIBLE
                else View.GONE
        }
        actualizarMensajeVacio()
    }

    private fun actualizarMensajeVacio() {
        var visibles = 0
        for (i in 0 until contenedor.childCount) {
            if (contenedor.getChildAt(i).visibility == View.VISIBLE) visibles++
        }
        tvSinResultados.visibility = if (visibles == 0) View.VISIBLE else View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_admin, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (manejarMenuAdmin(item)) true
        else super.onOptionsItemSelected(item)
    }
}
