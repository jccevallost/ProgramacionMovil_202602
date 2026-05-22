package com.example.programacionmovil.data

import kotlin.math.roundToInt

enum class TipoTransporteEcologico(
    val etiqueta: String,
    val descripcion: String,
    val factorEmision: Double
) {
    Tren("Tren", "Conexion ferroviaria de baja emision", 0.04),
    Bicicleta("Bicicleta", "Movilidad activa para tramos urbanos", 0.0),
    VehiculoElectrico("Vehiculo electrico", "Traslado flexible con energia limpia", 0.07);

    companion object {
        fun fromName(value: String?): TipoTransporteEcologico =
            entries.firstOrNull { it.name == value } ?: Tren
    }
}

data class PreferenciaViaje(
    val nombreViajero: String = "",
    val destino: String = "",
    val diasDuracion: Int = 0,
    val tipoTransporte: TipoTransporteEcologico = TipoTransporteEcologico.Tren,
    val soloRutasBajaHuella: Boolean = true
) {
    val tieneDatosGuardados: Boolean
        get() = nombreViajero.isNotBlank() ||
            destino.isNotBlank() ||
            diasDuracion > 0
}

data class RecomendacionRuta(
    val titulo: String,
    val distanciaKm: Int,
    val emisionesKg: Int,
    val ahorroEstimadoKg: Int,
    val segmentos: List<String>
)

fun generarRecomendacionRuta(preferencia: PreferenciaViaje): RecomendacionRuta {
    val distanciaKm = ((preferencia.destino.length + preferencia.nombreViajero.length) * 16)
        .coerceAtLeast(80) + preferencia.diasDuracion * 10
    val emisionesBase = (distanciaKm * preferencia.tipoTransporte.factorEmision).roundToInt()
    val emisionesKg = if (preferencia.soloRutasBajaHuella) {
        (emisionesBase * 0.65).roundToInt()
    } else {
        emisionesBase
    }.coerceAtLeast(0)
    val ahorroEstimadoKg = ((distanciaKm * 0.18) - emisionesKg).roundToInt().coerceAtLeast(0)
    val segmentos = when (preferencia.tipoTransporte) {
        TipoTransporteEcologico.Tren -> listOf("Estacion central", "Tren regional", "Caminata final")
        TipoTransporteEcologico.Bicicleta -> listOf("Ciclovia urbana", "Punto de hidratacion", "Ruta escenica")
        TipoTransporteEcologico.VehiculoElectrico -> listOf("Carga inicial", "Corredor electrico", "Parada eco")
    }

    return RecomendacionRuta(
        titulo = "${preferencia.nombreViajero.trim()} -> ${preferencia.destino.trim()}",
        distanciaKm = distanciaKm,
        emisionesKg = emisionesKg,
        ahorroEstimadoKg = ahorroEstimadoKg,
        segmentos = segmentos
    )
}
