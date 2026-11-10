package com.example.meserearte

import android.app.Activity
import android.content.Intent
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

// ---------- Claves de los Intent (extras) ----------
const val EXTRA_CORREO = "correo_usuario"
const val EXTRA_EVENTO = "evento_nombre"
const val EXTRA_FECHA = "evento_fecha"
const val EXTRA_LUGAR = "evento_lugar"

// ---------- Estados de un evento ----------
enum class EstadoEvento(val texto: String, val fondo: Int, val colorTexto: Int) {
    PENDIENTE("Pendiente", R.drawable.bg_chip_pending, R.color.warn_text),
    PROGRAMADO("Programado", R.drawable.bg_chip_pending, R.color.warn_text),
    CONFIRMADA("Confirmada", R.drawable.bg_chip_confirmed, R.color.ok_text),
    NO_DISPONIBLE("No disponible", R.drawable.bg_chip_cancelled, R.color.bad_text)
}

// Cambia el texto y los colores de una etiqueta de estado
fun TextView.aplicarEstado(estado: EstadoEvento) {
    text = estado.texto
    setBackgroundResource(estado.fondo)
    setTextColor(context.getColor(estado.colorTexto))
    tag = estado
}

// ---------- Snackbar con los colores de MesereArte ----------
fun Activity.mostrarSnackbar(
    mensaje: String,
    accion: String? = null,
    alAccionar: (() -> Unit)? = null
): Snackbar {
    val snackbar = Snackbar.make(
        findViewById(android.R.id.content),
        mensaje,
        Snackbar.LENGTH_LONG
    )
    snackbar.setBackgroundTint(getColor(R.color.brown_dark))
    snackbar.setTextColor(getColor(R.color.white))
    if (accion != null && alAccionar != null) {
        snackbar.setActionTextColor(getColor(R.color.tan))
        snackbar.setAction(accion) { alAccionar() }
    }
    snackbar.show()
    return snackbar
}

fun Activity.avisarEnDesarrollo(seccion: String) {
    mostrarSnackbar("$seccion: vista en desarrollo")
}

// ---------- Cerrar sesión ----------

