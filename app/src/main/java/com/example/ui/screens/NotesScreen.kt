package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.NoteAlt
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NoteItem
import com.example.ui.theme.NoteAmber
import com.example.ui.theme.NoteDarkAmber
import com.example.ui.theme.NoteDarkIndigo
import com.example.ui.theme.NoteDarkPurple
import com.example.ui.theme.NoteDarkRose
import com.example.ui.theme.NoteDarkSlate
import com.example.ui.theme.NoteDarkTeal
import com.example.ui.theme.NoteIndigo
import com.example.ui.theme.NotePurple
import com.example.ui.theme.NoteRose
import com.example.ui.theme.NoteSlate
import com.example.ui.theme.NoteTeal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotesScreen(
  notes: List<NoteItem>,
  searchQuery: String,
  onSearchChange: (String) -> Unit,
  onSelectNote: (NoteItem) -> Unit,
  onTogglePin: (NoteItem) -> Unit,
  onDeleteNote: (NoteItem) -> Unit,
  onOpenAddNote: () -> Unit,
  modifier: Modifier = Modifier
) {
  val filteredNotes = if (searchQuery.isBlank()) notes else {
    notes.filter {
      it.title.contains(searchQuery, ignoreCase = true) ||
          it.content.contains(searchQuery, ignoreCase = true)
    }
  }

  val pinnedNotes = filteredNotes.filter { it.isPinned }
  val otherNotes = filteredNotes.filter { !it.isPinned }

  Box(modifier = modifier.fillMaxSize()) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .testTag("notes_list"),
      contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Search Box
      item(key = "notes_search") {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = onSearchChange,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("note_search_input"),
          placeholder = { Text("Search notes and ideas...") },
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

      if (filteredNotes.isEmpty()) {
        item(key = "empty_notes") {
          EmptyNotesView(
            isSearching = searchQuery.isNotEmpty(),
            onClearSearch = { onSearchChange("") }
          )
        }
      } else {
        // Pinned section
        if (pinnedNotes.isNotEmpty()) {
          item(key = "pinned_header") {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
            ) {
              Icon(
                Icons.Filled.PushPin,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Pinned Notes",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
            }
          }

          items(pinnedNotes, key = { "pinned_${it.id}" }) { note ->
            NoteCard(
              note = note,
              onClick = { onSelectNote(note) },
              onTogglePin = { onTogglePin(note) },
              onDelete = { onDeleteNote(note) }
            )
          }
        }

        // All/Other notes section
        if (otherNotes.isNotEmpty()) {
          if (pinnedNotes.isNotEmpty()) {
            item(key = "other_header") {
              Text(
                text = "All Notes",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
              )
            }
          }

          items(otherNotes, key = { "note_${it.id}" }) { note ->
            NoteCard(
              note = note,
              onClick = { onSelectNote(note) },
              onTogglePin = { onTogglePin(note) },
              onDelete = { onDeleteNote(note) }
            )
          }
        }
      }
    }

    // Add Note FAB
    FloatingActionButton(
      onClick = onOpenAddNote,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(bottom = 16.dp, end = 16.dp)
        .testTag("add_note_fab"),
      containerColor = MaterialTheme.colorScheme.primary,
      contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
      Icon(Icons.Default.Add, contentDescription = "Create note")
    }
  }
}

@Composable
fun NoteCard(
  note: NoteItem,
  onClick: () -> Unit,
  onTogglePin: () -> Unit,
  onDelete: () -> Unit
) {
  val isDark = isSystemInDarkTheme()
  val bgColor = getNoteBackgroundColor(note.colorTag, isDark)
  val borderColor = if (isDark) MaterialTheme.colorScheme.outline.copy(alpha = 0.3f) else Color.Transparent

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(18.dp))
      .clickable { onClick() }
      .testTag("note_card_${note.id}"),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = bgColor),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .then(
          if (isDark) Modifier.border(1.dp, borderColor, RoundedCornerShape(18.dp))
          else Modifier
        )
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Text(
          text = if (note.title.isNotBlank()) note.title else "Untitled Note",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.weight(1f)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = onTogglePin,
            modifier = Modifier
              .size(32.dp)
              .testTag("pin_note_${note.id}")
          ) {
            Icon(
              imageVector = if (note.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
              contentDescription = if (note.isPinned) "Unpin note" else "Pin note",
              tint = if (note.isPinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
              modifier = Modifier.size(18.dp)
            )
          }

          IconButton(
            onClick = onDelete,
            modifier = Modifier
              .size(32.dp)
              .testTag("delete_note_${note.id}")
          ) {
            Icon(
              imageVector = Icons.Default.DeleteOutline,
              contentDescription = "Delete note",
              tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }

      if (note.content.isNotBlank()) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = note.content,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
          maxLines = 4,
          overflow = TextOverflow.Ellipsis,
          lineHeight = 20.sp
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        val dateText = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(note.updatedAt))
        Text(
          text = dateText,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f)
        )

        // Color indicator pill
        Box(
          modifier = Modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(getIndicatorColor(note.colorTag))
        )
      }
    }
  }
}

@Composable
fun getNoteBackgroundColor(tag: String, isDark: Boolean): Color {
  return if (isDark) {
    when (tag) {
      "indigo" -> NoteDarkIndigo
      "teal" -> NoteDarkTeal
      "amber" -> NoteDarkAmber
      "rose" -> NoteDarkRose
      "purple" -> NoteDarkPurple
      else -> NoteDarkSlate
    }
  } else {
    when (tag) {
      "indigo" -> NoteIndigo
      "teal" -> NoteTeal
      "amber" -> NoteAmber
      "rose" -> NoteRose
      "purple" -> NotePurple
      else -> NoteSlate
    }
  }
}

fun getIndicatorColor(tag: String): Color {
  return when (tag) {
    "indigo" -> Color(0xFF6366F1)
    "teal" -> Color(0xFF14B8A6)
    "amber" -> Color(0xFFF59E0B)
    "rose" -> Color(0xFFF43F5E)
    "purple" -> Color(0xFFA855F7)
    else -> Color(0xFF64748B)
  }
}

@Composable
private fun EmptyNotesView(
  isSearching: Boolean,
  onClearSearch: () -> Unit
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
            imageVector = Icons.Outlined.NoteAlt,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(36.dp)
          )
        }
      }

      Text(
        text = if (isSearching) "No matching notes found" else "No notes yet",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )

      Text(
        text = if (isSearching) "Try a different search query" else "Tap '+' below to jot down your thoughts and ideas",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      if (isSearching) {
        Spacer(modifier = Modifier.height(4.dp))
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = MaterialTheme.colorScheme.primary,
          modifier = Modifier.clickable { onClearSearch() }
        ) {
          Text(
            text = "Clear Search",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimary
          )
        }
      }
    }
  }
}
