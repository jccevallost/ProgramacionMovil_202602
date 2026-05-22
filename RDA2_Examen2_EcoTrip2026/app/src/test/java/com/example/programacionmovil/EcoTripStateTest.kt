package com.example.programacionmovil

import com.example.programacionmovil.data.PreferenciaViaje
import com.example.programacionmovil.data.TipoTransporteEcologico
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
            nombreViajero = "Ana",
            destino = "Cuenca",
            diasDuracionTexto = "4",
            tipoTransporte = TipoTransporteEcologico.Tren,
            soloRutasBajaHuella = true
        )

        val preferencia = state.preferenciaValida

        assertTrue(state.esValido)
        assertNotNull(preferencia)
        assertEquals(4, preferencia?.diasDuracion)
        assertEquals(TipoTransporteEcologico.Tren, preferencia?.tipoTransporte)
    }

    @Test
    fun entradaNumericaInvalidaNoGeneraPreferencia() {
        val state = EcoTripUiState(
            nombreViajero = "Luis",
            destino = "Loja",
            diasDuracionTexto = "abc"
        )

        assertFalse(state.esValido)
        assertNull(state.preferenciaValida)
    }

    @Test
    fun recomendacionProduceResumenConEmisionesNoNegativas() {
        val preferencia = PreferenciaViaje(
            nombreViajero = "Maya",
            destino = "Tena",
            diasDuracion = 3,
            tipoTransporte = TipoTransporteEcologico.Bicicleta,
            soloRutasBajaHuella = true
        )

        val recomendacion = generarRecomendacionRuta(preferencia)

        assertTrue(recomendacion.emisionesKg >= 0)
        assertTrue(recomendacion.distanciaKm > 0)
        assertTrue(recomendacion.segmentos.isNotEmpty())
    }
}
