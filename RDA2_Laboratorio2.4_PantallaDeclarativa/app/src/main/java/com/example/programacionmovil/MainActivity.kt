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
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Soporte para pantalla completa
        setContent {
            MaterialTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun LoginScreen(modifier: Modifier = Modifier) {
    // 1. Definición del Estado
    var username by remember { mutableStateOf("") }

    // 2. Lógica Declarativa (Se recalcula automáticamente)
    val isValid = username.length >= 3
    val message = if (isValid) "Nombre aceptado" else "Nombre muy corto (min. 3)"
    val color = if (isValid) Color(0xFF2E7D32) else Color.Red

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Campo de texto vinculado al estado 'username'
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Nombre de Usuario") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Mensaje dinámico
        Text(
            text = message,
            color = color,
            style = MaterialTheme.typography.bodyMedium
        )

        @Composable
        fun ChallengeResolution(username: String) {
            val isButtonEnabled = username.length >= 5 // Condición del reto

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* Acción de login */ },
                enabled = isButtonEnabled // El botón reacciona al estado
            ) {
                Text("Ingresar")
            }
        }
    }
}