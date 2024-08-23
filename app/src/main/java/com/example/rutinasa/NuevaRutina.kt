package com.example.rutinasa

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class NuevaRutina : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.nueva_rutina_layout)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val BtnRutine = findViewById<AppCompatButton>(R.id.BtnRutine)
        BtnRutine.setOnClickListener { ejectRutine() }

    }

    private fun ejectRutine() {
        val eNombre = findViewById<AppCompatEditText>(R.id.eNombre)
        val Nombre:String = eNombre.text.toString()

        if (Nombre.isNotEmpty()) {

            // Aca hay que crear la nueva rutina (poner nombre y descripcion)

            // A la nueva pantalla hay que pasar algun tipo de id
            // que permita identificar la rutina en la que estamos

            val intent = Intent(this, Rutina::class.java)
            startActivity(intent)
        }
    }

}