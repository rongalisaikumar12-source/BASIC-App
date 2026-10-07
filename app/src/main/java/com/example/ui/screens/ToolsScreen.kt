package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.HourglassBottom
import androidx.compose.material.icons.outlined.PlusOne
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ConverterCategory
import com.example.ui.ToolTab
import com.example.ui.theme.Amber500
import com.example.ui.theme.Teal600
import java.util.Locale

@Composable
fun ToolsScreen(
  activeTool: ToolTab,
  onSelectTool: (ToolTab) -> Unit,
  // Timer props
  timerTotal: Int,
  timerRemaining: Int,
  isTimerRunning: Boolean,
  onTimerPreset: (Int) -> Unit,
  onToggleTimer: () -> Unit,
  onResetTimer: () -> Unit,
  // Counter props
  counterValue: Int,
  counterStep: Int,
  counterTarget: Int,
  onIncrementCounter: () -> Unit,
  onDecrementCounter: () -> Unit,
  onResetCounter: () -> Unit,
  onSetCounterStep: (Int) -> Unit,
  // Converter props
  converterCategory: ConverterCategory,
  converterInput: String,
  converterFromUnit: String,
  converterToUnit: String,
  converterResult: String,
  onSetConverterCategory: (ConverterCategory) -> Unit,
  onSetConverterInput: (String) -> Unit,
  onSetConverterFromUnit: (String) -> Unit,
  onSetConverterToUnit: (String) -> Unit,
  onSwapConverterUnits: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
  ) {
    // Tool Selector segmented row
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .background(MaterialTheme.colorScheme.surfaceVariant)
        .padding(4.dp),
      horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      ToolTab.values().forEach { tool ->
        val isSelected = activeTool == tool
        val (title, icon) = when (tool) {
          ToolTab.TIMER -> Pair("Focus Timer", Icons.Outlined.Timer)
          ToolTab.COUNTER -> Pair("Tally Counter", Icons.Outlined.PlusOne)
          ToolTab.CONVERTER -> Pair("Unit Convert", Icons.Outlined.Calculate)
        }

        val bg by animateColorAsState(
          targetValue = if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent,
          label = "tool_bg"
        )
        val fg by animateColorAsState(
          targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
          label = "tool_fg"
        )

        Box(
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .clickable { onSelectTool(tool) }
            .padding(vertical = 10.dp)
            .testTag("tool_tab_${tool.name.lowercase()}"),
          contentAlignment = Alignment.Center
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Icon(
              imageVector = icon,
              contentDescription = null,
              tint = fg,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = title,
              style = MaterialTheme.typography.labelMedium,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              color = fg
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Active tool pane
    when (activeTool) {
      ToolTab.TIMER -> {
        FocusTimerPane(
          totalSeconds = timerTotal,
          remainingSeconds = timerRemaining,
          isRunning = isTimerRunning,
          onPreset = onTimerPreset,
          onToggle = onToggleTimer,
          onReset = onResetTimer
        )
      }
      ToolTab.COUNTER -> {
        TallyCounterPane(
          value = counterValue,
          step = counterStep,
          target = counterTarget,
          onIncrement = onIncrementCounter,
          onDecrement = onDecrementCounter,
          onReset = onResetCounter,
          onSetStep = onSetCounterStep
        )
      }
      ToolTab.CONVERTER -> {
        UnitConverterPane(
          category = converterCategory,
          input = converterInput,
          fromUnit = converterFromUnit,
          toUnit = converterToUnit,
          result = converterResult,
          onCategoryChange = onSetConverterCategory,
          onInputChange = onSetConverterInput,
          onFromUnitChange = onSetConverterFromUnit,
          onToUnitChange = onSetConverterToUnit,
          onSwapUnits = onSwapConverterUnits
        )
      }
    }
  }
}

// ------------------- FOCUS TIMER -------------------

@Composable
private fun FocusTimerPane(
  totalSeconds: Int,
  remainingSeconds: Int,
  isRunning: Boolean,
  onPreset: (Int) -> Unit,
  onToggle: () -> Unit,
  onReset: () -> Unit
) {
  val progress = if (totalSeconds > 0) remainingSeconds.toFloat() / totalSeconds.toFloat() else 0f
  val animatedProgress by animateFloatAsState(targetValue = progress, label = "timer_anim")

  val minutes = remainingSeconds / 60
  val seconds = remainingSeconds % 60
  val timeText = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .testTag("focus_timer_pane"),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(20.dp),
    contentPadding = PaddingValues(bottom = 96.dp)
  ) {
    // Presets
    item(key = "timer_presets") {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
      ) {
        listOf(5 to "5m Break", 15 to "15m Short", 25 to "25m Focus", 45 to "45m Deep").forEach { (mins, label) ->
          val isCurrent = totalSeconds == mins * 60
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isCurrent) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
              .clickable { onPreset(mins) }
              .testTag("timer_preset_${mins}")
          ) {
            Text(
              text = label,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
              style = MaterialTheme.typography.labelMedium,
              fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
              color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }

    // Circular Timer Display
    item(key = "timer_circle") {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier.size(220.dp),
            contentAlignment = Alignment.Center
          ) {
            CircularProgressIndicator(
              progress = { 1f },
              modifier = Modifier.fillMaxSize(),
              color = MaterialTheme.colorScheme.surfaceVariant,
              strokeWidth = 12.dp
            )
            CircularProgressIndicator(
              progress = { animatedProgress },
              modifier = Modifier.fillMaxSize(),
              color = if (remainingSeconds == 0) Teal600 else MaterialTheme.colorScheme.primary,
              strokeWidth = 12.dp
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = timeText,
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 44.sp),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = when {
                  remainingSeconds == 0 -> "Completed! 🎉"
                  isRunning -> "Focusing..."
                  else -> "Ready to Focus"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = if (remainingSeconds == 0) Teal600 else MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          Spacer(modifier = Modifier.height(28.dp))

          // Control buttons
          Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            OutlinedButton(
              onClick = onReset,
              shape = CircleShape,
              modifier = Modifier
                .size(54.dp)
                .testTag("reset_timer_btn"),
              contentPadding = PaddingValues(0.dp)
            ) {
              Icon(
                Icons.Default.Refresh,
                contentDescription = "Reset timer",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            Button(
              onClick = onToggle,
              shape = RoundedCornerShape(20.dp),
              modifier = Modifier
                .height(54.dp)
                .padding(horizontal = 8.dp)
                .testTag("toggle_timer_btn"),
              colors = ButtonDefaults.buttonColors(
                containerColor = if (isRunning) Amber500 else MaterialTheme.colorScheme.primary
              )
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
              ) {
                Icon(
                  imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                  contentDescription = if (isRunning) "Pause" else "Start",
                  tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = if (isRunning) "Pause" else "Start Session",
                  style = MaterialTheme.typography.titleMedium,
                  color = Color.White
                )
              }
            }
          }
        }
      }
    }
  }
}

// ------------------- TALLY COUNTER -------------------

@Composable
private fun TallyCounterPane(
  value: Int,
  step: Int,
  target: Int,
  onIncrement: () -> Unit,
  onDecrement: () -> Unit,
  onReset: () -> Unit,
  onSetStep: (Int) -> Unit
) {
  val progress = if (target > 0) (value.toFloat() / target.toFloat()).coerceIn(0f, 1f) else 0f
  val animatedProgress by animateFloatAsState(targetValue = progress, label = "counter_progress")

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .testTag("tally_counter_pane"),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(18.dp),
    contentPadding = PaddingValues(bottom = 96.dp)
  ) {
    // Goal & Step configuration
    item(key = "step_selector") {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Step Increment:",
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          listOf(1, 5, 10).forEach { s ->
            val isSelected = step == s
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier
                .clickable { onSetStep(s) }
                .testTag("counter_step_$s")
            ) {
              Text(
                text = "+$s",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }
    }

    // Huge Tap Card
    item(key = "counter_main_card") {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(24.dp))
          .clickable { onIncrement() }
          .testTag("counter_tap_surface"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 36.dp, horizontal = 20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "TAP ANYWHERE TO COUNT",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.sp
          )

          Spacer(modifier = Modifier.height(16.dp))

          Text(
            text = "$value",
            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 72.sp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.testTag("counter_value_text")
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Target Progress bar
          Column(
            modifier = Modifier.fillMaxWidth(0.8f),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            LinearProgressIndicator(
              progress = { animatedProgress },
              modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
              color = if (value >= target) Teal600 else MaterialTheme.colorScheme.primary,
              trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Target: $target ($value / $target)",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }

    // Decrement & Reset Buttons
    item(key = "counter_actions") {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        OutlinedButton(
          onClick = onDecrement,
          modifier = Modifier
            .weight(1f)
            .height(50.dp)
            .testTag("counter_decrement_btn"),
          shape = RoundedCornerShape(14.dp)
        ) {
          Icon(Icons.Default.Remove, contentDescription = "Decrement", modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Minus ($step)")
        }

        OutlinedButton(
          onClick = onReset,
          modifier = Modifier
            .weight(1f)
            .height(50.dp)
            .testTag("counter_reset_btn"),
          shape = RoundedCornerShape(14.dp)
        ) {
          Icon(Icons.Default.Refresh, contentDescription = "Reset", modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Reset")
        }
      }
    }
  }
}

// ------------------- QUICK CONVERTER -------------------

@Composable
private fun UnitConverterPane(
  category: ConverterCategory,
  input: String,
  fromUnit: String,
  toUnit: String,
  result: String,
  onCategoryChange: (ConverterCategory) -> Unit,
  onInputChange: (String) -> Unit,
  onFromUnitChange: (String) -> Unit,
  onToUnitChange: (String) -> Unit,
  onSwapUnits: () -> Unit
) {
  val unitOptions = when (category) {
    ConverterCategory.LENGTH -> listOf(
      "Meters (m)",
      "Centimeters (cm)",
      "Kilometers (km)",
      "Feet (ft)",
      "Inches (in)",
      "Miles (mi)"
    )
    ConverterCategory.WEIGHT -> listOf(
      "Kilograms (kg)",
      "Grams (g)",
      "Pounds (lb)",
      "Ounces (oz)"
    )
    ConverterCategory.TEMPERATURE -> listOf(
      "Celsius (°C)",
      "Fahrenheit (°F)",
      "Kelvin (K)"
    )
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .testTag("unit_converter_pane"),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    contentPadding = PaddingValues(bottom = 96.dp)
  ) {
    // Category Chips
    item(key = "conv_categories") {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        ConverterCategory.values().forEach { cat ->
          val isSelected = category == cat
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
              .weight(1f)
              .clickable { onCategoryChange(cat) }
              .testTag("conv_cat_${cat.name.lowercase()}")
          ) {
            Box(
              modifier = Modifier.padding(vertical = 10.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = cat.title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }
    }

    // Input Card
    item(key = "conv_card") {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
          verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          // Input value
          OutlinedTextField(
            value = input,
            onValueChange = onInputChange,
            label = { Text("Value to Convert") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("conv_input_field"),
            shape = RoundedCornerShape(14.dp)
          )

          // From unit dropdown
          UnitDropdownSelector(
            label = "From",
            selectedUnit = fromUnit,
            options = unitOptions,
            onSelect = onFromUnitChange,
            testTag = "from_unit_selector"
          )

          // Swap Units Button
          Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
          ) {
            IconButton(
              onClick = onSwapUnits,
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .testTag("swap_units_btn")
            ) {
              Icon(
                imageVector = Icons.Default.SwapVert,
                contentDescription = "Swap units",
                tint = MaterialTheme.colorScheme.primary
              )
            }
          }

          // To unit dropdown
          UnitDropdownSelector(
            label = "To",
            selectedUnit = toUnit,
            options = unitOptions,
            onSelect = onToUnitChange,
            testTag = "to_unit_selector"
          )
        }
      }
    }

    // Result Card
    item(key = "conv_result") {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("conv_result_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
        ) {
          Text(
            text = "Converted Result",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
          )

          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
          ) {
            Text(
              text = result,
              style = MaterialTheme.typography.headlineMedium.copy(fontSize = 32.sp),
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Text(
              text = toUnit,
              style = MaterialTheme.typography.titleMedium,
              color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
              modifier = Modifier.padding(bottom = 4.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
private fun UnitDropdownSelector(
  label: String,
  selectedUnit: String,
  options: List<String>,
  onSelect: (String) -> Unit,
  testTag: String
) {
  var expanded by remember { mutableStateOf(false) }

  Box(modifier = Modifier.fillMaxWidth()) {
    Column {
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Spacer(modifier = Modifier.height(4.dp))
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(MaterialTheme.colorScheme.surfaceVariant)
          .clickable { expanded = true }
          .padding(horizontal = 14.dp, vertical = 12.dp)
          .testTag(testTag),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = selectedUnit,
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.Medium,
          color = MaterialTheme.colorScheme.onSurface
        )
        Icon(
          imageVector = Icons.Default.ArrowDropDown,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    DropdownMenu(
      expanded = expanded,
      onDismissRequest = { expanded = false }
    ) {
      options.forEach { unit ->
        DropdownMenuItem(
          text = { Text(unit) },
          onClick = {
            onSelect(unit)
            expanded = false
          }
        )
      }
    }
  }
}
