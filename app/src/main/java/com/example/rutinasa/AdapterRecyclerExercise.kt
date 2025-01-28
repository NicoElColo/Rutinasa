package com.example.rutinasa;

import android.content.Context;
import android.content.Intent
import android.database.Cursor;
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView;
import com.example.rutinasa.databinding.ItemEjRecyclerBinding

import android.app.AlertDialog
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.Toast


class AdapterRecyclerExercise : RecyclerView.Adapter<AdapterRecyclerExercise.ViewHolderEj>(){
    lateinit var context : Context
    lateinit var cursor : Cursor

    fun initialize(context: Context, cursor: Cursor) {
        this.context = context
        this.cursor = cursor
    }

    // Esto representa a cada item de la vista
    // Son objetos que se van creando/destruyendo dinamicamente
    inner class ViewHolderEj(view: View) : RecyclerView.ViewHolder(view) {

        // Variables
        val tvExercise: TextView
        val tvSeriesRepes: TextView
        val tvDesc: TextView

        val btnAddRegister: androidx.appcompat.widget.AppCompatImageButton
        val card: androidx.cardview.widget.CardView
        var id: Int = -1

        // constructor primario
        init {
            val bindingItemsRV = ItemEjRecyclerBinding.bind(view)
            tvExercise = bindingItemsRV.ItemEjTitle
            tvSeriesRepes = bindingItemsRV.ItemEjSeries
            tvDesc = bindingItemsRV.ItemEjDesc
            btnAddRegister = bindingItemsRV.addRegisterBtn
            card = bindingItemsRV.itemEj

            /// puede dar problemas si queremos hacer el menu desplegable porque seguro ocupa la totalidad del  espacio
            /// hay que ver como meter botones "secundarios"

            card.setOnClickListener {
                val intent = Intent(context, Historico::class.java)
                intent.putExtra("id", id)
                context.startActivity(intent)
            }

            card.setOnLongClickListener {
                PopupMenu(context, view).apply {
                    inflate(R.menu.popup_menu)
                    setOnMenuItemClickListener {
                        when (it.itemId) {
                            R.id.edit -> {
                                val intent = Intent(context, NuevoEjercicio::class.java)
                                intent.putExtra("idExercise", id)
                                context.startActivity(intent)
                                true
                            }
                            R.id.borrar -> {

                                // Agregar confirmacion
                                val builder = AlertDialog.Builder(context)
                                builder.setTitle("Estas seguro de borrar el ejercicio?")

                                // Configurar el layout del diálogo
                                val layout = LinearLayout(context)
                                layout.orientation = LinearLayout.VERTICAL
                                layout.setPadding(50, 20, 50, 20)

                                builder.setView(layout)

                                // Botón Aceptar
                                builder.setPositiveButton("Aceptar") { dialog, _ ->

                                    val idDay = cursor.getInt(1)
                                    // Eliminamos el ejercicio
                                    if (DatabaseManager.deleteExercise(id) != 0) {
                                        Toast.makeText(context, "Ejercicio borrado", Toast.LENGTH_SHORT).show()

                                        // Actualizamos la vista
                                        DatabaseManager.openDatabase()
                                        cursor = DatabaseManager.getDatabase().rawQuery("SELECT * FROM Exercise WHERE id_day = ? ORDER BY nombre", arrayOf(idDay.toString()))
                                        notifyDataSetChanged()

                                    } else {
                                        Toast.makeText(context, "Error al borrar el ejercicio", Toast.LENGTH_SHORT).show()
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

            btnAddRegister.setOnClickListener {

                val builder = AlertDialog.Builder(context)
                builder.setTitle("Peso utilizado")

                // Crear el EditText dentro del diálogo
                val input = EditText(context)
                input.hint = "Peso (en kg)"

                // Configurar el layout del diálogo
                val layout = LinearLayout(context)
                layout.orientation = LinearLayout.VERTICAL
                layout.setPadding(50, 20, 50, 20)
                layout.addView(input)

                builder.setView(layout)

                // Botón Aceptar
                builder.setPositiveButton("Aceptar") { dialog, _ ->
                    val textoIngresado = input.text.toString()
                    Toast.makeText(context, "Moviste: ${textoIngresado}Kg", Toast.LENGTH_SHORT).show()
                    DatabaseManager.insertHistory(id,System.currentTimeMillis(),textoIngresado.toFloat())
                    dialog.dismiss()
                }

                // Botón Cancelar
                builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }

                // Mostrar el diálogo
                builder.create().show()

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderEj {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_ej_recycler, parent, false)
        return ViewHolderEj(view)
    }

    override fun getItemCount(): Int {
        if (cursor == null) {
            return 0
        } else {
            return cursor.count
        }
    }

    override fun onBindViewHolder(holder: ViewHolderEj, position: Int) {
        cursor.moveToPosition(position)
        holder.tvExercise.text = cursor.getString(2)
        holder.tvSeriesRepes.text = "Series: ${cursor.getInt(3)}     Repeticiones: ${cursor.getInt(4)}"
        holder.tvDesc.text = cursor.getString(5)
        if (cursor.getString(5).isBlank()) {
            holder.tvDesc.visibility = View.GONE
        } else {
            holder.tvDesc.visibility = View.VISIBLE
        }

        holder.id = cursor.getInt(0)
    }
}
