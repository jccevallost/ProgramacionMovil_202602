package com.example.programacionmovil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class Asistente(val nombre: String, val edad: Int?, val tipo: String)

// --- SEALED CLASS MODIFICADA ---
sealed class RegistroState {
    object Idle : RegistroState()
    object Loading : RegistroState()
    data class Error(val mensaje: String) : RegistroState()

    // Recibe la función de orden superior como parámetro 'procesador'
    data class Success(
        val asistente: Asistente,
        val entrada: String,
        val procesador: (String, (String) -> String) -> String
    ) : RegistroState()
}

// Higher-Order Function (La que pide el punto 4)
fun procesarValidacionEspecial(tipo: String, validadorVip: (String) -> String): String {
    return validadorVip(tipo)
}

// Extension Function (Punto 2)
fun Int?.esMayorDeEdad(): String = if (this != null && this >= 18) "Es mayor de edad" else "Es menor de edad"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { SafePassScreen() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafePassScreen() {
    var nombre by remember { mutableStateOf("") }
    var edadTexto by remember { mutableStateOf("") }
    var tipoEntrada by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf<RegistroState>(RegistroState.Idle) }
    val scope = rememberCoroutineScope()

    Scaffold(topBar = { TopAppBar(title = { Text("SafePass 2026 - Inyección HOF") }) }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = edadTexto, onValueChange = { edadTexto = it }, label = { Text("Edad") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = tipoEntrada, onValueChange = { tipoEntrada = it }, label = { Text("Categoría") }, modifier = Modifier.fillMaxWidth())

            Button(
                onClick = {
                    estado = RegistroState.Loading
                    scope.launch {
                        delay(1000)
                        val edadNum = edadTexto.toIntOrNull()
                        if (nombre.isNotBlank() && edadNum != null) {
                            // MANDAMOS LA FUNCIÓN SIN EJECUTARLA TODAVÍA
                            estado = RegistroState.Success(
                                asistente = Asistente(nombre, edadNum, tipoEntrada),
                                entrada = tipoEntrada,
                                procesador = ::procesarValidacionEspecial // Referencia a la función de orden superior
                            )
                        } else {
                            estado = RegistroState.Error("Datos inválidos")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Registrar") }

            when (val s = estado) {
                is RegistroState.Loading -> CircularProgressIndicator()
                is RegistroState.Success -> {

                    val resultadoHOF = s.procesador(s.entrada) { entrada ->
                        when (entrada.uppercase().trim()) {
                            "VIP" -> "ACCESO PRIORITARIO"
                            "GENERAL" -> "ACCESO ESTANDAR"
                            else -> "ACCESO ESTANDAR: $entrada"
                        }
                    }

                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("¡Registro Exitoso!", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                            Text("Asistente: ${s.asistente.nombre}")


                            Text(text = "$resultadoHOF | ${s.asistente.edad.esMayorDeEdad()}", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                is RegistroState.Error -> Text(s.mensaje, color = Color.Red)
                else -> {}
            }
        }
    }
}