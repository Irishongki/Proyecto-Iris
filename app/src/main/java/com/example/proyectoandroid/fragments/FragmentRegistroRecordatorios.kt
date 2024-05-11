package com.example.proyectoandroid.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.example.proyectoandroid.R
import java.time.Year
import java.util.Calendar


class FragmentRegistroRecordatorios : Fragment() {
    var listener : OnFragmentActionListener?= null
    private lateinit var fechaSeleccionada: EditText
    val seleccionarCalendario = Calendar.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registro_recordatorios, container, false)
        
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //boton
        val boton : Button = view.findViewById(R.id.btn_reserva)

        boton.setOnClickListener {
            listener?.mostrarMensaje("Reserva de su entrada realizada")

        }
        fechaSeleccionada = view.findViewById(R.id.fecha_concierto)
        fechaSeleccionada.setOnClickListener { mostrarDatePicker() }
    }

    private fun mostrarDatePicker() {
        val year = seleccionarCalendario.get(Calendar.YEAR)
        val month = seleccionarCalendario.get(Calendar.MONTH)
        val day = seleccionarCalendario.get(Calendar.DAY_OF_MONTH)

        val listener = DatePickerDialog.OnDateSetListener { _, y, m, d ->
            seleccionarCalendario.set(y, m, d)
            fechaSeleccionada.setText("$y-$m-$d")
        }

        // Creamos y mostramos el DatePickerDialog
        val datePickerDialog = DatePickerDialog(requireContext(), listener, year, month, day)
        datePickerDialog.show()

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentActionListener) listener = context
    }

    override fun onDetach() {
        super.onDetach()
        listener=null
    }

}