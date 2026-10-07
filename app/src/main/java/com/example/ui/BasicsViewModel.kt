package com.example.ui

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.HabitItem
import com.example.data.model.NoteItem
import com.example.data.model.TaskItem
import com.example.data.repository.BasicsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale

enum class BasicsTab {
  TASKS, NOTES, HABITS, TOOLS
}

enum class TaskFilter {
  ALL, ACTIVE, COMPLETED, HIGH_PRIORITY
}

enum class ToolTab {
  TIMER, COUNTER, CONVERTER
}

enum class ConverterCategory(val title: String) {
  LENGTH("Length"),
  WEIGHT("Weight"),
  TEMPERATURE("Temperature")
}

class BasicsViewModel(application: Application) : AndroidViewModel(application) {
  private val repository: BasicsRepository

  init {
    val database = AppDatabase.getDatabase(application)
    repository = BasicsRepository(
      taskDao = database.taskDao(),
      noteDao = database.noteDao(),
      habitDao = database.habitDao()
    )

    viewModelScope.launch {
      repository.initializeStarterDataIfEmpty()
    }
  }

  // Flows from DB
  val tasks: StateFlow<List<TaskItem>> = repository.allTasks
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val notes: StateFlow<List<NoteItem>> = repository.allNotes
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val habits: StateFlow<List<HabitItem>> = repository.allHabits
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Navigation & Filter state
  private val _activeTab = MutableStateFlow(BasicsTab.TASKS)
  val activeTab = _activeTab.asStateFlow()

  private val _taskFilter = MutableStateFlow(TaskFilter.ALL)
  val taskFilter = _taskFilter.asStateFlow()

  private val _searchQuery = MutableStateFlow("")
  val searchQuery = _searchQuery.asStateFlow()

  private val _activeTool = MutableStateFlow(ToolTab.TIMER)
  val activeTool = _activeTool.asStateFlow()

  // Dialog & Editing States
  private val _isAddTaskOpen = MutableStateFlow(false)
  val isAddTaskOpen = _isAddTaskOpen.asStateFlow()

  private val _editingNote = MutableStateFlow<NoteItem?>(null)
  val editingNote = _editingNote.asStateFlow()

  private val _isAddHabitOpen = MutableStateFlow(false)
  val isAddHabitOpen = _isAddHabitOpen.asStateFlow()

  // --- Tally Counter State ---
  private val _counterValue = MutableStateFlow(0)
  val counterValue = _counterValue.asStateFlow()

  private val _counterStep = MutableStateFlow(1)
  val counterStep = _counterStep.asStateFlow()

  private val _counterTarget = MutableStateFlow(50)
  val counterTarget = _counterTarget.asStateFlow()

  // --- Focus Timer State ---
  private val _timerTotalSeconds = MutableStateFlow(25 * 60)
  val timerTotalSeconds = _timerTotalSeconds.asStateFlow()

  private val _timerRemainingSeconds = MutableStateFlow(25 * 60)
  val timerRemainingSeconds = _timerRemainingSeconds.asStateFlow()

  private val _isTimerRunning = MutableStateFlow(false)
  val isTimerRunning = _isTimerRunning.asStateFlow()

  private var timerJob: Job? = null

  // --- Converter State ---
  private val _converterCategory = MutableStateFlow(ConverterCategory.LENGTH)
  val converterCategory = _converterCategory.asStateFlow()

  private val _converterInput = MutableStateFlow("1")
  val converterInput = _converterInput.asStateFlow()

  private val _converterFromUnit = MutableStateFlow("Meters (m)")
  val converterFromUnit = _converterFromUnit.asStateFlow()

  private val _converterToUnit = MutableStateFlow("Feet (ft)")
  val converterToUnit = _converterToUnit.asStateFlow()

  private val _converterResult = MutableStateFlow("3.2808")
  val converterResult = _converterResult.asStateFlow()

  // Tab & Navigation setters
  fun setActiveTab(tab: BasicsTab) {
    _activeTab.value = tab
  }

