package com.example.meserearte

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PanelAdminActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_panel_admin)
        findViewById<View>(R.id.panelAdminRoot).ajustarBarrasDelSistema()

        // Barra superior, pestañas y correo del administrador
        configurarBarraAdmin(R.id.tabInicio)

        val correo = intent.getStringExtra(EXTRA_CORREO) ?: ""
        if (correo.isNotEmpty()) {
            findViewById<TextView>(R.id.tvBienvenidaAdmin).text =
                "Bienvenido, $correo"
        }

        // La tarjeta de notificaciones abre la vista 7
        findViewById<View>(R.id.cardNotificaciones).setOnClickListener {
            abrirSeccionAdmin(NotificacionesAdminActivity::class.java)
        }
    }

    // Inflar el menú del administrador
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_admin, menu)
        return true
    }

    // Atender las opciones del menú
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (manejarMenuAdmin(item)) true
        else super.onOptionsItemSelected(item)
    }
}
