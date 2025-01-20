package com.example.rutinasa

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rutinasa.databinding.ItemHistRecyclerBinding
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale

class AdapterRecyclerHistory : RecyclerView.Adapter<AdapterRecyclerHistory.ViewHolder>() {

    lateinit var context : Context
    lateinit var cursor : Cursor

    fun initialize(context: Context,cursor: Cursor) {
        this.context = context
        this.cursor = cursor
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val tvPeso: TextView
        val tvFecha: TextView
        var id : Int = -1

        init {
            val bindingItemsRV = ItemHistRecyclerBinding.bind(view)
            tvPeso = bindingItemsRV.Peso
            tvFecha = bindingItemsRV.Fecha

//            view.setOnClickListener {
//                // Aca seguro va algo para borrar el registro
//            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_hist_recycler, parent, false)
        return ViewHolder(view)
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

        holder.tvPeso.text = "Peso: ${cursor.getFloat(3)}Kg"
        val timestamp = cursor.getLong(2)
        val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val fecha = formatoFecha.format(Date(timestamp))
        holder.tvFecha.text = "Fecha: $fecha"

        holder.id = cursor.getInt(0)

    }
}