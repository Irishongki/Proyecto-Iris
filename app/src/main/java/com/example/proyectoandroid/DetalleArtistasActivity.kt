package com.example.proyectoandroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.example.proyectoandroid.databinding.ActivityDetalleArtistasBinding
import com.example.proyectoandroid.models.Artist
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class DetalleArtistasActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetalleArtistasBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleArtistasBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        cogeryPintarDatos()
        setListeners()
    }

    private fun setListeners() {
        binding.btVolver.setOnClickListener {
            finish()
        }
    }

    private fun cogeryPintarDatos() {
        val datos = intent.extras
        val artista= datos?.getSerializable("ARTISTA") as Artist

        binding.tvTitulo.text= artista.nombre
        binding.ivArtistaDetalle.setImageResource(artista.imagen)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_opciones, menu)
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

    private fun cerrarSesion() {
        auth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun irActivityPerfil() {
        startActivity(Intent(this,Perfil_Usuario::class.java))
    }

}