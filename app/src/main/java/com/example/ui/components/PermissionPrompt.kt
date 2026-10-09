package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextDarkSecondary

@Composable
fun PermissionPrompt(
  onRequestPermission: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxSize()
      .padding(24.dp),
    contentAlignment = Alignment.Center
  ) {
    LiquidGlassSurface(
      modifier = Modifier.fillMaxWidth(),
      cornerRadius = 28.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        // Glowing Icon
        Box(
          modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(
              Brush.linearGradient(
                listOf(NeonCyan.copy(alpha = 0.25f), NeonPurple.copy(alpha = 0.25f))
              )
            )
            .border(1.5.dp, Brush.linearGradient(listOf(NeonCyan, NeonPurple)), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.PhotoLibrary,
            contentDescription = null,
            tint = NeonCyan,
            modifier = Modifier.size(36.dp)
          )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
          text = "Storage Access Required",
          color = Color.White,
          fontWeight = FontWeight.Bold,
          fontSize = 20.sp,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = "M-U-N Gallery needs permission to access your device photos and videos to display and organize your moments.",
          color = TextDarkSecondary,
          fontSize = 14.sp,
          textAlign = TextAlign.Center,
          lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        GlassButton(
          text = "Allow Access",
          icon = Icons.Default.Lock,
          onClick = onRequestPermission,
          isPrimary = true,
          glowColor = NeonCyan
        )
      }
    }
  }
}
