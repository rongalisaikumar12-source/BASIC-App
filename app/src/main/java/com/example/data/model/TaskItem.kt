package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskItem(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val title: String,
  val category: String = "General", // General, Work, Personal, Health, Urgent
  val isCompleted: Boolean = false,
  val priority: Int = 2, // 1 = High, 2 = Medium, 3 = Low
  val dueDate: String = "",
  val createdAt: Long = System.currentTimeMillis()
)
