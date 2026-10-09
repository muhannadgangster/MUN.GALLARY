package com.example

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.Album
import com.example.data.model.MediaItem
import com.example.ui.albums.AlbumDetailScreen
import com.example.ui.albums.AlbumsScreen
import com.example.ui.components.DeleteConfirmationDialog
import com.example.ui.components.GlassBottomNav
import com.example.ui.components.NavTab
import com.example.ui.components.PermissionPrompt
import com.example.ui.editor.PhotoEditorScreen
import com.example.ui.favorites.FavoritesScreen
import com.example.ui.gallery.GalleryScreen
import com.example.ui.gallery.GalleryViewModel
import com.example.ui.settings.BlurNeonSettingsScreen
import com.example.ui.settings.GlassCustomizationScreen
import com.example.ui.settings.SettingsScreen
import com.example.ui.settings.WallpaperSettingsScreen
import com.example.ui.theme.MunGalleryTheme
import com.example.ui.video.VideoPlayerScreen
import com.example.ui.viewer.PhotoViewerScreen
import kotlinx.coroutines.flow.collectLatest

sealed class Screen {
  object Main : Screen()
  data class PhotoViewer(val items: List<MediaItem>, val initialIndex: Int) : Screen()
  data class VideoPlayer(val item: MediaItem) : Screen()
  data class AlbumDetail(val album: Album) : Screen()
  data class PhotoEditor(val item: MediaItem) : Screen()
  object GlassCustomization : Screen()
  object WallpaperSettings : Screen()
  object BlurNeonSettings : Screen()
}

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      val galleryViewModel: GalleryViewModel = viewModel()
      val prefsManager = galleryViewModel.prefsManager

      val glassCustomization by prefsManager.glassState.collectAsState()
      val blurNeonCustomization by prefsManager.blurNeonState.collectAsState()
      val wallpaperCustomization by prefsManager.wallpaperState.collectAsState()
      val galleryPrefs by prefsManager.galleryPrefs.collectAsState()

      MunGalleryTheme(
        themeMode = galleryPrefs.themeMode,
        glassCustomization = glassCustomization,
        blurNeonCustomization = blurNeonCustomization,
        wallpaperCustomization = wallpaperCustomization
      ) {
        MunGalleryApp(viewModel = galleryViewModel)
      }
    }
  }
}

