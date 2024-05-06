package com.example.proyectoandroid

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.SearchView
import com.example.proyectoandroid.databinding.ActivityBuscadorBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Buscador : AppCompatActivity() {
    private lateinit var binding : ActivityBuscadorBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityBuscadorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        setWebView()
        setListeners()
    }

    private fun setListeners() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val busqueda = query.toString().trim().lowercase()
                if (busqueda.isEmpty()) return true

                val url = "https://www.google.es/search?q=$busqueda"
                binding.webView.loadUrl(url)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
        //Para que nos vuelva a cargar la página si recargamos (relojito de carga)
        binding.swRefresh.setOnRefreshListener {
        binding.webView.reload()
        }
    }

    private fun setWebView() {
        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return false
            }
            //Activar la animacion del reloj
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                binding.swRefresh.isRefreshing = true
            }
            //Quitar la animacion del reloj
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding.swRefresh.isRefreshing = false
            }
        }
        binding.webView.webChromeClient = object : WebChromeClient() {
        }

        //Activamos javascript
        binding.webView.settings.javaScriptEnabled=true
        //cargamos la url de google
        binding.webView.loadUrl("https://www.google.es")

    }

    override fun onBackPressed() {
        if (binding.webView.canGoBack()){
            binding.webView.goBack()
        }else{
            super.onBackPressed()
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

    private fun cerrarSesion() {
        auth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun irActivityPerfil() {
        startActivity(Intent(this,Perfil_Usuario::class.java))
    }
}