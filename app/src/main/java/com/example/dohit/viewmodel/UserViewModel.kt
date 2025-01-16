package com.example.dohit.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.dohit.data.user.User
import com.example.dohit.data.user.UserDatabase
import com.example.dohit.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: UserRepository
    val currentUser: LiveData<User?>
    val allUsers: LiveData<List<User>?>

    init {
        val dao = UserDatabase.getDatabase(application).userDao()
        repository = UserRepository(dao)
        allUsers = repository.getAllUsers()
        currentUser = allUsers.map { users: List<User>? -> users?.firstOrNull() }
    }

    fun updateUserProfile(user: User) = viewModelScope.launch {
        repository.updateUser(user)
    }
}