package com.example.proyectoandroid.fragments

import android.content.Intent
import androidx.fragment.app.Fragment

interface OnFragmentActionListener {
    fun mostrarMensaje(msj :String)
    fun cargarFragmentRegistro(fragment : Fragment)
    fun cargarFragmentLista(fragment : Fragment)
    fun irActivityPrincipal(intent: Intent)
}