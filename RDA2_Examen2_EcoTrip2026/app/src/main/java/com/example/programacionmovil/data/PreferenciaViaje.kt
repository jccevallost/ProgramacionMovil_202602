package com.example.programacionmovil.data

import kotlin.math.roundToInt

enum class PrioridadRuta(
    val etiqueta: String,
    val descripcion: String
) {
    Ecologica("Eco", "Menor huella"),
    Balanceada("Balance", "Costo y tiempo"),
    Rapida("Rapida", "Menor duracion");

    companion object {
        fun fromName(value: String?): PrioridadRuta =
            entries.firstOrNull { it.name == value } ?: Ecologica
    }
}

data class PreferenciaViaje(
    val origen: String = "",
    val destino: String = "",
    val dias: Int = 0,
    val presupuesto: Int = 0,
    val prioridad: PrioridadRuta = PrioridadRuta.Ecologica,
    val incluirHospedajeEco: Boolean = true
) {
    val tieneDatosGuardados: Boolean
        get() = origen.isNotBlank() ||
            destino.isNotBlank() ||
            dias > 0 ||
            presupuesto > 0
}

data class RecomendacionRuta(
    val titulo: String,
    val costoEstimado: Int,
    val distanciaKm: Int,
    val emisionesKg: Int,
    val segmentos: List<String>
)

fun generarRecomendacionRuta(preferencia: PreferenciaViaje): RecomendacionRuta {
    val distanciaBase = ((preferencia.origen.length + preferencia.destino.length) * 18)
        .coerceAtLeast(90)
    val distanciaKm = distanciaBase + preferencia.dias * 12
    val factorCosto = when (preferencia.prioridad) {
        PrioridadRuta.Ecologica -> 0.58
        PrioridadRuta.Balanceada -> 0.72
        PrioridadRuta.Rapida -> 0.86
    }
    val hospedaje = if (preferencia.incluirHospedajeEco) preferencia.dias * 28 else 0
    val costoEstimado = (preferencia.presupuesto * factorCosto + hospedaje)
        .roundToInt()
        .coerceAtMost(preferencia.presupuesto)
        .coerceAtLeast(1)
    val emisionesKg = when (preferencia.prioridad) {
        PrioridadRuta.Ecologica -> (distanciaKm * 0.05).roundToInt()
        PrioridadRuta.Balanceada -> (distanciaKm * 0.09).roundToInt()
        PrioridadRuta.Rapida -> (distanciaKm * 0.14).roundToInt()
    }.coerceAtLeast(1)
    val segmentos = when (preferencia.prioridad) {
        PrioridadRuta.Ecologica -> listOf("Tren regional", "Bici compartida", "Caminata urbana")
        PrioridadRuta.Balanceada -> listOf("Bus interprovincial", "Metro local", "Sendero guiado")
        PrioridadRuta.Rapida -> listOf("Conexion express", "Taxi electrico", "Ruta directa")
    }

    return RecomendacionRuta(
        titulo = "${preferencia.origen.trim()} -> ${preferencia.destino.trim()}",
        costoEstimado = costoEstimado,
        distanciaKm = distanciaKm,
        emisionesKg = emisionesKg,
        segmentos = segmentos
    )
}
