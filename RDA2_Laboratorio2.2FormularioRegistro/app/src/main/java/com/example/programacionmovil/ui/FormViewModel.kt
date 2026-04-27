// Archivo: ui/FormViewModel.kt
package com.example.programacionmovil.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.programacionmovil.data.UserPreferences
import kotlinx.coroutines.launch

class FormViewModel(
    private val stateHandle: SavedStateHandle,
    private val userPrefs: UserPreferences
) : ViewModel() {

    var email by mutableStateOf(stateHandle.get<String>("email_key") ?: "")
        private set

    val nombreDesdeDisco = userPrefs.userName.asLiveData()

    fun updateEmail(newEmail: String) {
        email = newEmail
        stateHandle["email_key"] = newEmail
    }

    fun guardarNombre(newName: String) {
        viewModelScope.launch {
            userPrefs.saveName(newName)
        }
    }
}