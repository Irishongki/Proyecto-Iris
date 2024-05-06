package com.example.proyectoandroid.adapters

import android.view.LayoutInflater
import android.view.View
import com.bumptech.glide.Glide
import com.example.proyectoandroid.databinding.LayoutMarcadorDetalleBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class VentanaInformacionAdapter(private val layoutInflater: LayoutInflater) : GoogleMap.InfoWindowAdapter {
    private val binding: LayoutMarcadorDetalleBinding = LayoutMarcadorDetalleBinding.inflate(layoutInflater)

    override fun getInfoWindow(marker: Marker): View? {
        return null // Devuelve null para usar la ventana de información predeterminada
    }

    override fun getInfoContents(marker: Marker): View {
        // Obtener referencias a las vistas en tu diseño personalizado
        val nombre = binding.txtDetalleNombre
        val informacion = binding.txtDetalleInformacion

        // Configurar las vistas con la información del marcador

        nombre.text = marker.title
        informacion.text = marker.snippet

        return binding.root
    }
}

