package com.example.dohit.data.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
class User (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var profileImageUri: String,
    var username: String,
    var email: String
)