package com.example.proyectoandroid.fragments

import androidx.fragment.app.Fragment

interface OnFragmentActionListener {
    fun mostrarMensaje(msj :String)
    fun cargarFragment(fragment : Fragment)
}