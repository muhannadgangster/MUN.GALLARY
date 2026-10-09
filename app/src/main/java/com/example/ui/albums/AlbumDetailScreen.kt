package com.example.ui.albums

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Album
import com.example.data.model.MediaItem
import com.example.ui.components.EmptyStateView
import com.example.ui.components.GlassIconButton
import com.example.ui.components.MediaItemCard
import com.example.ui.gallery.GalleryViewModel
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonCyan

@Composable
fun AlbumDetailScreen(
  album: Album,
  viewModel: GalleryViewModel,
  onBack: () -> Unit,
  onItemClick: (MediaItem, List<MediaItem>) -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler(onBack = onBack)

  val allMedia by viewModel.filteredMedia.collectAsState()
  val albumItems = allMedia.filter { item ->
    item.bucketId == album.id || item.bucketName.equals(album.name, ignoreCase = true)
  }

  val selectedIds by viewModel.selectedIds.collectAsState()
  val isSelectionMode by viewModel.isSelectionMode.collectAsState()

  Column(modifier = modifier.fillMaxSize()) {
    // Header
    val headerShape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(headerShape)
        .background(
          Brush.verticalGradient(
            listOf(Color(0xFF070B14).copy(alpha = 0.85f), Color(0xFF0F172A).copy(alpha = 0.6f))
          )
        )
        .border(
          1.dp,
          Brush.verticalGradient(
            listOf(Color.White.copy(alpha = 0.15f), NeonCyan.copy(alpha = 0.2f))
          ),
          headerShape
        )
        .statusBarsPadding()
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        GlassIconButton(
          icon = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Back",
          onClick = onBack,
          size = 38.dp
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column {
          Text(
            text = album.name,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
          )
          Text(
            text = "${albumItems.size} items",
            color = ElectricBlue,
            fontSize = 12.sp
          )
        }
      }
    }

    if (albumItems.isEmpty()) {
      EmptyStateView(
        title = "Album is empty",
        message = "No photos or videos found in this folder."
      )
    } else {
      BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenWidth = maxWidth
        val columns = when {
          screenWidth >= 840.dp -> 6
          screenWidth >= 600.dp -> 4
          else -> 3
        }

        LazyVerticalGrid(
          columns = GridCells.Fixed(columns),
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(14.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(albumItems, key = { it.id }) { item ->
            val isSelected = selectedIds.contains(item.id)
            MediaItemCard(
              item = item,
              isSelected = isSelected,
              isSelectionMode = isSelectionMode,
              onClick = {
                if (isSelectionMode) {
                  viewModel.toggleSelection(item.id)
                } else {
                  onItemClick(item, albumItems)
                }
              },
              onLongClick = {
                if (!isSelectionMode) {
                  viewModel.enterSelectionMode(item.id)
                } else {
                  viewModel.toggleSelection(item.id)
                }
              }
            )
          }

          item {
            Spacer(modifier = Modifier.height(72.dp))
          }
        }
      }
    }
  }
}
