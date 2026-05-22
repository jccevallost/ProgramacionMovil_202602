package com.example.programacionmovil

import com.example.programacionmovil.data.PreferenciaViaje
import com.example.programacionmovil.data.PrioridadRuta
import com.example.programacionmovil.data.generarRecomendacionRuta
import com.example.programacionmovil.ui.EcoTripUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class EcoTripStateTest {
    @Test
    fun formularioValidoConstruyePreferenciaSegura() {
        val state = EcoTripUiState(
            origen = "Quito",
            destino = "Cuenca",
            diasTexto = "4",
            presupuestoTexto = "350",
            prioridad = PrioridadRuta.Balanceada,
            incluirHospedajeEco = true
        )

        val preferencia = state.preferenciaValida

        assertTrue(state.esValido)
        assertNotNull(preferencia)
        assertEquals(4, preferencia?.dias)
        assertEquals(350, preferencia?.presupuesto)
    }

    @Test
    fun entradasNumericasInvalidasNoGeneranPreferencia() {
        val state = EcoTripUiState(
            origen = "Quito",
            destino = "Loja",
            diasTexto = "abc",
            presupuestoTexto = "10"
        )

        assertFalse(state.esValido)
        assertNull(state.preferenciaValida)
    }

    @Test
    fun recomendacionRespetaPresupuestoDisponible() {
        val preferencia = PreferenciaViaje(
            origen = "Manta",
            destino = "Tena",
            dias = 3,
            presupuesto = 220,
            prioridad = PrioridadRuta.Ecologica,
            incluirHospedajeEco = true
        )

        val recomendacion = generarRecomendacionRuta(preferencia)

        assertTrue(recomendacion.costoEstimado in 1..preferencia.presupuesto)
        assertTrue(recomendacion.distanciaKm > 0)
        assertTrue(recomendacion.segmentos.isNotEmpty())
    }
}
