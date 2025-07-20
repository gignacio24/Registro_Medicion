package com.example.registro_medicion.ui

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.registro_medicion.Aplicacion
import com.example.registro_medicion.dao.RegistroDao
import com.example.registro_medicion.entities.Registro
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ListaRegistrosViewModel( private val registroDao: RegistroDao): ViewModel(), IListaRegistros  {

    override var registros by mutableStateOf(listOf<Registro>())
        private set

    init {
        obtenerRegistros()
    }

    // Implementación de la interfaz
    override fun insertarRegistro(registro: Registro) {
        viewModelScope.launch(Dispatchers.IO) {
            registroDao.insertar(registro)
            obtenerRegistros() // Actualiza la lista después de insertar
        }
    }

    // Método interno para obtener los registros desde la base de datos
    private fun obtenerRegistros() {
        viewModelScope.launch(Dispatchers.IO) {
            val lista = registroDao.obtenerTodos()
            // Actualiza el estado en el hilo principal
            registros = lista
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val aplicacion = (this[APPLICATION_KEY] as Aplicacion)
                ListaRegistrosViewModel(aplicacion.registroDao)
            }
        }
    }
}