package com.example.proyectoandroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.proyectoandroid.databinding.ActivityPrincipalBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class Principal : AppCompatActivity() {
    private lateinit var binding: ActivityPrincipalBinding
    private lateinit var auth: FirebaseAuth
    //Para trabajar con storage
    private lateinit var storageReference: StorageReference
    //RealtimeDatabase
    private lateinit var db: FirebaseDatabase
    private lateinit var ref: DatabaseReference
    private val filename = "usuario.png"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        storageReference= FirebaseStorage.getInstance().reference
        db = FirebaseDatabase.getInstance("https://proyecto-88cf6-default-rtdb.firebaseio.com/")
        ref = db.getReference("usuarios")
       recogerDatos()
       setListeners()
        cargarImagen()
    }

    private fun cargarImagen() {
        val emailFormatted = getEmailFormatted()
        val userImageRef = storageReference.child(emailFormatted).child(filename)
        userImageRef.downloadUrl
            .addOnSuccessListener {
                Glide.with(this)
                    .load(it)
                    .fitCenter()
                    .centerCrop()
                    .into(binding.ivUsuario)
            }
            .addOnFailureListener {
                binding.ivUsuario.setImageResource(R.drawable.usuario)
            }
    }

    private fun getEmailFormatted(): String {
        return auth.currentUser?.uid ?: ""
    }

    private fun setListeners() {
        binding.ivBuscador.setOnClickListener {
            irActivityBuscador()
        }
        binding.ivSalir.setOnClickListener {
            finishAffinity()
        }
        binding.ivArtistas.setOnClickListener {
            irActivityArtistas()
        }
        binding.ivRecordatorios.setOnClickListener {
            irActivityReservas()
        }
        binding.ivConciertos.setOnClickListener {
            irActivityConciertos()
        }
    }

    private fun irActivityConciertos() {
        startActivity(Intent(this,Mapa::class.java))
    }

    private fun irActivityReservas() {
        startActivity(Intent(this,Recordatorios::class.java))
    }

    private fun irActivityArtistas() {
        startActivity(Intent(this,Artistas::class.java))
    }

    private fun irActivityBuscador() {
        startActivity(Intent(this,Buscador::class.java))
    }

    private fun recogerDatos() {
        val user = auth.currentUser
        if (user != null) {
            val userEmail = user.email
            if (!userEmail.isNullOrEmpty()) {
                ref.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (snapshot in dataSnapshot.children) {
                                val usuario = snapshot.child("usuario").value.toString()
                                val correoUsuario = snapshot.child("email").value.toString()

                                // Verificar si se encontró el usuario y el correo
                                if (usuario.isNotEmpty() && correoUsuario.isNotEmpty()) {
                                    binding.tvUsuario.text = "Hola, $usuario!"
                                    binding.tvEmail.text = correoUsuario
                                    return // Salir del bucle después de encontrar los datos
                                }
                            }
                        } else {
                            // Si el usuario se autenticó con Google, obtener el correo electrónico desde la cuenta de Google
                            val googleUser = GoogleSignIn.getLastSignedInAccount(this@Principal)
                            val correoUsuarioActual: String? = googleUser?.email

                            if (!correoUsuarioActual.isNullOrEmpty()) {
                                binding.tvUsuario.text = "Hola Usuario!"
                                binding.tvEmail.text = correoUsuarioActual
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Manejar el error en la carga de datos del perfil desde la base de datos
                        Toast.makeText(this@Principal, "Error al cargar datos de la base de datos: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater= menuInflater.inflate(R.menu.menu_opciones, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.item_sesion ->{
                cerrarSesion()
            }
            R.id.item_perfil ->{
                irActivityPerfil()
            }
            R.id.item_salir ->{
                finishAffinity()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun irActivityPerfil() {
        startActivity(Intent(this,Perfil_Usuario::class.java))
    }

    private fun cerrarSesion() {
        auth.signOut()
       startActivity(Intent(this, LoginActivity::class.java))
    }

    override fun onResume() {
        super.onResume()
        recogerDatos()
        cargarImagen()
    }

    override fun onRestart() {
        super.onRestart()
        recogerDatos()
        cargarImagen()
    }
}