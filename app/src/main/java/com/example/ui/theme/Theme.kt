package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.example.data.local.BlurNeonCustomization
import com.example.data.local.GlassCustomization
import com.example.data.local.WallpaperCustomization
import com.example.data.model.AppThemeMode

val LocalGlassCustomization = staticCompositionLocalOf { GlassCustomization() }
val LocalBlurNeonCustomization = staticCompositionLocalOf { BlurNeonCustomization() }
val LocalWallpaperCustomization = staticCompositionLocalOf { WallpaperCustomization() }

private val LiquidDarkColorScheme = darkColorScheme(
  primary = NeonCyan,
  onPrimary = Color.Black,
  primaryContainer = LiquidDarkCard,
  onPrimaryContainer = NeonCyan,
  secondary = NeonPurple,
  onSecondary = Color.White,
  secondaryContainer = LiquidDarkSurface,
  onSecondaryContainer = ElectricBlue,
  tertiary = NeonBlue,
  onTertiary = Color.White,
  background = LiquidDarkBg,
  onBackground = TextDarkPrimary,
  surface = LiquidDarkCard,
  onSurface = TextDarkPrimary,
  surfaceVariant = LiquidDarkSurface,
  onSurfaceVariant = TextDarkSecondary,
  outline = GlassBorderDark,
  error = AlertRed
)

private val LiquidLightColorScheme = lightColorScheme(
  primary = NeonBlue,
  onPrimary = Color.White,
  primaryContainer = LiquidLightCard,
  onPrimaryContainer = NeonBlue,
  secondary = DeepViolet,
  onSecondary = Color.White,
  secondaryContainer = LiquidLightSurface,
  onSecondaryContainer = DeepViolet,
  tertiary = LiquidTeal,
  onTertiary = Color.White,
  background = LiquidLightBg,
  onBackground = TextLightPrimary,
  surface = LiquidLightCard,
  onSurface = TextLightPrimary,
  surfaceVariant = LiquidLightSurface,
  onSurfaceVariant = TextLightSecondary,
  outline = GlassBorderLight,
  error = AlertRed
)

@Composable
fun MunGalleryTheme(
  themeMode: AppThemeMode = AppThemeMode.DARK,
  glassCustomization: GlassCustomization = GlassCustomization(),
  blurNeonCustomization: BlurNeonCustomization = BlurNeonCustomization(),
  wallpaperCustomization: WallpaperCustomization = WallpaperCustomization(),
  content: @Composable () -> Unit
) {
  val isDark = when (themeMode) {
    AppThemeMode.DARK -> true
    AppThemeMode.LIGHT -> false
    AppThemeMode.SYSTEM -> isSystemInDarkTheme()
  }

  val colorScheme = if (isDark) LiquidDarkColorScheme else LiquidLightColorScheme

  CompositionLocalProvider(
    LocalGlassCustomization provides glassCustomization,
    LocalBlurNeonCustomization provides blurNeonCustomization,
    LocalWallpaperCustomization provides wallpaperCustomization
  ) {
    MaterialTheme(
      colorScheme = colorScheme,
      typography = Typography,
      content = content
    )
  }
}
