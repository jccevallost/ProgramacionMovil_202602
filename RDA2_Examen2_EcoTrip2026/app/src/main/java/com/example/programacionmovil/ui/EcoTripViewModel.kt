package com.example.programacionmovil.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.programacionmovil.data.PreferenciaViaje
import com.example.programacionmovil.data.PreferenciaViajeRepository
import com.example.programacionmovil.data.TipoTransporteEcologico
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EcoTripViewModel(
    private val repository: PreferenciaViajeRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val guardando = MutableStateFlow(false)
    private val mensaje = MutableStateFlow<String?>(null)

    private val camposTexto = combine(
        savedStateHandle.getStateFlow(KEY_NOMBRE_VIAJERO, ""),
        savedStateHandle.getStateFlow(KEY_DESTINO, ""),
        savedStateHandle.getStateFlow(KEY_DIAS_DURACION, "")
    ) { nombreViajero, destino, diasDuracion ->
        CamposTexto(nombreViajero, destino, diasDuracion)
    }

    private val seleccion = combine(
        savedStateHandle.getStateFlow(KEY_TIPO_TRANSPORTE, TipoTransporteEcologico.Tren.name),
        savedStateHandle.getStateFlow(KEY_SOLO_RUTAS_BAJA_HUELLA, true),
        savedStateHandle.getStateFlow(KEY_INTENTO_ENVIO, false)
    ) { tipoTransporte, soloRutasBajaHuella, intentoEnvio ->
        Seleccion(
            tipoTransporte = TipoTransporteEcologico.fromName(tipoTransporte),
            soloRutasBajaHuella = soloRutasBajaHuella,
            intentoEnvio = intentoEnvio
        )
    }

    val uiState = combine(camposTexto, seleccion, guardando, mensaje) {
            campos,
            seleccion,
            guardando,
            mensaje ->
        EcoTripUiState(
            nombreViajero = campos.nombreViajero,
            destino = campos.destino,
            diasDuracionTexto = campos.diasDuracion,
            tipoTransporte = seleccion.tipoTransporte,
            soloRutasBajaHuella = seleccion.soloRutasBajaHuella,
            guardando = guardando,
            intentoEnvio = seleccion.intentoEnvio,
            mensaje = mensaje
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = EcoTripUiState()
    )

    init {
        viewModelScope.launch {
            val preferencia = repository.preferenciaViaje.first()
            if (debeRestaurarDesdeDisco(preferencia)) {
                savedStateHandle[KEY_NOMBRE_VIAJERO] = preferencia.nombreViajero
                savedStateHandle[KEY_DESTINO] = preferencia.destino
                savedStateHandle[KEY_DIAS_DURACION] = preferencia.diasDuracion.toString()
                savedStateHandle[KEY_TIPO_TRANSPORTE] = preferencia.tipoTransporte.name
                savedStateHandle[KEY_SOLO_RUTAS_BAJA_HUELLA] = preferencia.soloRutasBajaHuella
            }
            savedStateHandle[KEY_RESTAURADO_DESDE_DISCO] = true
        }
    }

    fun actualizarNombreViajero(value: String) {
        savedStateHandle[KEY_NOMBRE_VIAJERO] = value.take(MAX_TEXTO_CORTO)
    }

    fun actualizarDestino(value: String) {
        savedStateHandle[KEY_DESTINO] = value.take(MAX_TEXTO_CORTO)
    }

    fun actualizarDiasDuracion(value: String) {
        savedStateHandle[KEY_DIAS_DURACION] = value.filter(Char::isDigit).take(MAX_DIAS_DIGITOS)
    }

    fun actualizarTipoTransporte(value: TipoTransporteEcologico) {
        savedStateHandle[KEY_TIPO_TRANSPORTE] = value.name
    }

    fun actualizarSoloRutasBajaHuella(value: Boolean) {
        savedStateHandle[KEY_SOLO_RUTAS_BAJA_HUELLA] = value
    }

    fun guardarPreferencia(preferencia: PreferenciaViaje) {
        savedStateHandle[KEY_INTENTO_ENVIO] = true
        viewModelScope.launch {
            guardando.value = true
            runCatching {
                repository.guardar(preferencia)
            }.onSuccess {
                mensaje.value = "Preferencia guardada localmente"
            }.onFailure {
                mensaje.value = "No se pudo guardar en DataStore"
            }
            guardando.value = false
        }
    }

    fun marcarFormularioInvalido() {
        savedStateHandle[KEY_INTENTO_ENVIO] = true
        mensaje.value = "Revise los campos resaltados"
    }

    fun consumirMensaje() {
        mensaje.value = null
    }

    private fun debeRestaurarDesdeDisco(preferencia: PreferenciaViaje): Boolean {
        val yaRestaurado = savedStateHandle[KEY_RESTAURADO_DESDE_DISCO] ?: false
        return !yaRestaurado && preferencia.tieneDatosGuardados && formularioActualVacio()
    }

    private fun formularioActualVacio(): Boolean =
        savedStateHandle.get<String>(KEY_NOMBRE_VIAJERO).isNullOrBlank() &&
            savedStateHandle.get<String>(KEY_DESTINO).isNullOrBlank() &&
            savedStateHandle.get<String>(KEY_DIAS_DURACION).isNullOrBlank()

    private data class CamposTexto(
        val nombreViajero: String,
        val destino: String,
        val diasDuracion: String
    )

    private data class Seleccion(
        val tipoTransporte: TipoTransporteEcologico,
        val soloRutasBajaHuella: Boolean,
        val intentoEnvio: Boolean
    )

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = checkNotNull(this[APPLICATION_KEY])
                EcoTripViewModel(
                    repository = PreferenciaViajeRepository(application),
                    savedStateHandle = createSavedStateHandle()
                )
            }
        }

        private const val KEY_NOMBRE_VIAJERO = "nombre_viajero"
        private const val KEY_DESTINO = "destino"
        private const val KEY_DIAS_DURACION = "dias_duracion"
        private const val KEY_TIPO_TRANSPORTE = "tipo_transporte"
        private const val KEY_SOLO_RUTAS_BAJA_HUELLA = "solo_rutas_baja_huella"
        private const val KEY_INTENTO_ENVIO = "intento_envio"
        private const val KEY_RESTAURADO_DESDE_DISCO = "restaurado_desde_disco"
        private const val MAX_TEXTO_CORTO = 40
        private const val MAX_DIAS_DIGITOS = 2
    }
}
