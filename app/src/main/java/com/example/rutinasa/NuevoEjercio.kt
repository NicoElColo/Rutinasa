package com.example.rutinasa

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class NuevoEjercio : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.nuevo_ejercio_layout)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val BtnNewEj = findViewById<AppCompatButton>(R.id.BtnNewEj)
        BtnNewEj.setOnClickListener { saveEj() }

    }

    private fun saveEj() {
        val eNombreEj = findViewById<AppCompatEditText>(R.id.eNombreEj)
        val eSeries = findViewById<AppCompatEditText>(R.id.eSeries)
        val eRepes = findViewById<AppCompatEditText>(R.id.eRepes)

        val NombreEj:String = eNombreEj.text.toString()
        val Series:String = eSeries.text.toString()
        val Repes:String = eRepes.text.toString()

        if (NombreEj.isNotEmpty() and Series.isNotEmpty() and Repes.isNotEmpty()) {
            val numSeries = Series.toInt()
            val numRepes = Repes.toInt()
            val edescripcionEj = findViewById<AppCompatEditText>(R.id.eDescripcionEj)
            val descripcionEj:String = edescripcionEj.text.toString()

            // Aca guardamos el ejercicio

            // Volvemos a la vista del dia
            val intent = Intent(this, Dia::class.java)
            startActivity(intent)
        }

    }

}