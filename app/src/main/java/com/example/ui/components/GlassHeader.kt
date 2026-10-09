package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.SortOrder
import com.example.data.model.ViewMode
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.FavoritePink
import com.example.ui.theme.LocalGlassCustomization
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextDarkSecondary

@Composable
fun GlassHeader(
  searchQuery: String,
  onSearchQueryChange: (String) -> Unit,
  isSearchActive: Boolean,
  onToggleSearch: () -> Unit,
  isSelectionMode: Boolean,
  selectedCount: Int,
  totalCount: Int,
  onCancelSelection: () -> Unit,
  onSelectAll: () -> Unit,
  onDeselectAll: () -> Unit,
  onShareSelected: () -> Unit,
  onDeleteSelected: () -> Unit,
  onFavoriteSelected: () -> Unit,
  currentViewMode: ViewMode,
  onToggleViewMode: () -> Unit,
  currentSortOrder: SortOrder,
  onSelectSortOrder: (SortOrder) -> Unit,
  modifier: Modifier = Modifier
) {
  var showSortMenu by remember { mutableStateOf(false) }
  var showMoreMenu by remember { mutableStateOf(false) }

  val glass = LocalGlassCustomization.current
  val headerShape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)

  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(headerShape)
      .background(
        Brush.verticalGradient(
          listOf(
            Color(0xFF070B14).copy(alpha = 0.85f),
            Color(0xFF0F172A).copy(alpha = (glass.glassAlpha * 1.5f).coerceIn(0.2f, 0.95f))
          )
        )
      )
      .border(
        1.dp,
        Brush.verticalGradient(
          listOf(Color.White.copy(alpha = 0.2f), NeonCyan.copy(alpha = 0.25f))
        ),
        headerShape
      )
      .statusBarsPadding()
      .padding(horizontal = 16.dp, vertical = 12.dp)
  ) {
    if (isSelectionMode) {
      // Multi-select Action Bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          GlassIconButton(
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Cancel Selection",
            onClick = onCancelSelection,
            size = 38.dp,
            iconSize = 20.dp
          )
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = "$selectedCount selected",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 17.sp
            )
            Text(
              text = "of $totalCount items",
              color = TextDarkSecondary,
              fontSize = 12.sp
            )
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          if (selectedCount < totalCount) {
            GlassIconButton(
              icon = Icons.Default.Check,
              contentDescription = "Select All",
              onClick = onSelectAll,
              size = 38.dp,
              iconSize = 18.dp
            )
          } else {
            GlassIconButton(
              icon = Icons.Default.Close,
              contentDescription = "Deselect All",
              onClick = onDeselectAll,
              size = 38.dp,
              iconSize = 18.dp
            )
          }

          Spacer(modifier = Modifier.width(8.dp))

          GlassIconButton(
            icon = Icons.Default.Share,
            contentDescription = "Share Selected",
            onClick = onShareSelected,
            size = 38.dp,
            iconSize = 18.dp,
            isActive = selectedCount > 0
          )

          Spacer(modifier = Modifier.width(8.dp))

          GlassIconButton(
            icon = Icons.Default.Favorite,
            contentDescription = "Favorite Selected",
            onClick = onFavoriteSelected,
            size = 38.dp,
            iconSize = 18.dp,
            tint = FavoritePink,
            isActive = selectedCount > 0,
            activeGlow = FavoritePink
          )

          Spacer(modifier = Modifier.width(8.dp))

          GlassIconButton(
            icon = Icons.Default.Delete,
            contentDescription = "Delete Selected",
            onClick = onDeleteSelected,
            size = 38.dp,
            iconSize = 18.dp,
            tint = Color(0xFFEF4444),
            isActive = selectedCount > 0,
            activeGlow = Color(0xFFEF4444)
          )
        }
      }
    } else {
      // Normal Header
      Column(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Brand Logo & Title
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(NeonCyan, NeonPurple)))
                .padding(2.dp)
                .clip(CircleShape)
                .background(Color(0xFF0B132B)),
              contentAlignment = Alignment.Center
            ) {
              Image(
                painter = painterResource(id = R.drawable.ic_mun_launcher),
                contentDescription = "M-U-N Logo",
                modifier = Modifier.size(24.dp)
              )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
              Text(
                text = "M-U-N Gallery",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 19.sp,
                letterSpacing = 0.5.sp
              )
              Text(
                text = "Your Moments, Beautifully.",
                color = ElectricBlue,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
              )
            }
          }

          // Action buttons: Search, View Mode, Sort
          Row(verticalAlignment = Alignment.CenterVertically) {
            GlassIconButton(
              icon = if (isSearchActive) Icons.Default.Close else Icons.Default.Search,
              contentDescription = if (isSearchActive) "Close Search" else "Search",
              onClick = onToggleSearch,
              size = 38.dp,
              iconSize = 18.dp,
              isActive = isSearchActive
            )

            Spacer(modifier = Modifier.width(6.dp))

            GlassIconButton(
              icon = if (currentViewMode == ViewMode.GRID) Icons.AutoMirrored.Filled.ViewList else Icons.Default.GridView,
              contentDescription = "Toggle Grid/List View",
              onClick = onToggleViewMode,
              size = 38.dp,
              iconSize = 18.dp
            )

            Spacer(modifier = Modifier.width(6.dp))

            Box {
              GlassIconButton(
                icon = Icons.AutoMirrored.Filled.Sort,
                contentDescription = "Sort Media",
                onClick = { showSortMenu = true },
                size = 38.dp,
                iconSize = 18.dp
              )

              DropdownMenu(
                expanded = showSortMenu,
                onDismissRequest = { showSortMenu = false },
                modifier = Modifier
                  .background(Color(0xFF0F172A).copy(alpha = 0.95f))
                  .border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
              ) {
                SortOrder.values().forEach { order ->
                  DropdownMenuItem(
                    text = {
                      Text(
                        text = order.label,
                        color = if (order == currentSortOrder) NeonCyan else Color.White,
                        fontWeight = if (order == currentSortOrder) FontWeight.Bold else FontWeight.Normal
                      )
                    },
                    onClick = {
                      onSelectSortOrder(order)
                      showSortMenu = false
                    }
                  )
                }
              }
            }
          }
        }

        // Search Input Bar
        AnimatedVisibility(
          visible = isSearchActive,
          enter = fadeIn(),
          exit = fadeOut()
        ) {
          Column {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF090D1A).copy(alpha = 0.8f))
                .border(1.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = NeonCyan,
                modifier = Modifier.size(18.dp)
              )

              Spacer(modifier = Modifier.width(10.dp))

              BasicTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.weight(1f),
                textStyle = TextStyle(
                  color = Color.White,
                  fontSize = 14.sp
                ),
                cursorBrush = SolidColor(NeonCyan),
                singleLine = true,
                decorationBox = { innerTextField ->
                  if (searchQuery.isEmpty()) {
                    Text(
                      text = "Search by filename, album or video...",
                      color = TextDarkSecondary,
                      fontSize = 14.sp
                    )
                  }
                  innerTextField()
                }
              )

              if (searchQuery.isNotEmpty()) {
                IconButton(
                  onClick = { onSearchQueryChange("") },
                  modifier = Modifier.size(20.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear Search",
                    tint = TextDarkSecondary,
                    modifier = Modifier.size(16.dp)
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}
