package com.example.ui.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.PreferencesManager
import com.example.data.model.AppThemeMode
import com.example.data.model.SortOrder
import com.example.data.model.ViewMode
import com.example.ui.components.GlassButton
import com.example.ui.components.LiquidGlassSurface
import com.example.ui.gallery.GalleryViewModel
import com.example.ui.theme.AlertRed
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextDarkSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
  viewModel: GalleryViewModel,
  onNavigateToGlassCustomization: () -> Unit,
  onNavigateToWallpaper: () -> Unit,
  onNavigateToBlurNeon: () -> Unit,
  modifier: Modifier = Modifier
) {
  val prefsManager = viewModel.prefsManager
  val galleryPrefs by prefsManager.galleryPrefs.collectAsState()
  val allMedia by viewModel.filteredMedia.collectAsState()
  val isPermissionGranted by viewModel.isPermissionGranted.collectAsState()

  var showResetConfirmDialog by remember { mutableStateOf(false) }

  // Storage Stats Calculation
  val photoCount = allMedia.count { !it.isVideo }
  val videoCount = allMedia.count { it.isVideo }
  val totalSizeBytes = allMedia.sumOf { it.size }
  val formattedTotalStorage = remember(totalSizeBytes) {
    val kb = totalSizeBytes / 1024.0
    val mb = kb / 1024.0
    val gb = mb / 1024.0
    when {
      gb >= 1.0 -> String.format("%.2f GB", gb)
      mb >= 1.0 -> String.format("%.1f MB", mb)
      else -> String.format("%.1f KB", kb)
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
  ) {
    // Header
    val headerShape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(headerShape)
        .background(
          Brush.verticalGradient(
            listOf(Color(0xFF070B14).copy(alpha = 0.85f), Color(0xFF0F172A).copy(alpha = 0.6f))
          )
        )
        .border(
          1.dp,
          Brush.verticalGradient(
            listOf(Color.White.copy(alpha = 0.15f), NeonCyan.copy(alpha = 0.2f))
          ),
          headerShape
        )
        .statusBarsPadding()
        .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Settings",
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 22.sp,
            letterSpacing = 0.5.sp
          )
          Text(
            text = "Preferences & Liquid Glass System",
            color = ElectricBlue,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
          )
        }

        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.08f))
            .border(1.dp, NeonCyan.copy(alpha = 0.3f), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = NeonCyan,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }

    // Scrollable Settings List
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(16.dp)
        .navigationBarsPadding()
    ) {
      // 1. APPEARANCE SECTION
      SettingsSectionHeader(title = "APPEARANCE")
      LiquidGlassSurface(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
          // Theme Selector (Dark, Light, System)
          SettingsThemeRow(
            currentTheme = galleryPrefs.themeMode,
            onThemeSelected = { mode ->
              prefsManager.updateGalleryPrefs { it.copy(themeMode = mode) }
            }
          )

          SettingsDivider()

          // Glass Customization Subpage
          SettingsNavRow(
            icon = Icons.Default.AutoAwesome,
            title = "Liquid Glass Customization",
            subtitle = "Transparency, blur, reflection, corner radius & shadows",
            onClick = onNavigateToGlassCustomization
          )

          SettingsDivider()

          // Wallpaper Settings Subpage
          SettingsNavRow(
            icon = Icons.Default.Wallpaper,
            title = "Wallpaper Settings",
            subtitle = "Device gallery background, blur & dark overlay",
            onClick = onNavigateToWallpaper
          )

          SettingsDivider()

          // Blur & Neon Subpage
          SettingsNavRow(
            icon = Icons.Default.Lightbulb,
            title = "Blur & Neon",
            subtitle = "Background diffusion & cyber neon glow lights",
            onClick = onNavigateToBlurNeon
          )
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // 2. GALLERY PREFERENCES
      SettingsSectionHeader(title = "GALLERY")
      LiquidGlassSurface(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
          // View Mode (Grid vs List)
          SettingsToggleRow(
            icon = Icons.Default.GridView,
            title = "Grid View",
            subtitle = if (galleryPrefs.viewMode == ViewMode.GRID) "Displaying responsive grid" else "Displaying vertical list",
            checked = galleryPrefs.viewMode == ViewMode.GRID,
            onCheckedChange = { isGrid ->
              prefsManager.updateGalleryPrefs { it.copy(viewMode = if (isGrid) ViewMode.GRID else ViewMode.LIST) }
            }
          )

          SettingsDivider()

          // Show Videos
          SettingsToggleRow(
            icon = Icons.Default.Videocam,
            title = "Show Videos",
            subtitle = "Include video clips in main gallery timeline",
            checked = galleryPrefs.showVideos,
            onCheckedChange = { showVids ->
              prefsManager.updateGalleryPrefs { it.copy(showVideos = showVids) }
              viewModel.loadMedia()
            }
          )

          SettingsDivider()

          // Show Screenshots
          SettingsToggleRow(
            icon = Icons.Default.PhotoLibrary,
            title = "Show Screenshots",
            subtitle = "Include screenshots in main stream",
            checked = galleryPrefs.showScreenshots,
            onCheckedChange = { showShots ->
              prefsManager.updateGalleryPrefs { it.copy(showScreenshots = showShots) }
              viewModel.loadMedia()
            }
          )
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // 3. BEHAVIOR
      SettingsSectionHeader(title = "BEHAVIOR")
      LiquidGlassSurface(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
          SettingsToggleRow(
            icon = Icons.Default.Vibration,
            title = "Haptic Feedback",
            subtitle = "Vibrate on selection and interactions",
            checked = galleryPrefs.hapticFeedback,
            onCheckedChange = { haptic ->
              prefsManager.updateGalleryPrefs { it.copy(hapticFeedback = haptic) }
            }
          )

          SettingsDivider()

          SettingsToggleRow(
            icon = Icons.Default.Animation,
            title = "Animations",
            subtitle = "Smooth liquid transitions and reveals",
            checked = galleryPrefs.animationEnabled,
            onCheckedChange = { anim ->
              prefsManager.updateGalleryPrefs { it.copy(animationEnabled = anim) }
            }
          )
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // 4. STORAGE & SUMMARY
      SettingsSectionHeader(title = "STORAGE")
      LiquidGlassSurface(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Storage,
                contentDescription = null,
                tint = NeonCyan,
                modifier = Modifier.size(24.dp)
              )
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "Media Storage",
                  color = Color.White,
                  fontWeight = FontWeight.SemiBold,
                  fontSize = 15.sp
                )
                Text(
                  text = "$photoCount photos, $videoCount videos",
                  color = TextDarkSecondary,
                  fontSize = 12.sp
                )
              }
            }

            Text(
              text = formattedTotalStorage,
              color = NeonCyan,
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // 5. PRIVACY & PERMISSIONS
      SettingsSectionHeader(title = "PRIVACY")
      LiquidGlassSurface(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Security,
                contentDescription = null,
                tint = if (isPermissionGranted) SuccessGreen else AlertRed,
                modifier = Modifier.size(24.dp)
              )
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "MediaStore Permission",
                  color = Color.White,
                  fontWeight = FontWeight.SemiBold,
                  fontSize = 15.sp
                )
                Text(
                  text = if (isPermissionGranted) "Full Device Media Access Granted" else "Access Not Yet Granted",
                  color = TextDarkSecondary,
                  fontSize = 12.sp
                )
              }
            }

            Text(
              text = if (isPermissionGranted) "Granted" else "Pending",
              color = if (isPermissionGranted) SuccessGreen else AlertRed,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // 6. ABOUT
      SettingsSectionHeader(title = "ABOUT")
      LiquidGlassSurface(modifier = Modifier.fillMaxWidth()) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Image(
            painter = painterResource(id = R.drawable.ic_mun_launcher),
            contentDescription = "M-U-N Gallery Logo",
            modifier = Modifier
              .size(54.dp)
              .clip(CircleShape)
          )

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = "M-U-N Gallery",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
          )

          Text(
            text = "Version 1.0 (Flagship Edition)",
            color = ElectricBlue,
            fontSize = 12.sp
          )

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "Your Moments, Beautifully.\nCrafted with Liquid Glass aesthetics and offline-first media privacy.",
            color = TextDarkSecondary,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // 7. RESET ALL CUSTOMIZATION BUTTON
      LiquidGlassSurface(
        modifier = Modifier.fillMaxWidth(),
        onClick = { showResetConfirmDialog = true }
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = null,
            tint = AlertRed,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "Reset All Customization",
            color = AlertRed,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(72.dp))
    }
  }

  // Reset Confirmation Dialog
  if (showResetConfirmDialog) {
    BasicAlertDialog(onDismissRequest = { showResetConfirmDialog = false }) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(24.dp))
          .background(Color(0xFF0F172A))
          .border(1.dp, AlertRed.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
          .padding(24.dp)
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "Reset All Customization?",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
          )
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = "This will restore default liquid glass physics, wallpaper, blur, and neon lighting. Your photos and videos will not be deleted.",
            color = TextDarkSecondary,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
          )
          Spacer(modifier = Modifier.height(20.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
          ) {
            GlassButton(
              text = "Cancel",
              onClick = { showResetConfirmDialog = false },
              isPrimary = false
            )
            Spacer(modifier = Modifier.width(12.dp))
            GlassButton(
              text = "Reset All",
              onClick = {
                prefsManager.resetAllCustomization()
                showResetConfirmDialog = false
              },
              isPrimary = true,
              glowColor = AlertRed
            )
          }
        }
      }
    }
  }
}

