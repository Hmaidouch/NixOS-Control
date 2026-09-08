package com.example.myapplication.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CustomAction::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun actionDao(): ActionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ssh_control_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
