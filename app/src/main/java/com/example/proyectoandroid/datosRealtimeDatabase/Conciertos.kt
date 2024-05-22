package com.example.proyectoandroid.datosRealtimeDatabase

import java.io.Serializable

data class Conciertos(
    val id: String? = null,
    val titulo: String? = null ,
    val fechaHora : String? = null,
    val direccion : String? = null,
    val ciudad: String? = null,
    val artista: String? = null ,
    val precio : String? = null,
    val userId: String? = null
)