// Regresa a la pantalla de acceso y limpia la pila de pantallas
fun Activity.cerrarSesion() {
    val intent = Intent(this, LoginActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    startActivity(intent)
    finish()
}

// Alerta de confirmación antes de cerrar sesión
fun Activity.confirmarCerrarSesion() {
    MaterialAlertDialogBuilder(this)
        .setTitle("¿Cerrar sesión?")
        .setMessage("Regresarás a la pantalla de inicio de sesión.")
        .setNegativeButton("Cancelar", null)
        .setPositiveButton("Cerrar sesión") { _, _ -> cerrarSesion() }
        .show()
}

// ---------- Barra superior, pestañas y menú del ADMINISTRADOR ----------

private fun AppCompatActivity.correoActual(): String =
    intent.getStringExtra(EXTRA_CORREO) ?: ""

// Resalta la pestaña de la pantalla actual
private fun marcarPestanaActiva(tab: TextView) {
    tab.setBackgroundResource(R.drawable.bg_tab_selected)
    tab.setTextColor(tab.context.getColor(R.color.burgundy))
    tab.setTypeface(tab.typeface, android.graphics.Typeface.BOLD)
}

fun AppCompatActivity.configurarBarraAdmin(tabActual: Int) {
    val toolbar = findViewById<MaterialToolbar>(R.id.toolbarAdmin)
    setSupportActionBar(toolbar)

    val correo = correoActual()
    if (correo.isNotEmpty()) toolbar.subtitle = correo

    val pestanas = listOf(
        R.id.tabInicio to "Inicio",
        R.id.tabSalas to "Salas",
        R.id.tabUsuarios to "Usuarios",
        R.id.tabEventos to "Eventos",
        R.id.tabAsignaciones to "Asignaciones"
    )

    for ((id, nombre) in pestanas) {
        val tab = findViewById<TextView>(id)
        if (id == tabActual) marcarPestanaActiva(tab)

        tab.setOnClickListener {
            when (id) {
                R.id.tabInicio -> volverAlPanel()
                R.id.tabEventos -> abrirSeccionAdmin(EventosActivity::class.java)
                R.id.tabUsuarios -> abrirSeccionAdmin(UsuariosActivity::class.java)
                else -> avisarEnDesarrollo(nombre)
            }
        }
    }
}

// Intent explícito hacia otra sección del administrador (lleva el correo)
fun AppCompatActivity.abrirSeccionAdmin(destino: Class<*>) {
    if (this.javaClass == destino) return
    val intent = Intent(this, destino)
    intent.putExtra(EXTRA_CORREO, correoActual())
    startActivity(intent)
    // El panel queda en la pila; las demás secciones se reemplazan entre sí
    if (this !is PanelAdminActivity) finish()
}

fun AppCompatActivity.volverAlPanel() {
    if (this !is PanelAdminActivity) finish()
}

fun AppCompatActivity.manejarMenuAdmin(item: MenuItem): Boolean {
    return when (item.itemId) {
        R.id.menu_admin_cerrar_sesion -> {
            confirmarCerrarSesion()
            true
        }
        R.id.menu_admin_eventos -> {
            abrirSeccionAdmin(EventosActivity::class.java)
            true
        }
        R.id.menu_admin_usuarios -> {
            abrirSeccionAdmin(UsuariosActivity::class.java)
            true
        }
        R.id.menu_admin_notificaciones -> {
            abrirSeccionAdmin(NotificacionesAdminActivity::class.java)
            true
        }
        R.id.menu_admin_salas,
        R.id.menu_admin_asignaciones -> {
            avisarEnDesarrollo(item.title.toString())
            true
        }
        else -> false
    }
}

// ---------- Barra superior, pestañas y menú del MESERO ----------

fun AppCompatActivity.configurarBarraMesero(tabActual: Int) {
    val toolbar = findViewById<MaterialToolbar>(R.id.toolbarMesero)
    setSupportActionBar(toolbar)

    val correo = correoActual()
    if (correo.isNotEmpty()) toolbar.subtitle = correo

    val pestanas = listOf(
        R.id.tabInicio to "Inicio",
        R.id.tabMisEventos to "Mis eventos",
        R.id.tabDisponibilidad to "Disponibilidad",
        R.id.tabNotificaciones to "Notificaciones"
    )

    for ((id, nombre) in pestanas) {
        val tab = findViewById<TextView>(id)
        if (id == tabActual) marcarPestanaActiva(tab)

        tab.setOnClickListener {
            when (id) {
                R.id.tabMisEventos -> abrirMisEventos()
                R.id.tabInicio -> abrirInicioMesero()
                R.id.tabDisponibilidad -> abrirDisponibilidad()
                else -> avisarEnDesarrollo(nombre)
            }
        }
    }
}

fun AppCompatActivity.abrirInicioMesero() {
    if (this is InicioMeseroActivity) return
    val intent = Intent(this, InicioMeseroActivity::class.java)
    intent.putExtra(EXTRA_CORREO, correoActual())
    startActivity(intent)
}

// Intent explícito hacia Eventos asignados con nombre, fecha y lugar
fun AppCompatActivity.abrirMisEventos() {
    if (this is EventosAsignadosActivity) return
    val evento = DatosEventos.buscar("Boda Ramírez") ?: DatosEventos.lista.first()
    val intent = Intent(this, EventosAsignadosActivity::class.java)
    intent.putExtra(EXTRA_CORREO, correoActual())
    intent.putExtra(EXTRA_EVENTO, evento.nombre)
    intent.putExtra(EXTRA_FECHA, evento.fecha)
    intent.putExtra(EXTRA_LUGAR, evento.lugar)
    startActivity(intent)
}

fun AppCompatActivity.abrirDisponibilidad() {
    if (this is DisponibilidadActivity) return
    val intent = Intent(this, DisponibilidadActivity::class.java)
    intent.putExtra(EXTRA_CORREO, correoActual())
    startActivity(intent)
}

fun AppCompatActivity.manejarMenuMesero(item: MenuItem): Boolean {
    return when (item.itemId) {
        R.id.menu_mesero_cerrar_sesion -> {
            confirmarCerrarSesion()
            true
        }
        R.id.menu_mesero_capitan -> {
            startActivity(Intent(this, AsistenciasActivity::class.java).apply {
                putExtra(EXTRA_CORREO, correoActual())
            })
            true
        }
        R.id.menu_mesero_inicio -> {
            abrirInicioMesero()
            true
        }
        R.id.menu_mesero_mis_eventos -> {
            abrirMisEventos()
            true
        }
        R.id.menu_mesero_inicio,
        R.id.menu_mesero_notificaciones,
        R.id.menu_mesero_perfil -> {
            avisarEnDesarrollo(item.title.toString())
            true
        }
        else -> false
    }
}

// ---------- Márgenes para las barras del sistema ----------

// Evita que el contenido quede debajo de las barras del sistema
// (Android 15 en adelante dibuja la app de borde a borde)
fun View.ajustarBarrasDelSistema() {
    val izquierda = paddingLeft
    val arriba = paddingTop
    val derecha = paddingRight
    val abajo = paddingBottom

    ViewCompat.setOnApplyWindowInsetsListener(this) { vista, insets ->
        val barras = insets.getInsets(
            WindowInsetsCompat.Type.systemBars() or
                    WindowInsetsCompat.Type.displayCutout()
        )
        vista.setPadding(
            izquierda + barras.left,
            arriba + barras.top,
            derecha + barras.right,
            abajo + barras.bottom
        )
        insets
    }
    ViewCompat.requestApplyInsets(this)
}
