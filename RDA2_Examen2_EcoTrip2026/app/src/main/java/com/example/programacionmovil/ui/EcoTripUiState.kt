package com.example.programacionmovil.ui

import com.example.programacionmovil.data.PreferenciaViaje
import com.example.programacionmovil.data.PrioridadRuta

data class EcoTripUiState(
    val origen: String = "",
    val destino: String = "",
    val diasTexto: String = "",
    val presupuestoTexto: String = "",
    val prioridad: PrioridadRuta = PrioridadRuta.Ecologica,
    val incluirHospedajeEco: Boolean = true,
    val guardando: Boolean = false,
    val intentoEnvio: Boolean = false,
    val mensaje: String? = null
) {
    val dias: Int?
        get() = diasTexto.toIntOrNull()

    val presupuesto: Int?
        get() = presupuestoTexto.toIntOrNull()

    val origenValido: Boolean
        get() = origen.isNotBlank()

    val destinoValido: Boolean
        get() = destino.isNotBlank()

    val diasValidos: Boolean
        get() = dias?.let { it in 1..30 } == true

    val presupuestoValido: Boolean
        get() = presupuesto?.let { it >= 50 } == true

    val esValido: Boolean
        get() = origenValido && destinoValido && diasValidos && presupuestoValido

    val preferenciaValida: PreferenciaViaje?
        get() {
            val diasSeguros = dias ?: return null
            val presupuestoSeguro = presupuesto ?: return null
            return if (esValido) {
                PreferenciaViaje(
                    origen = origen.trim(),
                    destino = destino.trim(),
                    dias = diasSeguros,
                    presupuesto = presupuestoSeguro,
                    prioridad = prioridad,
                    incluirHospedajeEco = incluirHospedajeEco
                )
            } else {
                null
            }
        }
}
