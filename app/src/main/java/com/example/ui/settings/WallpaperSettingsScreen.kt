package com.example.ui.settings

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.local.PreferencesManager
import com.example.data.local.WallpaperCustomization
import com.example.ui.components.GlassButton
import com.example.ui.components.GlassIconButton
import com.example.ui.components.GlassSlider
import com.example.ui.components.LiquidGlassSurface
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextDarkSecondary

@Composable
fun WallpaperSettingsScreen(
  prefsManager: PreferencesManager,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler(onBack = onBack)

  val context = LocalContext.current
  val wp by prefsManager.wallpaperState.collectAsState()

  val pickMediaLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri: Uri? ->
    if (uri != null) {
      prefsManager.updateWallpaper { it.copy(customWallpaperUri = uri.toString()) }
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFF070B14))
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
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          GlassIconButton(
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            onClick = onBack,
            size = 38.dp
          )
          Spacer(modifier = Modifier.width(14.dp))
          Column {
            Text(
              text = "Wallpaper Settings",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 18.sp
            )
            Text(
              text = "Customize App Background",
              color = ElectricBlue,
              fontSize = 12.sp
            )
          }
        }

        GlassIconButton(
          icon = Icons.Default.Refresh,
          contentDescription = "Reset Wallpaper Defaults",
          onClick = {
            prefsManager.updateWallpaper { WallpaperCustomization() }
          },
          size = 38.dp
        )
      }
    }

    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(20.dp)
        .navigationBarsPadding()
    ) {
      // Wallpaper Preview Box
      Text(
        text = "PREVIEW",
        color = NeonCyan,
        fontSize = 11.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(bottom = 8.dp)
      )

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(220.dp)
          .clip(RoundedCornerShape(20.dp))
          .border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
      ) {
        if (wp.customWallpaperUri != null) {
          AsyncImage(
            model = ImageRequest.Builder(context)
              .data(wp.customWallpaperUri)
              .crossfade(true)
              .build(),
            contentDescription = "Wallpaper Preview",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )
        } else {
          Image(
            painter = painterResource(id = R.drawable.bg_liquid_glass),
            contentDescription = "Default Liquid Wallpaper",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )
        }

        if (wp.darkOverlay) {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(Color.Black.copy(alpha = (1f - wp.wallpaperOpacity).coerceIn(0.2f, 0.9f)))
          )
        }

        // Overlay preview glass card
        LiquidGlassSurface(
          modifier = Modifier
            .padding(16.dp)
            .align(Alignment.Center)
            .fillMaxWidth(0.85f),
          cornerRadius = 16.dp
        ) {
          Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = "Liquid Glass Effect",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = if (wp.customWallpaperUri != null) "Custom Device Image" else "M-U-N Liquid Background",
              color = NeonCyan,
              fontSize = 12.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Action buttons: Pick from Gallery, Reset to Default
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        GlassButton(
          text = "Choose from Gallery",
          icon = Icons.Default.Image,
          onClick = {
            pickMediaLauncher.launch(
              PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
          },
          isPrimary = true,
          glowColor = NeonCyan,
          modifier = Modifier.weight(1f)
        )

        if (wp.customWallpaperUri != null) {
          GlassButton(
            text = "Reset Default",
            icon = Icons.Default.Refresh,
            onClick = {
              prefsManager.updateWallpaper { it.copy(customWallpaperUri = null) }
            },
            isPrimary = false,
            modifier = Modifier.weight(0.8f)
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Sliders
      Text(
        text = "WALLPAPER CONTROLS",
        color = TextDarkSecondary,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(bottom = 6.dp)
      )

      LiquidGlassSurface(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 20.dp
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
          GlassSlider(
            label = "Wallpaper Opacity",
            value = wp.wallpaperOpacity,
            onValueChange = { v -> prefsManager.updateWallpaper { it.copy(wallpaperOpacity = v) } },
            valueRange = 0.10f..1.0f,
            valueDisplay = "${(wp.wallpaperOpacity * 100).toInt()}%"
          )

          GlassSlider(
            label = "Wallpaper Blur",
            value = wp.wallpaperBlur,
            onValueChange = { v -> prefsManager.updateWallpaper { it.copy(wallpaperBlur = v) } },
            valueRange = 0f..40f,
            valueDisplay = "${wp.wallpaperBlur.toInt()} px"
          )

          GlassSlider(
            label = "Wallpaper Brightness",
            value = wp.wallpaperBrightness,
            onValueChange = { v -> prefsManager.updateWallpaper { it.copy(wallpaperBrightness = v) } },
            valueRange = 0.2f..1.5f,
            valueDisplay = String.format("%.2fx", wp.wallpaperBrightness)
          )

          Spacer(modifier = Modifier.height(8.dp))

          // Dark Overlay Switch
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Dark Glass Overlay",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
              )
              Text(
                text = "Enhances contrast and readability",
                color = TextDarkSecondary,
                fontSize = 11.sp
              )
            }

            Switch(
              checked = wp.darkOverlay,
              onCheckedChange = { chk ->
                prefsManager.updateWallpaper { it.copy(darkOverlay = chk) }
              },
              colors = SwitchDefaults.colors(
                checkedThumbColor = NeonCyan,
                checkedTrackColor = NeonCyan.copy(alpha = 0.4f),
                uncheckedThumbColor = Color.LightGray,
                uncheckedTrackColor = Color.DarkGray
              )
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(40.dp))
    }
  }
}
