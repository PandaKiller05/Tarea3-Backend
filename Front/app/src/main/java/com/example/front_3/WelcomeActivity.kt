package com.example.front_3

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val tvWelcome = findViewById<TextView>(R.id.tvWelcomeUser)

        // Recuperamos el nombre que enviamos desde el MainActivity
        val userName = intent.getStringExtra("USER_NAME") ?: "Usuario"

        tvWelcome.text = "¡Bienvenido, $userName!"
    }
}