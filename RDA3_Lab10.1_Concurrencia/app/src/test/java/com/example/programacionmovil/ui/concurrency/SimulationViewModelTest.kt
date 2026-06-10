package com.example.programacionmovil.ui.concurrency

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SimulationViewModelTest {
    @Test
    fun restrictedInputDetectsErrorKeywordIgnoringCaseAndSpaces() {
        assertTrue(" ERROR ".isRestrictedSimulationInput())
        assertTrue("error".isRestrictedSimulationInput())
    }

    @Test
    fun restrictedInputAllowsNormalText() {
        assertFalse("datos de prueba".isRestrictedSimulationInput())
    }
}
