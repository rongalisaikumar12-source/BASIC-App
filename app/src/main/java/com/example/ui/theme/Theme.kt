package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = Indigo200,
  onPrimary = Slate950,
  primaryContainer = Indigo700,
  onPrimaryContainer = Color.White,
  secondary = Teal400,
  onSecondary = Slate950,
  secondaryContainer = Slate800,
  onSecondaryContainer = Teal100,
  tertiary = Amber500,
  onTertiary = Slate950,
  background = Slate950,
  onBackground = Slate100,
  surface = Slate900,
  onSurface = Slate100,
  surfaceVariant = Slate800,
  onSurfaceVariant = Slate300,
  outline = Slate700
)

private val LightColorScheme = lightColorScheme(
  primary = Indigo600,
  onPrimary = Color.White,
  primaryContainer = Indigo50,
  onPrimaryContainer = Indigo700,
  secondary = Teal600,
  onSecondary = Color.White,
  secondaryContainer = Teal100,
  onSecondaryContainer = Slate900,
  tertiary = Amber500,
  onTertiary = Color.White,
  background = Slate50,
  onBackground = Slate900,
  surface = Color.White,
  onSurface = Slate900,
  surfaceVariant = Slate100,
  onSurfaceVariant = Slate600,
  outline = Slate200
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Use our handcrafted refined theme for consistent elegance
  content: @Composable () -> Unit,
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
