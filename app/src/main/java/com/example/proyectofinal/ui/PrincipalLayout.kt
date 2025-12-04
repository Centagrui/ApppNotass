package com.example.proyectofinal.ui

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyectofinal.R
import com.example.proyectofinal.data.Nota
import com.example.proyectofinal.data.Tarea
import androidx.compose.ui.Alignment

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrincipalLayout(
    navController: NavController,
    tareasNotasViewModel: TareasNotasViewModel,
    tabs: List<String>,
    onTabSelected: (Int) -> Unit,
) {
    val filtros = listOf(
        stringResource(R.string.fecha_de_vencimiento),
        stringResource(R.string.fecha_de_creacion),
        stringResource(R.string.titulo)
    )

    val selectedTabIndex = rememberSaveable { mutableStateOf(0) }
    val filtroSeleccionado = rememberSaveable { mutableStateOf(filtros[0]) }
    val mostrarCompletadas = rememberSaveable { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val isLargeScreen = screenWidth > 600

    Scaffold(
        topBar = {
            if (!isLargeScreen) {
                TopBar(
                    navController,
                    stringResource(id = R.string.app_name) + " 📝",
                    onSearchClick = {
                        navController.navigate("buscar")
                    }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("agregar") },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar")
            }
        }
    ) { paddingValues ->

        Content(
            navController = navController,
            tareasNotasViewModel = tareasNotasViewModel,
            selectedTabIndex = selectedTabIndex.value,
            filtroSeleccionado = filtroSeleccionado.value,
            mostrarCompletadas = mostrarCompletadas.value,
            onTabSelected = {
                selectedTabIndex.value = it
                onTabSelected(it)
            },
            onFiltroSeleccionadoChange = { filtroSeleccionado.value = it },
            onMostrarCompletadasChange = { mostrarCompletadas.value = it },
            paddingValues = paddingValues,
            isLargeScreen = isLargeScreen
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Content(
    navController: NavController,
    tareasNotasViewModel: TareasNotasViewModel,
    selectedTabIndex: Int,
    filtroSeleccionado: String,
    mostrarCompletadas: Boolean,
    onTabSelected: (Int) -> Unit,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    onFiltroSeleccionadoChange: (String) -> Unit,
    onMostrarCompletadasChange: (Boolean) -> Unit,
    isLargeScreen: Boolean = false
) {

    val filtros = listOf(
        stringResource(R.string.fecha_de_vencimiento),
        stringResource(R.string.fecha_de_creacion),
        stringResource(R.string.titulo)
    )

    val uiState = tareasNotasViewModel.uiState
    val configuration = LocalConfiguration.current
    val isVertical = configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
    ) {

        if (!isLargeScreen) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                listOf(
                    stringResource(R.string.tareas),
                    stringResource(R.string.notas)
                ).forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = {
                            onTabSelected(index)
                            onFiltroSeleccionadoChange(if (index == 0) filtros[0] else filtros[1])
                        },
                        text = { Text(title) }
                    )
                }
            }
        }

        if (isVertical) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterButton(
                    tabIndex = selectedTabIndex,
                    filtroSeleccionado = filtroSeleccionado,
                    onFilterSelected = onFiltroSeleccionadoChange,
                )

                if (selectedTabIndex == 0) {
                    Button(onClick = { onMostrarCompletadasChange(!mostrarCompletadas) }) {
                        Text(
                            text = if (mostrarCompletadas)
                                stringResource(R.string.ver_tareas_completadas)
                            else stringResource(R.string.ver_tareas_pendientes)
                        )
                    }
                }
            }
        }

        val itemsFiltrados = when (selectedTabIndex) {
            0 -> tareasNotasViewModel.obtenerItemsFiltrados(filtroSeleccionado, 0, mostrarCompletadas)
            1 -> tareasNotasViewModel.obtenerItemsFiltrados(filtroSeleccionado, 1, mostrarCompletadas)
            else -> uiState.tareas + uiState.notas
        }

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(itemsFiltrados) { item ->
                when (item) {

                    is Tarea -> {
                        BoxTarea(
                            tarea = item,
                            onCardClick = {

                                // CORRECCIÓN ⚡ ABRIR POR ID REAL
                                navController.navigate("itemId/${item.id}")
                            },
                            onComplete = {
                                tareasNotasViewModel.completarTarea(item)
                            },
                            onEdit = {
                                tareasNotasViewModel.procesarTarea(item)
                                navController.navigate("itemEditar/${item.id}")
                            },
                            onDelete = {
                                tareasNotasViewModel.eliminarItem(item)
                            }
                        )
                    }

                    is Nota -> {
                        BoxNota(
                            nota = item,
                            onCardClick = {
                                // CORRECCIÓN ⚡ ABRIR POR ID REAL
                                navController.navigate("itemId/${item.id}")
                            },
                            onEdit = {
                                tareasNotasViewModel.procesarNota(item)
                                navController.navigate("itemEditar/${item.id}")
                            },
                            onDelete = {
                                tareasNotasViewModel.eliminarItem(item)
                            }
                        )
                    }
                }
            }
        }
    }
}
