package com.example.dohit.data.task

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

    //עבור עדכון הצאק בוקס
    @Query("UPDATE tasks SET isCompleted = :isCompleted WHERE id = :taskId")
    suspend fun updateTaskStatus(taskId: Int, isCompleted:Boolean)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("SELECT COUNT(*) FROM tasks WHERE category = :category AND isCompleted = 1")
    fun getCompletedTasksCount(category: String): LiveData<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE category = :category")
    fun getTotalTasksCount(category: String): LiveData<Int>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY lastModifiedDate ASC")
    fun getIncompleteTasks(): LiveData<List<Task>>

}
