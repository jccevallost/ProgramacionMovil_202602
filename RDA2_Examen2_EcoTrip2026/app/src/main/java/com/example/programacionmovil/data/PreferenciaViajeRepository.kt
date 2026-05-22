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
                nombreViajero = preferences[Keys.NOMBRE_VIAJERO].orEmpty(),
                destino = preferences[Keys.DESTINO].orEmpty(),
                diasDuracion = preferences[Keys.DIAS_DURACION] ?: 0,
                tipoTransporte = TipoTransporteEcologico.fromName(preferences[Keys.TIPO_TRANSPORTE]),
                soloRutasBajaHuella = preferences[Keys.SOLO_RUTAS_BAJA_HUELLA] ?: true
            )
        }

    suspend fun guardar(preferencia: PreferenciaViaje) {
        dataStore.edit { preferences ->
            preferences[Keys.NOMBRE_VIAJERO] = preferencia.nombreViajero.trim()
            preferences[Keys.DESTINO] = preferencia.destino.trim()
            preferences[Keys.DIAS_DURACION] = preferencia.diasDuracion
            preferences[Keys.TIPO_TRANSPORTE] = preferencia.tipoTransporte.name
            preferences[Keys.SOLO_RUTAS_BAJA_HUELLA] = preferencia.soloRutasBajaHuella
        }
    }

    private object Keys {
        val NOMBRE_VIAJERO = stringPreferencesKey("nombre_viajero")
        val DESTINO = stringPreferencesKey("destino")
        val DIAS_DURACION = intPreferencesKey("dias_duracion")
        val TIPO_TRANSPORTE = stringPreferencesKey("tipo_transporte")
        val SOLO_RUTAS_BAJA_HUELLA = booleanPreferencesKey("solo_rutas_baja_huella")
    }
}
