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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextDarkSecondary

@Composable
fun EmptyStateView(
  title: String,
  message: String,
  icon: ImageVector = Icons.Default.Image,
  actionButtonText: String? = null,
  onActionClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxSize()
      .padding(32.dp),
    contentAlignment = Alignment.Center
  ) {
    LiquidGlassSurface(
      modifier = Modifier.fillMaxWidth(),
      cornerRadius = 24.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        Box(
          modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(
              Brush.linearGradient(
                listOf(NeonCyan.copy(alpha = 0.2f), NeonPurple.copy(alpha = 0.15f))
              )
            )
            .border(1.dp, NeonCyan.copy(alpha = 0.4f), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = NeonCyan,
            modifier = Modifier.size(32.dp)
          )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
          text = title,
          color = Color.White,
          fontWeight = FontWeight.Bold,
          fontSize = 18.sp,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = message,
          color = TextDarkSecondary,
          fontSize = 13.sp,
          textAlign = TextAlign.Center,
          lineHeight = 18.sp
        )

        if (actionButtonText != null && onActionClick != null) {
          Spacer(modifier = Modifier.height(20.dp))
          GlassButton(
            text = actionButtonText,
            onClick = onActionClick,
            isPrimary = true,
            glowColor = NeonCyan
          )
        }
      }
    }
  }
}
