package com.example.programacionmovil.ui.concurrency

sealed class SimulationUiState {
    data object Idle : SimulationUiState()
    data object Loading : SimulationUiState()
    data class Success(val dataMessage: String) : SimulationUiState()
    data class Error(val errorMessage: String) : SimulationUiState()
}
