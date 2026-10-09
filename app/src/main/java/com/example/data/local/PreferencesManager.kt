package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.AppThemeMode
import com.example.data.model.SortOrder
import com.example.data.model.ViewMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class GlassCustomization(
  val glassAlpha: Float = 0.22f,
  val glassBorderAlpha: Float = 0.35f,
  val blurIntensity: Float = 20f,
  val glassBrightness: Float = 1.0f,
  val reflectionIntensity: Float = 0.65f,
  val cornerRadius: Int = 20,
  val shadowDepth: Int = 8,
  val buttonAlpha: Float = 0.30f,
  val buttonBrightness: Float = 1.0f,
  val animationIntensity: Float = 1.0f
)

data class BlurNeonCustomization(
  val backgroundBlurEnabled: Boolean = true,
  val blurIntensity: Float = 22f,
  val neonEnabled: Boolean = true,
  val neonIntensity: Float = 1.0f
)

data class WallpaperCustomization(
  val customWallpaperUri: String? = null,
  val wallpaperOpacity: Float = 0.85f,
  val wallpaperBlur: Float = 12f,
  val wallpaperBrightness: Float = 0.85f,
  val darkOverlay: Boolean = true
)

data class GalleryPreferences(
  val themeMode: AppThemeMode = AppThemeMode.DARK,
  val viewMode: ViewMode = ViewMode.GRID,
  val sortOrder: SortOrder = SortOrder.NEWEST,
  val showVideos: Boolean = true,
  val showScreenshots: Boolean = true,
  val hapticFeedback: Boolean = true,
  val soundEffects: Boolean = false,
  val animationEnabled: Boolean = true
)

class PreferencesManager(context: Context) {
  private val prefs: SharedPreferences =
    context.applicationContext.getSharedPreferences("mun_gallery_prefs", Context.MODE_PRIVATE)

  private val _glassState = MutableStateFlow(loadGlassSettings())
  val glassState: StateFlow<GlassCustomization> = _glassState.asStateFlow()

  private val _blurNeonState = MutableStateFlow(loadBlurNeonSettings())
  val blurNeonState: StateFlow<BlurNeonCustomization> = _blurNeonState.asStateFlow()

  private val _wallpaperState = MutableStateFlow(loadWallpaperSettings())
  val wallpaperState: StateFlow<WallpaperCustomization> = _wallpaperState.asStateFlow()

  private val _galleryPrefs = MutableStateFlow(loadGalleryPreferences())
  val galleryPrefs: StateFlow<GalleryPreferences> = _galleryPrefs.asStateFlow()

  private fun loadGlassSettings(): GlassCustomization {
    return GlassCustomization(
      glassAlpha = prefs.getFloat("glass_alpha", 0.22f),
      glassBorderAlpha = prefs.getFloat("glass_border_alpha", 0.35f),
      blurIntensity = prefs.getFloat("blur_intensity", 20f),
      glassBrightness = prefs.getFloat("glass_brightness", 1.0f),
      reflectionIntensity = prefs.getFloat("reflection_intensity", 0.65f),
      cornerRadius = prefs.getInt("corner_radius", 20),
      shadowDepth = prefs.getInt("shadow_depth", 8),
      buttonAlpha = prefs.getFloat("button_alpha", 0.30f),
      buttonBrightness = prefs.getFloat("button_brightness", 1.0f),
      animationIntensity = prefs.getFloat("animation_intensity", 1.0f)
    )
  }

  private fun loadBlurNeonSettings(): BlurNeonCustomization {
    return BlurNeonCustomization(
      backgroundBlurEnabled = prefs.getBoolean("bg_blur_enabled", true),
      blurIntensity = prefs.getFloat("neon_blur_intensity", 22f),
      neonEnabled = prefs.getBoolean("neon_enabled", true),
      neonIntensity = prefs.getFloat("neon_intensity", 1.0f)
    )
  }

  private fun loadWallpaperSettings(): WallpaperCustomization {
    return WallpaperCustomization(
      customWallpaperUri = prefs.getString("custom_wallpaper_uri", null),
      wallpaperOpacity = prefs.getFloat("wallpaper_opacity", 0.85f),
      wallpaperBlur = prefs.getFloat("wallpaper_blur", 12f),
      wallpaperBrightness = prefs.getFloat("wallpaper_brightness", 0.85f),
      darkOverlay = prefs.getBoolean("dark_overlay", true)
    )
  }