@Composable
fun MunGalleryApp(viewModel: GalleryViewModel) {
  val context = LocalContext.current
  val prefsManager = viewModel.prefsManager

  val wpSettings by prefsManager.wallpaperState.collectAsState()
  val isPermissionGranted by viewModel.isPermissionGranted.collectAsState()

  var currentTab by remember { mutableStateOf(NavTab.GALLERY) }
  var currentScreen by remember { mutableStateOf<Screen>(Screen.Main) }

  // Delete Confirmation Dialog State
  var itemsToDelete by remember { mutableStateOf<List<MediaItem>?>(null) }

  // System Delete Intent Launcher (Android 11+)
  val deleteIntentLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartIntentSenderForResult()
  ) { result ->
    if (result.resultCode == android.app.Activity.RESULT_OK) {
      viewModel.loadMedia()
      viewModel.cancelSelectionMode()
      Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show()
    }
  }

  // Permissions setup
  val permissionsToRequest = remember {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      arrayOf(
        Manifest.permission.READ_MEDIA_IMAGES,
        Manifest.permission.READ_MEDIA_VIDEO
      )
    } else {
      arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
  }

  fun checkHasPermissions(): Boolean {
    return permissionsToRequest.all {
      ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }
  }

  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestMultiplePermissions()
  ) { results ->
    val allGranted = results.values.all { it }
    viewModel.setPermissionGranted(allGranted)
  }

  // Initial check on launch
  LaunchedEffect(Unit) {
    val granted = checkHasPermissions()
    viewModel.setPermissionGranted(granted)
  }

  // Observe delete intent events from ViewModel
  LaunchedEffect(Unit) {
    viewModel.deleteIntentEvent.collectLatest { intentSenderRequest ->
      deleteIntentLauncher.launch(intentSenderRequest)
    }
  }

  // Observe toast messages
  LaunchedEffect(Unit) {
    viewModel.toastMessage.collectLatest { msg ->
      Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
  }

  // Share helper
  val shareMedia: (List<MediaItem>) -> Unit = { items ->
    if (items.isNotEmpty()) {
      try {
        val intent = viewModel.createShareIntent(items)
        val chooser = Intent.createChooser(intent, "Share via M-U-N Gallery")
        context.startActivity(chooser)
      } catch (e: Exception) {
        Toast.makeText(context, "Cannot share media", Toast.LENGTH_SHORT).show()
      }
    }
  }

  // Root Box with Customizable Liquid Glass Wallpaper Background
  Box(modifier = Modifier.fillMaxSize()) {
    // 1. Wallpaper Layer
    if (wpSettings.customWallpaperUri != null) {
      AsyncImage(
        model = ImageRequest.Builder(context)
          .data(wpSettings.customWallpaperUri)
          .crossfade(true)
          .build(),
        contentDescription = "Wallpaper",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
      )
    } else {
      Image(
        painter = painterResource(id = R.drawable.bg_liquid_glass),
        contentDescription = "Default Liquid Glass Wallpaper",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
      )
    }

    // 2. Dark Overlay & Opacity control
    if (wpSettings.darkOverlay) {
      val overlayAlpha = (1f - wpSettings.wallpaperOpacity).coerceIn(0.25f, 0.90f)
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color(0xFF070B14).copy(alpha = overlayAlpha))
      )
    }

    // 3. Main Content or Permission Prompt
    if (!isPermissionGranted) {
      PermissionPrompt(
        onRequestPermission = {
          permissionLauncher.launch(permissionsToRequest)
        }
      )
    } else {
      AnimatedContent(
        targetState = currentScreen,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "screen_transition",
        modifier = Modifier.fillMaxSize()
      ) { screen ->
        when (screen) {
          is Screen.Main -> {
            Scaffold(
              containerColor = Color.Transparent,
              bottomBar = {
                GlassBottomNav(
                  currentTab = currentTab,
                  onTabSelected = { tab ->
                    currentTab = tab
                  }
                )
              }
            ) { paddingValues ->
              Box(
                modifier = Modifier
                  .fillMaxSize()
                  .padding(paddingValues)
              ) {
                when (currentTab) {
                  NavTab.GALLERY -> {
                    GalleryScreen(
                      viewModel = viewModel,
                      onItemClick = { item, list ->
                        if (item.isVideo) {
                          currentScreen = Screen.VideoPlayer(item)
                        } else {
                          val idx = list.indexOfFirst { it.id == item.id }.coerceAtLeast(0)
                          currentScreen = Screen.PhotoViewer(list, idx)
                        }
                      },
                      onRequestDeleteItems = { items ->
                        itemsToDelete = items
                      },
                      onShareItems = shareMedia
                    )
                  }

                  NavTab.ALBUMS -> {
                    AlbumsScreen(
                      viewModel = viewModel,
                      onAlbumClick = { album ->
                        currentScreen = Screen.AlbumDetail(album)
                      }
                    )
                  }

                  NavTab.FAVORITES -> {
                    FavoritesScreen(
                      viewModel = viewModel,
                      onItemClick = { item, list ->
                        if (item.isVideo) {
                          currentScreen = Screen.VideoPlayer(item)
                        } else {
                          val idx = list.indexOfFirst { it.id == item.id }.coerceAtLeast(0)
                          currentScreen = Screen.PhotoViewer(list, idx)
                        }
                      }
                    )
                  }

                  NavTab.SETTINGS -> {
                    SettingsScreen(
                      viewModel = viewModel,
                      onNavigateToGlassCustomization = {
                        currentScreen = Screen.GlassCustomization
                      },
                      onNavigateToWallpaper = {
                        currentScreen = Screen.WallpaperSettings
                      },
                      onNavigateToBlurNeon = {
                        currentScreen = Screen.BlurNeonSettings
                      }
                    )
                  }
                }
              }
            }
          }

          is Screen.PhotoViewer -> {
            PhotoViewerScreen(
              items = screen.items,
              initialIndex = screen.initialIndex,
              onBack = { currentScreen = Screen.Main },
              onToggleFavorite = viewModel::toggleFavorite,
              onShare = { item -> shareMedia(listOf(item)) },
              onEdit = { item -> currentScreen = Screen.PhotoEditor(item) },
              onDelete = { item -> itemsToDelete = listOf(item) }
            )
          }

          is Screen.VideoPlayer -> {
            VideoPlayerScreen(
              item = screen.item,
              onBack = { currentScreen = Screen.Main },
              onToggleFavorite = viewModel::toggleFavorite,
              onShare = { item -> shareMedia(listOf(item)) },
              onDelete = { item -> itemsToDelete = listOf(item) }
            )
          }

          is Screen.AlbumDetail -> {
            AlbumDetailScreen(
              album = screen.album,
              viewModel = viewModel,
              onBack = { currentScreen = Screen.Main },
              onItemClick = { item, list ->
                if (item.isVideo) {
                  currentScreen = Screen.VideoPlayer(item)
                } else {
                  val idx = list.indexOfFirst { it.id == item.id }.coerceAtLeast(0)
                  currentScreen = Screen.PhotoViewer(list, idx)
                }
              }
            )
          }

          is Screen.PhotoEditor -> {
            PhotoEditorScreen(
              item = screen.item,
              viewModel = viewModel,
              onCancel = { currentScreen = Screen.Main },
              onSaved = { currentScreen = Screen.Main }
            )
          }

          is Screen.GlassCustomization -> {
            GlassCustomizationScreen(
              prefsManager = prefsManager,
              onBack = { currentScreen = Screen.Main }
            )
          }

          is Screen.WallpaperSettings -> {
            WallpaperSettingsScreen(
              prefsManager = prefsManager,
              onBack = { currentScreen = Screen.Main }
            )
          }

          is Screen.BlurNeonSettings -> {
            BlurNeonSettingsScreen(
              prefsManager = prefsManager,
              onBack = { currentScreen = Screen.Main }
            )
          }
        }
      }
    }

    // Delete Confirmation Dialog
    val deleteList = itemsToDelete
    if (deleteList != null && deleteList.isNotEmpty()) {
      DeleteConfirmationDialog(
        itemCount = deleteList.size,
        onConfirm = {
          viewModel.deleteItems(deleteList)
          itemsToDelete = null
          if (currentScreen is Screen.PhotoViewer || currentScreen is Screen.VideoPlayer) {
            currentScreen = Screen.Main
          }
        },
        onDismiss = {
          itemsToDelete = null
        }
      )
    }
  }
}
