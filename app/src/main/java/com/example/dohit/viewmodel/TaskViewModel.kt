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
    val incompleteTasks: LiveData<List<Task>>

    init {
        val dao = TaskDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(dao)
        allTasks = repository.allTasks
        incompleteTasks = repository.getIncompleteTasks()

    }

    //לעדכון הצאק בוקס
    fun updateTaskStatus(taskId: Int, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.updateTaskStatus(taskId, isCompleted)
        }
    }


    fun insertTask(task: Task) = viewModelScope.launch {
        repository.insertTask(task)
    }

    fun deleteTask(task: Task) = viewModelScope.launch {
        repository.deleteTask(task)
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
    fun getCompletionPercentageByCategory(category: TaskCategory): LiveData<Int> {
        val completionPercentage = MutableLiveData<Int>()
        allTasks.observeForever { tasks ->
            val totalTasks = tasks.filter { it.category == category }.size
            val completedTasks = tasks.filter { it.category == category && it.isCompleted }.size
            val percentage = if (totalTasks > 0) (completedTasks * 100 / totalTasks) else 0
            completionPercentage.value = percentage
        }
        return completionPercentage
    }

    fun getTaskStatisticsByCategory(category: TaskCategory): LiveData<Pair<Int, Int>> {
        return repository.getTaskStatisticsByCategory(category)
    }

    private val _selectedCategory = MutableLiveData<TaskCategory>()
    val selectedCategory: LiveData<TaskCategory> get() = _selectedCategory

    fun setSelectedCategory(category: TaskCategory) {
        _selectedCategory.value = category
    }


}
