package com.example.programacionmovil.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.ecoTripDataStore by preferencesDataStore(name = "ecotrip_preferencias")

class PreferenciaViajeRepository(context: Context) {
    private val dataStore: DataStore<Preferences> = context.applicationContext.ecoTripDataStore

    val preferenciaViaje: Flow<PreferenciaViaje> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            PreferenciaViaje(
                origen = preferences[Keys.ORIGEN].orEmpty(),
                destino = preferences[Keys.DESTINO].orEmpty(),
                dias = preferences[Keys.DIAS] ?: 0,
                presupuesto = preferences[Keys.PRESUPUESTO] ?: 0,
                prioridad = PrioridadRuta.fromName(preferences[Keys.PRIORIDAD]),
                incluirHospedajeEco = preferences[Keys.HOSPEDAJE_ECO] ?: true
            )
        }

    suspend fun guardar(preferencia: PreferenciaViaje) {
        dataStore.edit { preferences ->
            preferences[Keys.ORIGEN] = preferencia.origen.trim()
            preferences[Keys.DESTINO] = preferencia.destino.trim()
            preferences[Keys.DIAS] = preferencia.dias
            preferences[Keys.PRESUPUESTO] = preferencia.presupuesto
            preferences[Keys.PRIORIDAD] = preferencia.prioridad.name
            preferences[Keys.HOSPEDAJE_ECO] = preferencia.incluirHospedajeEco
        }
    }

    private object Keys {
        val ORIGEN = stringPreferencesKey("origen")
        val DESTINO = stringPreferencesKey("destino")
        val DIAS = intPreferencesKey("dias")
        val PRESUPUESTO = intPreferencesKey("presupuesto")
        val PRIORIDAD = stringPreferencesKey("prioridad")
        val HOSPEDAJE_ECO = booleanPreferencesKey("hospedaje_eco")
    }
}
