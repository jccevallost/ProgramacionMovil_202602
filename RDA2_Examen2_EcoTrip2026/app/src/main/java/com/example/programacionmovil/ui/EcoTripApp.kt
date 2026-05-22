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
                onNombreViajeroChange = viewModel::actualizarNombreViajero,
                onDestinoChange = viewModel::actualizarDestino,
                onDiasDuracionChange = viewModel::actualizarDiasDuracion,
                onTipoTransporteChange = viewModel::actualizarTipoTransporte,
                onSoloRutasBajaHuellaChange = viewModel::actualizarSoloRutasBajaHuella,
                onFormularioInvalido = viewModel::marcarFormularioInvalido,
                onMensajeMostrado = viewModel::consumirMensaje,
                onPlanificar = { preferencia ->
                    viewModel.guardarPreferencia(preferencia)
                    navController.navigate(
                        ResumenRutaRoute(
                            nombreViajero = preferencia.nombreViajero,
                            destino = preferencia.destino,
                            diasDuracion = preferencia.diasDuracion,
                            tipoTransporte = preferencia.tipoTransporte.name,
                            soloRutasBajaHuella = preferencia.soloRutasBajaHuella
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
