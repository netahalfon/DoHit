package com.example.dohit.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.dohit.data.task.Task
import com.example.dohit.data.task.TaskCategory
import com.example.dohit.data.task.TaskDao

class TaskRepository(private val taskDao: TaskDao) {
    val allTasks: LiveData<List<Task>> = taskDao.getAllTasks()

    suspend fun updateTaskStatus(taskId: Int, isCompleted: Boolean) {
        taskDao.updateTaskStatus(taskId, isCompleted)
    }

    suspend fun insertTask(task: Task) {
        taskDao.insertTask(task)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }
    fun getTasksByCategory(folderName: String): LiveData<List<Task>> {
        return taskDao.getTasksByFolder(folderName)
    }
    fun getTaskStatisticsByCategory(category: TaskCategory): LiveData<Pair<Int, Int>> {
        val completedTasks = taskDao.getCompletedTasksCount(category.name)
        val totalTasks = taskDao.getTotalTasksCount(category.name)

        return MediatorLiveData<Pair<Int, Int>>().apply {
            addSource(completedTasks) { completed ->
                val total = totalTasks.value ?: 0
                value = completed to total
            }
            addSource(totalTasks) { total ->
                val completed = completedTasks.value ?: 0
                value = completed to total
            }
        }
    }
    fun getIncompleteTasks(): LiveData<List<Task>> {
        return taskDao.getIncompleteTasks()
    }


}
