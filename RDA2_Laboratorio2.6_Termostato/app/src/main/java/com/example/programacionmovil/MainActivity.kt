package com.example.programacionmovil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Contenedor principal
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                ThermostatScreen()
            }
        }
    }
}

/**
 * 2. Componente Padre (Stateful)
 * Gestiona el estado de la temperatura y define la lógica.
 */
@Composable
fun ThermostatScreen() {
    // 3. Definición del Estado
    var temperature by remember { mutableIntStateOf(20) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 4. Componente de Visualización (Stateless)
        TemperatureDisplay(temp = temperature)

        Spacer(modifier = Modifier.height(32.dp))

        // 4. Componente de Controles (Stateless)
        TemperatureControls(
            onIncrease = { temperature++ },
            onDecrease = { temperature-- }
        )
    }
}

/**
 * 5. Componente de Visualización
 * Cambia su apariencia (color e icono) según el flujo de datos.
 */
@Composable
fun TemperatureDisplay(temp: Int) {
    // Lógica Declarativa para el diseño
    val isHot = temp >= 25
    val contentColor = if (isHot) Color.Red else Color.Blue
    val icon = if (isHot) Icons.Default.WbSunny else Icons.Default.AcUnit

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(80.dp)
        )
        Text(
            text = "$temp°C",
            fontSize = 48.sp,
            color = contentColor,
            style = MaterialTheme.typography.headlineLarge
        )
    }
}

/**
 * Componente de Controles
 * No posee lógica interna, solo ejecuta lambdas.
 */
@Composable
fun TemperatureControls(onIncrease: () -> Unit, onDecrease: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(onClick = onDecrease) {
            Text(text = "Bajar -")
        }
        Button(onClick = onIncrease) {
            Text(text = "Subir +")
        }
    }
}