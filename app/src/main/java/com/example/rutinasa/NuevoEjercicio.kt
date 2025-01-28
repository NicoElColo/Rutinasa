package com.example.rutinasa

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import com.example.rutinasa.databinding.NuevoEjercioLayoutBinding

class NuevoEjercicio : AppCompatActivity() {

    private var idDay = -1
    private var idExercise = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.nuevo_ejercio_layout)

        idDay = intent.getIntExtra("id", -1)
        idExercise = intent.getIntExtra("idExercise", -1)

        if (idExercise != -1) {

            val eNombreEj = findViewById<AppCompatEditText>(R.id.eNombreEj)
            val eSeries = findViewById<AppCompatEditText>(R.id.eSeries)
            val eRepes = findViewById<AppCompatEditText>(R.id.eRepes)
            val edescripcionEj = findViewById<AppCompatEditText>(R.id.eDescripcionEj)

            DatabaseManager.openDatabase()
            val cursor = DatabaseManager.getDatabase().rawQuery("SELECT * FROM Exercise WHERE id = ?", arrayOf(idExercise.toString()))
            cursor.moveToFirst()
            eNombreEj.setText(cursor.getString(2))
            eSeries.setText(cursor.getInt(3).toString())
            eRepes.setText(cursor.getInt(4).toString())
            edescripcionEj.setText(cursor.getString(5))
            cursor.close()
            DatabaseManager.closeDatabase()
        }

        val BtnNewEj = findViewById<AppCompatImageButton>(R.id.BtnNewEj)
        BtnNewEj.setOnClickListener {
            if (idExercise != -1) {
                updateEj()
            } else {
                saveEj()
            }
        }

    }

    private fun updateEj() {

        val eNombreEj = findViewById<AppCompatEditText>(R.id.eNombreEj)
        val eSeries = findViewById<AppCompatEditText>(R.id.eSeries)
        val eRepes = findViewById<AppCompatEditText>(R.id.eRepes)
        val edescripcionEj = findViewById<AppCompatEditText>(R.id.eDescripcionEj)

        val NombreEj:String = eNombreEj.text.toString()
        val Series:String = eSeries.text.toString()
        val Repes:String = eRepes.text.toString()
        val descripcionEj:String = edescripcionEj.text.toString()

        if (NombreEj.isNotEmpty() and Series.isNotEmpty() and Repes.isNotEmpty()) {
            val numSeries = Series.toInt()
            val numRepes = Repes.toInt()

            // Aca actualizamos el ejercicio
            DatabaseManager.updateExercise(idExercise, NombreEj, numSeries, numRepes, descripcionEj)

            // Volvemos a la vista del dia
            finish()
        }

    }

    private fun saveEj() {

        val eNombreEj = findViewById<AppCompatEditText>(R.id.eNombreEj)
        val eSeries = findViewById<AppCompatEditText>(R.id.eSeries)
        val eRepes = findViewById<AppCompatEditText>(R.id.eRepes)
        val edescripcionEj = findViewById<AppCompatEditText>(R.id.eDescripcionEj)

        val NombreEj:String = eNombreEj.text.toString()
        val Series:String = eSeries.text.toString()
        val Repes:String = eRepes.text.toString()

        if (NombreEj.isNotEmpty() and Series.isNotEmpty() and Repes.isNotEmpty()) {
            val numSeries = Series.toInt()
            val numRepes = Repes.toInt()
            val descripcionEj:String = edescripcionEj.text.toString()

            // Aca guardamos el ejercicio
            DatabaseManager.insertExercise(idDay, NombreEj, numSeries, numRepes, descripcionEj)

            // Volvemos a la vista del dia
            finish()
        }

    }

}