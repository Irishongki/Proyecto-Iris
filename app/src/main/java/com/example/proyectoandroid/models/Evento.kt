package com.example.proyectoandroid.models

import com.google.android.gms.maps.model.LatLng

data class Evento(
    val nombre: String,
    val posicion: LatLng,
    val fechaHora: String,
    val direccion: String,
    val ciudad: String,
    val nombreArtista : String,
    val precio : String
)