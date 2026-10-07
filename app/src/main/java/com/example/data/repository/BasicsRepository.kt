package com.example.data.repository

import com.example.data.local.HabitDao
import com.example.data.local.NoteDao
import com.example.data.local.TaskDao
import com.example.data.model.HabitItem
import com.example.data.model.NoteItem
import com.example.data.model.TaskItem
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BasicsRepository(
  private val taskDao: TaskDao,
  private val noteDao: NoteDao,
  private val habitDao: HabitDao
) {
  val allTasks: Flow<List<TaskItem>> = taskDao.getAllTasks()
  val allNotes: Flow<List<NoteItem>> = noteDao.getAllNotes()
  val allHabits: Flow<List<HabitItem>> = habitDao.getAllHabits()

  suspend fun insertTask(task: TaskItem) = taskDao.insertTask(task)
  suspend fun updateTask(task: TaskItem) = taskDao.updateTask(task)
  suspend fun deleteTask(task: TaskItem) = taskDao.deleteTask(task)
  suspend fun deleteTaskById(id: Long) = taskDao.deleteTaskById(id)
  suspend fun setTaskCompleted(id: Long, isCompleted: Boolean) = taskDao.setTaskCompleted(id, isCompleted)

  suspend fun insertNote(note: NoteItem) = noteDao.insertNote(note)
  suspend fun updateNote(note: NoteItem) = noteDao.updateNote(note)
  suspend fun deleteNote(note: NoteItem) = noteDao.deleteNote(note)
  suspend fun deleteNoteById(id: Long) = noteDao.deleteNoteById(id)
  suspend fun setNotePinned(id: Long, isPinned: Boolean) = noteDao.setPinned(id, isPinned)

  suspend fun insertHabit(habit: HabitItem) = habitDao.insertHabit(habit)
  suspend fun updateHabit(habit: HabitItem) = habitDao.updateHabit(habit)
  suspend fun deleteHabit(habit: HabitItem) = habitDao.deleteHabit(habit)

  suspend fun toggleHabitToday(habit: HabitItem) {
    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    if (habit.isCompletedToday) {
      // Uncheck
      val newStreak = maxOf(0, habit.currentStreak - 1)
      habitDao.updateHabit(
        habit.copy(
          isCompletedToday = false,
          currentStreak = newStreak
        )
      )
    } else {
      // Check as completed today
      val newStreak = habit.currentStreak + 1
      habitDao.updateHabit(
        habit.copy(
          isCompletedToday = true,
          currentStreak = newStreak,
          lastCompletedDate = today
        )
      )
    }
  }

  suspend fun initializeStarterDataIfEmpty() {
    val taskCount = taskDao.getTaskCount()
    if (taskCount == 0) {
      val defaultTasks = listOf(
        TaskItem(
          title = "Plan top 3 daily priorities",
          category = "Work",
          priority = 1,
          dueDate = "Today"
        ),
        TaskItem(
          title = "Drink a large glass of water",
          category = "Health",
          priority = 2,
          dueDate = "Morning",
          isCompleted = true
        ),
        TaskItem(
          title = "Try the 25-minute Focus Timer in Tools",
          category = "General",
          priority = 2,
          dueDate = "Today"
        ),
        TaskItem(
          title = "Take a 10-minute mindful break",
          category = "Health",
          priority = 3,
          dueDate = "Evening"
        )
      )
      taskDao.insertTasks(defaultTasks)
    }

    val noteCount = noteDao.getNoteCount()
    if (noteCount == 0) {
      val defaultNotes = listOf(
        NoteItem(
          title = "Welcome to Basics! ✨",
          content = "Basics is your all-in-one daily companion:\n\n• Tasks: Track checklists & priorities\n• Notes: Instant scratchpad with color tags\n• Habits: Build daily momentum & streaks\n• Tools: Focus Timer, Tally Counter & Quick Unit Converter\n\nTap any card to view or edit, and use the quick buttons below!",
          colorTag = "indigo",
          isPinned = true
        ),
        NoteItem(
          title = "Grocery & Shopping Essentials 🛒",
          content = "- Oat milk\n- Fresh avocados\n- Sourdough bread\n- Organic green tea\n- Dark chocolate 85%",
          colorTag = "teal",
          isPinned = false
        ),
        NoteItem(
          title = "Book & Podcast Recommendations 🎧",
          content = "1. Atomic Habits by James Clear\n2. Deep Work by Cal Newport\n3. Huberman Lab - Science of Focus",
          colorTag = "amber",
          isPinned = false
        )
      )
      noteDao.insertNotes(defaultNotes)
    }

    val habitCount = habitDao.getHabitCount()
    if (habitCount == 0) {
      val defaultHabits = listOf(
        HabitItem(
          title = "Drink 2L Water",
          iconEmoji = "💧",
          currentStreak = 4,
          isCompletedToday = true
        ),
        HabitItem(
          title = "Read 15 Pages",
          iconEmoji = "📖",
          currentStreak = 7,
          isCompletedToday = false
        ),
        HabitItem(
          title = "Daily Walk or Stretch",
          iconEmoji = "🚶",
          currentStreak = 3,
          isCompletedToday = false
        ),
        HabitItem(
          title = "Mindful Evening Wind-Down",
          iconEmoji = "🌙",
          currentStreak = 5,
          isCompletedToday = false
        )
      )
      habitDao.insertHabits(defaultHabits)
    }
  }
}
