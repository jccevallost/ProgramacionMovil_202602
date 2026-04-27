package com.example.programacionmovil.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import com.example.programacionmovil.data.UserPreferences
import kotlinx.coroutines.flow.map

//
private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    companion object{
        val USER_NAME_KEY = stringPreferencesKey("user_name")
    }

    //Leer el nombre (Flow que emite cambios en tiempo real)
    val userName: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[USER_NAME_KEY] ?: ""
    }

    //Guardar el nombre
    suspend fun saveName(name: String){
        context.dataStore.edit { prefs ->
            prefs[USER_NAME_KEY] = name
        }
    }
}

