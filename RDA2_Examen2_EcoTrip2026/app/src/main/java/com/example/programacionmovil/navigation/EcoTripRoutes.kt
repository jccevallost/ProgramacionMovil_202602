package com.example.programacionmovil.navigation

import kotlinx.serialization.Serializable

@Serializable
data object FormularioViajeRoute

@Serializable
data class ResumenRutaRoute(
    val origen: String,
    val destino: String
)
