package com.example.rutinasa

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.rutinasa.databinding.ItemRecyclerBinding
import com.google.android.material.card.MaterialCardView
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class AdapterRecyclerRoutineAndDay : RecyclerView.Adapter<AdapterRecyclerRoutineAndDay.ViewHolder>() {

    lateinit var context : Context
    lateinit var cursor : Cursor
    var bool : Boolean = true

    fun initialize(context: Context, cursor: Cursor, bool: Boolean) {
        this.context = context
        this.cursor = cursor
        this.bool = bool
    }

    // Esto representa a cada item de la vista
    // Son objetos que se van creando/destruyendo dinamicamente
    inner class ViewHolder(view: View, bool: Boolean) : RecyclerView.ViewHolder(view) {

        // Variables
        val tvTitle: TextView
        val tvDesc: TextView
        val card: MaterialCardView
        val shareBtn: androidx.appcompat.widget.AppCompatImageButton
        var id: Int = -1
        var rutinaOrDay : Boolean = bool

        // constructor primario
        init {
            val bindingItemsRV = ItemRecyclerBinding.bind(view)
            tvTitle = bindingItemsRV.ItemRoutineDayTitle
            tvDesc = bindingItemsRV.ItemRoutineDayDesc
            card = bindingItemsRV.itemRoutineDay
            shareBtn = bindingItemsRV.shareBtn

            /// puede dar problemas si queremos hacer el menu desplegable porque seguro ocupa la totalidad del  espacio
            /// hay que ver como meter botones "secundarios"

            card.setOnClickListener {
                if (rutinaOrDay) { // true -> Rutina
                    val intent = Intent(context, Rutina::class.java)
                    intent.putExtra("id", id)
                    context.startActivity(intent)
                } else { // false -> Dia
                    val intent = Intent(context, Dia::class.java)
                    intent.putExtra("id", id)
                    context.startActivity(intent)
                }
            }

            card.setOnLongClickListener {
                PopupMenu(context, view).apply {
                    inflate(R.menu.popup_menu)
                    setOnMenuItemClickListener {
                        when (it.itemId) {
                            R.id.edit -> {
                                if (rutinaOrDay) {
                                    val intent = Intent(context, NuevaRutina::class.java)
                                    intent.putExtra("idRoutine", id)
                                    context.startActivity(intent)

                                } else {
                                    val intent = Intent(context, NuevoDia::class.java)
                                    intent.putExtra("idDay", id)
                                    context.startActivity(intent)
                                }
                                true
                            }
                            R.id.borrar -> {

                                // Agregar confirmacion
                                val builder = AlertDialog.Builder(context)
                                if (rutinaOrDay) {
                                    builder.setTitle("Estas seguro de borrar la rutina?")
                                } else {
                                    builder.setTitle("Estas seguro de borrar el dia?")
                                }

                                // Configurar el layout del diálogo
                                val layout = LinearLayout(context)
                                layout.orientation = LinearLayout.VERTICAL
                                layout.setPadding(50, 20, 50, 20)

                                builder.setView(layout)

                                // Botón Aceptar
                                builder.setPositiveButton("Aceptar") { dialog, _ ->

                                    if (rutinaOrDay) {
                                        if (DatabaseManager.deleteRoutine(id) != 0) {
                                            Toast.makeText(context, "Rutina borrada", Toast.LENGTH_SHORT).show()

                                            // Actualizamos la vista
                                            DatabaseManager.openDatabase()
                                            cursor = DatabaseManager.getDatabase().rawQuery("SELECT * FROM Routine ORDER BY nombre", null)
                                            notifyDataSetChanged()
                                        } else {
                                            Toast.makeText(context, "Error al borrar la rutina", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        val idRoutine = cursor.getInt(1)
                                        if (DatabaseManager.deleteDay(id) != 0) {
                                            Toast.makeText(context, "Dia eliminado.", Toast.LENGTH_SHORT).show()

                                            // Actualizamos la vista
                                            DatabaseManager.openDatabase()
                                            cursor = DatabaseManager.getDatabase().rawQuery("SELECT * FROM Day WHERE id_routine = ? ORDER BY nombre", arrayOf(idRoutine.toString()))
                                            notifyDataSetChanged()
                                        } else {
                                            Toast.makeText(context, "Error al borrar el dia", Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                    dialog.dismiss()
                                }

                                // Botón Cancelar
                                builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }

                                // Mostrar el diálogo
                                builder.create().show()

                                true
                            }
                            else -> false
                        }
                    }
                }.show()
                false
            }

            shareBtn.setOnClickListener {
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Estas seguro de exportar la rutina?")

                // Configurar el layout del diálogo
                val layout = LinearLayout(context)
                layout.orientation = LinearLayout.VERTICAL
                layout.setPadding(50, 20, 50, 20)

                builder.setView(layout)

                // Botón Aceptar
                builder.setPositiveButton("Aceptar") { dialog, _ ->
                    exportRoutine()
                    dialog.dismiss()
                }

                // Botón Cancelar
                builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }

                builder.create().show()
            }
        }

        private fun exportRoutine(){
            val rutinaOBJ = JSONObject()
            rutinaOBJ.put("nombre", tvTitle.text)
            rutinaOBJ.put("descripcion", tvDesc.text)

            DatabaseManager.openDatabase()
            val cursorDay = DatabaseManager.getDatabase().rawQuery("SELECT * FROM Day WHERE id_routine = ? ORDER BY id", arrayOf(id.toString()))

            val diasArray = JSONArray()
            while (cursorDay.moveToNext()) {
                val diaOBJ = JSONObject()
                diaOBJ.put("nombre", cursorDay.getString(2))
                diaOBJ.put("descripcion", cursorDay.getString(3))

                val cursorExercise = DatabaseManager.getDatabase().rawQuery("SELECT * FROM Exercise WHERE id_day = ? ORDER BY id", arrayOf(cursorDay.getInt(0).toString()))
                val ejerciciosArray = JSONArray()

                while (cursorExercise.moveToNext()) {
                    val ejercicioOBJ = JSONObject()
                    ejercicioOBJ.put("nombre", cursorExercise.getString(2))
                    ejercicioOBJ.put("series", cursorExercise.getInt(3))
                    ejercicioOBJ.put("repeticiones", cursorExercise.getInt(4))
                    ejercicioOBJ.put("descripcion", cursorExercise.getString(5))

                    ejerciciosArray.put(ejercicioOBJ)
                }
                cursorExercise.close()
                diaOBJ.put("ejercicios", ejerciciosArray)
                diasArray.put(diaOBJ)
            }

            rutinaOBJ.put("dias", diasArray)

            cursorDay.close()
            DatabaseManager.closeDatabase()

            val file = File("/storage/emulated/0/Download/${tvTitle.text}.json")
            file.writeText(rutinaOBJ.toString())
            Toast.makeText(context, "Rutina guardada en ${file.absolutePath}", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_recycler, parent, false)
        return ViewHolder(view, bool)
    }

    override fun getItemCount(): Int {
        if (cursor == null) {
            return 0
        } else {
            return cursor.count
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        cursor.moveToPosition(position)

        if (bool) { // Rutina
            holder.tvTitle.text = cursor.getString(1)
            holder.tvDesc.text = cursor.getString(2)
            if (cursor.getString(2).isBlank()) {
                holder.tvDesc.visibility = View.GONE
            } else {
                holder.tvDesc.visibility = View.VISIBLE
            }
            holder.shareBtn.visibility = View.VISIBLE
        } else { // Dia
            holder.tvTitle.text = cursor.getString(2)
            holder.tvDesc.text = cursor.getString(3)
            if (cursor.getString(3).isBlank()) {
                holder.tvDesc.visibility = View.GONE
            } else {
                holder.tvDesc.visibility = View.VISIBLE
            }
            holder.shareBtn.visibility = View.GONE
        }
        holder.id = cursor.getInt(0)
        holder.rutinaOrDay = bool
    }
}