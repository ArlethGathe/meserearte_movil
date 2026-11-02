package com.example.meserearte

// Datos de ejemplo de los eventos (se conectarán a una base de datos después)
data class Evento(
    val nombre: String,
    val fecha: String,
    val lugar: String,
    val meseros: Int,
    val estado: EstadoEvento
)

object DatosEventos {

    // Todos los eventos conocidos por la aplicación
    val lista = listOf(
        Evento("Boda Hernández & Ruiz", "08 nov 2026 · 18:00",
            "Hacienda Santa Elena", 8, EstadoEvento.CONFIRMADA),
        Evento("XV años Valentina", "14 nov 2026 · 19:00",
            "Salón Terraza Real", 6, EstadoEvento.PENDIENTE),
        Evento("Graduación ESCOM", "28 nov 2026 · 10:00",
            "Auditorio ESCOM, IPN Zacatenco", 10, EstadoEvento.CONFIRMADA),
        Evento("Boda Ramírez", "15 nov 2026 · 17:00",
            "Jardín Los Encinos", 8, EstadoEvento.PENDIENTE),
        Evento("Evento Corporativo ABC", "22 nov 2026 · 08:00",
            "Hotel Fiesta Americana Reforma", 5, EstadoEvento.PENDIENTE)
    )

    // Eventos que se muestran en Gestión de eventos (Vista 4)
    val gestion = lista.take(3)

    fun buscar(nombre: String): Evento? = lista.firstOrNull { it.nombre == nombre }
}
