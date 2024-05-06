package com.example.proyectoandroid

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.proyectoandroid.databinding.ActivityPerfilUsuarioBinding
import com.example.proyectoandroid.datosRealtimeDatabase.Usuarios
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class Perfil_Usuario : AppCompatActivity() {
    // Para recoger la imagen de la galería de imágenes
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            this.uri = uri
            binding.ivPerfil.setImageURI(uri)
        } else {
            Toast.makeText(this, "No se eligió ninguna imagen", Toast.LENGTH_SHORT).show()
        }
    }

    private lateinit var binding: ActivityPerfilUsuarioBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var authUser: FirebaseUser
    // Para trabajar con Storage
    private lateinit var storageReference: StorageReference
    // Para Realtime Database
    private lateinit var db: FirebaseDatabase
    private lateinit var ref: DatabaseReference

    private var uri: Uri? = null
    private var usuario = ""
    private var password = ""
    private var email = ""
    private var emailFormateado=""
    private val filename = "usuario.png"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        storageReference = FirebaseStorage.getInstance().reference
        db = FirebaseDatabase.getInstance("https://proyecto-88cf6-default-rtdb.firebaseio.com/")
        ref = db.getReference("usuarios")

        email= auth.currentUser?.email.toString()
        emailFormateado = email.replace(".","-")
        binding.edtEmailPerfil.setText(email)
        binding.edtEmailPerfil.isEnabled=false

        authUser = auth.currentUser!!

        cargarImagen()
        setListeners()
    }

    private fun setListeners() {
        // Listener para seleccionar imagen de la galería
        binding.ivPerfil.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        // Listener para guardar perfil
        binding.btnGuardar.setOnClickListener {
            if (!errorEnCogerDatos()) {
                guardarPerfil()
                finish()
            }
        }
        binding.btnBaja.setOnClickListener {
            borrarPerfil()
        }
    }

    private fun borrarPerfil() {
    // Eliminar los datos del usuario en Realtime Database
        ref.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val id = snapshot.key
                    id?.let {
                        // Eliminar los datos del usuario
                        ref.child(it).removeValue()
                    }
                }
                Toast.makeText(this@Perfil_Usuario, "Perfil eliminado con éxito", Toast.LENGTH_SHORT).show()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Perfil_Usuario, "Error al eliminar el perfil", Toast.LENGTH_SHORT).show()
            }
        })

       //Eliminar usuario en Firebase Authentication
        authUser.delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Eliminación exitosa
                    Toast.makeText(this, "Usuario eliminado correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    // Error al eliminar el usuario
                    Toast.makeText(this, "Error al eliminar el usuario", Toast.LENGTH_SHORT).show()
                }
            }
        finishAffinity()
    }

    private fun guardarPerfil() {
        // Si se seleccionó una imagen, guardarla en Storage
        if (uri != null) {
            val userId = auth.currentUser?.uid ?: ""
            val imagenPerfil = storageReference.child("$userId/$filename")
            imagenPerfil.putFile(uri!!)
                .addOnFailureListener {
                    Toast.makeText(this, "No se pudo guardar la imagen", Toast.LENGTH_SHORT).show()
                }
                .addOnSuccessListener {
                    // Después de guardar la imagen, actualizar los datos del perfil en la base de datos
                    actualizarDatosUsuario()
                }
        } else {
            // Si no se seleccionó una imagen, actualizar los datos del perfil en la base de datos directamente
            actualizarDatosUsuario()
            Toast.makeText(this, "No se seleccionó ninguna imagen", Toast.LENGTH_SHORT).show()
        }
    }

    private fun actualizarDatosUsuario() {
        //Guardo el resto del perfil en RealtimeDatabase
        ref.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val id = snapshot.key
                    id?.let {
                        val usuariosDatos = Usuarios(it, usuario, email, password)
                        ref.child(it).setValue(usuariosDatos)
                    }
                }
                Toast.makeText(this@Perfil_Usuario, "Perfil actualizado con éxito", Toast.LENGTH_SHORT).show()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Perfil_Usuario, "Error al actualizar el perfil", Toast.LENGTH_SHORT).show()
            }
        })
        //Actualizar la contraseña en Firebase Authentication
        password= binding.edtPassword.text.toString().trim()
        if (password.isNotEmpty()) {
            authUser.updatePassword(password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this@Perfil_Usuario, "Contraseña actualizada con éxito", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@Perfil_Usuario, "Error al actualizar la contraseña: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }


    private fun cargarImagen() {
        val userId = auth.currentUser?.uid
        if (!userId.isNullOrEmpty()) {
            val userImageRef = storageReference.child("$userId/$filename")
            userImageRef.downloadUrl
                .addOnSuccessListener { uri ->
                    // Cargar la imagen usando Glide y establecerla en ImageView
                    Glide.with(this)
                        .load(uri)
                        .fitCenter()
                        .centerCrop()
                        .into(binding.ivPerfil)
                }
                .addOnFailureListener {
                    // Si falla la descarga de la imagen, establecer una imagen de usuario predeterminada
                    binding.ivPerfil.setImageResource(R.drawable.usuario)
                }
        }
    }

    private fun errorEnCogerDatos(): Boolean {
        usuario= binding.edtNombreUsuarioPerfil.text.toString().trim()
        if (usuario.length<3){
            binding.edtNombreUsuarioPerfil.setError("El usuario tiene que tener al menos tres caracteres")
            binding.edtNombreUsuarioPerfil.requestFocus()
            return true
        }
        password= binding.edtPassword.text.toString().trim()
        if (password.length < 6){
            binding.edtPassword.error= "La contraseña debe tener al menos 6 caracteres"
            return true
        }
        return false
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
}