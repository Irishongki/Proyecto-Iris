package com.example.proyectoandroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.proyectoandroid.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    //Con el responseLauncher busca si tenemos guardados los credenciales de nuestra cuenta
    // y si no los tenemos nos deja registrar el usuario
    private var responseLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
                try {
                    val cuenta = task.getResult(ApiException::class.java)
                    if (cuenta != null) {
                        val credenciales = GoogleAuthProvider.getCredential(cuenta.idToken, null)
                        FirebaseAuth.getInstance().signInWithCredential(credenciales)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    irActivityPrincipal()
                                } else {
                                    Log.d("Register_1", it.exception.toString())
                                }
                            }
                            .addOnFailureListener {
                                Log.d("Register_2", it.message.toString())
                            }
                    }
                } catch (e: ApiException) {

                }
            } else {
                Toast.makeText(this, "El usuario ha cancelado", Toast.LENGTH_SHORT).show()
            }
        }

    lateinit var binding: ActivityLoginBinding
    private var email= ""
    private var pass = ""
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        database = FirebaseDatabase.getInstance().reference
        setListeners()
    }

    //Clase que se va a lanzar una vez que se lanza el metodo onCreate y nos detecta si tenemos un usuario creado para acceder al Activity
    override fun onStart() {
        super.onStart()
        val usuario = auth.currentUser
        if (usuario != null){
            irActivityPrincipal()
        }
    }

    private fun setListeners() {
        binding.btnLogin.setOnClickListener {
            if (comprobarCampos()){
                loginBasico()
            }
        }
        binding.tvOlvidarContraseA.setOnClickListener {
            irActivityRecuperarPassword()
        }
        binding.tvRegistro.setOnClickListener {
            irActivityRegistro()
        }
        binding.btnGoogle.setOnClickListener {
            loginGoogle()
        }

    }


    //Metodo que comprueba que los campos introducidos esten correctos
    private fun comprobarCampos(): Boolean {
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

    //Metodo para loguearnos con un email y contraseña

    private fun loginBasico() {
        auth.signInWithEmailAndPassword(email,pass)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    irActivityPrincipal()
                    /* Actualizar la nueva password en la base de datos
                    por si hemos utilizado lo de restablecer contraseña*/
                    actualizarPasswordEnDB(pass)
                    Toast.makeText(this,"Bienvenido/a a IRIS", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this,"El correo o la contraseña no es correcta", Toast.LENGTH_SHORT).show()
                }
            }
    }

    /*Metodo para que solo actualizemos la contraseña de la base de datos
    una vez que nos logueamos en nuestra app
    */
    private fun actualizarPasswordEnDB(newPassword: String) {
        val usuarioActual = FirebaseAuth.getInstance().currentUser
        val userEmail = usuarioActual?.email

        if (userEmail != null) {
            val usuariosRef = FirebaseDatabase.getInstance().getReference("usuarios")
            val query = usuariosRef.orderByChild("email").equalTo(userEmail)

            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (usuarioSnapshot in dataSnapshot.children) {
                            usuarioSnapshot.ref.child("password").setValue(newPassword)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {

                                    } else {

                                    }
                                }
                        }
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "No se encontró ningún usuario con este correo electrónico",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Error al buscar usuarios en la base de datos",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        } else {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_LONG).show()
        }
    }

    //Lanza el Activity Principal
    private fun irActivityPrincipal() {
        startActivity(Intent(this, Principal::class.java))
        finish()
    }

    //Lanza el Activity Registro
    private fun irActivityRegistro () {
        startActivity(Intent(this, Registro::class.java))
    }

    //Lanza el Activity de Recuperación de password
    private fun irActivityRecuperarPassword() {
        startActivity(Intent(this, OlvidarPassword::class.java))
    }

    //Metodo para loguearnos con google
    private fun loginGoogle() {
        val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("162992114020-cqv4dfc9efp09ciki450qtnep95ti2r5.apps.googleusercontent.com")
            .requestEmail()
            .build()

        val googleClient = GoogleSignIn.getClient(this, googleConf)
        googleClient.signOut() //Esto es importante ponerlo para que nos cierre sesion y podamos elegir otro usuario

        //Lanzamos un intent
        responseLauncher.launch(googleClient.signInIntent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}