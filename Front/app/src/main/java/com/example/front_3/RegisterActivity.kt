package com.example.front_3

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.front_3.network.RegisterReq
import com.example.front_3.network.RetrofitClient
import kotlinx.coroutines.launch
import java.io.IOException

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etName = findViewById<EditText>(R.id.etNameReg)
        val etEmail = findViewById<EditText>(R.id.etEmailReg)
        val etPassword = findViewById<EditText>(R.id.etPasswordReg)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val name = etName.text.toString()
            val email = etEmail.text.toString()
            val pass = etPassword.text.toString()

            if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Llena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.instance.register(RegisterReq(name, email, pass))

                    if (response.isSuccessful) {
                        Toast.makeText(this@RegisterActivity, "Usuario registrado con éxito", Toast.LENGTH_LONG).show()
                        finish() // Cierra esta pantalla y regresa al login
                    } else if (response.code() == 409) {
                        Toast.makeText(this@RegisterActivity, "El usuario ya existe", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this@RegisterActivity, "Error al registrar", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: IOException) {
                    Toast.makeText(this@RegisterActivity, "Error de red: Servidor no disponible", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}