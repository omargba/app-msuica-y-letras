package com.example.appmusicayletras.modelos

data class Boleto(
    var id: String = "",
    var usuarioId: String = "",
    var nombre: String = "",
    var correo: String = "",
    var direccion: String = "",
    var marcaTarjeta: String = "",
    var ultimos4: String = "",
    var evento: String = "",
    var fechaEvento: String = ""
)
