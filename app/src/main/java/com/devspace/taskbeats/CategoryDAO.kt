package com.devspace.taskbeats

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CategoryDAO {

    @Query("Select * from categoryentity")
    fun getlAll():List<CategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(categoryEntity:List<CategoryEntity> )

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(categoryEntity:CategoryEntity )
}