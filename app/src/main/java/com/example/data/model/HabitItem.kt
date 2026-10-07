package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitItem(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val title: String,
  val iconEmoji: String = "✨",
  val currentStreak: Int = 0,
  val lastCompletedDate: String = "", // "YYYY-MM-DD"
  val isCompletedToday: Boolean = false,
  val targetDaysPerWeek: Int = 7
)
