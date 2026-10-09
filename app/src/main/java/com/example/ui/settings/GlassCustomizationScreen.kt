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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
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
import com.example.data.local.GlassCustomization
import com.example.data.local.PreferencesManager
import com.example.ui.components.GlassButton
import com.example.ui.components.GlassIconButton
import com.example.ui.components.GlassSlider
import com.example.ui.components.LiquidGlassSurface
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextDarkSecondary

@Composable
fun GlassCustomizationScreen(
  prefsManager: PreferencesManager,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler(onBack = onBack)

  val glass by prefsManager.glassState.collectAsState()

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
              text = "Glass Customization",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 18.sp
            )
            Text(
              text = "Tailor Liquid Glass Physics",
              color = ElectricBlue,
              fontSize = 12.sp
            )
          }
        }

        GlassIconButton(
          icon = Icons.Default.Refresh,
          contentDescription = "Reset Glass Defaults",
          onClick = {
            prefsManager.updateGlass { GlassCustomization() }
          },
          size = 38.dp
        )
      }
    }

    // Scrollable Sliders & Live Preview
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(20.dp)
        .navigationBarsPadding()
    ) {
      // Live Preview Glass Card
      Text(
        text = "LIVE GLASS PREVIEW",
        color = NeonCyan,
        fontSize = 11.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(bottom = 8.dp)
      )

      LiquidGlassSurface(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 20.dp),
        cornerRadius = glass.cornerRadius.dp
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = NeonCyan,
                modifier = Modifier.size(24.dp)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = "Liquid Surface",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
              )
            }
            Text(
              text = "Corner: ${glass.cornerRadius}dp",
              color = ElectricBlue,
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "Watch this glass card react in real time as you adjust the transparency, blur, depth, and reflection physics below.",
            color = TextDarkSecondary,
            fontSize = 13.sp,
            lineHeight = 18.sp
          )

          Spacer(modifier = Modifier.height(14.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
          ) {
            GlassButton(
              text = "Glass Button",
              onClick = {},
              isPrimary = true,
              glowColor = NeonCyan
            )
          }
        }
      }

      // Sliders Section
      Text(
        text = "SURFACE CONTROLS",
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
            label = "Glass Transparency",
            value = glass.glassAlpha,
            onValueChange = { v -> prefsManager.updateGlass { it.copy(glassAlpha = v) } },
            valueRange = 0.05f..0.70f,
            valueDisplay = "${(glass.glassAlpha * 100).toInt()}%"
          )

          GlassSlider(
            label = "Border Transparency",
            value = glass.glassBorderAlpha,
            onValueChange = { v -> prefsManager.updateGlass { it.copy(glassBorderAlpha = v) } },
            valueRange = 0.10f..0.85f,
            valueDisplay = "${(glass.glassBorderAlpha * 100).toInt()}%"
          )

          GlassSlider(
            label = "Blur Intensity",
            value = glass.blurIntensity,
            onValueChange = { v -> prefsManager.updateGlass { it.copy(blurIntensity = v) } },
            valueRange = 0f..50f,
            valueDisplay = "${glass.blurIntensity.toInt()} px"
          )

          GlassSlider(
            label = "Glass Brightness",
            value = glass.glassBrightness,
            onValueChange = { v -> prefsManager.updateGlass { it.copy(glassBrightness = v) } },
            valueRange = 0.5f..1.5f,
            valueDisplay = String.format("%.2fx", glass.glassBrightness)
          )

          GlassSlider(
            label = "Reflection Intensity",
            value = glass.reflectionIntensity,
            onValueChange = { v -> prefsManager.updateGlass { it.copy(reflectionIntensity = v) } },
            valueRange = 0f..1.0f,
            valueDisplay = "${(glass.reflectionIntensity * 100).toInt()}%"
          )

          GlassSlider(
            label = "Corner Radius",
            value = glass.cornerRadius.toFloat(),
            onValueChange = { v -> prefsManager.updateGlass { it.copy(cornerRadius = v.toInt()) } },
            valueRange = 8f..36f,
            valueDisplay = "${glass.cornerRadius} dp"
          )

          GlassSlider(
            label = "Shadow Depth",
            value = glass.shadowDepth.toFloat(),
            onValueChange = { v -> prefsManager.updateGlass { it.copy(shadowDepth = v.toInt()) } },
            valueRange = 0f..24f,
            valueDisplay = "${glass.shadowDepth} dp"
          )
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      Text(
        text = "INTERACTION CONTROLS",
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
            label = "Button Transparency",
            value = glass.buttonAlpha,
            onValueChange = { v -> prefsManager.updateGlass { it.copy(buttonAlpha = v) } },
            valueRange = 0.10f..0.80f,
            valueDisplay = "${(glass.buttonAlpha * 100).toInt()}%"
          )

          GlassSlider(
            label = "Button Brightness",
            value = glass.buttonBrightness,
            onValueChange = { v -> prefsManager.updateGlass { it.copy(buttonBrightness = v) } },
            valueRange = 0.5f..1.5f,
            valueDisplay = String.format("%.2fx", glass.buttonBrightness)
          )

          GlassSlider(
            label = "Animation Intensity",
            value = glass.animationIntensity,
            onValueChange = { v -> prefsManager.updateGlass { it.copy(animationIntensity = v) } },
            valueRange = 0.2f..2.0f,
            valueDisplay = String.format("%.1fx", glass.animationIntensity)
          )
        }
      }

      Spacer(modifier = Modifier.height(40.dp))
    }
  }
}
