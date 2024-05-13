package com.example.proyectoandroid

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.commit
import com.example.proyectoandroid.adapters.VentanaInformacionAdapter
import com.example.proyectoandroid.models.Evento
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.io.BufferedReader
import java.io.InputStreamReader

class Mapa : AppCompatActivity() , OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener{
    private lateinit var mapa:GoogleMap
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa)
        auth = Firebase.auth
        cargarMapa()
    }


    private fun cargarMapa() {
        val fragment = SupportMapFragment()
        fragment.getMapAsync(this)
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add(R.id.fg_mapa, fragment)
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        mapa = p0
        mapa.uiSettings.isZoomControlsEnabled= true
        mapa.mapType=GoogleMap.MAP_TYPE_NORMAL
        mapa.setOnMyLocationClickListener(this)
        mapa.setOnMyLocationButtonClickListener(this)
        ponerlocalizacion()
        var LocalizacionSpain= LatLng(41.284860333832604, -3.6993549851288705)
        mapa.animateCamera(
            CameraUpdateFactory.newLatLngZoom(LocalizacionSpain,5f)
        )

        val csvFile = "Eventos.csv"
        val eventos = leerArchivoEventos(csvFile)

        eventos?.forEach { evento ->
            ponerMarcador(evento)
        }

    }


    private fun leerArchivoEventos(archivo: String): List<Evento>?{
        val inputStream = assets.open(archivo)
        val reader = BufferedReader(InputStreamReader(inputStream))


        val eventos = mutableListOf<Evento>()
        reader.use { reader ->
            var line: String?
            var isFirstLine = true // Para ignorar la primera línea que contiene los encabezados
            while (reader.readLine().also { line = it } != null) {
                if (isFirstLine) {
                    isFirstLine = false
                    continue
                }

                val columnas = line!!.split(",") // Cambiar el carácter de separación a la coma

                    val latitud = columnas[39].toDouble()
                    val longitud = columnas[38].toDouble()
                    val nombre = columnas[1]
                    val fechaHora= columnas[6]
                    val direccion = columnas[37]
                    val ciudad = columnas[33]
                    val nombre_artista= columnas[45]
                    val precio = columnas[101]

                    val evento = Evento(nombre, LatLng(latitud, longitud),
                        fechaHora, direccion, ciudad, nombre_artista, precio)
                    eventos.add(evento)

            }
        }

        return eventos
    }

    private fun ponerMarcador(evento: Evento) {
        val layoutInflater = LayoutInflater.from(this)
        val ventanaInformacionAdapter = VentanaInformacionAdapter(layoutInflater)
        mapa.setInfoWindowAdapter(ventanaInformacionAdapter)

       mapa.addMarker(MarkerOptions()
            .position(evento.posicion)
            .title(evento.nombre+ "\n")
           .snippet(evento.fechaHora + "\n" +evento.direccion + "\n" +
                   evento.ciudad + "\n" +evento.nombreArtista + "\n" +
                   "Precio de las entradas " + evento.precio+ "€")
           )
    }

    private fun mostrarInformarcionMarcador(clickedMarker: Marker) {
        val intent = Intent(this, VentanaInformacionAdapter::class.java)
        clickedMarker.showInfoWindow()
        startActivity(intent)
    }



    private fun ponerlocalizacion() {
        if (!::mapa.isInitialized) return //si no se ha inicializado el mapa
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED
        ){
            mapa.isMyLocationEnabled=true
        }else{
            pedirPermisos()
        }
    }

    private fun pedirPermisos() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_COARSE_LOCATION)
            ||
            ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)
        ){
            //Han denegado los permisos y vamos a dar una explicacion de porque es bueno que nos los den
            mostrarExplicacion()
        }else{
            //Pedimos los permisos
            escogerPermisos()
        }
    }

    private fun mostrarExplicacion() {
        AlertDialog.Builder(this)
            .setTitle("Permisos Localizacion")
            .setMessage("Para que la App le pueda ofrecer la localizacion necesitamos que acepte los permisos")
            .setPositiveButton("Abrir Ajustes"){view, _->
                startActivity(Intent(Settings.ACTION_APPLICATION_SETTINGS))
                view.dismiss()
            }
            .setNegativeButton("CANCELAR",null)
            .setCancelable(false)
            .create()
            .show()
    }

    private fun escogerPermisos() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
            1000
        )
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this,"Has pulsado el boton", Toast.LENGTH_SHORT).show()
        return false
    }

    override fun onMyLocationClick(p0: Location) {
        Toast.makeText(this,"Coordenadas: Lat:${p0.latitude}, Long:${p0.longitude}",Toast.LENGTH_SHORT).show()
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

    private fun irActivityPerfil() {
        startActivity(Intent(this,Perfil_Usuario::class.java))
    }

    private fun cerrarSesion() {
        auth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
    }
}