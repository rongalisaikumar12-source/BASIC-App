package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.HabitItem
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
  @Query("SELECT * FROM habits ORDER BY isCompletedToday ASC, currentStreak DESC, id ASC")
  fun getAllHabits(): Flow<List<HabitItem>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertHabit(habit: HabitItem): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertHabits(habits: List<HabitItem>)

  @Update
  suspend fun updateHabit(habit: HabitItem)

  @Delete
  suspend fun deleteHabit(habit: HabitItem)

  @Query("DELETE FROM habits WHERE id = :id")
  suspend fun deleteHabitById(id: Long)

  @Query("SELECT COUNT(*) FROM habits")
  suspend fun getHabitCount(): Int
}
