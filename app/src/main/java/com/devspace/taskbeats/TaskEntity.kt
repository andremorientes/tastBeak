package com.devspace.taskbeats

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long=0,
    @ColumnInfo("Categoria") val category: String,
    @ColumnInfo("Task_Name") val name: String
)