package com.example.ui.viewer

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.MediaItem
import com.example.ui.components.GlassIconButton
import com.example.ui.components.MediaInfoBottomSheet
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.FavoritePink
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextDarkSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoViewerScreen(
  items: List<MediaItem>,
  initialIndex: Int,
  onBack: () -> Unit,
  onToggleFavorite: (MediaItem) -> Unit,
  onShare: (MediaItem) -> Unit,
  onEdit: (MediaItem) -> Unit,
  onDelete: (MediaItem) -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler(onBack = onBack)

  if (items.isEmpty()) {
    onBack()
    return
  }

  val safeInitialIndex = initialIndex.coerceIn(0, items.size - 1)
  val pagerState = rememberPagerState(
    initialPage = safeInitialIndex,
    pageCount = { items.size }
  )

  var showControls by remember { mutableStateOf(true) }
  var showInfoSheet by remember { mutableStateOf(false) }
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  val currentItem = items.getOrNull(pagerState.currentPage) ?: items[0]

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color.Black)
  ) {
    // Pager for swipe between photos
    HorizontalPager(
      state = pagerState,
      modifier = Modifier.fillMaxSize(),
      beyondViewportPageCount = 1
    ) { page ->
      val item = items[page]
      ZoomableImage(
        item = item,
        onTap = { showControls = !showControls }
      )
    }

    // Top Bar (Controls)
    AnimatedVisibility(
      visible = showControls,
      enter = fadeIn(),
      exit = fadeOut(),
      modifier = Modifier.align(Alignment.TopCenter)
    ) {
      val topShape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(topShape)
          .background(
            Brush.verticalGradient(
              listOf(Color.Black.copy(alpha = 0.85f), Color.Black.copy(alpha = 0.2f))
            )
          )
          .statusBarsPadding()
          .padding(horizontal = 16.dp, vertical = 10.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
          ) {
            GlassIconButton(
              icon = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              onClick = onBack,
              size = 40.dp
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
              Text(
                text = currentItem.name,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              val dateStr = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault())
                .format(Date(currentItem.dateAdded * 1000L))
              Text(
                text = "$dateStr (${pagerState.currentPage + 1}/${items.size})",
                color = ElectricBlue,
                fontSize = 12.sp
              )
            }
          }

          GlassIconButton(
            icon = Icons.Default.Info,
            contentDescription = "Info",
            onClick = { showInfoSheet = true },
            size = 40.dp
          )
        }
      }
    }

    // Bottom Bar (Controls)
    AnimatedVisibility(
      visible = showControls,
      enter = fadeIn(),
      exit = fadeOut(),
      modifier = Modifier.align(Alignment.BottomCenter)
    ) {
      val bottomShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(bottomShape)
          .background(
            Brush.verticalGradient(
              listOf(Color.Black.copy(alpha = 0.3f), Color.Black.copy(alpha = 0.9f))
            )
          )
          .navigationBarsPadding()
          .padding(horizontal = 24.dp, vertical = 14.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceEvenly,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Favorite
          GlassIconButton(
            icon = if (currentItem.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = "Favorite",
            onClick = { onToggleFavorite(currentItem) },
            size = 46.dp,
            iconSize = 22.dp,
            tint = if (currentItem.isFavorite) FavoritePink else Color.White,
            isActive = currentItem.isFavorite,
            activeGlow = FavoritePink
          )

          // Share
          GlassIconButton(
            icon = Icons.Default.Share,
            contentDescription = "Share",
            onClick = { onShare(currentItem) },
            size = 46.dp,
            iconSize = 22.dp
          )

          // Edit (Only for photos)
          if (!currentItem.isVideo) {
            GlassIconButton(
              icon = Icons.Default.Edit,
              contentDescription = "Edit",
              onClick = { onEdit(currentItem) },
              size = 46.dp,
              iconSize = 22.dp,
              tint = NeonCyan
            )
          }

          // Delete
          GlassIconButton(
            icon = Icons.Default.Delete,
            contentDescription = "Delete",
            onClick = { onDelete(currentItem) },
            size = 46.dp,
            iconSize = 22.dp,
            tint = Color(0xFFEF4444)
          )
        }
      }
    }

    // Info Bottom Sheet
    if (showInfoSheet) {
      MediaInfoBottomSheet(
        item = currentItem,
        sheetState = sheetState,
        onDismiss = { showInfoSheet = false }
      )
    }
  }
}

@Composable
fun ZoomableImage(
  item: MediaItem,
  onTap: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var scale by remember { mutableFloatStateOf(1f) }
  var offset by remember { mutableStateOf(Offset.Zero) }

  val animatedScale by animateFloatAsState(
    targetValue = scale,
    label = "scaleAnim"
  )

  val transformableState = rememberTransformableState { zoomChange, offsetChange, _ ->
    val newScale = (scale * zoomChange).coerceIn(1f, 5f)
    scale = newScale
    if (newScale > 1f) {
      offset += offsetChange
    } else {
      offset = Offset.Zero
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .pointerInput(Unit) {
        detectTapGestures(
          onTap = { onTap() },
          onDoubleTap = {
            if (scale > 1f) {
              scale = 1f
              offset = Offset.Zero
            } else {
              scale = 2.5f
            }
          }
        )
      }
      .transformable(state = transformableState),
    contentAlignment = Alignment.Center
  ) {
    AsyncImage(
      model = ImageRequest.Builder(context)
        .data(item.uri)
        .crossfade(true)
        .build(),
      contentDescription = item.name,
      contentScale = ContentScale.Fit,
      modifier = Modifier
        .fillMaxSize()
        .graphicsLayer(
          scaleX = animatedScale,
          scaleY = animatedScale,
          translationX = offset.x,
          translationY = offset.y
        )
    )
  }
}
