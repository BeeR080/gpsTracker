package com.beer080.gpstracker.main.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TrackItem::class], version = 1)

abstract class MainDataBase: RoomDatabase() {

    abstract fun getDao():DaoTrack

    companion object{
        @Volatile
         var INSTANCE:MainDataBase? = null
        fun getDatabase(context: Context): MainDataBase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MainDataBase::class.java,
                    "GpsTracker.db"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}