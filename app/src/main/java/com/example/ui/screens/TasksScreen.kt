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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskItem
import com.example.ui.TaskFilter
import com.example.ui.theme.Coral500
import com.example.ui.theme.Teal600

@Composable
fun TasksScreen(
  tasks: List<TaskItem>,
  currentFilter: TaskFilter,
  searchQuery: String,
  onFilterChange: (TaskFilter) -> Unit,
  onSearchChange: (String) -> Unit,
  onToggleTask: (TaskItem) -> Unit,
  onDeleteTask: (TaskItem) -> Unit,
  onOpenAddTask: () -> Unit,
  modifier: Modifier = Modifier
) {
  val filteredTasks = tasks.filter { task ->
    val matchesFilter = when (currentFilter) {
      TaskFilter.ALL -> true
      TaskFilter.ACTIVE -> !task.isCompleted
      TaskFilter.COMPLETED -> task.isCompleted
      TaskFilter.HIGH_PRIORITY -> task.priority == 1
    }
    val matchesSearch = if (searchQuery.isBlank()) true else {
      task.title.contains(searchQuery, ignoreCase = true) ||
          task.category.contains(searchQuery, ignoreCase = true)
    }
    matchesFilter && matchesSearch
  }

  val totalCount = tasks.size
  val completedCount = tasks.count { it.isCompleted }
  val progress = if (totalCount > 0) completedCount.toFloat() / totalCount.toFloat() else 0f
  val animatedProgress by animateFloatAsState(targetValue = progress, label = "progress")

  Box(modifier = modifier.fillMaxSize()) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .testTag("tasks_list"),
      contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // Progress banner
      item(key = "progress_banner") {
        ProgressBanner(
          completed = completedCount,
          total = totalCount,
          progress = animatedProgress
        )
      }

      // Search field
      item(key = "search_field") {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = onSearchChange,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("task_search_input"),
          placeholder = { Text("Search tasks or categories...") },
          leadingIcon = {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = "Search",
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
          },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { onSearchChange("") }) {
                Icon(
                  imageVector = Icons.Default.Close,
                  contentDescription = "Clear search",
                  tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          },
          shape = RoundedCornerShape(16.dp),
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
          )
        )
      }

      // Filter Chips
      item(key = "filter_chips") {
        FilterChipsRow(
          currentFilter = currentFilter,
          onFilterChange = onFilterChange
        )
      }

      // Tasks List
      if (filteredTasks.isEmpty()) {
        item(key = "empty_state") {
          EmptyTasksView(
            hasFilter = currentFilter != TaskFilter.ALL || searchQuery.isNotEmpty(),
            onResetFilter = {
              onFilterChange(TaskFilter.ALL)
              onSearchChange("")
            }
          )
        }
      } else {
        items(filteredTasks, key = { it.id }) { task ->
          TaskCard(
            task = task,
            onToggle = { onToggleTask(task) },
            onDelete = { onDeleteTask(task) }
          )
        }
      }
    }

    // Floating action button
    FloatingActionButton(
      onClick = onOpenAddTask,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(bottom = 16.dp, end = 16.dp)
        .testTag("add_task_fab"),
      containerColor = MaterialTheme.colorScheme.primary,
      contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
      Icon(Icons.Default.Add, contentDescription = "Add new task")
    }
  }
}

@Composable
private fun ProgressBanner(
  completed: Int,
  total: Int,
  progress: Float
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("task_progress_card"),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.primaryContainer
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
            text = "Today's Checklist",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = if (total == 0) "No tasks yet" else "$completed of $total tasks completed",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
          )
        }
        val percent = (progress * 100).toInt()
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        ) {
          Text(
            text = "$percent%",
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )
        }
      }
      Spacer(modifier = Modifier.height(14.dp))
      LinearProgressIndicator(
        progress = { progress },
        modifier = Modifier
          .fillMaxWidth()
          .height(8.dp)
          .clip(CircleShape),
        color = MaterialTheme.colorScheme.primary,
        trackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)
      )
    }
  }
}

