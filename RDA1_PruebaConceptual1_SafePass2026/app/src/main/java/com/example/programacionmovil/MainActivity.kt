package com.example.programacionmovil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Clase principal obligatoria
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                SafePassScreen()
            }
        }
    }
}

// 1. Data class inmutable (Nombre, Edad anulable, Tipo Entrada)
data class Asistente(
    val nombre: String,
    val edad: Int?,
    val tipoEntrada: String
)

// 2. Sealed class para gestión de estados (Idle, Success, Error)
sealed class RegistroState {
    data object Idle : RegistroState()
    data class Success(val asistente: Asistente, val mensaje: String) : RegistroState()
    data class Error(val mensaje: String) : RegistroState()
}

// 3. Extension Function para validar reglas (ej. Mayor de edad)
fun Int.esMayorDeEdad(): Boolean = this >= 18

// 4. Lógica con Higher-Order Function y Scope Functions
fun realizarRegistro(
    nom: String,
    ed: String,
    tipo: String,
    validarPrioridad: (String) -> String,
    onResult: (RegistroState) -> Unit
) {
    run {
        val nombreLimpio = nom.trim().takeIf { it.isNotEmpty() }

        // 1. Validamos que el nombre no esté vacío
        if (nombreLimpio == null) {
            onResult(RegistroState.Error("El nombre es obligatorio."))
            return@run
        }

        // 2. Intentamos convertir la edad
        val edadConvertida = ed.toIntOrNull()

        if (edadConvertida == null) {
            // Error elegante si se ingresan letras [cite: 26, 27]
            onResult(RegistroState.Error("Ingresa un valor numérico válido en la edad."))
        } else {
            // 3. Si es número, validamos la regla de negocio con la Extension Function [cite: 15]
            if (!edadConvertida.esMayorDeEdad()) {
                onResult(RegistroState.Error("Acceso denegado: El asistente debe ser mayor de 18 años."))
            } else {
                // Registro exitoso usando Scope Functions [cite: 18]
                val asistenteValido = Asistente(nombreLimpio, edadConvertida, tipo).apply {
                    println("Auditoría: Registro exitoso para $nombre")
                }
                val mensajePrioridad = validarPrioridad(tipo)
                onResult(RegistroState.Success(asistenteValido, mensajePrioridad))
            }
        }
    }
}

// 5. Interfaz en Compose con Scaffold y when exhaustivo
@Composable
fun SafePassScreen() {
    var nombre by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("General") }
    var estado by remember { mutableStateOf<RegistroState>(RegistroState.Idle) }

    Scaffold { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
            Text("SafePass 2026 - TechEvent", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
            OutlinedTextField(value = edad, onValueChange = { edad = it }, label = { Text("Edad") })
            OutlinedTextField(value = tipo, onValueChange = { tipo = it }, label = { Text("Tipo Entrada (VIP/General)") })

            Button(
                onClick = {
                    realizarRegistro(nombre, edad, tipo, { t ->
                        if(t.uppercase() == "VIP") "Prioridad Alta" else "Prioridad Estándar"
                    }) { estado = it }
                },
                modifier = Modifier.padding(top = 16.dp)
            ) { Text("Registrar") }

            Spacer(modifier = Modifier.height(20.dp))

            // When exhaustivo solicitado
            when (val s = estado) {
                is RegistroState.Idle -> Text("Ingrese los datos del asistente.")
                is RegistroState.Success -> Text("✅ Éxito: ${s.asistente.nombre} - ${s.mensaje}", color = MaterialTheme.colorScheme.primary)
                is RegistroState.Error -> Text("❌ Error: ${s.mensaje}", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}