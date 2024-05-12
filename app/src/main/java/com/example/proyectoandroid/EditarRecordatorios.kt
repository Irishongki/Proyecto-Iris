package com.example.proyectoandroid

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import com.example.proyectoandroid.databinding.ActivityEditarRecordatoriosBinding
import com.example.proyectoandroid.datosRealtimeDatabase.Conciertos
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditarRecordatorios : AppCompatActivity() {
    private lateinit var conciertoId: String
    private lateinit var binding :ActivityEditarRecordatoriosBinding
    val seleccionarCalendario = Calendar.getInstance()
    private lateinit var fechaHoraSeleccionada: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditarRecordatoriosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtener el ID del concierto de los extras del intent
        conciertoId = intent.getStringExtra("concierto_id") ?: ""

        // Aquí puedes cargar los detalles del concierto usando el ID y mostrarlos en los EditTexts u otros componentes de tu diseño
        cargarDetallesConcierto(conciertoId)
        fechaHoraSeleccionada = binding.fechaHoraConcierto
        fechaHoraSeleccionada.setOnClickListener { mostrarDatePicker() }
        // Configurar el botón de guardar
        val editarBoton = binding.btnEditar
        editarBoton.setOnClickListener {
            guardarCambiosConcierto()
            startActivity(Intent(this,Recordatorios::class.java))
        }
    }

    private fun cargarDetallesConcierto(conciertoId: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("conciertos").child(conciertoId)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val concierto = snapshot.getValue(Conciertos::class.java)

                    concierto?.let {
                        binding.nombreConcierto.setText(it.titulo)
                        binding.direccionConcierto.setText(it.direccion)
                        binding.ciudadConcierto.setText(it.ciudad)
                        binding.artistaConcierto.setText(it.artista)
                        binding.precioConcierto.setText(it.precio)
                    }
                } else {
                    Toast.makeText(this@EditarRecordatorios, "El concierto no existe", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@EditarRecordatorios, "Error al cargar los detalles del concierto", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun guardarCambiosConcierto() {
        val nuevosDatosConcierto = obtenerNuevosDatosConcierto()

        actualizarDatosConcierto(conciertoId, nuevosDatosConcierto)
    }

    private fun obtenerNuevosDatosConcierto(): Map<String, Any> {

        val nuevosDatos = mutableMapOf<String, Any>()
        nuevosDatos["titulo"] = binding.nombreConcierto.text.toString()
        nuevosDatos["fechaHora"] = fechaHoraSeleccionada.text.toString()
        nuevosDatos["direccion"] = binding.direccionConcierto.text.toString()
        nuevosDatos["ciudad"] = binding.ciudadConcierto.text.toString()
        nuevosDatos["artista"] = binding.artistaConcierto.text.toString()
        nuevosDatos["precio"] = binding.precioConcierto.text.toString()
        return nuevosDatos
    }

    private fun actualizarDatosConcierto(conciertoId: String, nuevosDatosConcierto: Map<String, Any>) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("conciertos").child(conciertoId)
        databaseReference.updateChildren(nuevosDatosConcierto)
            .addOnSuccessListener {
                Toast.makeText(this, "Concierto actualizado correctamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al actualizar concierto: ${e.message}", Toast.LENGTH_SHORT).show()
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
                this,
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
        val datePickerDialog = DatePickerDialog(this, dateListener, year, month, day)
        datePickerDialog.show()

    }
    private fun actualizarFechaHoraSeleccionada() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val fechaHoraFormateada = dateFormat.format(seleccionarCalendario.time)
        fechaHoraSeleccionada.setText(fechaHoraFormateada)
    }

}