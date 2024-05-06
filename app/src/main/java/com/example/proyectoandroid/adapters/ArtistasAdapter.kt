package com.example.proyectoandroid.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoandroid.R
import com.example.proyectoandroid.models.Artist

class ArtistasAdapter (var lista: List<Artist>, val onItemClick: (Artist)-> Unit) : RecyclerView.Adapter<ArtistasViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistasViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.artistas_layout, parent, false)
        return ArtistasViewHolder(v)
    }

    override fun getItemCount(): Int {
        return lista.size
    }

    override fun onBindViewHolder(holder: ArtistasViewHolder, position: Int) {
        holder.render(lista[position], onItemClick)
    }
}