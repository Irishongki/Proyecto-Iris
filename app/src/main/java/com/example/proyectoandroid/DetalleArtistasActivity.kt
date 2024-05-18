package com.example.proyectoandroid

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.widget.Button
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
    }


    private fun cogeryPintarDatos() {
        val datos = intent.extras
        val artista= datos?.getSerializable("ARTISTA") as Artist

        binding.tvTitulo.text= artista.nombre
        binding.ivArtistaDetalle.setImageResource(artista.imagen)
        binding.tvInformacion.text = artista.descripcion

        //Subrayar un texto con la clase SpannableString
        var infoTitulo = binding.tituloInformacion
        var spannableString = SpannableString(infoTitulo.text);
        spannableString.setSpan( UnderlineSpan(), 0, spannableString.length, 0);
        infoTitulo.setText(spannableString);
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_opciones, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.item_sesion ->{
                mostrarMensajeCerrarSesion()
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

    private fun mostrarMensajeCerrarSesion() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialogo_cerrar_sesion)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnAfirmativo : Button = dialog.findViewById(R.id.btn_afirmativo)
        val btnNegativo : Button = dialog.findViewById(R.id.btn_negativo)

        btnAfirmativo.setOnClickListener {
            cerrarSesion()
        }

        btnNegativo.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun cerrarSesion() {
        auth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun irActivityPerfil() {
        startActivity(Intent(this,Perfil_Usuario::class.java))
    }

}