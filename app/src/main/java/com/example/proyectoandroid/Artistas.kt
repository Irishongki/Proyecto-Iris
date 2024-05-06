package com.example.proyectoandroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.GridLayoutManager
import com.example.proyectoandroid.adapters.ArtistasAdapter
import com.example.proyectoandroid.databinding.ActivityArtistasBinding
import com.example.proyectoandroid.models.Artist
import com.example.proyectoandroid.providers.ArtistasProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Artistas : AppCompatActivity() {
    private lateinit var binding: ActivityArtistasBinding
    private lateinit var auth: FirebaseAuth
    private var lista = ArtistasProvider.misArtistas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityArtistasBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        setRecycler()
    }

    private fun setRecycler() {
        val layoutManager = GridLayoutManager(this,2)
        binding.recyclerView.layoutManager = layoutManager

        binding.recyclerView.adapter=ArtistasAdapter(lista, {artista-> mostrarArtista(artista)})
    }

    private fun mostrarArtista(artista: Artist) {
        val i = Intent(this, DetalleArtistasActivity::class.java).apply {
            putExtra("ARTISTA", artista)
        }
        startActivity(i)
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