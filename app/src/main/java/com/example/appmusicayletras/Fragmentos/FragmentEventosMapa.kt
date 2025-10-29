package com.example.appmusicayletras.Fragmentos

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.appmusicayletras.R
import com.example.appmusicayletras.databinding.FragmentEventosMapaBinding
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class FragmentEventosMapa : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentEventosMapaBinding
    private lateinit var mMap: GoogleMap

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): android.view.View? {
        binding = FragmentEventosMapaBinding.inflate(inflater, container, false)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        return binding.root
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Activar controles de zoom
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isZoomGesturesEnabled = true

        // Lista de eventos (nombre, latitud, longitud)
        val eventos = listOf(
            Triple("🎸 Rock Fest CDMX", 19.4326, -99.1332),
            Triple("🎷 Jazz & Blues GDL", 20.6597, -103.3496),
            Triple("🎶 Pop Fest Monterrey", 25.6866, -100.3161),
            Triple("🎤 Indie Live Puebla", 19.0414, -98.2063),
            Triple("🎧 EDM Party Cancún", 21.1619, -86.8515),
            Triple("🎹 Electro Veracruz", 19.1738, -96.1342),
            Triple("🎻 Orquesta Mérida", 20.9674, -89.5926)
        )

        // Obtener ubicación actual 
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->

            val userLat = location?.latitude ?: 19.4326
            val userLon = location?.longitude ?: -99.1332
            val userLatLng = LatLng(userLat, userLon)

            // Marcador de usuario
            mMap.addMarker(
                MarkerOptions()
                    .position(userLatLng)
                    .title("📍 Tú estás aquí")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            )

            // Calcular distancias
            val eventosConDistancia = eventos.map { (nombre, lat, lon) ->
                val distancia = calcularDistancia(userLat, userLon, lat, lon)
                Triple(nombre, LatLng(lat, lon), distancia)
            }.sortedBy { it.third } // ordenar por distancia

            // Mostrar todos los eventos
            for ((index, evento) in eventosConDistancia.withIndex()) {
                val (nombre, posicion, distancia) = evento
                val marcador = MarkerOptions()
                    .position(posicion)
                    .title(nombre)
                    .snippet("A %.1f km de tu ubicación".format(distancia))

                // Los 3 más cercanos tendrán color diferente
                if (index < 3) {
                    marcador.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                } else {
                    marcador.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                }

                mMap.addMarker(marcador)
            }

            // Centrar el mapa en todo México
            val mexicoCenter = LatLng(22.0, -102.0)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mexicoCenter, 5f))

            Toast.makeText(requireContext(), "Eventos más cercanos resaltados en rojo 🎵", Toast.LENGTH_LONG).show()
        }
    }

    // Fórmula de Haversine (distancia en km)
    private fun calcularDistancia(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371 // radio de la Tierra en km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return R * c
    }
}
