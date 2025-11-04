package com.example.appmusicayletras.modelos

data class Noticia(
    var titulo: String = "",
    var descripcion: String = "",
    var fecha: String = "",
    var imagenUrl: String = "",
    var latitud: Double = 0.0,
    var longitud: Double = 0.0
)