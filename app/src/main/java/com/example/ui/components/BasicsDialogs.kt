package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NoteItem
import com.example.ui.screens.getIndicatorColor
import com.example.ui.theme.Coral500

// ------------------- ADD TASK DIALOG -------------------

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddTaskDialog(
  onDismiss: () -> Unit,
  onConfirm: (title: String, category: String, priority: Int, dueDate: String) -> Unit
) {
  var title by remember { mutableStateOf("") }
  var category by remember { mutableStateOf("Work") }
  var priority by remember { mutableStateOf(2) } // 1=High, 2=Medium, 3=Low
  var dueDate by remember { mutableStateOf("Today") }

  val categories = listOf("Work", "Personal", "Health", "Study", "General")
  val dueDates = listOf("Today", "Tomorrow", "This Week", "Later")

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = "New Task",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
      )
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Task description") },
          placeholder = { Text("e.g., Finalize project review") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("new_task_title_input"),
          shape = RoundedCornerShape(12.dp)
        )

        // Category selection
        Column {
          Text(
            text = "Category",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(6.dp))
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            categories.forEach { cat ->
              val isSelected = category == cat
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                  .clickable { category = cat }
                  .testTag("task_cat_chip_$cat")
              ) {
                Text(
                  text = cat,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                  color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }

        // Priority selection
        Column {
          Text(
            text = "Priority",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(6.dp))
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(1 to "Urgent", 2 to "Normal", 3 to "Low").forEach { (p, label) ->
              val isSelected = priority == p
              val chipBg = if (isSelected) {
                if (p == 1) Coral500.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primaryContainer
              } else MaterialTheme.colorScheme.surfaceVariant

              val chipFg = if (isSelected) {
                if (p == 1) Coral500 else MaterialTheme.colorScheme.primary
              } else MaterialTheme.colorScheme.onSurfaceVariant

              Surface(
                shape = RoundedCornerShape(8.dp),
                color = chipBg,
                modifier = Modifier
                  .clickable { priority = p }
                  .testTag("task_priority_chip_$p")
              ) {
                Text(
                  text = label,
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                  color = chipFg
                )
              }
            }
          }
        }

        // Due date selection
        Column {
          Text(
            text = "Due",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(6.dp))
          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            dueDates.forEach { due ->
              val isSelected = dueDate == due
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.clickable { dueDate = due }
              ) {
                Text(
                  text = due,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                  color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (title.isNotBlank()) {
            onConfirm(title, category, priority, dueDate)
          }
        },
        enabled = title.isNotBlank(),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.testTag("submit_add_task_btn")
      ) {
        Text("Add Task")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}

// ------------------- ADD / EDIT NOTE DIALOG -------------------

@Composable
fun AddEditNoteDialog(
  initialNote: NoteItem?,
  onDismiss: () -> Unit,
  onConfirm: (title: String, content: String, colorTag: String, isPinned: Boolean) -> Unit
) {
  var title by remember { mutableStateOf(initialNote?.title ?: "") }
  var content by remember { mutableStateOf(initialNote?.content ?: "") }
  var colorTag by remember { mutableStateOf(initialNote?.colorTag ?: "indigo") }
  var isPinned by remember { mutableStateOf(initialNote?.isPinned ?: false) }

  val colorOptions = listOf("slate", "indigo", "teal", "amber", "rose", "purple")

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (initialNote != null) "Edit Note" else "New Note",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold
        )

        Surface(
          shape = CircleShape,
          color = if (isPinned) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
          modifier = Modifier
            .clickable { isPinned = !isPinned }
            .size(36.dp)
            .testTag("dialog_pin_toggle")
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = if (isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
              contentDescription = "Pin note",
              tint = if (isPinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Title") },
          placeholder = { Text("Note title...") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("note_dialog_title_input"),
          shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
          value = content,
          onValueChange = { content = it },
          label = { Text("Note details") },
          placeholder = { Text("Write your thoughts, checklist or ideas here...") },
          minLines = 4,
          maxLines = 8,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("note_dialog_content_input"),
          shape = RoundedCornerShape(12.dp)
        )

        // Color selector
        Column {
          Text(
            text = "Accent Color",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            colorOptions.forEach { opt ->
              val isSelected = colorTag == opt
              val color = getIndicatorColor(opt)

              Box(
                modifier = Modifier
                  .size(28.dp)
                  .clip(CircleShape)
                  .background(color)
                  .then(
                    if (isSelected) Modifier.border(3.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                    else Modifier
                  )
                  .clickable { colorTag = opt }
                  .testTag("color_opt_$opt")
              )
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (title.isNotBlank() || content.isNotBlank()) {
            onConfirm(title, content, colorTag, isPinned)
          }
        },
        enabled = title.isNotBlank() || content.isNotBlank(),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.testTag("submit_note_dialog_btn")
      ) {
        Text("Save Note")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}

// ------------------- ADD HABIT DIALOG -------------------

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddHabitDialog(
  onDismiss: () -> Unit,
  onConfirm: (title: String, iconEmoji: String) -> Unit
) {
  var title by remember { mutableStateOf("") }
  var emoji by remember { mutableStateOf("💧") }

  val emojis = listOf("💧", "📖", "🚶", "🧘", "🏃", "🥗", "🌙", "🎯", "✍️", "💻", "💪", "🌱")

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = "New Daily Habit",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
      )
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Habit name") },
          placeholder = { Text("e.g., Morning Meditation 10m") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("habit_dialog_title_input"),
          shape = RoundedCornerShape(12.dp)
        )

        Column {
          Text(
            text = "Pick an Emoji",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(8.dp))
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            emojis.forEach { e ->
              val isSelected = emoji == e
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                  .clickable { emoji = e }
                  .size(42.dp)
                  .testTag("habit_emoji_$e")
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Text(text = e, fontSize = 20.sp)
                }
              }
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (title.isNotBlank()) {
            onConfirm(title, emoji)
          }
        },
        enabled = title.isNotBlank(),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.testTag("submit_habit_dialog_btn")
      ) {
        Text("Create Habit")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}