@Composable
private fun FilterChipsRow(
  currentFilter: TaskFilter,
  onFilterChange: (TaskFilter) -> Unit
) {
  LazyRow(
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    contentPadding = PaddingValues(vertical = 4.dp)
  ) {
    items(TaskFilter.values()) { filter ->
      val isSelected = currentFilter == filter
      val label = when (filter) {
        TaskFilter.ALL -> "All"
        TaskFilter.ACTIVE -> "Active"
        TaskFilter.COMPLETED -> "Done"
        TaskFilter.HIGH_PRIORITY -> "🔥 Urgent"
      }
      val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        label = "chip_bg"
      )
      val contentColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "chip_fg"
      )

      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .background(backgroundColor)
          .clickable { onFilterChange(filter) }
          .padding(horizontal = 14.dp, vertical = 8.dp)
          .testTag("filter_chip_${filter.name.lowercase()}"),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = label,
          style = MaterialTheme.typography.labelMedium,
          fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
          color = contentColor
        )
      }
    }
  }
}

@Composable
fun TaskCard(
  task: TaskItem,
  onToggle: () -> Unit,
  onDelete: () -> Unit
) {
  val cardBg = if (task.isCompleted) {
    MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
  } else {
    MaterialTheme.colorScheme.surface
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("task_card_${task.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = cardBg),
    elevation = CardDefaults.cardElevation(defaultElevation = if (task.isCompleted) 0.dp else 1.5.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Custom animated circular checkbox
      Box(
        modifier = Modifier
          .size(28.dp)
          .clip(CircleShape)
          .background(
            if (task.isCompleted) Teal600 else Color.Transparent
          )
          .border(
            width = 2.dp,
            color = if (task.isCompleted) Teal600 else MaterialTheme.colorScheme.outline,
            shape = CircleShape
          )
          .clickable { onToggle() }
          .testTag("task_checkbox_${task.id}"),
        contentAlignment = Alignment.Center
      ) {
        if (task.isCompleted) {
          Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Completed",
            tint = Color.White,
            modifier = Modifier.size(16.dp)
          )
        }
      }

      Spacer(modifier = Modifier.width(14.dp))

      Column(
        modifier = Modifier
          .weight(1f)
          .clickable { onToggle() }
      ) {
        Text(
          text = task.title,
          style = MaterialTheme.typography.bodyLarge,
          fontWeight = FontWeight.Medium,
          color = if (task.isCompleted) {
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
          } else {
            MaterialTheme.colorScheme.onSurface
          },
          textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Category badge
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
          ) {
            Text(
              text = task.category,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          // Priority badge
          if (task.priority == 1) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = Coral500.copy(alpha = 0.15f)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  Icons.Outlined.Flag,
                  contentDescription = "High Priority",
                  tint = Coral500,
                  modifier = Modifier.size(11.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                  text = "Urgent",
                  style = MaterialTheme.typography.labelSmall,
                  color = Coral500,
                  fontWeight = FontWeight.SemiBold
                )
              }
            }
          }

          // Due date badge if present
          if (task.dueDate.isNotBlank()) {
            Row(
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                Icons.Outlined.Event,
                contentDescription = "Due Date",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.size(12.dp)
              )
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = task.dueDate,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
              )
            }
          }
        }
      }

      IconButton(
        onClick = onDelete,
        modifier = Modifier.testTag("delete_task_${task.id}")
      ) {
        Icon(
          imageVector = Icons.Default.DeleteOutline,
          contentDescription = "Delete task",
          tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
      }
    }
  }
}

@Composable
private fun EmptyTasksView(
  hasFilter: Boolean,
  onResetFilter: () -> Unit
) {
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
            imageVector = Icons.Outlined.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(36.dp)
          )
        }
      }

      Text(
        text = if (hasFilter) "No matching tasks found" else "All caught up!",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )

      Text(
        text = if (hasFilter) "Try clearing search or filters" else "Add a task to plan your day with confidence",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      if (hasFilter) {
        Spacer(modifier = Modifier.height(4.dp))
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = MaterialTheme.colorScheme.primary,
          modifier = Modifier.clickable { onResetFilter() }
        ) {
          Text(
            text = "Reset Filters",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimary
          )
        }
      }
    }
  }
}
