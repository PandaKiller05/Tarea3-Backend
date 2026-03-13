package com.example.front_3

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.front_3.network.LoginReq
import com.example.front_3.network.RetrofitClient
import kotlinx.coroutines.launch
import java.io.IOException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tvApiStatus = findViewById<TextView>(R.id.tvApiStatus)
        val etEmail = findViewById<EditText>(R.id.etEmailLogin)
        val etPassword = findViewById<EditText>(R.id.etPasswordLogin)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnGoToRegister = findViewById<Button>(R.id.btnGoToRegister)

        // EJERCICIO 1: Petición GET a raíz al iniciar la app
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.checkApi()
                if (response.isSuccessful) {
                    tvApiStatus.text = "API: ${response.body()?.message}"
                } else {
                    tvApiStatus.text = "API: Error en respuesta"
                }
            } catch (e: Exception) {
                tvApiStatus.text = "API: Desconectada"
            }
        }

        // EJERCICIOS 3 y 4: Login y manejo de errores de red
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val pass = etPassword.text.toString()

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Llena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.instance.login(LoginReq(email, pass))

                    if (response.isSuccessful) {
                        val userName = response.body()?.user?.name ?: "Usuario"
                        Toast.makeText(this@MainActivity, "Login exitoso", Toast.LENGTH_SHORT).show()

                        // Navegar a la pantalla de bienvenida
                        val intent = Intent(this@MainActivity, WelcomeActivity::class.java)
                        intent.putExtra("USER_NAME", userName)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@MainActivity, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: IOException) {
                    // EJERCICIO 4: Captura de excepción si se detiene el contenedor Docker
                    Toast.makeText(this@MainActivity, "Error de red: No se pudo conectar al servidor. Intenta más tarde.", Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "Error inesperado", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Navegar a la pantalla de registro
        btnGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}