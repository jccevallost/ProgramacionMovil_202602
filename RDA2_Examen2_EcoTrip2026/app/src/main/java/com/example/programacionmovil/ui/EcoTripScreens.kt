package com.example.programacionmovil.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.programacionmovil.data.PreferenciaViaje
import com.example.programacionmovil.data.PrioridadRuta
import com.example.programacionmovil.data.generarRecomendacionRuta
import com.example.programacionmovil.navigation.ResumenRutaRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioViajeScreen(
    state: EcoTripUiState,
    onOrigenChange: (String) -> Unit,
    onDestinoChange: (String) -> Unit,
    onDiasChange: (String) -> Unit,
    onPresupuestoChange: (String) -> Unit,
    onPrioridadChange: (PrioridadRuta) -> Unit,
    onHospedajeEcoChange: (Boolean) -> Unit,
    onFormularioInvalido: () -> Unit,
    onMensajeMostrado: () -> Unit,
    onPlanificar: (PreferenciaViaje) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.mensaje) {
        state.mensaje?.let { mensaje ->
            snackbarHostState.showSnackbar(mensaje)
            onMensajeMostrado()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "EcoTrip 2026",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    state.preferenciaValida?.let(onPlanificar) ?: onFormularioInvalido()
                },
                containerColor = if (state.esValido) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                },
                contentColor = if (state.esValido) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            ) {
                Text(
                    text = if (state.guardando) "..." else "Ir",
                    modifier = Modifier.padding(horizontal = 10.dp),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Formulario de viaje",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Origen, destino y preferencias locales",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            item {
                ElevatedCard(
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = state.origen,
                            onValueChange = onOrigenChange,
                            label = { Text("Origen") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            isError = state.intentoEnvio && !state.origenValido,
                            supportingText = {
                                ErrorText(
                                    visible = state.intentoEnvio && !state.origenValido,
                                    text = "Ingrese la ciudad de salida"
                                )
                            }
                        )
                        OutlinedTextField(
                            value = state.destino,
                            onValueChange = onDestinoChange,
                            label = { Text("Destino") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            isError = state.intentoEnvio && !state.destinoValido,
                            supportingText = {
                                ErrorText(
                                    visible = state.intentoEnvio && !state.destinoValido,
                                    text = "Ingrese la ciudad de llegada"
                                )
                            }
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = state.diasTexto,
                                onValueChange = onDiasChange,
                                label = { Text("Dias") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                isError = state.intentoEnvio && !state.diasValidos,
                                supportingText = {
                                    ErrorText(
                                        visible = state.intentoEnvio && !state.diasValidos,
                                        text = "1 a 30"
                                    )
                                }
                            )
                            OutlinedTextField(
                                value = state.presupuestoTexto,
                                onValueChange = onPresupuestoChange,
                                label = { Text("USD") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                isError = state.intentoEnvio && !state.presupuestoValido,
                                supportingText = {
                                    ErrorText(
                                        visible = state.intentoEnvio && !state.presupuestoValido,
                                        text = "Min. 50"
                                    )
                                }
                            )
                        }
                    }
                }
            }
            item {
                ElevatedCard(
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "Preferencia de ruta",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PrioridadRuta.entries.forEach { prioridad ->
                                FilterChip(
                                    selected = state.prioridad == prioridad,
                                    onClick = { onPrioridadChange(prioridad) },
                                    label = { Text(prioridad.etiqueta) }
                                )
                            }
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Hospedaje eco",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Text(
                                    text = "Se incluye en el presupuesto",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.78f)
                                )
                            }
                            Switch(
                                checked = state.incluirHospedajeEco,
                                onCheckedChange = onHospedajeEcoChange
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumenRutaScreen(
    route: ResumenRutaRoute,
    state: EcoTripUiState,
    onEditar: () -> Unit
) {
    val preferencia = state.preferenciaValida
    val recomendacion = preferencia?.let(::generarRecomendacionRuta)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Resumen de ruta",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onTertiaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onEditar,
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
            ) {
                Text(
                    text = "Edit",
                    modifier = Modifier.padding(horizontal = 8.dp),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            if (preferencia == null || recomendacion == null) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.errorContainer
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "${route.origen} -> ${route.destino}",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text(
                                text = "Complete los datos para calcular la ruta.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            } else {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = recomendacion.titulo,
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = preferencia.prioridad.descripcion,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MetricCard(
                            label = "Dias",
                            value = preferencia.dias.toString(),
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            label = "Costo",
                            value = "$${recomendacion.costoEstimado}",
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            label = "CO2",
                            value = "${recomendacion.emisionesKg} kg",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    ElevatedCard(
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                        )
                    ) {
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            ListItem(
                                headlineContent = { Text("Distancia estimada") },
                                supportingContent = { Text("${recomendacion.distanciaKm} km") }
                            )
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                            ListItem(
                                headlineContent = { Text("Presupuesto disponible") },
                                supportingContent = { Text("$${preferencia.presupuesto}") }
                            )
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                            ListItem(
                                headlineContent = { Text("Hospedaje eco") },
                                supportingContent = {
                                    Text(if (preferencia.incluirHospedajeEco) "Incluido" else "No incluido")
                                }
                            )
                        }
                    }
                }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Tramos sugeridos",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            recomendacion.segmentos.forEach { segmento ->
                                AssistChip(
                                    onClick = {},
                                    label = { Text(segmento) }
                                )
                            }
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(72.dp))
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ErrorText(
    visible: Boolean,
    text: String
) {
    if (visible) {
        Text(text = text)
    }
}
