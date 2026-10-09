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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MediaItem
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextDarkSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaInfoBottomSheet(
  item: MediaItem,
  sheetState: SheetState,
  onDismiss: () -> Unit
) {
  val dateFormatted = SimpleDateFormat("MMMM dd, yyyy • HH:mm:ss", Locale.getDefault())
    .format(Date(item.dateAdded * 1000L))

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = Color(0xFF0F172A).copy(alpha = 0.96f),
    scrimColor = Color.Black.copy(alpha = 0.6f),
    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)
        .padding(bottom = 36.dp)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 16.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Info,
          contentDescription = null,
          tint = NeonCyan,
          modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
          text = "Details",
          color = Color.White,
          fontSize = 20.sp,
          fontWeight = FontWeight.Bold
        )
      }

      InfoRow(
        icon = Icons.AutoMirrored.Filled.InsertDriveFile,
        label = "File Name",
        value = item.name
      )

      InfoRow(
        icon = Icons.Default.CalendarToday,
        label = "Date Added",
        value = dateFormatted
      )

      if (item.width > 0 && item.height > 0) {
        InfoRow(
          icon = Icons.Default.AspectRatio,
          label = "Resolution",
          value = "${item.width} × ${item.height} px"
        )
      }

      InfoRow(
        icon = Icons.Default.Storage,
        label = "File Size",
        value = "${item.formattedSize} (${item.mimeType})"
      )

      InfoRow(
        icon = Icons.Default.Folder,
        label = "Location",
        value = if (item.path.isNotBlank()) item.path else "Device MediaStore (${item.bucketName})"
      )
    }
  }
}

@Composable
private fun InfoRow(
  icon: ImageVector,
  label: String,
  value: String
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 8.dp),
    verticalAlignment = Alignment.Top
  ) {
    Box(
      modifier = Modifier
        .size(36.dp)
        .clip(RoundedCornerShape(10.dp))
        .background(Color.White.copy(alpha = 0.05f))
        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = ElectricBlue,
        modifier = Modifier.size(18.dp)
      )
    }

    Spacer(modifier = Modifier.width(14.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = label,
        color = TextDarkSecondary,
        fontSize = 12.sp
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = value,
        color = Color.White,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium
      )
    }
  }
}
