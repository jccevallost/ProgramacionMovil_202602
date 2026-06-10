package com.example.programacionmovil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.programacionmovil.ui.EcoTripApp
import com.example.programacionmovil.ui.EcoTripViewModel
import com.example.programacionmovil.ui.theme.ProgramacionMovilTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = ViewModelProvider(this, EcoTripViewModel.Factory)[EcoTripViewModel::class.java]
        enableEdgeToEdge()
        setContent {
            ProgramacionMovilTheme {
                EcoTripApp(viewModel = viewModel)
            }
        }
    }
}







