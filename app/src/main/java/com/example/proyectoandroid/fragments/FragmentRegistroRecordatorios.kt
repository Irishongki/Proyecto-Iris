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
import com.example.proyectoandroid.datosRealtimeDatabase.Conciertos
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class FragmentRegistroRecordatorios : Fragment() {
    var listener : OnFragmentActionListener?= null
    private lateinit var fechaHoraSeleccionada: EditText
    private lateinit var nombreConcierto: EditText
    private lateinit var direccionConcierto: EditText
    private lateinit var ciudadConcierto: EditText
    private lateinit var artistaConcierto: EditText
    private lateinit var precioConcierto: EditText
    val seleccionarCalendario = Calendar.getInstance()
    private lateinit var miDB_Reference: DatabaseReference
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

        // Inicializar EditTexts
        fechaHoraSeleccionada = view.findViewById(R.id.fecha_hora_concierto)
        nombreConcierto = view.findViewById(R.id.nombre_concierto)
        direccionConcierto = view.findViewById(R.id.direccion_concierto)
        ciudadConcierto = view.findViewById(R.id.ciudad_concierto)
        artistaConcierto = view.findViewById(R.id.artista_concierto)
        precioConcierto = view.findViewById(R.id.precio_concierto)

        // Inicializar referencia a la base de datos
        miDB_Reference = FirebaseDatabase.getInstance().reference.child("conciertos")

        //boton
        val boton : Button = view.findViewById(R.id.btn_reserva)

        boton.setOnClickListener {
            guardarConciertoDB()
        }
        fechaHoraSeleccionada.setOnClickListener { mostrarDatePicker() }
    }

    private fun guardarConciertoDB() {
        // Obtenemos los valores de los EditText
        val nombre = nombreConcierto.text.toString().trim()
        val fechaHora = fechaHoraSeleccionada.text.toString().trim()
        val direccion = direccionConcierto.text.toString().trim()
        val ciudad = ciudadConcierto.text.toString().trim()
        val artista = artistaConcierto.text.toString().trim()
        val precio = precioConcierto.text.toString().trim()

    // Verificamos que los campos no estén vacíos
        if (nombre.isNotEmpty() && fechaHora.isNotEmpty() && direccion.isNotEmpty()
            && ciudad.isNotEmpty() && artista.isNotEmpty() && precio.isNotEmpty()) {

            // Generamos un ID único para el concierto
            val conciertoId = miDB_Reference.push().key

            // Obtener UID del usuario actual
            val userId = FirebaseAuth.getInstance().currentUser?.uid

            val concierto = Conciertos(conciertoId, nombre, fechaHora, direccion, ciudad, artista, precio, userId)

            // Guardamos el concierto en la base de datos
            conciertoId?.let {
                miDB_Reference.child(it).setValue(concierto)
                    .addOnSuccessListener {
                        listener?.mostrarMensaje("Recordatorio del concierto guardado correctamente")
                    }
                    .addOnFailureListener {
                        listener?.mostrarMensaje("Error al guardar el recordatorio")
                    }
            }
        }else{
            listener?.mostrarMensaje("Por favor, introduzca todos los campos")
        }

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

        // Creamos y mostramos el DatePickerDialog
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