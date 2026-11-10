package com.example.meserearte

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class UsuariosActivity : AppCompatActivity() {

    private lateinit var adapter: MeseroAdapter
    private lateinit var buscar: EditText
    private lateinit var filtros: ChipGroup
    private lateinit var contador: TextView
    private lateinit var vacio: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usuarios)
        findViewById<View>(R.id.usuariosRoot).ajustarBarrasDelSistema()
        configurarBarraAdmin(R.id.tabUsuarios)

        buscar = findViewById(R.id.etBuscarMesero)
        filtros = findViewById(R.id.chipFiltroMeseros)
        contador = findViewById(R.id.tvUsuariosActivos)
        vacio = findViewById(R.id.tvUsuariosVacio)
        adapter = MeseroAdapter(
            alCambiar = { mesero, activo ->
                mesero.activo = activo
                refrescar()
                mostrarSnackbar("${mesero.nombre}: ${if (activo) "Activo" else "Inactivo"}")
            },
            alVer = { m -> MaterialAlertDialogBuilder(this)
                .setTitle(m.nombre)
                .setMessage("Sala: ${m.sala}\nEventos participados: ${m.eventos}\nEstado: ${if (m.activo) "Activo" else "Inactivo"}")
                .setPositiveButton("Cerrar", null).show() },
            alEditar = { m -> editarMesero(m) },
            alEliminar = { m -> eliminarMesero(m) }
        )
        val lista = findViewById<RecyclerView>(R.id.rvMeseros)
        lista.layoutManager = LinearLayoutManager(this)
        lista.adapter = adapter
        buscar.doAfterTextChanged { refrescar() }
        filtros.setOnCheckedStateChangeListener { _, _ -> refrescar() }
        findViewById<Button>(R.id.btnNuevoMesero).setOnClickListener {
            editarMesero(null)
        }
        refrescar()
    }

    private fun refrescar() {
        val texto = buscar.text.toString().trim()
        val estado = filtros.checkedChipId
        val visibles = DatosMeseros.lista.filter { mesero ->
            mesero.nombre.contains(texto, ignoreCase = true) && when (estado) {
                R.id.chipActivos -> mesero.activo
                R.id.chipInactivos -> !mesero.activo
                else -> true
            }
        }
        adapter.actualizar(visibles)
        vacio.visibility = if (visibles.isEmpty()) View.VISIBLE else View.GONE
        contador.text = "Meseros activos: ${DatosMeseros.lista.count { it.activo }}"
    }

    private fun editarMesero(mesero: Mesero?) {
        val campoNombre = EditText(this).apply {
            hint = "Nombre del mesero"; setSingleLine(); setText(mesero?.nombre ?: "")
        }
        val campoSala = EditText(this).apply {
            hint = "Sala"; setSingleLine(); setText(mesero?.sala ?: "")
        }
        val campos = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            val margen = (20 * resources.displayMetrics.density).toInt()
            setPadding(margen, 0, margen, 0)
            addView(campoNombre); addView(campoSala)
        }
        val dialogo = MaterialAlertDialogBuilder(this)
            .setTitle(if (mesero == null) "Nuevo mesero" else "Editar mesero")
            .setView(campos)
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Guardar", null)
            .create()
        dialogo.setOnShowListener {
            dialogo.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val nombre = campoNombre.text.toString().trim()
                val sala = campoSala.text.toString().trim()
                if (nombre.isEmpty()) { campoNombre.error = "Escribe un nombre"; return@setOnClickListener }
                if (sala.isEmpty()) { campoSala.error = "Escribe una sala"; return@setOnClickListener }
                if (mesero == null) {
                    DatosMeseros.lista.add(Mesero(DatosMeseros.siguienteId(), nombre, sala, 0, true))
                } else {
                    mesero.nombre = nombre; mesero.sala = sala
                }
                dialogo.dismiss()
                refrescar()
                mostrarSnackbar("Mesero guardado")
            }
        }
        dialogo.show()
    }

    private fun eliminarMesero(mesero: Mesero) {
        MaterialAlertDialogBuilder(this)
            .setTitle("¿Eliminar mesero?")
            .setMessage("Se quitará a ${mesero.nombre} de la lista de demostración.")
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Eliminar") { _, _ ->
                val posicion = DatosMeseros.lista.indexOf(mesero)
                DatosMeseros.lista.remove(mesero)
                refrescar()
                mostrarSnackbar("Mesero eliminado", "Deshacer") {
                    DatosMeseros.lista.add(posicion.coerceIn(0, DatosMeseros.lista.size), mesero)
                    refrescar()
                }
            }.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_admin, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        manejarMenuAdmin(item) || super.onOptionsItemSelected(item)
}
