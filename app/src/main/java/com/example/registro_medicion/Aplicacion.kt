package com.example.registro_medicion

import android.app.Application
import androidx.room.Room
import com.example.registro_medicion.db.AppDatabase

class Aplicacion  : Application(){
    //crea cada vez que lo va necesitando
    private val db by lazy { Room.databaseBuilder( this, AppDatabase::class.java, "registro.db" ).build() }
    val registroDao by lazy{ db.registroDao()}
}
