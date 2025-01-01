package com.example.dohit.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val category: TaskCategory,
    val lastModifiedDate: String,
    val imageUri: String? = null,
    var isCompleted: Boolean,
    val startTime: String? = null,  // זמן התחלה (אופציונלי)
    val endTime: String? = null
)
