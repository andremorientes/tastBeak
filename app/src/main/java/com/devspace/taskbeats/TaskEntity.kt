package com.devspace.taskbeats

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(

    foreignKeys =[
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["key"],
            childColumns = ["category"]
        )
    ]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long=0,
    @ColumnInfo("category") val category: String,
    @ColumnInfo("task_Name") val name: String
)