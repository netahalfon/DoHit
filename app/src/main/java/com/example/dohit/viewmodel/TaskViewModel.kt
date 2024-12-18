package com.example.dohit.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.dohit.data.Task
import com.example.dohit.data.TaskDatabase
import com.example.dohit.repository.TaskRepository
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository
    val allTasks: LiveData<List<Task>>

    init {
        val dao = TaskDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(dao)
        allTasks = repository.allTasks
    }

    fun insertTask(task: Task) = viewModelScope.launch {
        repository.insertTask(task)
    }

    fun deleteTask(task: Task) = viewModelScope.launch {
        repository.deleteTask(task)
    }
    fun addSampleTasks() = viewModelScope.launch {
        repository.insertTask(Task(title = "Sample Task 1", description = "Description 1", dueDate = "2024-12-31", priority = 1))
        repository.insertTask(Task(title = "Sample Task 2", description = "Description 2", dueDate = "2024-12-25", priority = 2))
    }

}
