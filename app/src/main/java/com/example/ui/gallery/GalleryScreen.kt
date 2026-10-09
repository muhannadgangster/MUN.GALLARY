package com.example.ui.gallery

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CategoryFilter
import com.example.data.model.MediaItem
import com.example.data.model.ViewMode
import com.example.ui.components.EmptyStateView
import com.example.ui.components.GlassHeader
import com.example.ui.components.MediaItemCard
import com.example.ui.components.MediaListItem
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.LocalBlurNeonCustomization
import com.example.ui.theme.LocalGlassCustomization
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextDarkSecondary

@Composable
fun GalleryScreen(
  viewModel: GalleryViewModel,
  onItemClick: (MediaItem, List<MediaItem>) -> Unit,
  onRequestDeleteItems: (List<MediaItem>) -> Unit,
  onShareItems: (List<MediaItem>) -> Unit,
  modifier: Modifier = Modifier
) {
  val searchQuery by viewModel.searchQuery.collectAsState()
  val isSearchActive by viewModel.isSearchActive.collectAsState()
  val categoryFilter by viewModel.categoryFilter.collectAsState()
  val isSelectionMode by viewModel.isSelectionMode.collectAsState()
  val selectedIds by viewModel.selectedIds.collectAsState()
  val isLoading by viewModel.isLoading.collectAsState()
  val filteredMedia by viewModel.filteredMedia.collectAsState()
  val dateSections by viewModel.dateSections.collectAsState()
  val galleryPrefs by viewModel.prefsManager.galleryPrefs.collectAsState()

  val glass = LocalGlassCustomization.current
  val neon = LocalBlurNeonCustomization.current

  Column(
    modifier = modifier.fillMaxSize()
  ) {
    // Glass Header
    GlassHeader(
      searchQuery = searchQuery,
      onSearchQueryChange = viewModel::onSearchQueryChange,
      isSearchActive = isSearchActive,
      onToggleSearch = viewModel::toggleSearch,
      isSelectionMode = isSelectionMode,
      selectedCount = selectedIds.size,
      totalCount = filteredMedia.size,
      onCancelSelection = viewModel::cancelSelectionMode,
      onSelectAll = viewModel::selectAll,
      onDeselectAll = viewModel::deselectAll,
      onShareSelected = {
        val selected = filteredMedia.filter { selectedIds.contains(it.id) }
        onShareItems(selected)
      },
      onDeleteSelected = {
        val selected = filteredMedia.filter { selectedIds.contains(it.id) }
        onRequestDeleteItems(selected)
      },
      onFavoriteSelected = viewModel::favoriteSelected,
      currentViewMode = galleryPrefs.viewMode,
      onToggleViewMode = {
        viewModel.prefsManager.updateGalleryPrefs {
          it.copy(viewMode = if (it.viewMode == ViewMode.GRID) ViewMode.LIST else ViewMode.GRID)
        }
      },
      currentSortOrder = galleryPrefs.sortOrder,
      onSelectSortOrder = { order ->
        viewModel.prefsManager.updateGalleryPrefs { it.copy(sortOrder = order) }
      }
    )

    // Category Filter Chips
    if (!isSelectionMode) {
      LazyRow(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 10.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(CategoryFilter.values()) { filter ->
          val isSelected = categoryFilter == filter
          val chipShape = RoundedCornerShape(16.dp)

          val chipBg = if (isSelected) {
            Brush.horizontalGradient(
              listOf(NeonCyan.copy(alpha = 0.25f), NeonPurple.copy(alpha = 0.20f))
            )
          } else {
            Brush.horizontalGradient(
              listOf(Color.White.copy(alpha = 0.06f), Color.White.copy(alpha = 0.02f))
            )
          }

          val chipBorder = if (isSelected) {
            Brush.horizontalGradient(listOf(NeonCyan, NeonPurple))
          } else {
            Brush.horizontalGradient(
              listOf(Color.White.copy(alpha = 0.15f), Color.White.copy(alpha = 0.05f))
            )
          }

          Box(
            modifier = Modifier
              .clip(chipShape)
              .background(chipBg)
              .border(if (isSelected) 1.5.dp else 1.dp, chipBorder, chipShape)
              .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = NeonCyan),
                onClick = { viewModel.setCategoryFilter(filter) }
              )
              .padding(horizontal = 16.dp, vertical = 7.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = filter.label,
              color = if (isSelected) Color.White else TextDarkSecondary,
              fontSize = 13.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
          }
        }
      }
    }

    // Media Content / Sections
    Box(
      modifier = Modifier
        .fillMaxSize()
        .weight(1f)
    ) {
      if (isLoading && filteredMedia.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          CircularProgressIndicator(color = NeonCyan, modifier = Modifier.size(36.dp))
        }
      } else if (filteredMedia.isEmpty()) {
        EmptyStateView(
          title = if (categoryFilter == CategoryFilter.VIDEOS) "No videos yet" else "No photos yet",
          message = if (searchQuery.isNotEmpty()) {
            "No media found matching \"$searchQuery\"."
          } else {
            "Your moments will appear here once captured or added."
          },
          actionButtonText = "Refresh Media",
          onActionClick = viewModel::loadMedia
        )
      } else {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
          val screenWidth = maxWidth
          val columns = when {
            screenWidth >= 840.dp -> 6
            screenWidth >= 600.dp -> 4
            else -> 3
          }

          if (galleryPrefs.viewMode == ViewMode.GRID) {
            LazyVerticalGrid(
              columns = GridCells.Fixed(columns),
              modifier = Modifier.fillMaxSize(),
              contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              dateSections.forEach { section ->
                item(span = { GridItemSpan(columns) }) {
                  SectionHeader(title = section.title, count = section.items.size)
                }

                items(section.items, key = { it.id }) { item ->
                  val isSelected = selectedIds.contains(item.id)
                  MediaItemCard(
                    item = item,
                    isSelected = isSelected,
                    isSelectionMode = isSelectionMode,
                    onClick = {
                      if (isSelectionMode) {
                        viewModel.toggleSelection(item.id)
                      } else {
                        onItemClick(item, filteredMedia)
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
              }

              item(span = { GridItemSpan(columns) }) {
                Spacer(modifier = Modifier.height(72.dp))
              }
            }
          } else {
            // List View
            LazyColumn(
              modifier = Modifier.fillMaxSize(),
              contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              dateSections.forEach { section ->
                item {
                  SectionHeader(title = section.title, count = section.items.size)
                }

                items(section.items, key = { it.id }) { item ->
                  val isSelected = selectedIds.contains(item.id)
                  MediaListItem(
                    item = item,
                    isSelected = isSelected,
                    isSelectionMode = isSelectionMode,
                    onClick = {
                      if (isSelectionMode) {
                        viewModel.toggleSelection(item.id)
                      } else {
                        onItemClick(item, filteredMedia)
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
              }

              item {
                Spacer(modifier = Modifier.height(72.dp))
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun SectionHeader(title: String, count: Int) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(top = 16.dp, bottom = 8.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = title,
      color = Color.White,
      fontWeight = FontWeight.Bold,
      fontSize = 17.sp,
      letterSpacing = 0.3.sp
    )
    Spacer(modifier = Modifier.weight(1f))
    Text(
      text = "$count items",
      color = ElectricBlue,
      fontSize = 12.sp,
      fontWeight = FontWeight.Medium
    )
  }
}
