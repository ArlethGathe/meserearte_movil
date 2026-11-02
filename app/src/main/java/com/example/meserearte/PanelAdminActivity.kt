package com.example.meserearte

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class PanelAdminActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_panel_admin)
        findViewById<View>(R.id.panelAdminRoot).ajustarBarrasDelSistema()

        // Barra superior con menú de opciones
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbarAdmin)
        setSupportActionBar(toolbar)

        // Mostrar el correo recibido desde el inicio de sesión
        val correo = intent.getStringExtra(EXTRA_CORREO) ?: ""
        if (correo.isNotEmpty()) {
            toolbar.subtitle = correo
            findViewById<TextView>(R.id.tvBienvenidaAdmin).text =
                "Bienvenido, $correo"
        }

        // Pestañas de navegación (sus vistas se desarrollarán después)
        val pestanas = mapOf(
            R.id.tabInicio to "Inicio",
            R.id.tabSalas to "Salas",
            R.id.tabUsuarios to "Usuarios",
            R.id.tabEventos to "Eventos",
            R.id.tabAsignaciones to "Asignaciones"
        )
        for ((id, nombre) in pestanas) {
            findViewById<TextView>(id).setOnClickListener {
                avisar(nombre)
            }
        }
    }

    // Inflar el menú del administrador
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_admin, menu)
        return true
    }

    // Atender las opciones del menú
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_admin_cerrar_sesion -> {
                cerrarSesion()
                true
            }
            R.id.menu_admin_salas,
            R.id.menu_admin_usuarios,
            R.id.menu_admin_eventos,
            R.id.menu_admin_asignaciones -> {
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
