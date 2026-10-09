package com.example.ui.video

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem as ExoMediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.data.model.MediaItem
import com.example.ui.components.GlassIconButton
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.FavoritePink
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextDarkSecondary
import kotlinx.coroutines.delay

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerScreen(
  item: MediaItem,
  onBack: () -> Unit,
  onToggleFavorite: (MediaItem) -> Unit,
  onShare: (MediaItem) -> Unit,
  onDelete: (MediaItem) -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler(onBack = onBack)

  val context = LocalContext.current
  var isPlaying by remember { mutableStateOf(true) }
  var isMuted by remember { mutableStateOf(false) }
  var currentPosition by remember { mutableLongStateOf(0L) }
  var totalDuration by remember { mutableLongStateOf(item.duration.coerceAtLeast(1L)) }
  var showControls by remember { mutableStateOf(true) }
  var isUserSeeking by remember { mutableStateOf(false) }
  var seekPosition by remember { mutableFloatStateOf(0f) }

  val exoPlayer = remember {
    ExoPlayer.Builder(context).build().apply {
      setMediaItem(ExoMediaItem.fromUri(item.uri))
      prepare()
      playWhenReady = true
    }
  }

  // Periodic position updates
  LaunchedEffect(exoPlayer, isUserSeeking) {
    while (true) {
      if (!isUserSeeking) {
        currentPosition = exoPlayer.currentPosition
        if (exoPlayer.duration > 0) {
          totalDuration = exoPlayer.duration
        }
      }
      delay(250)
    }
  }

  // Auto-hide controls
  LaunchedEffect(showControls, isPlaying) {
    if (showControls && isPlaying) {
      delay(4000)
      showControls = false
    }
  }

  // ExoPlayer event listener
  DisposableEffect(exoPlayer) {
    val listener = object : Player.Listener {
      override fun onIsPlayingChanged(playing: Boolean) {
        isPlaying = playing
      }

      override fun onPlaybackStateChanged(playbackState: Int) {
        if (playbackState == Player.STATE_READY && exoPlayer.duration > 0) {
          totalDuration = exoPlayer.duration
        }
      }
    }
    exoPlayer.addListener(listener)

    onDispose {
      exoPlayer.removeListener(listener)
      exoPlayer.release()
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color.Black)
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = { showControls = !showControls }
      )
  ) {
    // Android Media3 PlayerView
    AndroidView(
      factory = { ctx ->
        PlayerView(ctx).apply {
          player = exoPlayer
          useController = false
          layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
          )
        }
      },
      modifier = Modifier.fillMaxSize()
    )

    // Center Play/Pause & Skip buttons
    AnimatedVisibility(
      visible = showControls,
      enter = fadeIn(),
      exit = fadeOut(),
      modifier = Modifier.align(Alignment.Center)
    ) {
      Row(
        horizontalArrangement = Arrangement.spacedBy(32.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Rewind 10s
        GlassIconButton(
          icon = Icons.Default.Replay10,
          contentDescription = "Rewind 10s",
          onClick = {
            val newPos = (exoPlayer.currentPosition - 10000).coerceAtLeast(0)
            exoPlayer.seekTo(newPos)
          },
          size = 48.dp,
          iconSize = 26.dp
        )

        // Center Play / Pause
        Box(
          modifier = Modifier
            .size(70.dp)
            .clip(CircleShape)
            .background(
              Brush.linearGradient(
                listOf(NeonCyan.copy(alpha = 0.45f), NeonPurple.copy(alpha = 0.40f))
              )
            )
            .clickable {
              if (isPlaying) exoPlayer.pause() else exoPlayer.play()
            },
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = if (isPlaying) "Pause" else "Play",
            tint = Color.White,
            modifier = Modifier.size(36.dp)
          )
        }

        // Forward 10s
        GlassIconButton(
          icon = Icons.Default.Forward10,
          contentDescription = "Forward 10s",
          onClick = {
            val newPos = (exoPlayer.currentPosition + 10000).coerceAtMost(totalDuration)
            exoPlayer.seekTo(newPos)
          },
          size = 48.dp,
          iconSize = 26.dp
        )
      }
    }

    // Top Controls Bar
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
                text = item.name,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              Text(
                text = "${item.formattedSize} • ${item.bucketName}",
                color = ElectricBlue,
                fontSize = 12.sp
              )
            }
          }

          GlassIconButton(
            icon = if (isMuted) Icons.AutoMirrored.Filled.VolumeMute else Icons.AutoMirrored.Filled.VolumeUp,
            contentDescription = if (isMuted) "Unmute" else "Mute",
            onClick = {
              isMuted = !isMuted
              exoPlayer.volume = if (isMuted) 0f else 1f
            },
            size = 40.dp
          )
        }
      }
    }

    // Bottom Seekbar & Actions Bar
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
              listOf(Color.Black.copy(alpha = 0.3f), Color.Black.copy(alpha = 0.95f))
            )
          )
          .navigationBarsPadding()
          .padding(horizontal = 20.dp, vertical = 14.dp)
      ) {
        Column(modifier = Modifier.fillMaxWidth()) {
          // Time Labels & Slider
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            val displayPos = if (isUserSeeking) (seekPosition * totalDuration).toLong() else currentPosition
            Text(
              text = formatTime(displayPos),
              color = NeonCyan,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = formatTime(totalDuration),
              color = TextDarkSecondary,
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium
            )
          }

          val sliderFraction = if (totalDuration > 0) {
            (currentPosition.toFloat() / totalDuration.toFloat()).coerceIn(0f, 1f)
          } else 0f

          Slider(
            value = if (isUserSeeking) seekPosition else sliderFraction,
            onValueChange = { frac ->
              isUserSeeking = true
              seekPosition = frac
            },
            onValueChangeFinished = {
              val targetMs = (seekPosition * totalDuration).toLong()
              exoPlayer.seekTo(targetMs)
              currentPosition = targetMs
              isUserSeeking = false
            },
            colors = SliderDefaults.colors(
              thumbColor = NeonCyan,
              activeTrackColor = NeonCyan,
              inactiveTrackColor = Color.White.copy(alpha = 0.2f)
            ),
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(8.dp))

          // Bottom Actions
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
          ) {
            GlassIconButton(
              icon = if (item.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
              contentDescription = "Favorite",
              onClick = { onToggleFavorite(item) },
              size = 44.dp,
              tint = if (item.isFavorite) FavoritePink else Color.White,
              isActive = item.isFavorite,
              activeGlow = FavoritePink
            )

            GlassIconButton(
              icon = Icons.Default.Share,
              contentDescription = "Share",
              onClick = { onShare(item) },
              size = 44.dp
            )

            GlassIconButton(
              icon = Icons.Default.Delete,
              contentDescription = "Delete",
              onClick = { onDelete(item) },
              size = 44.dp,
              tint = Color(0xFFEF4444)
            )
          }
        }
      }
    }
  }
}

private fun formatTime(millis: Long): String {
  val totalSeconds = (millis / 1000).coerceAtLeast(0)
  val minutes = totalSeconds / 60
  val seconds = totalSeconds % 60
  val hours = minutes / 60
  return if (hours > 0) {
    String.format("%d:%02d:%02d", hours, minutes % 60, seconds)
  } else {
    String.format("%02d:%02d", minutes, seconds)
  }
}
