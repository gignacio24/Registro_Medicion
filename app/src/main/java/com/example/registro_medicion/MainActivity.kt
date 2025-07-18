package com.example.registro_medicion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.registro_medicion.entities.Registro
import com.example.registro_medicion.ui.ListaRegistrosViewModel
import com.example.registro_medicion.ui.theme.Registro_MedicionTheme
import java.time.LocalDate


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Registro_MedicionTheme {
                AppRegistrosUI()
            }
        }
    }
}
@Composable
fun AppRegistrosUI(
    navController: NavHostController = rememberNavController(),
    vmListaRegistros: ListaRegistrosViewModel = viewModel(factory = ListaRegistrosViewModel.Factory)
) {
    NavHost(
        navController = navController,
        startDestination = "inicio"
    ) {
        composable("inicio") {
            PantallaListaRegistros(
                registros = vmListaRegistros.registros,
                onAdd = { navController.navigate("form") }
            )
        }
        composable("form") {
            PantallaFormRegistro(
                vmListaRegistros = vmListaRegistros,
                onRegistroExitoso = { navController.popBackStack() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaListaRegistros(
    registros: List<Registro> = listOf(),
    onAdd: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_list)) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAdd) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.add_button)
                )
            }
        }
    ) { paddingValues ->
        if (registros.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay registros aún")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                items(registros) { registro ->
                    RegistroItem(registro = registro)
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun RegistroItem(registro: Registro) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Usamos las imágenes drawable según el tipo de registro
        val iconRes = when (registro.tipo) {
            stringResource(R.string.water) -> R.drawable.ic_agua
            stringResource(R.string.electricity) -> R.drawable.ic_luz
            stringResource(R.string.gas) -> R.drawable.ic_gas
            else -> R.drawable.ic_agua // Por defecto
        }

        Image(
            painter = painterResource(id = iconRes),
            contentDescription = registro.tipo,
            modifier = Modifier
                .size(40.dp)
                .padding(end = 16.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = registro.tipo,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = stringResource(R.string.measurement, registro.medidor),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = stringResource(R.string.date, registro.fecha.toString()),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun PantallaFormRegistro(
    vmListaRegistros: ListaRegistrosViewModel,
    onRegistroExitoso: () -> Unit
) {
    var medidor by rememberSaveable { mutableIntStateOf(0) }
    var fecha by rememberSaveable { mutableStateOf("") }
    //var tipo by rememberSaveable { mutableStateOf(stringResource(R.string.water)) }
    var tipo by rememberSaveable { mutableStateOf("Agua") }
    var errorFecha by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        Text(
            text = stringResource(R.string.title_form),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        TextField(
            value = medidor.toString(),
            onValueChange = { medidor = it.toIntOrNull() ?: 0 },
            label = { Text(stringResource(R.string.meter_hint)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = fecha,
            onValueChange = { fecha = it },
            label = { Text(stringResource(R.string.date_hint)) },
            isError = errorFecha,
            modifier = Modifier.fillMaxWidth()
        )

        if (errorFecha) {
            Text(
                text = "Formato de fecha inválido (YYYY-MM-DD)",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(stringResource(R.string.meter_type))
        OpcionesTiposUi(
            tipoSeleccionado = tipo,
            onTipoSeleccionado = { tipoSeleccionado ->
                tipo = tipoSeleccionado
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                errorFecha = !isValidDate(fecha)
                if (!errorFecha) {
                    val nuevoRegistro = Registro(
                        null,
                        medidor,
                        if (fecha.isNotEmpty()) LocalDate.parse(fecha) else LocalDate.now(),
                        tipo
                    )
                    vmListaRegistros.insertarRegistro(nuevoRegistro)
                    onRegistroExitoso()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.register_button))
        }
    }
}

@Composable
fun OpcionesTiposUi(
    tipoSeleccionado: String,
    onTipoSeleccionado: (String) -> Unit
) {
    val tipos = listOf(
        stringResource(R.string.water),
        stringResource(R.string.electricity),
        stringResource(R.string.gas)
    )

    Column(Modifier.selectableGroup()) {
        tipos.forEach { text ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .selectable(
                        selected = (text == tipoSeleccionado),
                        onClick = {
                            onTipoSeleccionado(text)
                        },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (text == tipoSeleccionado),
                    onClick = null
                )

                // Mostramos el icono correspondiente al lado del texto
                val iconRes = when (text) {
                    stringResource(R.string.water) -> R.drawable.ic_agua
                    stringResource(R.string.electricity) -> R.drawable.ic_luz
                    stringResource(R.string.gas) -> R.drawable.ic_gas
                    else -> R.drawable.ic_agua
                }

                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = text,
                    modifier = Modifier
                        .size(24.dp)
                        .padding(start = 8.dp, end = 16.dp)
                )

                Text(
                    text = text,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

private fun isValidDate(dateStr: String): Boolean {
    return if (dateStr.isEmpty()) true
    else try {
        LocalDate.parse(dateStr)
        true
    } catch (e: Exception) {
        false
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewPantallaListaRegistros() {
    MaterialTheme {
        PantallaListaRegistros(
            registros = listOf(
                Registro(1, 12345, LocalDate.now(), "Agua"),
                Registro(2, 67890, LocalDate.now().minusDays(1), "Luz"),
                Registro(3, 54321, LocalDate.now().minusDays(2), "Gas")
            )
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewPantallaFormRegistro() {
    MaterialTheme {
        PantallaFormRegistro(
            vmListaRegistros = viewModel(factory = ListaRegistrosViewModel.Factory),
            onRegistroExitoso = {}
            )
    }
}