package com.example.programacionmovil.ui.concurrency

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val LOG_TAG = "ANR_LAB"

class SimulationViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<SimulationUiState>(SimulationUiState.Idle)
    val uiState: StateFlow<SimulationUiState> = _uiState.asStateFlow()

    fun cargarDatosAsincronosConValidacion(textoUsuario: String) {
        if (_uiState.value is SimulationUiState.Loading) return

        viewModelScope.launch {
            Log.d(LOG_TAG, "Inicio Corrutina en hilo: ${Thread.currentThread().name}")
            _uiState.value = SimulationUiState.Loading

            try {
                if (textoUsuario.isRestrictedSimulationInput()) {
                    throw IllegalArgumentException(
                        "Entrada restringida detectada por simulacion de seguridad."
                    )
                }

                val resultado = withContext(Dispatchers.IO) {
                    Log.d(
                        LOG_TAG,
                        "Procesando calculo intensivo en segundo plano de forma no bloqueante " +
                            "sobre hilo: ${Thread.currentThread().name}"
                    )
                    Thread.sleep(5000)
                    "Procesamiento asincrono limpio completado desde Dispatchers.IO."
                }

                Log.d(
                    LOG_TAG,
                    "Retorno seguro a Dispatchers.Main sobre hilo: ${Thread.currentThread().name}"
                )
                _uiState.value = SimulationUiState.Success(resultado)
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                Log.e(LOG_TAG, "Fallo asincrono controlado", exception)
                _uiState.value = SimulationUiState.Error(
                    exception.localizedMessage ?: "Error desconocido"
                )
            }
        }
    }
}

internal fun String.isRestrictedSimulationInput(): Boolean =
    trim().equals("ERROR", ignoreCase = true)
