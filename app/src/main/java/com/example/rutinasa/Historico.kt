package com.example.rutinasa

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rutinasa.databinding.DiaLayoutBinding
import com.example.rutinasa.databinding.HistoricoLayoutBinding

class Historico : AppCompatActivity() {

    private lateinit var binding: HistoricoLayoutBinding
    private var idExercise = -1
    private val query : String = "SELECT * FROM History WHERE id_exercise = ? ORDER BY fecha DESC"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        DatabaseManager.openDatabase()

        binding = HistoricoLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        idExercise = intent.getIntExtra("id", -1)

        val cursor : Cursor = DatabaseManager.getDatabase().rawQuery(query, arrayOf(idExercise.toString()))

        val adapter = AdapterRecyclerHistory()
        adapter.initialize(this, cursor)

        binding.recyclerHist.setHasFixedSize(true)
        binding.recyclerHist.layoutManager = LinearLayoutManager(this)
        binding.recyclerHist.adapter = adapter


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
        val cursor: Cursor = DatabaseManager.getDatabase().rawQuery(query, arrayOf(idExercise.toString()))
        (binding.recyclerHist.adapter as? AdapterRecyclerHistory)!!.initialize(this, cursor)
        binding.recyclerHist.adapter?.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        DatabaseManager.closeDatabase()
    }

}