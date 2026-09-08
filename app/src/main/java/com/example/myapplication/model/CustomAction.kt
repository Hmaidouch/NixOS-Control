package com.example.myapplication.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "custom_actions")
data class CustomAction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val command: String,
    val iconName: String, // اسم الأيقونة من Material Icons
    val requiresSudo: Boolean = true
)
