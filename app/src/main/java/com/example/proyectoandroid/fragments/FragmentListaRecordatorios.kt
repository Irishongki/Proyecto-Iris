package com.example.proyectoandroid.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectoandroid.EditarRecordatorios
import com.example.proyectoandroid.R
import com.example.proyectoandroid.adapters.RecordatoriosAdapter
import com.example.proyectoandroid.datosRealtimeDatabase.Conciertos
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class FragmentListaRecordatorios : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var recordatoriosAdapter: RecordatoriosAdapter
    var listener : OnFragmentActionListener?= null
    private var lista = mutableListOf<Conciertos>()
        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            return inflater.inflate(R.layout.fragment_lista_recordatorios, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            recyclerView = view.findViewById(R.id.recycler_conciertos)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recordatoriosAdapter = RecordatoriosAdapter(lista, ::eliminarConcierto, ::editarConcierto)
            recyclerView.adapter = recordatoriosAdapter

            obtenerDatosRecordatorios()
        }

    private fun editarConcierto(id: String?){
        val intent = Intent(requireContext(), EditarRecordatorios::class.java)
        intent.putExtra("concierto_id", id)
        startActivity(intent)
    }


    private fun eliminarConcierto(concierto: String?) {
        if (concierto != null) {
            val databaseReference = FirebaseDatabase.getInstance().getReference("conciertos").child(concierto)
            databaseReference.removeValue()
                .addOnSuccessListener {
                    listener?.mostrarMensaje("Concierto eliminado")
                }
                .addOnFailureListener { exception ->
                    listener?.mostrarMensaje("Error al eliminar el concierto ")
                }
        } else {
            listener?.mostrarMensaje( "ID de concierto nulo")
        }
    }

    private fun obtenerDatosRecordatorios() {
            val databaseReference = FirebaseDatabase.getInstance().getReference("conciertos")
            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val conciertosList = mutableListOf<Conciertos>()
                    for (dataSnapshot in snapshot.children) {
                        val concierto = dataSnapshot.getValue(Conciertos::class.java)
                        concierto?.let { conciertosList.add(it) }
                    }
                    recordatoriosAdapter.lista = conciertosList
                    recordatoriosAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Manejar error de lectura de la base de datos
                }
            })
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