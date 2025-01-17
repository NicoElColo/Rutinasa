package com.example.rutinasa

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class NuevoDia : AppCompatActivity() {

    private var idRoutine : Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        setContentView(R.layout.nuevo_dia_layout)

        idRoutine = intent.getIntExtra("id", -1)

        val BtnDia = findViewById<AppCompatButton>(R.id.BtnDia)
        BtnDia.setOnClickListener { ejectDay() }

    }

    private fun ejectDay() {
        val eNombre = findViewById<AppCompatEditText>(R.id.eNombreDia)
        val Nombre:String = eNombre.text.toString()
        val eDescripcion = findViewById<AppCompatEditText>(R.id.eDescripcionDia)
        val Descripcion:String = eDescripcion.text.toString()

        if (Nombre.isNotEmpty()) {

            // Guardamos la rutina en la base de datos
            val id = DatabaseManager.insertDay(idRoutine, Nombre, Descripcion)

            if (id != -1) {
                val intent = Intent(this, Dia::class.java)
                intent.putExtra("id", id)
                //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Ya existe una dia con ese nombre en esta rutina.")
                    .setPositiveButton("Aceptar") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
    }
}