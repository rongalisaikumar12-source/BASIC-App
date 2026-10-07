package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.outlined.ElectricBolt
import androidx.compose.material.icons.outlined.Loop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HabitItem
import com.example.ui.theme.Amber500
import com.example.ui.theme.Teal600

@Composable
fun HabitsScreen(
  habits: List<HabitItem>,
  onToggleHabit: (HabitItem) -> Unit,
  onDeleteHabit: (HabitItem) -> Unit,
  onOpenAddHabit: () -> Unit,
  modifier: Modifier = Modifier
) {
  val total = habits.size
  val completedCount = habits.count { it.isCompletedToday }
  val progress = if (total > 0) completedCount.toFloat() / total.toFloat() else 0f
  val animatedProgress by animateFloatAsState(targetValue = progress, label = "habit_prog")
  val maxStreak = habits.maxOfOrNull { it.currentStreak } ?: 0

  Box(modifier = modifier.fillMaxSize()) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .testTag("habits_list"),
      contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Habit summary banner
      item(key = "habit_banner") {
        HabitsSummaryBanner(
          completed = completedCount,
          total = total,
          maxStreak = maxStreak,
          progress = animatedProgress
        )
      }

      if (habits.isEmpty()) {
        item(key = "empty_habits") {
          EmptyHabitsView(onOpenAddHabit = onOpenAddHabit)
        }
      } else {
        items(habits, key = { it.id }) { habit ->
          HabitCard(
            habit = habit,
            onToggle = { onToggleHabit(habit) },
            onDelete = { onDeleteHabit(habit) }
          )
        }
      }
    }

    // Add Habit FAB
    FloatingActionButton(
      onClick = onOpenAddHabit,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(bottom = 16.dp, end = 16.dp)
        .testTag("add_habit_fab"),
      containerColor = MaterialTheme.colorScheme.primary,
      contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
      Icon(Icons.Default.Add, contentDescription = "Add new habit")
    }
  }
}

@Composable
private fun HabitsSummaryBanner(
  completed: Int,
  total: Int,
  maxStreak: Int,
  progress: Float
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("habit_summary_card"),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant
    )
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Daily Momentum",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "$completed of $total done today",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Surface(
          shape = RoundedCornerShape(12.dp),
          color = Amber500.copy(alpha = 0.15f)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = "🔥", fontSize = 14.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "$maxStreak days best",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold,
              color = Amber500
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      LinearProgressIndicator(
        progress = { progress },
        modifier = Modifier
          .fillMaxWidth()
          .height(8.dp)
          .clip(CircleShape),
        color = Teal600,
        trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
      )
    }
  }
}

@Composable
fun HabitCard(
  habit: HabitItem,
  onToggle: () -> Unit,
  onDelete: () -> Unit
) {
  val cardBg = if (habit.isCompletedToday) {
    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
  } else {
    MaterialTheme.colorScheme.surface
  }

  val checkBg by animateColorAsState(
    targetValue = if (habit.isCompletedToday) Teal600 else Color.Transparent,
    label = "checkBg"
  )

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("habit_card_${habit.id}"),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = cardBg),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Emoji Avatar
      Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.size(46.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Text(text = habit.iconEmoji, fontSize = 22.sp)
        }
      }

      Spacer(modifier = Modifier.width(14.dp))

      Column(
        modifier = Modifier
          .weight(1f)
          .clickable { onToggle() }
      ) {
        Text(
          text = habit.title,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text(
            text = "🔥 ${habit.currentStreak} day streak",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = if (habit.currentStreak > 0) Amber500 else MaterialTheme.colorScheme.onSurfaceVariant
          )

          Text(
            text = "•",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
          )

          Text(
            text = if (habit.isCompletedToday) "Completed today" else "Tap to check in",
            style = MaterialTheme.typography.bodySmall,
            color = if (habit.isCompletedToday) Teal600 else MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      // Check In Button
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(checkBg)
          .border(
            width = 2.dp,
            color = if (habit.isCompletedToday) Teal600 else MaterialTheme.colorScheme.outline,
            shape = CircleShape
          )
          .clickable { onToggle() }
          .testTag("habit_toggle_${habit.id}"),
        contentAlignment = Alignment.Center
      ) {
        if (habit.isCompletedToday) {
          Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Completed today",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
          )
        }
      }

      IconButton(
        onClick = onDelete,
        modifier = Modifier.testTag("delete_habit_${habit.id}")
      ) {
        Icon(
          imageVector = Icons.Default.DeleteOutline,
          contentDescription = "Delete habit",
          tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
      }
    }
  }
}

@Composable
private fun EmptyHabitsView(onOpenAddHabit: () -> Unit) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 48.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.size(72.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = Icons.Outlined.Loop,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(36.dp)
          )
        }
      }

      Text(
        text = "Build positive habits",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )

      Text(
        text = "Small consistent actions compound over time",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(4.dp))

      Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.clickable { onOpenAddHabit() }
      ) {
        Text(
          text = "Create First Habit",
          modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
          style = MaterialTheme.typography.labelLarge,
          color = MaterialTheme.colorScheme.onPrimary
        )
      }
    }
  }
}
