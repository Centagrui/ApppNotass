package com.example.proyectofinal

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.proyectofinal.ui.theme.ProyectoFinalTheme

class MainActivity : ComponentActivity() {

    private var startTaskId: String? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startTaskId = intent.getStringExtra("TASK_ID")

        setContent {
            ProyectoFinalTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppNavigation(startTaskId = startTaskId)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val newId = intent.getStringExtra("TASK_ID")
        if (newId != null) {
            startTaskId = newId
        }
    }
}




/*var Titulos = listOf("Terminar el diseño", "Jugar fortnite", "Jugar carreritas GTA")
var Fechas = listOf("06-10-2024", "08-10-2024", "15-10-2024")

@Composable
fun PrincipalLayout(){
    Scaffold(
        topBar =
        {
            MyTopBar()
        },
        floatingActionButton = {
            BotonFlotante()
        }
    ) { paddingValues ->
        // Contenido principal de la pantalla
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            //var buscarText by remember { mutableStateOf("") }
            Column(
                modifier = Modifier
                    //.padding(20.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            )
            {
                FilterButton()
                BoxTarea("Hacer el diseño", "06-10-2024")
                BoxTarea("Jugar fornais", "08-10-2024")
            }
        }
    }
}

@Composable
fun BoxTarea(
    Titulo : String,
    Fecha : String ,// Cambiarlo despues por el parametro del DatePicker o algo asi,
    onCardClick: () -> Unit = {}
)
{
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onCardClick() }, //Clicable
        shape = RoundedCornerShape(16.dp), //Bordes redondeados
        elevation = CardDefaults.cardElevation(8.dp) //Sombra a la Card
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = Titulo,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = Fecha,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }


            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón para marcar la tarea como completada
                IconButton(
                    onClick = { /* Acción de marcar como completada */ },
                    modifier = Modifier.size(50.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = "Marcar tarea como lista",
                        modifier = Modifier.size(36.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                // Botón para eliminar la tarea
                IconButton(
                    onClick = { /* Acción de eliminar la tarea */ },
                    modifier = Modifier.size(50.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar tarea",
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }
    }

}

@Composable
fun MyTopBar() {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* Acción */ }) {
                    Icon(Icons.Filled.Menu, contentDescription = "Menu")
                }
                Text("Itsur Notes")
            }
        },
        actions = {
            IconButton(onClick = { /* Acción */ }) {
                Icon(Icons.Filled.Search, contentDescription = "Buscar")
            }
            IconButton(onClick = { /* Acción */ }) {
                Icon(Icons.Filled.Settings, contentDescription = "Configuracion")
            }
        }
    )
}



@Composable
fun BotonFlotante()
{
    FloatingActionButton(
        onClick =  {},
        containerColor = Color.Blue,
        contentColor = Color.White
    )
    {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Agregar"
        )
    }

}

@Composable
fun FilterButton() {
    var expanded by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf(0) }
    val items = listOf("Titulo", "Fecha de creación", "Fecha de modificación")
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End

        ) {
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Filled.List, contentDescription = "Filtrado")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = {expanded = false}
            )
            {
                items.forEachIndexed(){index, item ->
                    DropdownMenuItem(
                        text = {Text(text = item)},
                        onClick = { selectedFilter = index
                        expanded = false
                        })
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        )
        {
            Text(text = "Filtrando por: " + items[selectedFilter])
        }
    }

}




@Preview(showBackground = true)
@Composable
fun ProyectoFinalPreview() {
    ProyectoFinalTheme {
        PrincipalLayout()
    }
}*/