
package com.example.meserearte

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Conectar la pantalla XML
        setContentView(R.layout.activity_login)
        findViewById<View>(R.id.loginRoot).ajustarBarrasDelSistema()

        // Obtener los elementos de la interfaz
        val rgRol = findViewById<RadioGroup>(R.id.rgRol)
        val etCorreo = findViewById<EditText>(R.id.etCorreo)
        val etContrasena = findViewById<EditText>(R.id.etContrasena)

        val btnAdmin = findViewById<Button>(R.id.btnEntrarAdmin)
        val btnMesero = findViewById<Button>(R.id.btnEntrarMesero)

        val tvRegistro = findViewById<TextView>(R.id.tvRegistro)

        // Cambiar botón según el rol seleccionado
        rgRol.setOnCheckedChangeListener { _, checkedId ->

            if (checkedId == R.id.rbAdministrador) {
                btnAdmin.visibility = View.VISIBLE
                btnMesero.visibility = View.GONE
            } else if (checkedId == R.id.rbMesero) {
                btnAdmin.visibility = View.GONE
                btnMesero.visibility = View.VISIBLE
            }
        }

        // Validar correo y contraseña
        fun validarAcceso(destino: Class<*>) {

            val correo = etCorreo.text.toString().trim()
            val contrasena = etContrasena.text.toString()

            // Validar correo electrónico
            if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                etCorreo.error = "Ingresa un correo válido"
                etCorreo.requestFocus()
                return
            }

            // Validar longitud de contraseña
            if (contrasena.length < 6) {
                etContrasena.error =
                    "La contraseña debe tener al menos 6 caracteres"
                etContrasena.requestFocus()
                return
            }

            // Datos válidos: abrir la pantalla del rol enviando el correo
            val intent = Intent(this, destino)
            intent.putExtra(EXTRA_CORREO, correo)
            startActivity(intent)
        }

        // Acceso del administrador
        btnAdmin.setOnClickListener {
            validarAcceso(PanelAdminActivity::class.java)
        }

        // Acceso del mesero
        btnMesero.setOnClickListener {
            validarAcceso(DisponibilidadActivity::class.java)
        }

        // Abrir la pantalla de registro
        tvRegistro.setOnClickListener {

            val intent = Intent(
                this,
                RegistroActivity::class.java
            )

            startActivity(intent)
        }
    }
}
