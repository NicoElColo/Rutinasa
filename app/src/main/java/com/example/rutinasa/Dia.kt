package com.example.rutinasa

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rutinasa.databinding.DiaLayoutBinding

class Dia : AppCompatActivity() {

    private lateinit var binding: DiaLayoutBinding
    private var idDay = -1
    private val query : String = "SELECT * FROM Exercise WHERE id_day = ? ORDER BY nombre" //nombre esta mal

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        DatabaseManager.openDatabase()

        binding = DiaLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idDay = intent.getIntExtra("id", -1)
        val cursor : Cursor = DatabaseManager.getDatabase().rawQuery(query, arrayOf(idDay.toString()))

        val adapter = AdapterRecyclerExercise()
        adapter.initialize(this, cursor)

        binding.recyclerEj.setHasFixedSize(true)
        binding.recyclerEj.layoutManager = LinearLayoutManager(this)
        binding.recyclerEj.adapter = adapter


        val BtnNewEj = findViewById<AppCompatButton>(R.id.BtnNewEj)
        BtnNewEj.setOnClickListener { addExercise() }
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
        val cursor: Cursor = DatabaseManager.getDatabase().rawQuery(query, arrayOf(idDay.toString()))
        (binding.recyclerEj.adapter as? AdapterRecyclerExercise)!!.initialize(this, cursor)
        binding.recyclerEj.adapter?.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        DatabaseManager.closeDatabase()
    }

    private fun addExercise() {
        val intent = Intent(this, NuevoEjercio::class.java)
        intent.putExtra("id", idDay)
        startActivity(intent)
    }
}