package com.example.meserearte

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class NotificacionesAdminActivity : AppCompatActivity() {

    // Solicitud del permiso POST_NOTIFICATIONS (Android 13 o superior)
    private val solicitarPermiso = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* El resultado se verifica al intentar enviar el aviso */ }

    private lateinit var spTipo: Spinner
    private lateinit var spEvento: Spinner
    private lateinit var etTitulo: EditText
    private lateinit var etMensaje: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notificaciones_admin)
        findViewById<View>(R.id.notificacionesRoot).ajustarBarrasDelSistema()

        // Barra superior, menú y pestañas (ninguna pestaña activa)
        configurarBarraAdmin(0)

        spTipo = findViewById(R.id.spTipo)
        spEvento = findViewById(R.id.spEvento)
        etTitulo = findViewById(R.id.etTitulo)
        etMensaje = findViewById(R.id.etMensaje)

        spTipo.adapter = crearAdaptador(
            listOf("Recordatorio", "Cambio de horario", "Uniforme actualizado")
        )
        spEvento.adapter = crearAdaptador(DatosEventos.lista.map { it.nombre })

        pedirPermisoDeNotificaciones()

        findViewById<Button>(R.id.btnEnviar).setOnClickListener {
            validarYConfirmar()
        }
    }

    private fun crearAdaptador(opciones: List<String>): ArrayAdapter<String> {
        val adaptador = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            opciones
        )
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        return adaptador
    }

    // Android 13 o superior: pedir el permiso en tiempo de ejecución
    private fun pedirPermisoDeNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val concedido = ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!concedido) {
                solicitarPermiso.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // Validación previa: el título y el mensaje no pueden estar vacíos
    private fun validarYConfirmar() {
        val titulo = etTitulo.text.toString().trim()
        val mensaje = etMensaje.text.toString().trim()

        if (titulo.isEmpty()) {
            etTitulo.requestFocus()
            mostrarSnackbar("Escribe un título para el aviso")
            return
        }

        if (mensaje.isEmpty()) {
            etMensaje.requestFocus()
            mostrarSnackbar("Escribe el mensaje del aviso")
            return
        }

        // Alerta de confirmación
        MaterialAlertDialogBuilder(this)
            .setTitle("¿Enviar notificación?")
            .setMessage("Se enviará a los destinatarios seleccionados.")
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Enviar") { _, _ -> enviar(titulo, mensaje) }
            .show()
    }

    private fun enviar(titulo: String, mensaje: String) {
        // Sin permiso (o con las notificaciones desactivadas) no se envía
        if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            mostrarSnackbar(
                "Activa el permiso de notificaciones para enviar avisos",
                "Ajustes"
            ) { abrirAjustesDeNotificaciones() }
            return
        }

        val tipo = spTipo.selectedItem.toString()
        val evento = DatosEventos.lista[spEvento.selectedItemPosition]

        Notificaciones.enviar(this, tipo, evento, titulo, mensaje)

        etTitulo.text.clear()
        etMensaje.text.clear()
        mostrarSnackbar("Notificación enviada a todos los meseros asignados")
    }

    private fun abrirAjustesDeNotificaciones() {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        } else {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .setData(Uri.parse("package:$packageName"))
        }
        startActivity(intent)
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
