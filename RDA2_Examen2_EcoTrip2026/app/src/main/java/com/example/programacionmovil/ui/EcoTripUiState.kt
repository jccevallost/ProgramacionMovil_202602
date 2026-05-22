package com.example.programacionmovil.ui

import com.example.programacionmovil.data.PreferenciaViaje
import com.example.programacionmovil.data.TipoTransporteEcologico

data class EcoTripUiState(
    val nombreViajero: String = "",
    val destino: String = "",
    val diasDuracionTexto: String = "",
    val tipoTransporte: TipoTransporteEcologico = TipoTransporteEcologico.Tren,
    val soloRutasBajaHuella: Boolean = true,
    val guardando: Boolean = false,
    val intentoEnvio: Boolean = false,
    val mensaje: String? = null
) {
    val diasDuracion: Int?
        get() = diasDuracionTexto.toIntOrNull()

    val nombreValido: Boolean
        get() = nombreViajero.isNotBlank()

    val destinoValido: Boolean
        get() = destino.isNotBlank()

    val diasValidos: Boolean
        get() = diasDuracion?.let { it in 1..30 } == true

    val esValido: Boolean
        get() = nombreValido && destinoValido && diasValidos

    val preferenciaValida: PreferenciaViaje?
        get() {
            val diasSeguros = diasDuracion ?: return null
            return if (esValido) {
                PreferenciaViaje(
                    nombreViajero = nombreViajero.trim(),
                    destino = destino.trim(),
                    diasDuracion = diasSeguros,
                    tipoTransporte = tipoTransporte,
                    soloRutasBajaHuella = soloRutasBajaHuella
                )
            } else {
                null
            }
        }
}
