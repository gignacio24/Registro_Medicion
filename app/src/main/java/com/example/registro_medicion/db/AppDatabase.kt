package com.example.registro_medicion.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.registro_medicion.dao.RegistroDao
import com.example.registro_medicion.entities.LocalDateConverter
import com.example.registro_medicion.entities.Registro

@Database(entities = [Registro::class], version = 1)
@TypeConverters(LocalDateConverter::class)
abstract class AppDatabase : RoomDatabase (){
    abstract fun registroDao(): RegistroDao

}
