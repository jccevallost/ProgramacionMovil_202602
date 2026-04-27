package com.example.programacionmovil

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.programacionmovil.data.UserPreferences
import com.example.programacionmovil.ui.FormViewModel
import com.example.programacionmovil.ui.theme.ProgramacionMovilTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userPrefs = UserPreferences(applicationContext)

        enableEdgeToEdge()

        val viewModel: FormViewModel by viewModels {
            object : ViewModelProvider.Factory {

                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras
                ): T {
                    return FormViewModel(
                        stateHandle = extras.createSavedStateHandle(),
                        userPrefs = userPrefs
                    ) as T
                }
            }
        }

        setContent {
            ProgramacionMovilTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ResilientFormScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun ResilientFormScreen(viewModel: FormViewModel) {

    val nameFromDisk by viewModel.nombreDesdeDisco.observeAsState("")
    var nameInput by remember(nameFromDisk) {
        mutableStateOf(nameFromDisk)
    }

    BackHandler(enabled = nameInput.isNotEmpty() || viewModel.email.isNotEmpty()) {
        Log.d("NAV", "El usuario intentó retroceder")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(24.dp)
    ) {
        Text(
            text = "Borrador de Perfil",
            style = MaterialTheme.typography.headlineMedium
        )

        if (viewModel.saveSuccess) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "☁️ Guardado correctamente",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nameInput,
            onValueChange = {
                nameInput = it
                viewModel.guardarNombre(it)
            },
            label = { Text("Nombre Completo") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = viewModel.email,
            onValueChange = { viewModel.updateEmail(it) },
            label = { Text("Correo Electrónico") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}