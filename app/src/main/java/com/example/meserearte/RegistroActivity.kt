
package com.example.meserearte

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegistroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Conectar la interfaz XML
        setContentView(R.layout.activity_registro)

        // Elementos del formulario
        val rgRol = findViewById<RadioGroup>(R.id.rgRolRegistro)

        val etNombre = findViewById<EditText>(R.id.etNombre)
        val etApellido = findViewById<EditText>(R.id.etApellido)
        val etCorreo = findViewById<EditText>(R.id.etCorreoRegistro)
        val etContrasena = findViewById<EditText>(R.id.etContrasenaRegistro)
        val etSala = findViewById<EditText>(R.id.etSala)

        val tvSalaEtiqueta =
            findViewById<TextView>(R.id.tvSalaEtiqueta)

        val cbPrivacidad =
            findViewById<CheckBox>(R.id.cbPrivacidad)

        val btnCrearSala =
            findViewById<Button>(R.id.btnCrearSala)

        val btnUnirme =
            findViewById<Button>(R.id.btnUnirme)

        val tvVolverLogin =
            findViewById<TextView>(R.id.tvVolverLogin)

        // Cambiar la interfaz según el rol
        rgRol.setOnCheckedChangeListener { _, checkedId ->

            if (checkedId == R.id.rbRegistroAdmin) {

                tvSalaEtiqueta.text = "Nombre de la nueva sala"
                etSala.hint = "Nombre de tu sala"

                btnCrearSala.visibility = View.VISIBLE
                btnUnirme.visibility = View.GONE

            } else if (checkedId == R.id.rbRegistroMesero) {

                tvSalaEtiqueta.text = "Código de sala"
                etSala.hint = "Ingresa el código de tu sala"

                btnCrearSala.visibility = View.GONE
                btnUnirme.visibility = View.VISIBLE
            }
        }

        // Validar los datos del registro
        fun validarRegistro(rol: String) {

            val nombre = etNombre.text.toString().trim()
            val apellido = etApellido.text.toString().trim()
            val correo = etCorreo.text.toString().trim()
            val contrasena = etContrasena.text.toString()
            val sala = etSala.text.toString().trim()

            // Validar nombre
            if (nombre.isEmpty()) {
                etNombre.error = "Ingresa tu nombre"
                etNombre.requestFocus()
                return
            }

            // Validar apellido
            if (apellido.isEmpty()) {
                etApellido.error = "Ingresa tu apellido"
                etApellido.requestFocus()
                return
            }

            // Validar correo
            if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                etCorreo.error = "Ingresa un correo válido"
                etCorreo.requestFocus()
                return
            }

            // Validar contraseña
            if (contrasena.length < 6) {
                etContrasena.error =
                    "La contraseña debe tener al menos 6 caracteres"
                etContrasena.requestFocus()
                return
            }

            // Validar nombre o código de sala
            if (sala.isEmpty()) {
                etSala.error = "Este campo es obligatorio"
                etSala.requestFocus()
                return
            }

            // Validar aviso de privacidad
            if (!cbPrivacidad.isChecked) {
                Toast.makeText(
                    this,
                    "Debes aceptar el aviso de privacidad",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            // Confirmación del formulario
            Toast.makeText(
                this,
                "Formulario de $rol validado correctamente",
                Toast.LENGTH_LONG
            ).show()

            // Regresar a la pantalla anterior
            finish()
        }

        // Registrar administrador
        btnCrearSala.setOnClickListener {
            validarRegistro("Administrador")
        }

        // Registrar mesero
        btnUnirme.setOnClickListener {
            validarRegistro("Mesero")
        }

        // Volver al inicio de sesión
        tvVolverLogin.setOnClickListener {
            finish()
        }
    }
}
