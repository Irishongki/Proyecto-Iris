package com.example.proyectoandroid.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoandroid.R
import com.example.proyectoandroid.datosRealtimeDatabase.Conciertos

class RecordatoriosAdapter(var lista: List<Conciertos>,   val eliminarConcierto: (String?) -> Unit,
                           val editarConcierto: (String?) -> Unit               ) :
    RecyclerView.Adapter<RecordatoriosViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordatoriosViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_recordatorio_concierto,
            parent, false)
        return RecordatoriosViewHolder(v)
    }

    override fun getItemCount(): Int {
        return lista.size
    }

    override fun onBindViewHolder(holder: RecordatoriosViewHolder, position: Int) {
        holder.render(lista[position], eliminarConcierto, editarConcierto)
    }
}
