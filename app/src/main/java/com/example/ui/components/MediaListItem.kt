package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.MediaItem
import com.example.ui.theme.FavoritePink
import com.example.ui.theme.LocalBlurNeonCustomization
import com.example.ui.theme.LocalGlassCustomization
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextDarkSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MediaListItem(
  item: MediaItem,
  isSelected: Boolean,
  isSelectionMode: Boolean,
  onClick: () -> Unit,
  onLongClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val glass = LocalGlassCustomization.current
  val neon = LocalBlurNeonCustomization.current

  val itemShape = RoundedCornerShape(16.dp)
  val borderBrush = if (isSelected) {
    Brush.linearGradient(listOf(NeonCyan, NeonPurple))
  } else {
    Brush.linearGradient(
      listOf(
        Color.White.copy(alpha = 0.15f * glass.reflectionIntensity),
        NeonCyan.copy(alpha = if (neon.neonEnabled) 0.15f else 0.05f)
      )
    )
  }

  val dateStr = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault())
    .format(Date(item.dateAdded * 1000L))

  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(itemShape)
      .background(Color(0xFF0F172A).copy(alpha = (glass.glassAlpha * 1.4f).coerceIn(0.15f, 0.95f)))
      .border(if (isSelected) 2.dp else 1.dp, borderBrush, itemShape)
      .combinedClickable(
        onClick = onClick,
        onLongClick = onLongClick
      )
      .padding(10.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Thumbnail
      Box(
        modifier = Modifier
          .size(64.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(Color(0xFF1E293B))
      ) {
        AsyncImage(
          model = ImageRequest.Builder(context)
            .data(item.uri)
            .crossfade(true)
            .build(),
          contentDescription = item.name,
          contentScale = ContentScale.Crop,
          modifier = Modifier.size(64.dp)
        )

        if (item.isVideo) {
          Box(
            modifier = Modifier
              .align(Alignment.BottomEnd)
              .padding(4.dp)
              .clip(RoundedCornerShape(6.dp))
              .background(Color.Black.copy(alpha = 0.7f))
              .padding(horizontal = 4.dp, vertical = 2.dp)
          ) {
            Text(
              text = item.formattedDuration,
              color = NeonCyan,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      Spacer(modifier = Modifier.width(12.dp))

      // Info
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = item.name,
          color = Color.White,
          fontWeight = FontWeight.SemiBold,
          fontSize = 15.sp,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Text(
          text = dateStr,
          color = TextDarkSecondary,
          fontSize = 12.sp
        )
        Text(
          text = "${item.formattedSize} • ${if (item.isVideo) "Video" else "Photo"}",
          color = NeonCyan.copy(alpha = 0.8f),
          fontSize = 11.sp
        )
      }

      // Actions / status
      if (item.isFavorite) {
        Icon(
          imageVector = Icons.Default.Favorite,
          contentDescription = "Favorite",
          tint = FavoritePink,
          modifier = Modifier
            .size(20.dp)
            .padding(end = 6.dp)
        )
      }

      if (isSelectionMode || isSelected) {
        Box(
          modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(if (isSelected) NeonCyan else Color.Black.copy(alpha = 0.4f))
            .border(1.5.dp, if (isSelected) Color.White else Color.White.copy(alpha = 0.6f), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          androidx.compose.animation.AnimatedVisibility(
            visible = isSelected,
            enter = scaleIn(),
            exit = scaleOut()
          ) {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = null,
              tint = Color.Black,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }
    }
  }
}
