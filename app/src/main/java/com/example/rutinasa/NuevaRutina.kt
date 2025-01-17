package com.example.rutinasa

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File

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
        val eDescripcion = findViewById<AppCompatEditText>(R.id.eDescripcion)
        val Descripcion:String = eDescripcion.text.toString()

        if (Nombre.isNotEmpty()) {

            // Guardamos la rutina en la base de datos
            val id = DatabaseManager.insertRoutine(Nombre, Descripcion)

            if (id != -1) {
                val intent = Intent(this, Rutina::class.java)
                intent.putExtra("id", id)
                //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Ya existe una rutina con ese nombre.")
                    .setPositiveButton("Aceptar") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
    }

}