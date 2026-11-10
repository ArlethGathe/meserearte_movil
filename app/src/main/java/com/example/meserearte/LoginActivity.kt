package com.example.meserearte

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    // Resultado de la pantalla de registro: devuelve el correo capturado
    private val registroLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { resultado ->
        if (resultado.resultCode == RESULT_OK) {
            val correo = resultado.data?.getStringExtra(EXTRA_CORREO) ?: ""
            findViewById<EditText>(R.id.etCorreo).setText(correo)
            mostrarSnackbar("Cuenta creada. Ya puedes iniciar sesión")
        }
    }

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
                etCorreo.requestFocus()
                mostrarSnackbar("Ingresa un correo válido")
                return
            }

            // Validar longitud de contraseña
            if (contrasena.length < 6) {
                etContrasena.requestFocus()
                mostrarSnackbar("La contraseña debe tener al menos 6 caracteres")
                return
            }

            // Datos válidos: intent explícito enviando el correo
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
            validarAcceso(InicioMeseroActivity::class.java)
        }

        // Abrir el registro esperando un resultado
        tvRegistro.setOnClickListener {
            registroLauncher.launch(Intent(this, RegistroActivity::class.java))
        }
    }
}
