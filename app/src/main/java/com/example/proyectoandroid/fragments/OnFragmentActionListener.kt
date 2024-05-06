package com.example.proyectoandroid.fragments

import androidx.fragment.app.Fragment

interface OnFragmentActionListener {
    fun mostrarMensaje(msj :String)
    fun cargarFragmentRegistro(fragment : Fragment)
    fun cargarFragmentLista(fragment : Fragment)
}