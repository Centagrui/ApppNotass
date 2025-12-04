package com.example.proyectofinal

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.proyectofinal.ui.TareasNotasViewModel
import com.example.proyectofinal.ui.PrincipalLayout
import com.example.proyectofinal.ui.Agregar
import com.example.proyectofinal.ui.AppViewModelProvider
import com.example.proyectofinal.ui.Buscar
import com.example.proyectofinal.ui.Editar
import com.example.proyectofinal.ui.ItemLayout
import com.example.proyectofinal.ui.editNotificaciones
import com.example.proyectofinal.ui.notificaciones
import com.example.proyectofinal.ui.notificacionesSimples

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(startTaskId: String? = null) {

    val navController = rememberNavController()

    val tareasNotasViewModel: TareasNotasViewModel =
        viewModel(factory = AppViewModelProvider.Factory)

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf(
        stringResource(id = R.string.tarea) + "s",
        stringResource(id = R.string.nota) + "s"
    )

    var hasNavigatedFromNotification by remember { mutableStateOf(false) }

    LaunchedEffect(startTaskId) {
        if (startTaskId != null && !hasNavigatedFromNotification) {
            hasNavigatedFromNotification = true
            navController.navigate("itemId/$startTaskId")
        }
    }

    NavHost(
        navController = navController,
        startDestination = "principal"
    ) {

        composable("principal") {
            PrincipalLayout(
                navController = navController,
                tareasNotasViewModel = tareasNotasViewModel,
                tabs = tabs,
                onTabSelected = { selectedTabIndex = it }
            )
        }

        composable("agregar") {
            Agregar(navController, tareasNotasViewModel)
        }

        composable("buscar") {
            Buscar(navController, tareasNotasViewModel, tabs, selectedTabIndex) {
                selectedTabIndex = it
            }
        }

        composable("itemEditar/{itemId}") { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: return@composable
            Editar(navController, tareasNotasViewModel, itemId)
        }

        composable("itemId/{itemId}") { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: return@composable
            ItemLayout(navController, tareasNotasViewModel, itemId)
        }

        composable("notificaciones") {
            notificaciones(navController, tareasNotasViewModel)
        }

        composable("editarNotificaciones") {
            editNotificaciones(navController, tareasNotasViewModel)
        }

        composable("notificacionesSimples") {
            notificacionesSimples(navController, tareasNotasViewModel)
        }
    }
}
