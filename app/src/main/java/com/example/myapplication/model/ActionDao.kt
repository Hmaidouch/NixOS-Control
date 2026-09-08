package com.example.myapplication.model

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ActionDao {
    @Query("SELECT * FROM custom_actions")
    fun getAllActions(): Flow<List<CustomAction>>

    @Query("SELECT * FROM custom_actions")
    suspend fun getAllActionsList(): List<CustomAction>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAction(action: CustomAction)

    @Delete
    suspend fun deleteAction(action: CustomAction)
    
    @Query("SELECT COUNT(*) FROM custom_actions")
    suspend fun getCount(): Int
}
