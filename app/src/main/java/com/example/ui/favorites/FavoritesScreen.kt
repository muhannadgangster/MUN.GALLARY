package com.example.ui.favorites

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
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
import com.example.data.model.MediaItem
import com.example.ui.components.EmptyStateView
import com.example.ui.components.MediaItemCard
import com.example.ui.gallery.GalleryViewModel
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.FavoritePink
import com.example.ui.theme.NeonCyan

@Composable
fun FavoritesScreen(
  viewModel: GalleryViewModel,
  onItemClick: (MediaItem, List<MediaItem>) -> Unit,
  modifier: Modifier = Modifier
) {
  val allMedia by viewModel.filteredMedia.collectAsState()
  val favorites = allMedia.filter { it.isFavorite }
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
            listOf(Color.White.copy(alpha = 0.15f), FavoritePink.copy(alpha = 0.3f))
          ),
          headerShape
        )
        .statusBarsPadding()
        .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Favorites",
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 22.sp,
            letterSpacing = 0.5.sp
          )
          Text(
            text = "${favorites.size} Cherished Moments",
            color = ElectricBlue,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
          )
        }

        Box(
          modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(FavoritePink.copy(alpha = 0.15f))
            .border(1.dp, FavoritePink.copy(alpha = 0.4f), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = null,
            tint = FavoritePink,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }

    if (favorites.isEmpty()) {
      EmptyStateView(
        title = "No Favorites Yet",
        message = "Tap the heart icon on any photo or video to add it to your favorites.",
        icon = Icons.Default.Favorite
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
          items(favorites, key = { it.id }) { item ->
            val isSelected = selectedIds.contains(item.id)
            MediaItemCard(
              item = item,
              isSelected = isSelected,
              isSelectionMode = isSelectionMode,
              onClick = {
                if (isSelectionMode) {
                  viewModel.toggleSelection(item.id)
                } else {
                  onItemClick(item, favorites)
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