  private fun loadGalleryPreferences(): GalleryPreferences {
    val themeStr = prefs.getString("theme_mode", AppThemeMode.DARK.name) ?: AppThemeMode.DARK.name
    val viewStr = prefs.getString("view_mode", ViewMode.GRID.name) ?: ViewMode.GRID.name
    val sortStr = prefs.getString("sort_order", SortOrder.NEWEST.name) ?: SortOrder.NEWEST.name

    return GalleryPreferences(
      themeMode = runCatching { AppThemeMode.valueOf(themeStr) }.getOrDefault(AppThemeMode.DARK),
      viewMode = runCatching { ViewMode.valueOf(viewStr) }.getOrDefault(ViewMode.GRID),
      sortOrder = runCatching { SortOrder.valueOf(sortStr) }.getOrDefault(SortOrder.NEWEST),
      showVideos = prefs.getBoolean("show_videos", true),
      showScreenshots = prefs.getBoolean("show_screenshots", true),
      hapticFeedback = prefs.getBoolean("haptic_feedback", true),
      soundEffects = prefs.getBoolean("sound_effects", false),
      animationEnabled = prefs.getBoolean("animation_enabled", true)
    )
  }

  fun updateGlass(update: (GlassCustomization) -> GlassCustomization) {
    val newSettings = update(_glassState.value)
    _glassState.value = newSettings
    prefs.edit()
      .putFloat("glass_alpha", newSettings.glassAlpha)
      .putFloat("glass_border_alpha", newSettings.glassBorderAlpha)
      .putFloat("blur_intensity", newSettings.blurIntensity)
      .putFloat("glass_brightness", newSettings.glassBrightness)
      .putFloat("reflection_intensity", newSettings.reflectionIntensity)
      .putInt("corner_radius", newSettings.cornerRadius)
      .putInt("shadow_depth", newSettings.shadowDepth)
      .putFloat("button_alpha", newSettings.buttonAlpha)
      .putFloat("button_brightness", newSettings.buttonBrightness)
      .putFloat("animation_intensity", newSettings.animationIntensity)
      .apply()
  }

  fun updateBlurNeon(update: (BlurNeonCustomization) -> BlurNeonCustomization) {
    val newSettings = update(_blurNeonState.value)
    _blurNeonState.value = newSettings
    prefs.edit()
      .putBoolean("bg_blur_enabled", newSettings.backgroundBlurEnabled)
      .putFloat("neon_blur_intensity", newSettings.blurIntensity)
      .putBoolean("neon_enabled", newSettings.neonEnabled)
      .putFloat("neon_intensity", newSettings.neonIntensity)
      .apply()
  }

  fun updateWallpaper(update: (WallpaperCustomization) -> WallpaperCustomization) {
    val newSettings = update(_wallpaperState.value)
    _wallpaperState.value = newSettings
    prefs.edit()
      .putString("custom_wallpaper_uri", newSettings.customWallpaperUri)
      .putFloat("wallpaper_opacity", newSettings.wallpaperOpacity)
      .putFloat("wallpaper_blur", newSettings.wallpaperBlur)
      .putFloat("wallpaper_brightness", newSettings.wallpaperBrightness)
      .putBoolean("dark_overlay", newSettings.darkOverlay)
      .apply()
  }

  fun updateGalleryPrefs(update: (GalleryPreferences) -> GalleryPreferences) {
    val newPrefs = update(_galleryPrefs.value)
    _galleryPrefs.value = newPrefs
    prefs.edit()
      .putString("theme_mode", newPrefs.themeMode.name)
      .putString("view_mode", newPrefs.viewMode.name)
      .putString("sort_order", newPrefs.sortOrder.name)
      .putBoolean("show_videos", newPrefs.showVideos)
      .putBoolean("show_screenshots", newPrefs.showScreenshots)
      .putBoolean("haptic_feedback", newPrefs.hapticFeedback)
      .putBoolean("sound_effects", newPrefs.soundEffects)
      .putBoolean("animation_enabled", newPrefs.animationEnabled)
      .apply()
  }

  fun resetAllCustomization() {
    val defaultGlass = GlassCustomization()
    val defaultBlurNeon = BlurNeonCustomization()
    val defaultWallpaper = WallpaperCustomization()

    _glassState.value = defaultGlass
    _blurNeonState.value = defaultBlurNeon
    _wallpaperState.value = defaultWallpaper

    prefs.edit()
      .remove("glass_alpha")
      .remove("glass_border_alpha")
      .remove("blur_intensity")
      .remove("glass_brightness")
      .remove("reflection_intensity")
      .remove("corner_radius")
      .remove("shadow_depth")
      .remove("button_alpha")
      .remove("button_brightness")
      .remove("animation_intensity")
      .remove("bg_blur_enabled")
      .remove("neon_blur_intensity")
      .remove("neon_enabled")
      .remove("neon_intensity")
      .remove("custom_wallpaper_uri")
      .remove("wallpaper_opacity")
      .remove("wallpaper_blur")
      .remove("wallpaper_brightness")
      .remove("dark_overlay")
      .apply()
  }

  companion object {
    @Volatile
    private var INSTANCE: PreferencesManager? = null

    fun getInstance(context: Context): PreferencesManager {
      return INSTANCE ?: synchronized(this) {
        val instance = PreferencesManager(context)
        INSTANCE = instance
        instance
      }
    }
  }
}
