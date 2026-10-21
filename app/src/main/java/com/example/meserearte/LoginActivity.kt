
package com.example.meserearte

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        // Elementos de la interfaz
        val rgRol = findViewById<RadioGroup>(R.id.rgRol)
        val etCorreo = findViewById<EditText>(R.id.etCorreo)
        val etContrasena = findViewById<EditText>(R.id.etContrasena)

        val btnAdmin = findViewById<Button>(R.id.btnEntrarAdmin)
        val btnMesero = findViewById<Button>(R.id.btnEntrarMesero)

        // Cambiar el botón según el rol seleccionado
        rgRol.setOnCheckedChangeListener { _, checkedId ->

            if (checkedId == R.id.rbAdministrador) {
                btnAdmin.visibility = View.VISIBLE
                btnMesero.visibility = View.GONE
            } else {
                btnAdmin.visibility = View.GONE
                btnMesero.visibility = View.VISIBLE
            }
        }

        // Validar correo y contraseña
        fun validarAcceso(rol: String) {

            val correo = etCorreo.text.toString().trim()
            val contrasena = etContrasena.text.toString()

            if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                etCorreo.error = "Ingresa un correo válido"
                etCorreo.requestFocus()
                return
            }

            if (contrasena.length < 6) {
                etContrasena.error =
                    "La contraseña debe tener al menos 6 caracteres"
                etContrasena.requestFocus()
                return
            }

            Toast.makeText(
                this,
                "Formulario válido para $rol",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Acceso del administrador
        btnAdmin.setOnClickListener {
            validarAcceso("Administrador")
        }

        // Acceso del mesero
        btnMesero.setOnClickListener {
            validarAcceso("Mesero")
        }
    }
}
