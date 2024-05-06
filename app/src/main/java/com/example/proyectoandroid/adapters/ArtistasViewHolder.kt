package com.example.proyectoandroid.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoandroid.databinding.ArtistasLayoutBinding
import com.example.proyectoandroid.models.Artist


class ArtistasViewHolder (v : View) : RecyclerView.ViewHolder(v){
    private val binding= ArtistasLayoutBinding.bind(v)

    fun render(artista: Artist, onItemClick: (Artist) -> Unit){
        binding.tvNombre.text = artista.nombre
        binding.ivArtista.setImageResource(artista.imagen)
        itemView.setOnClickListener{
            onItemClick(artista)
        }
    }
}