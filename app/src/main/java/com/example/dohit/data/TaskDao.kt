package com.example.dohit.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks")
    fun getAllTasksRaw(): List<Task>


    @Query("SELECT * FROM tasks WHERE category = :category ORDER BY lastModifiedDate ASC")
    fun getTasksByFolder(category: String): LiveData<List<Task>>

    @Query("SELECT * FROM tasks ORDER BY lastModifiedDate ASC")
    fun getAllTasks(): LiveData<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)
}
