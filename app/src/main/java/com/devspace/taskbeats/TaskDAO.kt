package com.devspace.taskbeats

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface TaskDAO {

    @Query("Select * from taskentity")
    fun getAll(): List<TaskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(taskEntity: List<TaskEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(taskEntity: TaskEntity)

    @Update
    fun update(taskEntity: TaskEntity)

    @Delete
    fun delete(taskEntity: TaskEntity)

    @Query("Select * from taskentity where category is :categoryName")
    fun getAllByCategoryName(categoryName: String ): List<TaskEntity>

    @Delete
    fun deleteAll(taskEntity: List<TaskEntity>)
}