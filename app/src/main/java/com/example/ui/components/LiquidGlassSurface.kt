package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LocalBlurNeonCustomization
import com.example.ui.theme.LocalGlassCustomization
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple

@Composable
fun LiquidGlassSurface(
  modifier: Modifier = Modifier,
  shape: Shape? = null,
  cornerRadius: Dp? = null,
  borderWidth: Dp = 1.dp,
  onClick: (() -> Unit)? = null,
  highlightColor: Color? = null,
  content: @Composable () -> Unit
) {
  val glass = LocalGlassCustomization.current
  val neon = LocalBlurNeonCustomization.current

  val finalRadius = cornerRadius ?: glass.cornerRadius.dp
  val finalShape = shape ?: RoundedCornerShape(finalRadius)

  val baseAlpha = (glass.glassAlpha * glass.glassBrightness).coerceIn(0.04f, 0.95f)
  val borderAlpha = (glass.glassBorderAlpha * if (neon.neonEnabled) neon.neonIntensity else 1f).coerceIn(0.08f, 0.95f)

  val baseColor = Color(0xFF0F172A).copy(alpha = baseAlpha)
  val topHighlight = (highlightColor ?: Color.White).copy(alpha = 0.22f * glass.reflectionIntensity)
  val bottomHighlight = Color(0xFF020617).copy(alpha = 0.40f * baseAlpha)

  val neonGlowColor1 = if (neon.neonEnabled) NeonCyan.copy(alpha = 0.45f * borderAlpha) else Color.White.copy(alpha = borderAlpha * 0.3f)
  val neonGlowColor2 = if (neon.neonEnabled) NeonPurple.copy(alpha = 0.35f * borderAlpha) else Color.White.copy(alpha = borderAlpha * 0.1f)

  val borderBrush = Brush.linearGradient(
    colors = listOf(
      neonGlowColor1,
      neonGlowColor2,
      Color.White.copy(alpha = 0.08f)
    )
  )

  val surfaceBrush = Brush.verticalGradient(
    colors = listOf(
      topHighlight,
      baseColor,
      bottomHighlight
    )
  )

  val shadowElevation = (glass.shadowDepth.dp).coerceAtLeast(0.dp)

  val clickableModifier = if (onClick != null) {
    Modifier.clickable(
      interactionSource = remember { MutableInteractionSource() },
      indication = ripple(color = NeonCyan),
      onClick = onClick
    )
  } else Modifier

  Box(
    modifier = modifier
      .then(if (shadowElevation > 0.dp) Modifier.shadow(shadowElevation, finalShape, ambientColor = NeonCyan, spotColor = NeonPurple) else Modifier)
      .clip(finalShape)
      .background(surfaceBrush)
      .border(borderWidth, borderBrush, finalShape)
      .then(clickableModifier)
  ) {
    content()
  }
}
