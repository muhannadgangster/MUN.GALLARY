package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PhotoAlbum
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.PhotoAlbum
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.LocalBlurNeonCustomization
import com.example.ui.theme.LocalGlassCustomization
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextDarkSecondary

enum class NavTab(val title: String, val activeIcon: ImageVector, val inactiveIcon: ImageVector) {
  GALLERY("Gallery", Icons.Filled.PhotoLibrary, Icons.Outlined.PhotoLibrary),
  ALBUMS("Albums", Icons.Filled.PhotoAlbum, Icons.Outlined.PhotoAlbum),
  FAVORITES("Favorites", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder),
  SETTINGS("Settings", Icons.Filled.MoreHoriz, Icons.Outlined.MoreHoriz)
}

@Composable
fun GlassBottomNav(
  currentTab: NavTab,
  onTabSelected: (NavTab) -> Unit,
  modifier: Modifier = Modifier
) {
  val glass = LocalGlassCustomization.current
  val neon = LocalBlurNeonCustomization.current

  val navShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
  val baseAlpha = (glass.glassAlpha * 1.6f).coerceIn(0.25f, 0.95f)

  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(navShape)
      .background(
        Brush.verticalGradient(
          listOf(
            Color(0xFF0F172A).copy(alpha = baseAlpha),
            Color(0xFF070B14).copy(alpha = (baseAlpha * 1.2f).coerceAtMost(0.98f))
          )
        )
      )
      .border(
        1.dp,
        Brush.verticalGradient(
          listOf(
            NeonCyan.copy(alpha = if (neon.neonEnabled) 0.35f else 0.15f),
            Color.White.copy(alpha = 0.05f)
          )
        ),
        navShape
      )
      .navigationBarsPadding()
      .padding(horizontal = 16.dp, vertical = 8.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically
    ) {
      NavTab.values().forEach { tab ->
        val isSelected = currentTab == tab

        val animatedGlow by animateColorAsState(
          targetValue = if (isSelected) NeonCyan else Color.Transparent,
          animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
          label = "glowColor"
        )

        val pillShape = RoundedCornerShape(20.dp)

        Box(
          modifier = Modifier
            .clip(pillShape)
            .then(
              if (isSelected) {
                Modifier
                  .background(
                    Brush.horizontalGradient(
                      listOf(
                        NeonCyan.copy(alpha = 0.22f),
                        NeonPurple.copy(alpha = 0.18f)
                      )
                    )
                  )
                  .border(
                    1.dp,
                    Brush.horizontalGradient(
                      listOf(
                        NeonCyan.copy(alpha = 0.6f),
                        NeonPurple.copy(alpha = 0.4f)
                      )
                    ),
                    pillShape
                  )
              } else Modifier
            )
            .clickable(
              interactionSource = remember { MutableInteractionSource() },
              indication = ripple(bounded = true, color = NeonCyan),
              onClick = { onTabSelected(tab) }
            )
            .padding(horizontal = 14.dp, vertical = 6.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
            Icon(
              imageVector = if (isSelected) tab.activeIcon else tab.inactiveIcon,
              contentDescription = tab.title,
              tint = if (isSelected) NeonCyan else TextDarkSecondary,
              modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
              text = tab.title,
              color = if (isSelected) Color.White else TextDarkSecondary,
              fontSize = 11.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
          }
        }
      }
    }
  }
}