  fun setTaskFilter(filter: TaskFilter) {
    _taskFilter.value = filter
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun setActiveTool(tool: ToolTab) {
    _activeTool.value = tool
  }

  fun setAddTaskOpen(open: Boolean) {
    _isAddTaskOpen.value = open
  }

  fun setEditingNote(note: NoteItem?) {
    _editingNote.value = note
  }

  fun setAddHabitOpen(open: Boolean) {
    _isAddHabitOpen.value = open
  }

  // Task actions
  fun addTask(title: String, category: String, priority: Int, dueDate: String) {
    if (title.isBlank()) return
    viewModelScope.launch {
      repository.insertTask(
        TaskItem(
          title = title.trim(),
          category = category.ifBlank { "General" },
          priority = priority,
          dueDate = dueDate.trim()
        )
      )
    }
  }

  fun toggleTask(task: TaskItem) {
    viewModelScope.launch {
      repository.setTaskCompleted(task.id, !task.isCompleted)
      if (!task.isCompleted) {
        vibratePhone(40)
      }
    }
  }

  fun deleteTask(task: TaskItem) {
    viewModelScope.launch {
      repository.deleteTask(task)
    }
  }

  // Note actions
  fun saveNote(id: Long, title: String, content: String, colorTag: String, isPinned: Boolean) {
    if (title.isBlank() && content.isBlank()) return
    viewModelScope.launch {
      val note = NoteItem(
        id = id,
        title = title.trim(),
        content = content.trim(),
        colorTag = colorTag,
        isPinned = isPinned,
        updatedAt = System.currentTimeMillis()
      )
      if (id == 0L) {
        repository.insertNote(note)
      } else {
        repository.updateNote(note)
      }
      _editingNote.value = null
    }
  }

  fun togglePinNote(note: NoteItem) {
    viewModelScope.launch {
      repository.setNotePinned(note.id, !note.isPinned)
    }
  }

  fun deleteNote(note: NoteItem) {
    viewModelScope.launch {
      repository.deleteNote(note)
      if (_editingNote.value?.id == note.id) {
        _editingNote.value = null
      }
    }
  }

  // Habit actions
  fun addHabit(title: String, iconEmoji: String) {
    if (title.isBlank()) return
    viewModelScope.launch {
      repository.insertHabit(
        HabitItem(
          title = title.trim(),
          iconEmoji = iconEmoji.ifBlank { "✨" }
        )
      )
    }
  }

  fun toggleHabit(habit: HabitItem) {
    viewModelScope.launch {
      repository.toggleHabitToday(habit)
      vibratePhone(50)
    }
  }

  fun deleteHabit(habit: HabitItem) {
    viewModelScope.launch {
      repository.deleteHabit(habit)
    }
  }

  // Counter actions
  fun incrementCounter() {
    _counterValue.value += _counterStep.value
    vibratePhone(25)
  }

  fun decrementCounter() {
    if (_counterValue.value > 0) {
      _counterValue.value = maxOf(0, _counterValue.value - _counterStep.value)
      vibratePhone(20)
    }
  }

  fun resetCounter() {
    _counterValue.value = 0
    vibratePhone(50)
  }

  fun setCounterStep(step: Int) {
    _counterStep.value = step
  }

  fun setCounterTarget(target: Int) {
    _counterTarget.value = target
  }

  // Timer actions
  fun setTimerPreset(minutes: Int) {
    pauseTimer()
    val totalSec = minutes * 60
    _timerTotalSeconds.value = totalSec
    _timerRemainingSeconds.value = totalSec
  }

  fun toggleTimer() {
    if (_isTimerRunning.value) {
      pauseTimer()
    } else {
      startTimer()
    }
  }

  private fun startTimer() {
    if (_timerRemainingSeconds.value <= 0) {
      _timerRemainingSeconds.value = _timerTotalSeconds.value
    }
    _isTimerRunning.value = true
    timerJob?.cancel()
    timerJob = viewModelScope.launch {
      while (_timerRemainingSeconds.value > 0 && _isTimerRunning.value) {
        delay(1000)
        _timerRemainingSeconds.value -= 1
      }
      if (_timerRemainingSeconds.value == 0) {
        _isTimerRunning.value = false
        vibratePattern()
      }
    }
  }

  fun pauseTimer() {
    _isTimerRunning.value = false
    timerJob?.cancel()
    timerJob = null
  }

  fun resetTimer() {
    pauseTimer()
    _timerRemainingSeconds.value = _timerTotalSeconds.value
  }

  // Converter actions
  fun setConverterCategory(category: ConverterCategory) {
    _converterCategory.value = category
    when (category) {
      ConverterCategory.LENGTH -> {
        _converterFromUnit.value = "Meters (m)"
        _converterToUnit.value = "Feet (ft)"
      }
      ConverterCategory.WEIGHT -> {
        _converterFromUnit.value = "Kilograms (kg)"
        _converterToUnit.value = "Pounds (lb)"
      }
      ConverterCategory.TEMPERATURE -> {
        _converterFromUnit.value = "Celsius (°C)"
        _converterToUnit.value = "Fahrenheit (°F)"
      }
    }
    recalculateConversion()
  }

  fun setConverterInput(value: String) {
    _converterInput.value = value
    recalculateConversion()
  }

  fun setConverterFromUnit(unit: String) {
    _converterFromUnit.value = unit
    recalculateConversion()
  }

  fun setConverterToUnit(unit: String) {
    _converterToUnit.value = unit
    recalculateConversion()
  }

  fun swapConverterUnits() {
    val temp = _converterFromUnit.value
    _converterFromUnit.value = _converterToUnit.value
    _converterToUnit.value = temp
    recalculateConversion()
  }

  private fun recalculateConversion() {
    val input = _converterInput.value.toDoubleOrNull() ?: 0.0
    val from = _converterFromUnit.value
    val to = _converterToUnit.value
    val cat = _converterCategory.value

    val result: Double = when (cat) {
      ConverterCategory.LENGTH -> convertLength(input, from, to)
      ConverterCategory.WEIGHT -> convertWeight(input, from, to)
      ConverterCategory.TEMPERATURE -> convertTemperature(input, from, to)
    }

    _converterResult.value = if (result % 1.0 == 0.0) {
      result.toLong().toString()
    } else {
      String.format(Locale.US, "%.4f", result).trimEnd('0').trimEnd('.')
    }
  }

  private fun convertLength(valIn: Double, from: String, to: String): Double {
    // base unit: meters
    val meters = when {
      from.startsWith("Meters") -> valIn
      from.startsWith("Centimeters") -> valIn / 100.0
      from.startsWith("Kilometers") -> valIn * 1000.0
      from.startsWith("Feet") -> valIn * 0.3048
      from.startsWith("Inches") -> valIn * 0.0254
      from.startsWith("Miles") -> valIn * 1609.344
      else -> valIn
    }
    return when {
      to.startsWith("Meters") -> meters
      to.startsWith("Centimeters") -> meters * 100.0
      to.startsWith("Kilometers") -> meters / 1000.0
      to.startsWith("Feet") -> meters / 0.3048
      to.startsWith("Inches") -> meters / 0.0254
      to.startsWith("Miles") -> meters / 1609.344
      else -> meters
    }
  }

  private fun convertWeight(valIn: Double, from: String, to: String): Double {
    // base unit: kg
    val kg = when {
      from.startsWith("Kilograms") -> valIn
      from.startsWith("Grams") -> valIn / 1000.0
      from.startsWith("Pounds") -> valIn * 0.45359237
      from.startsWith("Ounces") -> valIn * 0.02834952
      else -> valIn
    }
    return when {
      to.startsWith("Kilograms") -> kg
      to.startsWith("Grams") -> kg * 1000.0
      to.startsWith("Pounds") -> kg / 0.45359237
      to.startsWith("Ounces") -> kg / 0.02834952
      else -> kg
    }
  }

  private fun convertTemperature(valIn: Double, from: String, to: String): Double {
    val celsius = when {
      from.startsWith("Celsius") -> valIn
      from.startsWith("Fahrenheit") -> (valIn - 32) * 5.0 / 9.0
      from.startsWith("Kelvin") -> valIn - 273.15
      else -> valIn
    }
    return when {
      to.startsWith("Celsius") -> celsius
      to.startsWith("Fahrenheit") -> (celsius * 9.0 / 5.0) + 32
      to.startsWith("Kelvin") -> celsius + 273.15
      else -> celsius
    }
  }

  // Haptic feedback
  private fun vibratePhone(durationMs: Long) {
    try {
      val app = getApplication<Application>()
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = app.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator?.vibrate(
          VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE)
        )
      } else {
        @Suppress("DEPRECATION")
        val vibrator = app.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
          @Suppress("DEPRECATION")
          vibrator?.vibrate(durationMs)
        }
      }
    } catch (_: Exception) {
      // Safe fallback if vibration hardware isn't available
    }
  }

  private fun vibratePattern() {
    try {
      val app = getApplication<Application>()
      val pattern = longArrayOf(0, 200, 150, 300)
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = app.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator?.vibrate(
          VibrationEffect.createWaveform(pattern, -1)
        )
      } else {
        @Suppress("DEPRECATION")
        val vibrator = app.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          vibrator?.vibrate(VibrationEffect.createWaveform(pattern, -1))
        } else {
          @Suppress("DEPRECATION")
          vibrator?.vibrate(pattern, -1)
        }
      }
    } catch (_: Exception) {
    }
  }

  override fun onCleared() {
    super.onCleared()
    timerJob?.cancel()
  }
}
