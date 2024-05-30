package com.example.proyectoandroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.proyectoandroid.databinding.ActivityLoginBinding
import com.example.proyectoandroid.databinding.ActivityOlvidarPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class OlvidarPassword : AppCompatActivity() {
        lateinit var binding: ActivityOlvidarPasswordBinding
        private lateinit var email: EditText
        private lateinit var auth: FirebaseAuth
        private lateinit var botonRecuperarPassword: Button

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityOlvidarPasswordBinding.inflate(layoutInflater)
            setContentView(binding.root)
            auth = FirebaseAuth.getInstance()
            email = binding.etEmail
            botonRecuperarPassword = binding.btnRecuperar
            setListeners()
        }

        private fun setListeners() {
            botonRecuperarPassword.setOnClickListener {
                if (validar()) {
                    val correo = email.text.toString().trim()
                    if (correo.isNotEmpty()) {
                        enviarEmail(correo)
                    } else {
                        Toast.makeText(
                            this,
                            "Por favor, introduce tu dirección de correo electrónico",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            binding.tvVolverInicio.setOnClickListener {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }

        private fun validar(): Boolean {
            val correo = email.text.toString().trim()
            if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                binding.etEmail.error = "Introduce un email válido"
                return false
            }
            return true
        }

    //Función para enviar al email que tenmos registrado en la autenticación,
    //un correo para restablecer la contraseña
        private fun enviarEmail(email: String) {
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this,
                            "Correo de restablecimiento de contraseña enviado",
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    } else {
                        val errorMessage = task.exception?.message
                            ?: "Error al enviar el correo de restablecimiento de contraseña"
                        Toast.makeText(
                            this,
                            errorMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        override fun onBackPressed() {
            super.onBackPressed()
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
