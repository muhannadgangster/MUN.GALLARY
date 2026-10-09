package com.example.ui.editor

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.RotateRight
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MediaItem
import com.example.ui.components.GlassButton
import com.example.ui.components.GlassIconButton
import com.example.ui.components.GlassSlider
import com.example.ui.gallery.GalleryViewModel
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextDarkSecondary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class EditorTab(val label: String) {
  ADJUST("Adjust"),
  TRANSFORM("Transform"),
  CROP("Crop")
}

enum class CropRatio(val label: String, val ratio: Float?) {
  ORIGINAL("Original", null),
  SQUARE("1:1", 1f),
  FOUR_THREE("4:3", 4f / 3f),
  SIXTEEN_NINE("16:9", 16f / 9f)
}

@Composable
fun PhotoEditorScreen(
  item: MediaItem,
  viewModel: GalleryViewModel,
  onCancel: () -> Unit,
  onSaved: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler(onBack = onCancel)

  val context = LocalContext.current
  val scope = rememberCoroutineScope()

  var baseBitmap by remember { mutableStateOf<Bitmap?>(null) }
  var isProcessing by remember { mutableStateOf(true) }
  var isSaving by remember { mutableStateOf(false) }

  // Adjustments
  var brightness by remember { mutableFloatStateOf(0f) } // -100 to +100
  var contrast by remember { mutableFloatStateOf(1f) } // 0.5 to 2.0
  var saturation by remember { mutableFloatStateOf(1f) } // 0.0 to 2.0
  var warmth by remember { mutableFloatStateOf(0f) } // -50 to +50

  // Transforms
  var rotationDegrees by remember { mutableIntStateOf(0) }
  var isFlippedH by remember { mutableStateOf(false) }
  var isFlippedV by remember { mutableStateOf(false) }

  // Crop
  var selectedCropRatio by remember { mutableStateOf(CropRatio.ORIGINAL) }

  // Tab selection
  var currentTab by remember { mutableStateOf(EditorTab.ADJUST) }

  // Load image safely scaled to avoid OOM
  LaunchedEffect(item.uri) {
    withContext(Dispatchers.IO) {
      try {
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(item.uri)?.use { stream ->
          BitmapFactory.decodeStream(stream, null, options)
        }

        var sampleSize = 1
        val maxDim = 1920
        while (options.outWidth / sampleSize > maxDim || options.outHeight / sampleSize > maxDim) {
          sampleSize *= 2
        }

        val decodeOptions = BitmapFactory.Options().apply {
          inSampleSize = sampleSize
          inPreferredConfig = Bitmap.Config.ARGB_8888
        }

        val decoded = context.contentResolver.openInputStream(item.uri)?.use { stream ->
          BitmapFactory.decodeStream(stream, null, decodeOptions)
        }
        baseBitmap = decoded
      } catch (e: Exception) {
        e.printStackTrace()
      }
      isProcessing = false
    }
  }

  // Generate live preview bitmap when parameters change
  var previewBitmap by remember { mutableStateOf<Bitmap?>(null) }

  LaunchedEffect(baseBitmap, brightness, contrast, saturation, warmth, rotationDegrees, isFlippedH, isFlippedV, selectedCropRatio) {
    val src = baseBitmap ?: return@LaunchedEffect
    withContext(Dispatchers.Default) {
      previewBitmap = applyModifications(
        src = src,
        brightness = brightness,
        contrast = contrast,
        saturation = saturation,
        warmth = warmth,
        rotation = rotationDegrees,
        flipH = isFlippedH,
        flipV = isFlippedV,
        cropRatio = selectedCropRatio.ratio
      )
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFF070B14))
  ) {
    // Top Bar
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(Color.Black.copy(alpha = 0.85f))
        .statusBarsPadding()
        .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          GlassIconButton(
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Cancel",
            onClick = onCancel,
            size = 38.dp
          )
          Spacer(modifier = Modifier.width(12.dp))
          Text(
            text = "Photo Editor",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          // Reset
          GlassIconButton(
            icon = Icons.Default.Refresh,
            contentDescription = "Reset All",
            onClick = {
              brightness = 0f
              contrast = 1f
              saturation = 1f
              warmth = 0f
              rotationDegrees = 0
              isFlippedH = false
              isFlippedV = false
              selectedCropRatio = CropRatio.ORIGINAL
            },
            size = 38.dp
          )

          Spacer(modifier = Modifier.width(8.dp))

          // Save Copy
          GlassButton(
            text = if (isSaving) "Saving..." else "Save Copy",
            icon = Icons.Default.Save,
            onClick = {
              val toSave = previewBitmap ?: return@GlassButton
              isSaving = true
              viewModel.saveEditedCopy(toSave, item.name) { success ->
                isSaving = false
                if (success) onSaved()
              }
            },
            isPrimary = true,
            glowColor = NeonCyan
          )
        }
      }
    }

    // Image Canvas Preview Area
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .padding(16.dp),
      contentAlignment = Alignment.Center
    ) {
      if (isProcessing) {
        CircularProgressIndicator(color = NeonCyan)
      } else {
        val disp = previewBitmap ?: baseBitmap
        if (disp != null) {
          Image(
            bitmap = disp.asImageBitmap(),
            contentDescription = "Edited Preview",
            contentScale = ContentScale.Fit,
            modifier = Modifier
              .fillMaxSize()
              .clip(RoundedCornerShape(12.dp))
          )
        }
      }
    }

    // Bottom Controls & Sliders
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
        .background(Color(0xFF0F172A).copy(alpha = 0.95f))
        .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
        .navigationBarsPadding()
        .padding(bottom = 12.dp)
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        // Tab Selector (Adjust, Transform, Crop)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceAround
        ) {
          EditorTab.values().forEach { tab ->
            val isSelected = currentTab == tab
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .then(
                  if (isSelected) {
                    Modifier.background(
                      Brush.horizontalGradient(
                        listOf(NeonCyan.copy(alpha = 0.25f), NeonPurple.copy(alpha = 0.2f))
                      )
                    )
                  } else {
                    Modifier.background(Color.Transparent)
                  }
                )
                .clickable { currentTab = tab }
                .padding(horizontal = 18.dp, vertical = 8.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = tab.label,
                color = if (isSelected) NeonCyan else TextDarkSecondary,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 14.sp
              )
            }
          }
        }

        // Tab Content
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(horizontal = 20.dp)
        ) {
          when (currentTab) {
            EditorTab.ADJUST -> {
              Column(
                modifier = Modifier
                  .fillMaxSize()
                  .verticalScroll(rememberScrollState())
              ) {
                GlassSlider(
                  label = "Brightness",
                  value = brightness,
                  onValueChange = { brightness = it },
                  valueRange = -100f..100f,
                  valueDisplay = "${brightness.toInt()}"
                )
                GlassSlider(
                  label = "Contrast",
                  value = contrast,
                  onValueChange = { contrast = it },
                  valueRange = 0.5f..2.0f,
                  valueDisplay = String.format("%.2f", contrast)
                )
                GlassSlider(
                  label = "Saturation",
                  value = saturation,
                  onValueChange = { saturation = it },
                  valueRange = 0.0f..2.0f,
                  valueDisplay = String.format("%.2f", saturation)
                )
                GlassSlider(
                  label = "Warmth",
                  value = warmth,
                  onValueChange = { warmth = it },
                  valueRange = -50f..50f,
                  valueDisplay = "${warmth.toInt()}"
                )
              }
            }

            EditorTab.TRANSFORM -> {
              Row(
                modifier = Modifier
                  .fillMaxSize()
                  .padding(top = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
              ) {
                // Rotate 90°
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  GlassIconButton(
                    icon = Icons.AutoMirrored.Filled.RotateRight,
                    contentDescription = "Rotate 90°",
                    onClick = { rotationDegrees = (rotationDegrees + 90) % 360 },
                    size = 52.dp,
                    iconSize = 26.dp
                  )
                  Spacer(modifier = Modifier.height(8.dp))
                  Text(text = "Rotate 90° (${rotationDegrees}°)", color = Color.White, fontSize = 12.sp)
                }

                // Flip Horizontal
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  GlassIconButton(
                    icon = Icons.Default.Flip,
                    contentDescription = "Flip Horizontal",
                    onClick = { isFlippedH = !isFlippedH },
                    size = 52.dp,
                    iconSize = 26.dp,
                    isActive = isFlippedH
                  )
                  Spacer(modifier = Modifier.height(8.dp))
                  Text(text = "Flip H", color = Color.White, fontSize = 12.sp)
                }

                // Flip Vertical
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  GlassIconButton(
                    icon = Icons.Default.Flip,
                    contentDescription = "Flip Vertical",
                    onClick = { isFlippedV = !isFlippedV },
                    size = 52.dp,
                    iconSize = 26.dp,
                    isActive = isFlippedV
                  )
                  Spacer(modifier = Modifier.height(8.dp))
                  Text(text = "Flip V", color = Color.White, fontSize = 12.sp)
                }
              }
            }

            EditorTab.CROP -> {
              Row(
                modifier = Modifier
                  .fillMaxSize()
                  .padding(top = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
              ) {
                CropRatio.values().forEach { ratio ->
                  val isSelected = selectedCropRatio == ratio
                  Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                      modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) NeonCyan.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.08f))
                        .border(1.dp, if (isSelected) NeonCyan else Color.White.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                        .clickable { selectedCropRatio = ratio },
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(
                        imageVector = Icons.Default.Crop,
                        contentDescription = ratio.label,
                        tint = if (isSelected) NeonCyan else Color.White
                      )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                      text = ratio.label,
                      color = if (isSelected) NeonCyan else TextDarkSecondary,
                      fontSize = 12.sp,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
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
}

private fun applyModifications(
  src: Bitmap,
  brightness: Float,
  contrast: Float,
  saturation: Float,
  warmth: Float,
  rotation: Int,
  flipH: Boolean,
  flipV: Boolean,
  cropRatio: Float?
): Bitmap {
  // Step 1: Crop aspect ratio if requested
  var current = if (cropRatio != null) {
    val srcW = src.width.toFloat()
    val srcH = src.height.toFloat()
    val currentRatio = srcW / srcH

    val (targetW, targetH) = if (currentRatio > cropRatio) {
      val h = srcH
      val w = h * cropRatio
      Pair(w.toInt(), h.toInt())
    } else {
      val w = srcW
      val h = w / cropRatio
      Pair(w.toInt(), h.toInt())
    }

    val x = ((srcW - targetW) / 2f).coerceAtLeast(0f).toInt()
    val y = ((srcH - targetH) / 2f).coerceAtLeast(0f).toInt()
    Bitmap.createBitmap(src, x, y, targetW.coerceAtMost(src.width - x), targetH.coerceAtMost(src.height - y))
  } else src

  // Step 2: Transform (Rotation, Flip)
  val matrix = Matrix()
  if (flipH) matrix.postScale(-1f, 1f)
  if (flipV) matrix.postScale(1f, -1f)
  if (rotation != 0) matrix.postRotate(rotation.toFloat())

  val transformed = Bitmap.createBitmap(current, 0, 0, current.width, current.height, matrix, true)

  // Step 3: Color Matrix adjustments (Brightness, Contrast, Saturation, Warmth)
  val result = Bitmap.createBitmap(transformed.width, transformed.height, Bitmap.Config.ARGB_8888)
  val canvas = Canvas(result)
  val paint = Paint(Paint.ANTI_ALIAS_FLAG)

  val cm = ColorMatrix()

  // Contrast & Brightness
  val scale = contrast
  val translate = brightness
  val contrastMatrix = ColorMatrix(
    floatArrayOf(
      scale, 0f, 0f, 0f, translate,
      0f, scale, 0f, 0f, translate,
      0f, 0f, scale, 0f, translate,
      0f, 0f, 0f, 1f, 0f
    )
  )
  cm.postConcat(contrastMatrix)

  // Saturation
  val satMatrix = ColorMatrix()
  satMatrix.setSaturation(saturation)
  cm.postConcat(satMatrix)

  // Warmth (increase red, decrease blue)
  if (warmth != 0f) {
    val warmthMatrix = ColorMatrix(
      floatArrayOf(
        1f + (warmth / 200f), 0f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f, 0f,
        0f, 0f, 1f - (warmth / 200f), 0f, 0f,
        0f, 0f, 0f, 1f, 0f
      )
    )
    cm.postConcat(warmthMatrix)
  }

  paint.colorFilter = ColorMatrixColorFilter(cm)
  canvas.drawBitmap(transformed, 0f, 0f, paint)

  return result
}