@Composable
private fun SettingsSectionHeader(title: String) {
  Text(
    text = title,
    color = NeonCyan,
    fontSize = 11.sp,
    fontWeight = FontWeight.ExtraBold,
    letterSpacing = 1.sp,
    modifier = Modifier.padding(start = 6.dp, bottom = 8.dp)
  )
}

@Composable
private fun SettingsDivider() {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(1.dp)
      .background(Color.White.copy(alpha = 0.08f))
  )
}

@Composable
private fun SettingsNavRow(
  icon: ImageVector,
  title: String,
  subtitle: String,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = ripple(color = NeonCyan),
        onClick = onClick
      )
      .padding(horizontal = 16.dp, vertical = 14.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(38.dp)
        .clip(RoundedCornerShape(10.dp))
        .background(Color.White.copy(alpha = 0.06f)),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = NeonCyan,
        modifier = Modifier.size(20.dp)
      )
    }

    Spacer(modifier = Modifier.width(14.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        color = Color.White,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp
      )
      Text(
        text = subtitle,
        color = TextDarkSecondary,
        fontSize = 12.sp
      )
    }

    Icon(
      imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
      contentDescription = null,
      tint = TextDarkSecondary,
      modifier = Modifier.size(14.dp)
    )
  }
}

@Composable
private fun SettingsToggleRow(
  icon: ImageVector,
  title: String,
  subtitle: String,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
      Box(
        modifier = Modifier
          .size(38.dp)
          .clip(RoundedCornerShape(10.dp))
        .background(Color.White.copy(alpha = 0.06f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = ElectricBlue,
          modifier = Modifier.size(20.dp)
        )
      }

      Spacer(modifier = Modifier.width(14.dp))

      Column {
        Text(
          text = title,
          color = Color.White,
          fontWeight = FontWeight.SemiBold,
          fontSize = 15.sp
        )
        Text(
          text = subtitle,
          color = TextDarkSecondary,
          fontSize = 12.sp
        )
      }
    }

    Switch(
      checked = checked,
      onCheckedChange = onCheckedChange,
      colors = SwitchDefaults.colors(
        checkedThumbColor = NeonCyan,
        checkedTrackColor = NeonCyan.copy(alpha = 0.4f),
        uncheckedThumbColor = Color.LightGray,
        uncheckedTrackColor = Color.DarkGray
      )
    )
  }
}

