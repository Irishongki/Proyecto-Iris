package com.example.proyectoandroid.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.example.proyectoandroid.R
import java.text.SimpleDateFormat
import java.time.Year
import java.util.Calendar
import java.util.Locale


class FragmentRegistroRecordatorios : Fragment() {
    var listener : OnFragmentActionListener?= null
    private lateinit var fechaHoraSeleccionada: EditText
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
        fechaHoraSeleccionada = view.findViewById(R.id.fecha_hora_concierto)
        fechaHoraSeleccionada.setOnClickListener { mostrarDatePicker() }
    }

    private fun mostrarDatePicker() {
        val year = seleccionarCalendario.get(Calendar.YEAR)
        val month = seleccionarCalendario.get(Calendar.MONTH)
        val day = seleccionarCalendario.get(Calendar.DAY_OF_MONTH)
        val hour = seleccionarCalendario.get(Calendar.HOUR_OF_DAY)
        val minute = seleccionarCalendario.get(Calendar.MINUTE)

        val dateListener = DatePickerDialog.OnDateSetListener { _, y, m, d ->
            seleccionarCalendario.set(y, m, d)
            TimePickerDialog(
                requireContext(),
                { _, h, min ->
                    seleccionarCalendario.set(Calendar.HOUR_OF_DAY, h)
                    seleccionarCalendario.set(Calendar.MINUTE, min)
                    actualizarFechaHoraSeleccionada()
                },
                hour,
                minute,
                true
            ).show()
        }

        // Crear y mostrar el DatePickerDialog
        val datePickerDialog = DatePickerDialog(requireContext(), dateListener, year, month, day)
        datePickerDialog.show()

    }
    private fun actualizarFechaHoraSeleccionada() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val fechaHoraFormateada = dateFormat.format(seleccionarCalendario.time)
        fechaHoraSeleccionada.setText(fechaHoraFormateada)
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