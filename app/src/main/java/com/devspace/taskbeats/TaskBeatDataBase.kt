package com.devspace.taskbeats

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CategoryEntity::class,TaskEntity::class], version = 1)
abstract class TaskBeatDataBase: RoomDatabase() {

    abstract fun getCategoryDao(): CategoryDAO
    abstract fun getTaskDao(): TaskDAO
}