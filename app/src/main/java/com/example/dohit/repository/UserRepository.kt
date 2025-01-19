package com.example.dohit.repository
import androidx.lifecycle.LiveData
import com.example.dohit.data.task.Task
import com.example.dohit.data.user.User
import com.example.dohit.data.user.UserDao

class UserRepository(private val userDao: UserDao) {
    // TODO: Manage multiple users
    fun getUser(userId:Int = 1):LiveData<User?>{
        return userDao.getUserById(userId)
    }
    fun getAllUsers(): LiveData<List<User>?> {
        return userDao.getAllUsers()
    }
    suspend fun updateUser(user: User) {
        userDao.insertUser(user)
    }
}