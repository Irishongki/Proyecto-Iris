package com.example.proyectoandroid.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.proyectoandroid.Principal
import com.example.proyectoandroid.R


class FragmentMenu : Fragment() {
    var listener : OnFragmentActionListener?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu_recordatorios, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imagenDatos : ImageView = view.findViewById(R.id.iv_recordatorio)
        val imagenLista : ImageView = view.findViewById(R.id.iv_lista_recordatorios)
        val imagenPrincipal : ImageView = view.findViewById(R.id.iv_Principal)
        imagenDatos.setOnClickListener {
            listener?.cargarFragmentRegistro(FragmentRegistroRecordatorios())
        }
        imagenLista.setOnClickListener {
            listener?.cargarFragmentLista(FragmentListaRecordatorios())
        }
        imagenPrincipal.setOnClickListener {
            val intent = Intent(requireContext(), Principal::class.java)
            listener?.irActivityPrincipal(intent)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentActionListener) listener= context
    }

    override fun onDetach() {
        super.onDetach()
        listener=null
    }
}