package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MediaItemCard(
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

  val cardShape = RoundedCornerShape(glass.cornerRadius.coerceIn(12, 28).dp)

  val borderBrush = if (isSelected) {
    Brush.linearGradient(listOf(NeonCyan, NeonPurple))
  } else {
    Brush.linearGradient(
      listOf(
        Color.White.copy(alpha = 0.25f * glass.reflectionIntensity),
        Color.Transparent,
        NeonCyan.copy(alpha = if (neon.neonEnabled) 0.2f else 0.05f)
      )
    )
  }

  val borderWidth = if (isSelected) 2.dp else 1.dp

  Box(
    modifier = modifier
      .aspectRatio(1f)
      .clip(cardShape)
      .background(Color(0xFF0F172A))
      .border(borderWidth, borderBrush, cardShape)
      .combinedClickable(
        onClick = onClick,
        onLongClick = onLongClick
      )
  ) {
    AsyncImage(
      model = ImageRequest.Builder(context)
        .data(item.uri)
        .crossfade(true)
        .build(),
      contentDescription = item.name,
      contentScale = ContentScale.Crop,
      modifier = Modifier.fillMaxSize()
    )

    // Subtle glass bottom vignette
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          Brush.verticalGradient(
            colors = listOf(
              Color.Transparent,
              Color.Black.copy(alpha = 0.45f)
            ),
            startY = 150f
          )
        )
    )

    // Top-left: Favorite badge
    if (item.isFavorite) {
      Box(
        modifier = Modifier
          .align(Alignment.TopStart)
          .padding(6.dp)
          .size(24.dp)
          .clip(CircleShape)
          .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Favorite,
          contentDescription = "Favorite",
          tint = FavoritePink,
          modifier = Modifier.size(14.dp)
        )
      }
    }

    // Bottom: Video indicator with duration
    if (item.isVideo) {
      Row(
        modifier = Modifier
          .align(Alignment.BottomStart)
          .padding(6.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(Color.Black.copy(alpha = 0.65f))
          .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.PlayArrow,
          contentDescription = null,
          tint = NeonCyan,
          modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
          text = item.formattedDuration,
          color = Color.White,
          fontSize = 10.sp,
          fontWeight = FontWeight.SemiBold
        )
      }
    }

    // Top-Right: Selection checkbox
    if (isSelectionMode || isSelected) {
      Box(
        modifier = Modifier
          .align(Alignment.TopEnd)
          .padding(6.dp)
          .size(24.dp)
          .clip(CircleShape)
          .background(
            if (isSelected) NeonCyan else Color.Black.copy(alpha = 0.5f)
          )
          .border(
            1.5.dp,
            if (isSelected) Color.White else Color.White.copy(alpha = 0.7f),
            CircleShape
          ),
        contentAlignment = Alignment.Center
      ) {
        androidx.compose.animation.AnimatedVisibility(
          visible = isSelected,
          enter = scaleIn(),
          exit = scaleOut()
        ) {
          Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Selected",
            tint = Color.Black,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }
  }
}
