package com.example.dohit.repository

import androidx.lifecycle.LiveData
import com.example.dohit.data.Task
import com.example.dohit.data.TaskDao

class TaskRepository(private val taskDao: TaskDao) {
    val allTasks: LiveData<List<Task>> = taskDao.getAllTasks()

    suspend fun insertTask(task: Task) {
        taskDao.insertTask(task)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }
}
