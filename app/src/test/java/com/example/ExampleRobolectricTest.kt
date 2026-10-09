package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.PreferencesManager
import com.example.data.model.AppThemeMode
import com.example.data.model.SortOrder
import com.example.data.model.ViewMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read app name string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("M-U-N Gallery", appName)
  }

  @Test
  fun `preferences manager default values and persistence`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val prefsManager = PreferencesManager.getInstance(context)

    assertNotNull(prefsManager.glassState.value)
    assertNotNull(prefsManager.blurNeonState.value)
    assertNotNull(prefsManager.wallpaperState.value)
    assertNotNull(prefsManager.galleryPrefs.value)

    assertEquals(AppThemeMode.DARK, prefsManager.galleryPrefs.value.themeMode)
    assertEquals(ViewMode.GRID, prefsManager.galleryPrefs.value.viewMode)
    assertEquals(SortOrder.NEWEST, prefsManager.galleryPrefs.value.sortOrder)
    assertTrue(prefsManager.glassState.value.glassAlpha > 0f)
  }
}
