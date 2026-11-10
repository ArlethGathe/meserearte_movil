package com.example.meserearte

// Datos de muestra para la práctica de widgets: aún no tienen base de datos.
data class Mesero(
    val id: Int,
    var nombre: String,
    var sala: String,
    var eventos: Int,
    var activo: Boolean
)

object DatosMeseros {
    val lista = mutableListOf(
        Mesero(1, "María López", "Salón Elegance", 8, true),
        Mesero(2, "Juan Pérez", "Salón Aurora", 5, true),
        Mesero(3, "Luis Gómez", "Salón Elegance", 3, false),
        Mesero(4, "Ana Torres", "Salón Aurora", 6, true)
    )
    fun siguienteId(): Int = (lista.maxOfOrNull { it.id } ?: 0) + 1
}
