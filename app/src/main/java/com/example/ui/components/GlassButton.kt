package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalBlurNeonCustomization
import com.example.ui.theme.LocalGlassCustomization
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple

@Composable
fun GlassIconButton(
  icon: ImageVector,
  contentDescription: String?,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  size: Dp = 44.dp,
  iconSize: Dp = 22.dp,
  tint: Color = Color.White,
  isActive: Boolean = false,
  activeGlow: Color = NeonCyan
) {
  val glass = LocalGlassCustomization.current
  val neon = LocalBlurNeonCustomization.current

  val btnAlpha = (glass.buttonAlpha * glass.buttonBrightness).coerceIn(0.1f, 0.9f)
  val bgBrush = if (isActive) {
    Brush.linearGradient(
      listOf(
        activeGlow.copy(alpha = 0.45f),
        NeonPurple.copy(alpha = 0.35f)
      )
    )
  } else {
    Brush.linearGradient(
      listOf(
        Color.White.copy(alpha = 0.15f * glass.reflectionIntensity),
        Color(0xFF0F172A).copy(alpha = btnAlpha)
      )
    )
  }

  val borderBrush = if (isActive && neon.neonEnabled) {
    Brush.linearGradient(listOf(activeGlow, NeonPurple))
  } else {
    Brush.linearGradient(
      listOf(
        Color.White.copy(alpha = 0.35f),
        Color.White.copy(alpha = 0.08f)
      )
    )
  }

  Box(
    modifier = modifier
      .size(size)
      .clip(CircleShape)
      .background(bgBrush)
      .border(1.dp, borderBrush, CircleShape)
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = ripple(bounded = true, color = if (isActive) activeGlow else NeonCyan),
        onClick = onClick
      ),
    contentAlignment = Alignment.Center
  ) {
    Icon(
      imageVector = icon,
      contentDescription = contentDescription,
      tint = if (isActive) activeGlow else tint,
      modifier = Modifier.size(iconSize)
    )
  }
}

@Composable
fun GlassButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  icon: ImageVector? = null,
  isPrimary: Boolean = true,
  glowColor: Color = NeonCyan
) {
  val glass = LocalGlassCustomization.current
  val neon = LocalBlurNeonCustomization.current

  val shape = RoundedCornerShape(glass.cornerRadius.dp)
  val btnAlpha = (glass.buttonAlpha * glass.buttonBrightness).coerceIn(0.15f, 0.95f)

  val bgBrush = if (isPrimary) {
    Brush.horizontalGradient(
      listOf(
        glowColor.copy(alpha = 0.35f * neon.neonIntensity),
        NeonPurple.copy(alpha = 0.30f * neon.neonIntensity)
      )
    )
  } else {
    Brush.horizontalGradient(
      listOf(
        Color.White.copy(alpha = 0.12f),
        Color(0xFF0F172A).copy(alpha = btnAlpha)
      )
    )
  }

  val borderBrush = Brush.horizontalGradient(
    if (isPrimary && neon.neonEnabled) {
      listOf(glowColor, NeonPurple)
    } else {
      listOf(Color.White.copy(alpha = 0.4f), Color.White.copy(alpha = 0.1f))
    }
  )

  Row(
    modifier = modifier
      .clip(shape)
      .background(bgBrush)
      .border(1.dp, borderBrush, shape)
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = ripple(color = glowColor),
        onClick = onClick
      )
      .padding(horizontal = 20.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    if (icon != null) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = if (isPrimary) glowColor else Color.White,
        modifier = Modifier.size(18.dp)
      )
      Spacer(modifier = Modifier.width(8.dp))
    }
    Text(
      text = text,
      color = Color.White,
      fontWeight = FontWeight.SemiBold,
      fontSize = 14.sp
    )
  }
}
