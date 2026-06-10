package com.example.programacionmovil.navigation

import kotlinx.serialization.Serializable

@Serializable
data object FormularioViajeRoute

@Serializable
data class ResumenRutaRoute(
    val nombreViajero: String,
    val destino: String,
    val diasDuracion: Int,
    val tipoTransporte: String,
    val soloRutasBajaHuella: Boolean,
    val ticketCode: String
)
