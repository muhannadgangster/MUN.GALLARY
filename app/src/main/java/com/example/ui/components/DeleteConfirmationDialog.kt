package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.example.ui.theme.AlertRed
import com.example.ui.theme.TextDarkSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteConfirmationDialog(
  itemCount: Int,
  onConfirm: () -> Unit,
  onDismiss: () -> Unit
) {
  val title = if (itemCount > 1) "Delete $itemCount items?" else "Delete this item?"
  val message = if (itemCount > 1) {
    "These items will be permanently removed from your device storage."
  } else {
    "This media will be permanently removed from your device storage."
  }

  BasicAlertDialog(
    onDismissRequest = onDismiss
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(24.dp))
        .background(
          Brush.verticalGradient(
            listOf(
              Color(0xFF1E1428).copy(alpha = 0.95f),
              Color(0xFF0F172A).copy(alpha = 0.98f)
            )
          )
        )
        .border(1.dp, AlertRed.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
        .padding(24.dp)
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
      ) {
        Box(
          modifier = Modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(AlertRed.copy(alpha = 0.2f))
            .border(1.dp, AlertRed.copy(alpha = 0.5f), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = null,
            tint = AlertRed,
            modifier = Modifier.size(28.dp)
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

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

        Spacer(modifier = Modifier.height(24.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End,
          verticalAlignment = Alignment.CenterVertically
        ) {
          GlassButton(
            text = "Cancel",
            onClick = onDismiss,
            isPrimary = false
          )

          Spacer(modifier = Modifier.width(12.dp))

          GlassButton(
            text = "Delete",
            onClick = onConfirm,
            isPrimary = true,
            glowColor = AlertRed
          )
        }
      }
    }
  }
}