@Composable
private fun SettingsThemeRow(
  currentTheme: AppThemeMode,
  onThemeSelected: (AppThemeMode) -> Unit
) {
  Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .size(38.dp)
          .clip(RoundedCornerShape(10.dp))
          .background(Color.White.copy(alpha = 0.06f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Brightness4,
          contentDescription = null,
          tint = NeonPurple,
          modifier = Modifier.size(20.dp)
        )
      }
      Spacer(modifier = Modifier.width(14.dp))
      Column {
        Text(
          text = "App Theme",
          color = Color.White,
          fontWeight = FontWeight.SemiBold,
          fontSize = 15.sp
        )
        Text(
          text = "Choose light, dark or system appearance",
          color = TextDarkSecondary,
          fontSize = 12.sp
        )
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      AppThemeMode.values().forEach { mode ->
        val isSelected = currentTheme == mode
        val pillShape = RoundedCornerShape(12.dp)

        Box(
          modifier = Modifier
            .weight(1f)
            .clip(pillShape)
            .then(
              if (isSelected) {
                Modifier.background(
                  Brush.horizontalGradient(
                    listOf(NeonCyan.copy(alpha = 0.3f), NeonPurple.copy(alpha = 0.25f))
                  )
                )
              } else {
                Modifier.background(Color.White.copy(alpha = 0.05f))
              }
            )
            .border(
              1.dp,
              if (isSelected) NeonCyan else Color.White.copy(alpha = 0.1f),
              pillShape
            )
            .clickable { onThemeSelected(mode) }
            .padding(vertical = 10.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = mode.label,
            color = if (isSelected) NeonCyan else Color.White,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 13.sp
          )
        }
      }
    }
  }
}
