package com.example.proyectoandroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.proyectoandroid.databinding.ActivityRegistroBinding
import com.example.proyectoandroid.datosRealtimeDatabase.Usuarios
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class Registro : AppCompatActivity() {
    private lateinit var binding: ActivityRegistroBinding
    private var email= ""
    private var pass = ""
    private var user = ""
    private lateinit var auth: FirebaseAuth
    private lateinit var miDB : FirebaseDatabase
    private lateinit var miDB_Reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        miDB = FirebaseDatabase.getInstance()
        miDB_Reference = miDB.reference.child("usuarios")
        setListeners()
    }

    private fun setListeners() {
        binding.btnRegistro.setOnClickListener {
            if (comprobarCampos()){
                registroBasico()
                registroUsuariosDB(user,email,pass)
            }
        }
        binding.tvInicioSesion.setOnClickListener {
            irActivityLogin()
        }
    }

    //Metodo que comprueba que los campos introducidos esten correctos
    private fun comprobarCampos(): Boolean {
        user = binding.etUsuario.text.toString().trim()
        if (user.length <3){
            binding.etUsuario.setError("El usuario tiene que tener al menos tres caracteres")
            return false
        }
        email = binding.etEmail.text.toString().trim()
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.etEmail.error = "Introduce un email válido"
            return false
        }
        pass= binding.etPassword.text.toString().trim()
        if (pass.length < 6){
            binding.etPassword.error= "La contraseña debe tener al menos 6 caracteres"
            return false
        }
        return true
    }

    private fun registroBasico() {
        auth.createUserWithEmailAndPassword(email,pass)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    irActivityPrincipal()
                    Toast.makeText(this,"Usuario Registrado", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this,"Este email ya esta registrado", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun registroUsuariosDB(usuario : String, email: String, password:String){
    //Esta funcion es para registrar los usuarios en la base de datos de Firebase
        miDB_Reference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(!snapshot.exists()){
                    val id = miDB_Reference.push().key
                    val usuariosDatos = Usuarios(id,usuario,email,password)
                    miDB_Reference.child(id!!).setValue(usuariosDatos)
                }
                else{

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Registro,"Error de Base de Datos: ${error.message}",Toast.LENGTH_LONG).show()
            }
        })
    }

    //Lanza el Activity Principal
    private fun irActivityPrincipal() {
        startActivity(Intent(this, Principal::class.java))
    }

    private fun irActivityLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}