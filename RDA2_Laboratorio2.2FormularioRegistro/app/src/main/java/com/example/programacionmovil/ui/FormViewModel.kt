package com.example.programacionmovil.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.programacionmovil.data.UserPreferences
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FormViewModel(
    private val stateHandle: SavedStateHandle,
    private val userPrefs: UserPreferences
) : ViewModel() {

    var email by mutableStateOf(stateHandle.get<String>("email_key") ?: "")
        private set

    var saveSuccess by mutableStateOf(false)
        private set

    private var saveIndicatorJob: Job? = null

    val nombreDesdeDisco = userPrefs.userName.asLiveData()

    init {
        viewModelScope.launch {
            userPrefs.userEmail.collectLatest { emailGuardado ->
                if (emailGuardado.isNotEmpty()) {
                    email = emailGuardado
                    stateHandle["email_key"] = emailGuardado
                }
            }
        }
    }

    fun updateEmail(newEmail: String) {
        email = newEmail
        stateHandle["email_key"] = newEmail

        viewModelScope.launch {
            userPrefs.saveEmail(newEmail)
            mostrarIndicadorGuardado()
        }
    }

    fun guardarNombre(newName: String) {
        viewModelScope.launch {
            userPrefs.saveName(newName)
            mostrarIndicadorGuardado()
        }
    }

    private fun mostrarIndicadorGuardado() {
        saveIndicatorJob?.cancel()

        saveIndicatorJob = viewModelScope.launch {
            saveSuccess = true
            delay(1000)
            saveSuccess = false
        }
    }
}