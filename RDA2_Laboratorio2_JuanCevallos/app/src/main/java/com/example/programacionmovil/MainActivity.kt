package com.example.programacionmovil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.programacionmovil.ui.theme.ProgramacionMovilTheme

class MainActivity : ComponentActivity() {
    private val TAG = "LIFECYCLE_DEBUG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: La Activity se está creando")
        setContent {
            // 1. Variable local efímera usando 'remember'
            // Esta variable solo vive mientras la Activity no se destruya.
            var localCounter by remember { mutableStateOf(0) }
            // 2. Interfaz básica para la prueba [cite: 55, 59]
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Contador Local: $localCounter",
                    style = MaterialTheme.typography.headlineLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { localCounter++ }) {
                    Text("Incrementar")
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: La Activity es visible")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: La Activity tiene el foco (interactuable)")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: La Activity pierde el foco")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: La Activity ya no es visible")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: La Activity va a ser destruida")
    }
}



