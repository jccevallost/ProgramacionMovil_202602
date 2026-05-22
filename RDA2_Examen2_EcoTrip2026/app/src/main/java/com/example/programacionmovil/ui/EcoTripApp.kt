package com.example.programacionmovil.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.programacionmovil.navigation.FormularioViajeRoute
import com.example.programacionmovil.navigation.ResumenRutaRoute

@Composable
fun EcoTripApp(viewModel: EcoTripViewModel) {
    val navController = rememberNavController()
    val state by viewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = FormularioViajeRoute
    ) {
        composable<FormularioViajeRoute> {
            FormularioViajeScreen(
                state = state,
                onOrigenChange = viewModel::actualizarOrigen,
                onDestinoChange = viewModel::actualizarDestino,
                onDiasChange = viewModel::actualizarDias,
                onPresupuestoChange = viewModel::actualizarPresupuesto,
                onPrioridadChange = viewModel::actualizarPrioridad,
                onHospedajeEcoChange = viewModel::actualizarHospedajeEco,
                onFormularioInvalido = viewModel::marcarFormularioInvalido,
                onMensajeMostrado = viewModel::consumirMensaje,
                onPlanificar = { preferencia ->
                    viewModel.guardarPreferencia(preferencia)
                    navController.navigate(
                        ResumenRutaRoute(
                            origen = preferencia.origen,
                            destino = preferencia.destino
                        )
                    ) {
                        launchSingleTop = true
                        popUpTo<FormularioViajeRoute> {
                            saveState = true
                        }
                    }
                }
            )
        }
        composable<ResumenRutaRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<ResumenRutaRoute>()
            ResumenRutaScreen(
                route = route,
                state = state,
                onEditar = {
                    navController.navigate(FormularioViajeRoute) {
                        launchSingleTop = true
                        popUpTo<FormularioViajeRoute> {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}
