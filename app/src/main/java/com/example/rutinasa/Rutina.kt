package com.example.rutinasa

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rutinasa.databinding.MenuActLayoutBinding
import com.example.rutinasa.databinding.RutinaLayoutBinding
import com.google.android.material.card.MaterialCardView
import kotlin.properties.Delegates

class Rutina : AppCompatActivity() {

    private lateinit var binding: RutinaLayoutBinding
    private var idRoutine = -1
    private val query : String = "SELECT * FROM Day WHERE id_routine = ? ORDER BY id" //nombre no queda tan bien

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DatabaseManager.openDatabase()

        binding = RutinaLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idRoutine = intent.getIntExtra("id", -1)
        val cursor : Cursor = DatabaseManager.getDatabase().rawQuery(query, arrayOf(idRoutine.toString()))

        val adapter = AdapterRecyclerRoutineAndDay()
        adapter.initialize(this, cursor, false)

        binding.recyclerDay.setHasFixedSize(true)
        binding.recyclerDay.layoutManager = LinearLayoutManager(this)
        binding.recyclerDay.adapter = adapter

        val BtnND = findViewById<MaterialCardView>(R.id.BtnND)
        BtnND.setOnClickListener { createDay(idRoutine) }
    }


    override fun onPause() {
        super.onPause()
        DatabaseManager.closeDatabase()
    }

    override fun onStop() {
        super.onStop()
        DatabaseManager.closeDatabase()
    }

    override fun onResume() {
        super.onResume()
        DatabaseManager.openDatabase()
        refreshData()
    }

    private fun refreshData() {
        val cursor: Cursor = DatabaseManager.getDatabase().rawQuery(query, arrayOf(idRoutine.toString()))
        (binding.recyclerDay.adapter as? AdapterRecyclerRoutineAndDay)!!.initialize(this, cursor, false)
        binding.recyclerDay.adapter?.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        DatabaseManager.closeDatabase()
    }


    private fun createDay(idRoutine: Int) {
        val intent = Intent(this, NuevoDia::class.java)
        intent.putExtra("id", idRoutine)
        startActivity(intent)
    }
}