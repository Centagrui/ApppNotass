package com.example.proyectofinal.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavController, title: String, onSearchClick: () -> Unit) {
    TopAppBar(
        title = { Text(title) },
        actions = {
            IconButton(onClick = { onSearchClick() }) {
                Icon(Icons.Default.Search, contentDescription = "Buscar")
            }
        },
        modifier = Modifier.padding(4.dp)
    )
}