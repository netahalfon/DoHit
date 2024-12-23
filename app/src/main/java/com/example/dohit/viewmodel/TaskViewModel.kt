package com.example.dohit.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.dohit.data.Task
import com.example.dohit.data.TaskCategory
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
        repository.insertTask(Task(title = "Sample Task 1", description = "Description 1", lastModifiedDate = "2024-12-31", category = TaskCategory.Work, isCompleted = false))
        repository.insertTask(Task(title = "Sample Task 2", description = "Description 2", lastModifiedDate = "2024-12-25",category = TaskCategory.Sport, isCompleted = true))
    }
    fun getTasksByCategory(category: String): LiveData<List<Task>> {
        val tasks = repository.getTasksByCategory(category)
        tasks.observeForever { taskList ->
            taskList.forEach { task ->
                Log.d("TaskCheck", "Task: ${task.title}, Category: ${task.category}")
            }
        }
        return tasks
    }
    private val _completedTasksCount = MutableLiveData<Int>()
    val completedTasksCount: LiveData<Int> get() = _completedTasksCount

    private val _activeTasksCount = MutableLiveData<Int>()
    val activeTasksCount: LiveData<Int> get() = _activeTasksCount

    fun updateTaskCounts(tasks: List<Task>) {
        _completedTasksCount.value = tasks.count { it.isCompleted }
        _activeTasksCount.value = tasks.count { !it.isCompleted }
    }

    init {
        allTasks.observeForever { tasks ->
            updateTaskCounts(tasks)
        }
    }


}
