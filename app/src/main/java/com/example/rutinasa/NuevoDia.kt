package com.example.rutinasa

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class NuevoDia : AppCompatActivity() {

    private var idRoutine : Int = -1
    private var idDay : Int = -1

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
        idDay = intent.getIntExtra("idDay", -1)

        if (idDay != -1) {

            val eNombreDay = findViewById<AppCompatEditText>(R.id.eNombreDia)
            val eDescripcionDay = findViewById<AppCompatEditText>(R.id.eDescripcionDia)

            DatabaseManager.openDatabase()
            val cursor = DatabaseManager.getDatabase().rawQuery("SELECT * FROM Day WHERE id = ?", arrayOf(idDay.toString()))
            cursor.moveToFirst()
            eNombreDay.setText(cursor.getString(2))
            eDescripcionDay.setText(cursor.getString(3))
            cursor.close()
            DatabaseManager.closeDatabase()
        }

        val BtnDia = findViewById<AppCompatImageButton>(R.id.BtnDia)
        BtnDia.setOnClickListener {
            if (idDay != -1) {
                updateDay()
            } else {
                ejectDay()
            }
        }

    }

    private fun updateDay() {
        val eNombre = findViewById<AppCompatEditText>(R.id.eNombreDia)
        val eDescripcion = findViewById<AppCompatEditText>(R.id.eDescripcionDia)

        val Nombre:String = eNombre.text.toString()
        val Descripcion:String = eDescripcion.text.toString()

        if (Nombre.isNotEmpty()) {

            val rows = DatabaseManager.updateDay(idDay, Nombre, Descripcion)
            if (rows != 0) {
                Toast.makeText(this, "Dia actualizado", Toast.LENGTH_SHORT).show()
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