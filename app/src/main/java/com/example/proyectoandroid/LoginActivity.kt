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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
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
                    Toast.makeText(this,"Bienvenido/a a IRIS", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this,"El correo o la contraseña no es correcta", Toast.LENGTH_SHORT).show()
                }
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
}