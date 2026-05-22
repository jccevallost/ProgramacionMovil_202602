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
import com.example.programacionmovil.data.PrioridadRuta
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
        savedStateHandle.getStateFlow(KEY_ORIGEN, ""),
        savedStateHandle.getStateFlow(KEY_DESTINO, ""),
        savedStateHandle.getStateFlow(KEY_DIAS, ""),
        savedStateHandle.getStateFlow(KEY_PRESUPUESTO, "")
    ) { origen, destino, dias, presupuesto ->
        CamposTexto(origen, destino, dias, presupuesto)
    }

    private val seleccion = combine(
        savedStateHandle.getStateFlow(KEY_PRIORIDAD, PrioridadRuta.Ecologica.name),
        savedStateHandle.getStateFlow(KEY_HOSPEDAJE_ECO, true),
        savedStateHandle.getStateFlow(KEY_INTENTO_ENVIO, false)
    ) { prioridad, hospedajeEco, intentoEnvio ->
        Seleccion(
            prioridad = PrioridadRuta.fromName(prioridad),
            incluirHospedajeEco = hospedajeEco,
            intentoEnvio = intentoEnvio
        )
    }

    val uiState = combine(camposTexto, seleccion, guardando, mensaje) {
            campos,
            seleccion,
            guardando,
            mensaje ->
        EcoTripUiState(
            origen = campos.origen,
            destino = campos.destino,
            diasTexto = campos.dias,
            presupuestoTexto = campos.presupuesto,
            prioridad = seleccion.prioridad,
            incluirHospedajeEco = seleccion.incluirHospedajeEco,
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
                savedStateHandle[KEY_ORIGEN] = preferencia.origen
                savedStateHandle[KEY_DESTINO] = preferencia.destino
                savedStateHandle[KEY_DIAS] = preferencia.dias.toString()
                savedStateHandle[KEY_PRESUPUESTO] = preferencia.presupuesto.toString()
                savedStateHandle[KEY_PRIORIDAD] = preferencia.prioridad.name
                savedStateHandle[KEY_HOSPEDAJE_ECO] = preferencia.incluirHospedajeEco
            }
            savedStateHandle[KEY_RESTAURADO_DESDE_DISCO] = true
        }
    }

    fun actualizarOrigen(value: String) {
        savedStateHandle[KEY_ORIGEN] = value.take(MAX_TEXTO_CIUDAD)
    }

    fun actualizarDestino(value: String) {
        savedStateHandle[KEY_DESTINO] = value.take(MAX_TEXTO_CIUDAD)
    }

    fun actualizarDias(value: String) {
        savedStateHandle[KEY_DIAS] = value.filter(Char::isDigit).take(MAX_DIAS_DIGITOS)
    }

    fun actualizarPresupuesto(value: String) {
        savedStateHandle[KEY_PRESUPUESTO] = value.filter(Char::isDigit).take(MAX_PRESUPUESTO_DIGITOS)
    }

    fun actualizarPrioridad(value: PrioridadRuta) {
        savedStateHandle[KEY_PRIORIDAD] = value.name
    }

    fun actualizarHospedajeEco(value: Boolean) {
        savedStateHandle[KEY_HOSPEDAJE_ECO] = value
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
        savedStateHandle.get<String>(KEY_ORIGEN).isNullOrBlank() &&
            savedStateHandle.get<String>(KEY_DESTINO).isNullOrBlank() &&
            savedStateHandle.get<String>(KEY_DIAS).isNullOrBlank() &&
            savedStateHandle.get<String>(KEY_PRESUPUESTO).isNullOrBlank()

    private data class CamposTexto(
        val origen: String,
        val destino: String,
        val dias: String,
        val presupuesto: String
    )

    private data class Seleccion(
        val prioridad: PrioridadRuta,
        val incluirHospedajeEco: Boolean,
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

        private const val KEY_ORIGEN = "origen"
        private const val KEY_DESTINO = "destino"
        private const val KEY_DIAS = "dias"
        private const val KEY_PRESUPUESTO = "presupuesto"
        private const val KEY_PRIORIDAD = "prioridad"
        private const val KEY_HOSPEDAJE_ECO = "hospedaje_eco"
        private const val KEY_INTENTO_ENVIO = "intento_envio"
        private const val KEY_RESTAURADO_DESDE_DISCO = "restaurado_desde_disco"
        private const val MAX_TEXTO_CIUDAD = 40
        private const val MAX_DIAS_DIGITOS = 2
        private const val MAX_PRESUPUESTO_DIGITOS = 6
    }
}
