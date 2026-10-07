package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.TaskItem
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
  @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, priority ASC, createdAt DESC")
  fun getAllTasks(): Flow<List<TaskItem>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTask(task: TaskItem): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTasks(tasks: List<TaskItem>)

  @Update
  suspend fun updateTask(task: TaskItem)

  @Delete
  suspend fun deleteTask(task: TaskItem)

  @Query("DELETE FROM tasks WHERE id = :id")
  suspend fun deleteTaskById(id: Long)

  @Query("UPDATE tasks SET isCompleted = :isCompleted WHERE id = :id")
  suspend fun setTaskCompleted(id: Long, isCompleted: Boolean)

  @Query("SELECT COUNT(*) FROM tasks")
  suspend fun getTaskCount(): Int
}
