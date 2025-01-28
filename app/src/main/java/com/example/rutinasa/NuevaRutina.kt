package com.example.rutinasa

import android.content.Context
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
import java.io.File

class NuevaRutina : AppCompatActivity() {

    private var idRoutine : Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nueva_rutina_layout)

        idRoutine = intent.getIntExtra("idRoutine", -1)
        if (idRoutine != -1) {

            val eNombreRoutine = findViewById<AppCompatEditText>(R.id.eNombre)
            val eDescripcionRoutine = findViewById<AppCompatEditText>(R.id.eDescripcion)

            DatabaseManager.openDatabase()
            val cursor = DatabaseManager.getDatabase().rawQuery("SELECT * FROM Routine WHERE id = ?", arrayOf(idRoutine.toString()))
            cursor.moveToFirst()
            eNombreRoutine.setText(cursor.getString(1))
            eDescripcionRoutine.setText(cursor.getString(2))
            cursor.close()
            DatabaseManager.closeDatabase()
        }




        val BtnRutine = findViewById<AppCompatImageButton>(R.id.BtnRutine)
        BtnRutine.setOnClickListener {
            if (idRoutine != -1) {
                updateRutine()
            } else {
                ejectRutine()
            }
        }

    }

    private fun updateRutine() {
        val eNombre = findViewById<AppCompatEditText>(R.id.eNombre)
        val eDescripcion = findViewById<AppCompatEditText>(R.id.eDescripcion)

        val Nombre:String = eNombre.text.toString()
        val Descripcion:String = eDescripcion.text.toString()

        if (Nombre.isNotEmpty()) {

            val rows = DatabaseManager.updateRoutine(idRoutine, Nombre, Descripcion)
            if (rows != 0) {
                Toast.makeText(this, "Rutina actualizada", Toast.LENGTH_SHORT).show()
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