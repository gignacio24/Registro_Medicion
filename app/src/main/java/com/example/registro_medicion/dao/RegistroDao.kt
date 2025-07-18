package com.example.registro_medicion.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.registro_medicion.entities.Registro

@Dao
interface RegistroDao {
    @Query("SELECT * FROM Registro ORDER BY fecha DESC")
    suspend fun obtenerTodos(): List<Registro>

    @Insert
    suspend fun insertar(registro:Registro )

    @Update
    suspend fun modificar(registro:Registro)
}