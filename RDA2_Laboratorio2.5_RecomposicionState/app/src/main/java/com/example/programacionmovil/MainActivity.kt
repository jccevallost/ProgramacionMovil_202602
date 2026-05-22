package com.example.programacionmovil


import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SmartHome()
                }
            }
        }
    }
}

@Composable
fun SmartHome() {
    // Trazabilidad del Padre
    Log.d("RECOMPOSE", "DIBUJANDO PADRE (Smart Home)")

    // State Hoisting: El padre posee la "Fuente Única de Verdad"
    var isLightOn by remember { mutableStateOf(false) }

    // Estado  (Skipping)
    var refreshCount by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Centro de Control Domótico",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Enviamos el estado AL HIJO
        LightBulb(isOn = isLightOn)

        Spacer(modifier = Modifier.height(32.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Interruptor Principal")
            Switch(
                checked = isLightOn,
                onCheckedChange = { isLightOn = it }
            )
        }

        // --- RESOLUCIÓN DEL DESAFÍO ---
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { refreshCount++ }) {
            Text("Refrescar UI ($refreshCount)")
        }
    }
}

@Composable
fun LightBulb(isOn: Boolean) {
    // Trazabilidad del Hijo
    Log.d("RECOMPOSE", "DIBUJANDO HIJO (LightBulb)")

    Icon(
        imageVector = Icons.Default.Lightbulb,
        contentDescription = "Bombilla",
        modifier = Modifier.size(120.dp),
        tint = if (isOn) Color.Yellow else Color.Gray
    )
}