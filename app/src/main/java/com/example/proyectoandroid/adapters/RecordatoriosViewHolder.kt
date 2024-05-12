package com.example.proyectoandroid.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoandroid.databinding.LayoutRecordatorioConciertoBinding
import com.example.proyectoandroid.datosRealtimeDatabase.Conciertos

class RecordatoriosViewHolder (v: View) : RecyclerView.ViewHolder(v){
    private val binding = LayoutRecordatorioConciertoBinding.bind(v)
    fun render(
        concierto: Conciertos,
        eliminarConcierto: (String?) -> Unit

    ){
        binding.tvNombreConcierto.text = concierto.titulo
        binding.tvFechaHoraConcierto.text= concierto.fechaHora
        binding.tvDireccionConcierto.text= concierto.direccion
        binding.tvCiudadConcierto.text = concierto.ciudad
        binding.tvArtista.text = concierto.artista
        binding.tvPrecioConcierto.text = concierto.precio
        binding.ivBorrarRecordatorio.setOnClickListener {
            eliminarConcierto(concierto.id)
        }


    }
}
