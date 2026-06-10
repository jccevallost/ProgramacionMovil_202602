package com.example.programacionmovil.ui.concurrency

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

private const val LOG_TAG = "ANR_LAB"

@Composable
fun AnrSimulationScreen(
    modifier: Modifier = Modifier,
    simulationViewModel: SimulationViewModel = viewModel()
) {
    var textInput by remember { mutableStateOf("") }
    var blockingResult by remember { mutableStateOf<String?>(null) }
    val currentUiState by simulationViewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Laboratorio Concurrencia Avanzada - Apps Fluidas",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = textInput,
            onValueChange = { textInput = it },
            label = { Text("Escriba aqui para comprobar que la UI no se congela") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                blockingResult = null
                simulationViewModel.cargarDatosAsincronosConValidacion(textInput)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = currentUiState !is SimulationUiState.Loading
        ) {
            Text("Cargar Datos Pesados (Asincrono y Seguro)")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = {
                Log.d(
                    LOG_TAG,
                    "Inicio de operacion pesada en Hilo Principal: " +
                        Thread.currentThread().name
                )
                blockingResult = ejecutarCalculoMasivoBloqueante()
                Log.d(LOG_TAG, "Fin de operacion bloqueante en Hilo Principal")
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = currentUiState !is SimulationUiState.Loading
        ) {
            Text("Simular Bloqueo Main Thread")
        }

        Spacer(modifier = Modifier.height(24.dp))

        SimulationStatus(currentUiState = currentUiState)

        blockingResult?.let { result ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Resultado bloqueante: $result",
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun SimulationStatus(currentUiState: SimulationUiState) {
    when (currentUiState) {
        is SimulationUiState.Idle -> {
            Text("Estado: Esperando accion del usuario")
        }

        is SimulationUiState.Loading -> {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(8.dp))
            Text("Procesando en segundo plano en Dispatchers.IO...")
        }

        is SimulationUiState.Success -> {
            Text(
                text = "Resultado: ${currentUiState.dataMessage}",
                color = MaterialTheme.colorScheme.primary
            )
        }

        is SimulationUiState.Error -> {
            Text(
                text = "Error: ${currentUiState.errorMessage}",
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

fun ejecutarCalculoMasivoBloqueante(): String {
    val tiempoInicio = System.currentTimeMillis()
    Log.d(LOG_TAG, "Procesando bucle de alta densidad en el Main Thread...")

    try {
        Thread.sleep(5000)
    } catch (exception: InterruptedException) {
        Thread.currentThread().interrupt()
        return "Procesamiento interrumpido"
    }

    val tiempoTotal = System.currentTimeMillis() - tiempoInicio
    return "Procesamiento completado con exito en $tiempoTotal ms"
}
