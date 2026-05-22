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
import androidx.compose.material3.TextButton
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
import com.example.programacionmovil.data.TipoTransporteEcologico
import com.example.programacionmovil.data.generarRecomendacionRuta
import com.example.programacionmovil.navigation.ResumenRutaRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioViajeScreen(
    state: EcoTripUiState,
    onNombreViajeroChange: (String) -> Unit,
    onDestinoChange: (String) -> Unit,
    onDiasDuracionChange: (String) -> Unit,
    onTipoTransporteChange: (TipoTransporteEcologico) -> Unit,
    onSoloRutasBajaHuellaChange: (Boolean) -> Unit,
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
                        text = "Configuracion del viaje",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Estado resiliente ante rotacion y muerte del proceso",
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
                            value = state.nombreViajero,
                            onValueChange = onNombreViajeroChange,
                            label = { Text("Nombre del viajero") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            isError = state.intentoEnvio && !state.nombreValido,
                            supportingText = {
                                ErrorText(
                                    visible = state.intentoEnvio && !state.nombreValido,
                                    text = "Ingrese el nombre"
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
                                    text = "Ingrese el destino"
                                )
                            }
                        )
                        OutlinedTextField(
                            value = state.diasDuracionTexto,
                            onValueChange = onDiasDuracionChange,
                            label = { Text("Duracion en dias") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            isError = state.intentoEnvio && !state.diasValidos,
                            supportingText = {
                                ErrorText(
                                    visible = state.intentoEnvio && !state.diasValidos,
                                    text = "Ingrese un valor entre 1 y 30"
                                )
                            }
                        )
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
                            text = "Transporte ecologico",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TipoTransporteEcologico.entries.forEach { transporte ->
                                FilterChip(
                                    selected = state.tipoTransporte == transporte,
                                    onClick = { onTipoTransporteChange(transporte) },
                                    label = { Text(transporte.etiqueta) }
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
                                    text = "Solo rutas de baja huella",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Text(
                                    text = "Preferencia global persistida en disco",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.78f)
                                )
                            }
                            Switch(
                                checked = state.soloRutasBajaHuella,
                                onCheckedChange = onSoloRutasBajaHuellaChange
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
    onEditar: () -> Unit
) {
    val preferencia = PreferenciaViaje(
        nombreViajero = route.nombreViajero,
        destino = route.destino,
        diasDuracion = route.diasDuracion,
        tipoTransporte = TipoTransporteEcologico.fromName(route.tipoTransporte),
        soloRutasBajaHuella = route.soloRutasBajaHuella
    )
    val recomendacion = generarRecomendacionRuta(preferencia)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Resumen tipado",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onTertiaryContainer
                ),
                actions = {
                    TextButton(onClick = onEditar) {
                        Text("Salir")
                    }
                }
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
                            text = preferencia.tipoTransporte.descripcion,
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
                        value = preferencia.diasDuracion.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        label = "CO2",
                        value = "${recomendacion.emisionesKg} kg",
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        label = "Ahorro",
                        value = "${recomendacion.ahorroEstimadoKg} kg",
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
                            headlineContent = { Text("Viajero") },
                            supportingContent = { Text(preferencia.nombreViajero) }
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        ListItem(
                            headlineContent = { Text("Destino") },
                            supportingContent = { Text(preferencia.destino) }
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        ListItem(
                            headlineContent = { Text("Transporte") },
                            supportingContent = { Text(preferencia.tipoTransporte.etiqueta) }
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        ListItem(
                            headlineContent = { Text("Rutas de baja huella") },
                            supportingContent = {
                                Text(if (preferencia.soloRutasBajaHuella) "Activadas" else "Opcionales")
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
