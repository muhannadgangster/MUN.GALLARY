package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextDarkSecondary

@Composable
fun GlassSlider(
  label: String,
  value: Float,
  onValueChange: (Float) -> Unit,
  valueRange: ClosedFloatingPointRange<Float>,
  modifier: Modifier = Modifier,
  valueDisplay: String = String.format("%.2f", value),
  steps: Int = 0
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 6.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = label,
        color = Color.White,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium
      )
      Text(
        text = valueDisplay,
        color = NeonCyan,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold
      )
    }

    Spacer(modifier = Modifier.height(4.dp))

    Slider(
      value = value,
      onValueChange = onValueChange,
      valueRange = valueRange,
      steps = steps,
      colors = SliderDefaults.colors(
        thumbColor = NeonCyan,
        activeTrackColor = NeonCyan,
        inactiveTrackColor = Color.White.copy(alpha = 0.15f),
        activeTickColor = Color.Transparent,
        inactiveTickColor = Color.Transparent
      ),
      modifier = Modifier.fillMaxWidth()
    )
  }
}
