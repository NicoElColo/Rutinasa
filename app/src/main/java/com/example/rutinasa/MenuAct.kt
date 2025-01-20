package com.example.rutinasa

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rutinasa.databinding.MenuActLayoutBinding

class MenuAct : AppCompatActivity() {

    private lateinit var binding: MenuActLayoutBinding
    private val query : String = "SELECT * FROM Routine ORDER BY nombre"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        DatabaseManager.openDatabase()

        binding = MenuActLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val cursor : Cursor = DatabaseManager.getDatabase().rawQuery(query, null)

        val adapter = AdapterRecyclerRoutineAndDay()
        adapter.initialize(this, cursor, true)

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter


        val BtnNR = findViewById<AppCompatButton>(R.id.BtnNR)
        BtnNR.setOnClickListener { createRutine() }

    }

    override fun onRestart() {
        super.onRestart()
        DatabaseManager.openDatabase()
        refreshData()
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
        val cursor: Cursor = DatabaseManager.getDatabase().rawQuery(query, null)
        (binding.recyclerView.adapter as? AdapterRecyclerRoutineAndDay)!!.initialize(this, cursor, true)
        binding.recyclerView.adapter?.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        DatabaseManager.closeDatabase()
    }

    private fun createRutine() {
        val intent = Intent(this, NuevaRutina::class.java)
        startActivity(intent)
    }
}