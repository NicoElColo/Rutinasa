package com.example.rutinasa

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rutinasa.databinding.MenuActLayoutBinding
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.json.JSONException
import org.json.JSONObject

class MenuAct : AppCompatActivity() {

    private lateinit var binding: MenuActLayoutBinding
    private val query : String = "SELECT * FROM Routine ORDER BY nombre"
    private val jsonPickerLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            // Aca proceso el json
            val jsonString = readJsonFromUri(it)
            procesarJson(jsonString)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()

        DatabaseManager.openDatabase()

        binding = MenuActLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val cursor : Cursor = DatabaseManager.getDatabase().rawQuery(query, null)

        val adapter = AdapterRecyclerRoutineAndDay()
        adapter.initialize(this, cursor, true)

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter


        val BtnNR = findViewById<MaterialCardView>(R.id.BtnNR)
        val BtnIR = findViewById<MaterialCardView>(R.id.BtnIR)
        BtnNR.setOnClickListener { createRutine() }
        BtnIR.setOnClickListener { importRutine() }

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

    fun readJsonFromUri(uri: Uri): String {
        val inputStream = contentResolver.openInputStream(uri)
        return inputStream?.bufferedReader().use { it?.readText() } ?: ""
    }


    fun procesarJson(jsonString: String) {
        try {

            val jsonObject = JSONObject(jsonString) // Convertir string a JSONObject
            val nombre = jsonObject.getString("nombre") // Obtener el nombre
            val descripcion = jsonObject.getString("descripcion") // Obtener la descripcion


            val idImportRoutine = DatabaseManager.insertRoutine("", "")
            val diasArray = jsonObject.getJSONArray("dias") // Obtener el array

            for (i in 0 until diasArray.length()) {
                val diaOBJ = diasArray.getJSONObject(i)
                val nombreDia = diaOBJ.getString("nombre")
                val descripcionDia = diaOBJ.getString("descripcion")

                val idImportDay = DatabaseManager.insertDay(idImportRoutine, nombreDia, descripcionDia)
                val ejerciciosArray = diaOBJ.getJSONArray("ejercicios")

                for (j in 0 until ejerciciosArray.length()) {
                    val ejercicioOBJ = ejerciciosArray.getJSONObject(j)
                    val nombreEjercicio = ejercicioOBJ.getString("nombre")
                    val series = ejercicioOBJ.getInt("series")
                    val repeticiones = ejercicioOBJ.getInt("repeticiones")
                    val descripcionEjercicio = ejercicioOBJ.getString("descripcion")

                    DatabaseManager.insertExercise(idImportDay, nombreEjercicio, series, repeticiones, descripcionEjercicio)
                }
            }

            // Nombrado
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Nombre para la rutina")

            // Crear el EditText dentro del diálogo
            val input = EditText(this)
            input.setText(nombre)

            // Configurar el layout del diálogo
            val layout = LinearLayout(this)
            layout.orientation = LinearLayout.VERTICAL
            layout.setPadding(50, 20, 50, 20)
            layout.addView(input)

            builder.setView(layout)

            var flag : Boolean = false

            // Botón Aceptar
            builder.setPositiveButton("Aceptar") { dialog, _ ->
                val textoIngresado = input.text.toString()
                val rows : Int = DatabaseManager.updateRoutine(idImportRoutine, textoIngresado,descripcion)
                if (rows > 0) {
                    Toast.makeText(this, "Rutina guardada", Toast.LENGTH_SHORT).show()
                    flag = true
                } else {
                    Toast.makeText(this, "Ya hay una rutina con ese nombre", Toast.LENGTH_SHORT).show()
                    DatabaseManager.deleteRoutine(idImportRoutine)
                }
                dialog.dismiss()
                runOnUiThread {
                    DatabaseManager.openDatabase()  // Asegura que la base de datos esté abierta antes de actualizar
                    refreshData()
                }
            }

            // Botón Cancelar
            builder.setNegativeButton("Cancelar") { dialog, _ ->
                DatabaseManager.deleteRoutine(idImportRoutine)
                dialog.dismiss()
                runOnUiThread {
                    DatabaseManager.openDatabase()  // Asegura que la base de datos esté abierta antes de actualizar
                    refreshData()
                }
            }

            builder.setOnDismissListener {
                if (!flag) {
                    DatabaseManager.deleteRoutine(idImportRoutine) // Eliminar la rutina si no se guardó
                    runOnUiThread {
                        DatabaseManager.openDatabase()
                        refreshData() // Refrescar la UI
                    }
                }
            }

            // Mostrar el diálogo
            builder.create().show()


        } catch (e: JSONException) {
            e.printStackTrace()
            Log.e("JSON_ERROR", "Error al procesar JSON")
        }
    }


    private fun importRutine() {
        jsonPickerLauncher.launch(arrayOf("application/json"))
    }
}