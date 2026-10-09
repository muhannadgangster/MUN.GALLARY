package com.example.ui.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BlurNeonCustomization
import com.example.data.local.PreferencesManager
import com.example.ui.components.GlassIconButton
import com.example.ui.components.GlassSlider
import com.example.ui.components.LiquidGlassSurface
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextDarkSecondary

@Composable
fun BlurNeonSettingsScreen(
  prefsManager: PreferencesManager,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler(onBack = onBack)

  val bn by prefsManager.blurNeonState.collectAsState()

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
            listOf(Color.White.copy(alpha = 0.15f), NeonPurple.copy(alpha = 0.3f))
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
              text = "Blur & Neon",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 18.sp
            )
            Text(
              text = "Lighting & Atmosphere",
              color = ElectricBlue,
              fontSize = 12.sp
            )
          }
        }

        GlassIconButton(
          icon = Icons.Default.Refresh,
          contentDescription = "Reset Blur & Neon Defaults",
          onClick = {
            prefsManager.updateBlurNeon { BlurNeonCustomization() }
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
      // Live Glow Preview Box
      Text(
        text = "NEON GLOW PREVIEW",
        color = NeonPurple,
        fontSize = 11.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(bottom = 8.dp)
      )

      LiquidGlassSurface(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 20.dp),
        cornerRadius = 22.dp
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(22.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier
              .size(56.dp)
              .clip(RoundedCornerShape(16.dp))
              .background(
                Brush.linearGradient(
                  if (bn.neonEnabled) listOf(NeonCyan, NeonPurple)
                  else listOf(Color.Gray, Color.DarkGray)
                )
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Lightbulb,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(28.dp)
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          Text(
            text = if (bn.neonEnabled) "Neon Accents Active" else "Neon Accents Disabled",
            color = if (bn.neonEnabled) NeonCyan else Color.LightGray,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
          )

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = "Intensity: ${(bn.neonIntensity * 100).toInt()}% • Blur: ${bn.blurIntensity.toInt()}px",
            color = TextDarkSecondary,
            fontSize = 12.sp
          )
        }
      }

      // Neon Lights Section
      Text(
        text = "NEON LIGHTING",
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
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Neon Lights",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
              )
              Text(
                text = "Enable cyber cyan & violet edge luminescence",
                color = TextDarkSecondary,
                fontSize = 11.sp
              )
            }

            Switch(
              checked = bn.neonEnabled,
              onCheckedChange = { chk ->
                prefsManager.updateBlurNeon { it.copy(neonEnabled = chk) }
              },
              colors = SwitchDefaults.colors(
                checkedThumbColor = NeonPurple,
                checkedTrackColor = NeonPurple.copy(alpha = 0.4f),
                uncheckedThumbColor = Color.LightGray,
                uncheckedTrackColor = Color.DarkGray
              )
            )
          }

          if (bn.neonEnabled) {
            Spacer(modifier = Modifier.height(10.dp))
            GlassSlider(
              label = "Neon Intensity",
              value = bn.neonIntensity,
              onValueChange = { v -> prefsManager.updateBlurNeon { it.copy(neonIntensity = v) } },
              valueRange = 0.2f..2.0f,
              valueDisplay = String.format("%.2fx", bn.neonIntensity)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Background Blur Section
      Text(
        text = "BLUR EFFECTS",
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
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Background Blur",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
              )
              Text(
                text = "Simulate frosted glass background diffusion",
                color = TextDarkSecondary,
                fontSize = 11.sp
              )
            }

            Switch(
              checked = bn.backgroundBlurEnabled,
              onCheckedChange = { chk ->
                prefsManager.updateBlurNeon { it.copy(backgroundBlurEnabled = chk) }
              },
              colors = SwitchDefaults.colors(
                checkedThumbColor = NeonCyan,
                checkedTrackColor = NeonCyan.copy(alpha = 0.4f),
                uncheckedThumbColor = Color.LightGray,
                uncheckedTrackColor = Color.DarkGray
              )
            )
          }

          if (bn.backgroundBlurEnabled) {
            Spacer(modifier = Modifier.height(10.dp))
            GlassSlider(
              label = "Blur Intensity",
              value = bn.blurIntensity,
              onValueChange = { v -> prefsManager.updateBlurNeon { it.copy(blurIntensity = v) } },
              valueRange = 4f..40f,
              valueDisplay = "${bn.blurIntensity.toInt()} px"
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(40.dp))
    }
  }
}
