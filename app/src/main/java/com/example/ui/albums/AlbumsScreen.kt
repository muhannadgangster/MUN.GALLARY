package com.example.ui.albums

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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PhotoAlbum
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.Album
import com.example.ui.components.EmptyStateView
import com.example.ui.gallery.GalleryViewModel
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.LocalBlurNeonCustomization
import com.example.ui.theme.LocalGlassCustomization
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextDarkSecondary

@Composable
fun AlbumsScreen(
  viewModel: GalleryViewModel,
  onAlbumClick: (Album) -> Unit,
  modifier: Modifier = Modifier
) {
  val albums by viewModel.albums.collectAsState()
  val glass = LocalGlassCustomization.current
  val neon = LocalBlurNeonCustomization.current

  Column(
    modifier = modifier.fillMaxSize()
  ) {
    // Header
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
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
          RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
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
            text = "Albums",
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 22.sp,
            letterSpacing = 0.5.sp
          )
          Text(
            text = "${albums.size} Device Folders",
            color = ElectricBlue,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
          )
        }

        Box(
          modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.08f))
            .border(1.dp, NeonCyan.copy(alpha = 0.3f), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Folder,
            contentDescription = null,
            tint = NeonCyan,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }

    if (albums.isEmpty()) {
      EmptyStateView(
        title = "No Albums Found",
        message = "No photo or video folders detected on this device.",
        icon = Icons.Default.PhotoAlbum,
        actionButtonText = "Refresh Albums",
        onActionClick = viewModel::loadMedia
      )
    } else {
      BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenWidth = maxWidth
        val columns = when {
          screenWidth >= 840.dp -> 4
          screenWidth >= 600.dp -> 3
          else -> 2
        }

        LazyVerticalGrid(
          columns = GridCells.Fixed(columns),
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(16.dp),
          horizontalArrangement = Arrangement.spacedBy(14.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          items(albums, key = { it.id }) { album ->
            AlbumCard(
              album = album,
              onClick = { onAlbumClick(album) }
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

@Composable
fun AlbumCard(
  album: Album,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val glass = LocalGlassCustomization.current
  val neon = LocalBlurNeonCustomization.current

  val cardShape = RoundedCornerShape(glass.cornerRadius.coerceIn(16, 28).dp)

  val borderBrush = Brush.linearGradient(
    listOf(
      Color.White.copy(alpha = 0.2f * glass.reflectionIntensity),
      NeonCyan.copy(alpha = if (neon.neonEnabled) 0.3f else 0.1f)
    )
  )

  Column(
    modifier = modifier
      .fillMaxWidth()
      .clip(cardShape)
      .background(Color(0xFF0F172A).copy(alpha = (glass.glassAlpha * 1.5f).coerceIn(0.2f, 0.95f)))
      .border(1.dp, borderBrush, cardShape)
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = ripple(color = NeonCyan),
        onClick = onClick
      )
  ) {
    // Album Cover
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1f)
        .background(Color(0xFF1E293B))
    ) {
      if (album.coverUri != null) {
        AsyncImage(
          model = ImageRequest.Builder(context)
            .data(album.coverUri)
            .crossfade(true)
            .build(),
          contentDescription = album.name,
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )
      } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          Icon(
            imageVector = Icons.Default.Folder,
            contentDescription = null,
            tint = TextDarkSecondary,
            modifier = Modifier.size(40.dp)
          )
        }
      }

      // Bottom gradient on cover
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(
            Brush.verticalGradient(
              listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
              startY = 120f
            )
          )
      )
    }

    // Title & count
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
      Text(
        text = album.name,
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(2.dp))

      Text(
        text = "${album.itemCount} items" +
          if (album.videoCount > 0) " (${album.photoCount} photos, ${album.videoCount} videos)" else "",
        color = ElectricBlue,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium
      )
    }
  }
}
