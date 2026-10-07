package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.StickyNote2
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material.icons.outlined.StickyNote2
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AddEditNoteDialog
import com.example.ui.components.AddHabitDialog
import com.example.ui.components.AddTaskDialog
import com.example.ui.components.BasicsHeader
import com.example.ui.screens.HabitsScreen
import com.example.ui.screens.NotesScreen
import com.example.ui.screens.TasksScreen
import com.example.ui.screens.ToolsScreen

@Composable
fun BasicsApp(
  viewModel: BasicsViewModel,
  modifier: Modifier = Modifier
) {
  val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
  val tasks by viewModel.tasks.collectAsStateWithLifecycle()
  val notes by viewModel.notes.collectAsStateWithLifecycle()
  val habits by viewModel.habits.collectAsStateWithLifecycle()

  val taskFilter by viewModel.taskFilter.collectAsStateWithLifecycle()
  val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
  val activeTool by viewModel.activeTool.collectAsStateWithLifecycle()

  val isAddTaskOpen by viewModel.isAddTaskOpen.collectAsStateWithLifecycle()
  val editingNote by viewModel.editingNote.collectAsStateWithLifecycle()
  var isAddNoteOpen by remember { mutableStateOf(false) }
  val isAddHabitOpen by viewModel.isAddHabitOpen.collectAsStateWithLifecycle()

  // Tool states
  val counterValue by viewModel.counterValue.collectAsStateWithLifecycle()
  val counterStep by viewModel.counterStep.collectAsStateWithLifecycle()
  val counterTarget by viewModel.counterTarget.collectAsStateWithLifecycle()

  val timerTotal by viewModel.timerTotalSeconds.collectAsStateWithLifecycle()
  val timerRemaining by viewModel.timerRemainingSeconds.collectAsStateWithLifecycle()
  val isTimerRunning by viewModel.isTimerRunning.collectAsStateWithLifecycle()

  val converterCategory by viewModel.converterCategory.collectAsStateWithLifecycle()
  val converterInput by viewModel.converterInput.collectAsStateWithLifecycle()
  val converterFromUnit by viewModel.converterFromUnit.collectAsStateWithLifecycle()
  val converterToUnit by viewModel.converterToUnit.collectAsStateWithLifecycle()
  val converterResult by viewModel.converterResult.collectAsStateWithLifecycle()

  // Handle back button when not on primary tab
  BackHandler(enabled = activeTab != BasicsTab.TASKS) {
    viewModel.setActiveTab(BasicsTab.TASKS)
  }

  val pendingTasksCount = tasks.count { !it.isCompleted }

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .windowInsetsPadding(WindowInsets.statusBars),
    topBar = {
      BasicsHeader(pendingTasksCount = pendingTasksCount)
    },
    bottomBar = {
      NavigationBar(
        modifier = Modifier
          .windowInsetsPadding(WindowInsets.navigationBars)
          .testTag("basics_bottom_nav"),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
      ) {
        listOf(
          BasicsTab.TASKS to Pair("Tasks", Pair(Icons.Filled.CheckCircle, Icons.Outlined.CheckCircle)),
          BasicsTab.NOTES to Pair("Notes", Pair(Icons.Filled.StickyNote2, Icons.Outlined.StickyNote2)),
          BasicsTab.HABITS to Pair("Habits", Pair(Icons.Filled.Repeat, Icons.Outlined.Repeat)),
          BasicsTab.TOOLS to Pair("Tools", Pair(Icons.Filled.Build, Icons.Outlined.Build))
        ).forEach { (tab, details) ->
          val (label, icons) = details
          val (filledIcon, outlinedIcon) = icons
          val isSelected = activeTab == tab

          NavigationBarItem(
            selected = isSelected,
            onClick = {
              if (activeTab != tab) {
                viewModel.setSearchQuery("")
                viewModel.setActiveTab(tab)
              }
            },
            icon = {
              Icon(
                imageVector = if (isSelected) filledIcon else outlinedIcon,
                contentDescription = label
              )
            },
            label = {
              Text(
                text = label,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              )
            },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = MaterialTheme.colorScheme.primary,
              selectedTextColor = MaterialTheme.colorScheme.primary,
              indicatorColor = MaterialTheme.colorScheme.primaryContainer,
              unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
              unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.testTag("nav_${tab.name.lowercase()}")
          )
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      AnimatedContent(
        targetState = activeTab,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "tab_switch"
      ) { tab ->
        when (tab) {
          BasicsTab.TASKS -> {
            TasksScreen(
              tasks = tasks,
              currentFilter = taskFilter,
              searchQuery = searchQuery,
              onFilterChange = { viewModel.setTaskFilter(it) },
              onSearchChange = { viewModel.setSearchQuery(it) },
              onToggleTask = { viewModel.toggleTask(it) },
              onDeleteTask = { viewModel.deleteTask(it) },
              onOpenAddTask = { viewModel.setAddTaskOpen(true) }
            )
          }
          BasicsTab.NOTES -> {
            NotesScreen(
              notes = notes,
              searchQuery = searchQuery,
              onSearchChange = { viewModel.setSearchQuery(it) },
              onSelectNote = { viewModel.setEditingNote(it) },
              onTogglePin = { viewModel.togglePinNote(it) },
              onDeleteNote = { viewModel.deleteNote(it) },
              onOpenAddNote = { isAddNoteOpen = true }
            )
          }
          BasicsTab.HABITS -> {
            HabitsScreen(
              habits = habits,
              onToggleHabit = { viewModel.toggleHabit(it) },
              onDeleteHabit = { viewModel.deleteHabit(it) },
              onOpenAddHabit = { viewModel.setAddHabitOpen(true) }
            )
          }
          BasicsTab.TOOLS -> {
            ToolsScreen(
              activeTool = activeTool,
              onSelectTool = { viewModel.setActiveTool(it) },
              timerTotal = timerTotal,
              timerRemaining = timerRemaining,
              isTimerRunning = isTimerRunning,
              onTimerPreset = { viewModel.setTimerPreset(it) },
              onToggleTimer = { viewModel.toggleTimer() },
              onResetTimer = { viewModel.resetTimer() },
              counterValue = counterValue,
              counterStep = counterStep,
              counterTarget = counterTarget,
              onIncrementCounter = { viewModel.incrementCounter() },
              onDecrementCounter = { viewModel.decrementCounter() },
              onResetCounter = { viewModel.resetCounter() },
              onSetCounterStep = { viewModel.setCounterStep(it) },
              converterCategory = converterCategory,
              converterInput = converterInput,
              converterFromUnit = converterFromUnit,
              converterToUnit = converterToUnit,
              converterResult = converterResult,
              onSetConverterCategory = { viewModel.setConverterCategory(it) },
              onSetConverterInput = { viewModel.setConverterInput(it) },
              onSetConverterFromUnit = { viewModel.setConverterFromUnit(it) },
              onSetConverterToUnit = { viewModel.setConverterToUnit(it) },
              onSwapConverterUnits = { viewModel.swapConverterUnits() }
            )
          }
        }
      }
    }

    // Dialogs
    if (isAddTaskOpen) {
      AddTaskDialog(
        onDismiss = { viewModel.setAddTaskOpen(false) },
        onConfirm = { title, category, priority, due ->
          viewModel.addTask(title, category, priority, due)
          viewModel.setAddTaskOpen(false)
        }
      )
    }

    if (isAddNoteOpen) {
      AddEditNoteDialog(
        initialNote = null,
        onDismiss = { isAddNoteOpen = false },
        onConfirm = { title, content, colorTag, isPinned ->
          viewModel.saveNote(0L, title, content, colorTag, isPinned)
          isAddNoteOpen = false
        }
      )
    }

    if (editingNote != null) {
      AddEditNoteDialog(
        initialNote = editingNote,
        onDismiss = { viewModel.setEditingNote(null) },
        onConfirm = { title, content, colorTag, isPinned ->
          viewModel.saveNote(editingNote!!.id, title, content, colorTag, isPinned)
        }
      )
    }

    if (isAddHabitOpen) {
      AddHabitDialog(
        onDismiss = { viewModel.setAddHabitOpen(false) },
        onConfirm = { title, emoji ->
          viewModel.addHabit(title, emoji)
          viewModel.setAddHabitOpen(false)
        }
      )
    }
  }
}
