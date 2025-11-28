package com.example.appmusicayletras

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.appmusicayletras.databinding.ActivityDetalleNoticiaBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class DetalleNoticiaActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityDetalleNoticiaBinding
    private lateinit var mMap: GoogleMap
    private var lat = 0.0
    private var lon = 0.0
    private var titulo = ""
    private var fecha = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleNoticiaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recibir datos primero
        titulo = intent.getStringExtra("titulo") ?: ""
        val descripcion = intent.getStringExtra("descripcion") ?: ""
        fecha = intent.getStringExtra("fecha") ?: ""
        val imagen = intent.getStringExtra("imagenUrl") ?: ""   // ✅ AHORA SÍ EXISTE
        lat = intent.getDoubleExtra("latitud", 19.4326)
        lon = intent.getDoubleExtra("longitud", -99.1332)

        // Mostrar en UI
        binding.tvTituloDetalle.text = titulo
        binding.tvDescripcionDetalle.text = descripcion
        binding.tvFechaDetalle.text = fecha
        Glide.with(this).load(imagen).into(binding.ivDetalle)

        // BOTÓN PARA COMPRAR BOLETO (ya puede usar imagen)
        binding.btnComprarBoleto.setOnClickListener {
            val intent = Intent(this, FormularioCompraActivity::class.java)
            intent.putExtra("tituloEvento", titulo)
            intent.putExtra("fechaEvento", fecha)
            intent.putExtra("imagenUrlEvento", imagen)   // <---- ESTE
            startActivity(intent)
        }

        // Configurar mapa
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapDetalle) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val eventoUbicacion = LatLng(lat, lon)
        mMap.addMarker(MarkerOptions().position(eventoUbicacion).title(titulo))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventoUbicacion, 14f))
    }


}
