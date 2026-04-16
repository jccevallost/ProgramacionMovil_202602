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
@OptIn(ExperimentalMaterial3Api::class)
fun SafePassScreen() {
    var nombre by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var tipoEntrada by remember { mutableStateOf("General") } // Valor por defecto
    var menuExpandido by remember { mutableStateOf(false) }   // Controla si el menú está abierto
    val opcionesEntrada = listOf("General", "VIP", "Cortesía") // Lista de opciones para el TechEvent 2026 [cite: 8, 9]
    var estado by remember { mutableStateOf<RegistroState>(RegistroState.Idle) }

    Scaffold { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
            Text("SafePass 2026 - TechEvent", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del Asistente") },
                isError = nombre.isBlank(), // Se pone rojo si está vacío [cite: 30]
                supportingText = {
                    if (nombre.isBlank()) Text("El nombre es obligatorio")
                },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = edad,
                onValueChange = { edad = it },
                label = { Text("Edad") },
                // REQUISITO: KeyboardOptions para mejorar la experiencia de usuario
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                ),
                modifier = Modifier.fillMaxWidth()
            )
            // Contenedor del menú desplegable
            ExposedDropdownMenuBox(
                expanded = menuExpandido,
                onExpandedChange = { menuExpandido = !menuExpandido },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = tipoEntrada,
                    onValueChange = {},
                    readOnly = true, // Evita que el usuario escriba manualmente [cite: 24]
                    label = { Text("Tipo de Entrada") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuExpandido) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = menuExpandido,
                    onDismissRequest = { menuExpandido = false }
                ) {
                    opcionesEntrada.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                tipoEntrada = opcion
                                menuExpandido = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }

            Button(
                onClick = {
                    realizarRegistro(nombre, edad, tipoEntrada, { t ->
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