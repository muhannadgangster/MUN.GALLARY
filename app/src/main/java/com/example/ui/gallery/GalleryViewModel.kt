package com.example.ui.gallery

import android.app.Application
import android.content.Intent
import android.graphics.Bitmap
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.FavoriteEntity
import com.example.data.local.PreferencesManager
import com.example.data.model.Album
import com.example.data.model.CategoryFilter
import com.example.data.model.MediaItem
import com.example.data.model.SortOrder
import com.example.data.model.ViewMode
import com.example.data.repository.MediaStoreRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class MediaDateSection(
  val title: String,
  val items: List<MediaItem>
)

class GalleryViewModel(application: Application) : AndroidViewModel(application) {
  private val mediaRepo = MediaStoreRepository(application)
  private val favDao = AppDatabase.getDatabase(application).favoriteDao()
  val prefsManager = PreferencesManager.getInstance(application)

  private val _rawMediaList = MutableStateFlow<List<MediaItem>>(emptyList())
  private val _favoriteIds = MutableStateFlow<Set<Long>>(emptySet())

  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _isSearchActive = MutableStateFlow(false)
  val isSearchActive: StateFlow<Boolean> = _isSearchActive.asStateFlow()

  private val _categoryFilter = MutableStateFlow(CategoryFilter.ALL)
  val categoryFilter: StateFlow<CategoryFilter> = _categoryFilter.asStateFlow()

  private val _selectedIds = MutableStateFlow<Set<Long>>(emptySet())
  val selectedIds: StateFlow<Set<Long>> = _selectedIds.asStateFlow()

  private val _isSelectionMode = MutableStateFlow(false)
  val isSelectionMode: StateFlow<Boolean> = _isSelectionMode.asStateFlow()

  private val _isLoading = MutableStateFlow(false)
  val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

  private val _isPermissionGranted = MutableStateFlow(false)
  val isPermissionGranted: StateFlow<Boolean> = _isPermissionGranted.asStateFlow()

  private val _albums = MutableStateFlow<List<Album>>(emptyList())
  val albums: StateFlow<List<Album>> = _albums.asStateFlow()

  // IntentSenderRequest event for deletion prompt on Android 11+
  private val _deleteIntentEvent = MutableSharedFlow<IntentSenderRequest>()
  val deleteIntentEvent: SharedFlow<IntentSenderRequest> = _deleteIntentEvent.asSharedFlow()

  private val _toastMessage = MutableSharedFlow<String>()
  val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

  // Filtered & Sorted Media items
  val filteredMedia = MutableStateFlow<List<MediaItem>>(emptyList())
  val dateSections = MutableStateFlow<List<MediaDateSection>>(emptyList())

  init {
    viewModelScope.launch {
      favDao.getAllFavoriteIds().collectLatest { ids ->
        _favoriteIds.value = ids.toSet()
        recomputeMedia()
      }
    }

    viewModelScope.launch {
      combine(
        _rawMediaList,
        _favoriteIds,
        _searchQuery,
        _categoryFilter,
        prefsManager.galleryPrefs
      ) { raw, favs, query, filter, prefs ->
        processMediaList(raw, favs, query, filter, prefs.sortOrder, prefs.showVideos, prefs.showScreenshots)
      }.collectLatest { (processedList, sections) ->
        filteredMedia.value = processedList
        dateSections.value = sections
      }
    }
  }

  fun setPermissionGranted(granted: Boolean) {
    _isPermissionGranted.value = granted
    if (granted) {
      loadMedia()
    }
  }

  fun loadMedia() {
    viewModelScope.launch {
      _isLoading.value = true
      val prefs = prefsManager.galleryPrefs.value
      val items = mediaRepo.fetchAllMedia(
        includeVideos = prefs.showVideos,
        includeScreenshots = prefs.showScreenshots
      )
      _rawMediaList.value = items
      _albums.value = mediaRepo.fetchAlbums(items)
      _isLoading.value = false
    }
  }

  private fun recomputeMedia() {
    val prefs = prefsManager.galleryPrefs.value
    val (processedList, sections) = processMediaList(
      _rawMediaList.value,
      _favoriteIds.value,
      _searchQuery.value,
      _categoryFilter.value,
      prefs.sortOrder,
      prefs.showVideos,
      prefs.showScreenshots
    )
    filteredMedia.value = processedList
    dateSections.value = sections
  }

  private fun processMediaList(
    raw: List<MediaItem>,
    favs: Set<Long>,
    query: String,
    filter: CategoryFilter,
    sortOrder: SortOrder,
    showVideos: Boolean,
    showScreenshots: Boolean
  ): Pair<List<MediaItem>, List<MediaDateSection>> {
    val itemsWithFav = raw.map { it.copy(isFavorite = favs.contains(it.id)) }

    var result = itemsWithFav.filter { item ->
      val matchesType = when (filter) {
        CategoryFilter.ALL -> true
        CategoryFilter.PHOTOS -> !item.isVideo
        CategoryFilter.VIDEOS -> item.isVideo
        CategoryFilter.ALBUMS -> true
        CategoryFilter.FAVORITES -> item.isFavorite
      }
      val matchesQuery = if (query.isBlank()) true else {
        item.name.contains(query, ignoreCase = true) ||
          item.bucketName.contains(query, ignoreCase = true) ||
          (query.equals("video", ignoreCase = true) && item.isVideo) ||
          (query.equals("photo", ignoreCase = true) && !item.isVideo)
      }
      matchesType && matchesQuery
    }

    result = when (sortOrder) {
      SortOrder.NEWEST -> result.sortedByDescending { it.dateAdded }
      SortOrder.OLDEST -> result.sortedBy { it.dateAdded }
      SortOrder.LARGEST -> result.sortedByDescending { it.size }
      SortOrder.SMALLEST -> result.sortedBy { it.size }
      SortOrder.NAME_AZ -> result.sortedBy { it.name.lowercase(Locale.ROOT) }
      SortOrder.NAME_ZA -> result.sortedByDescending { it.name.lowercase(Locale.ROOT) }
    }

    val sections = groupIntoDateSections(result)
    return Pair(result, sections)
  }

  private fun groupIntoDateSections(items: List<MediaItem>): List<MediaDateSection> {
    if (items.isEmpty()) return emptyList()

    val calendar = Calendar.getInstance()
    val today = calendar.get(Calendar.DAY_OF_YEAR)
    val thisYear = calendar.get(Calendar.YEAR)

    calendar.add(Calendar.DAY_OF_YEAR, -1)
    val yesterday = calendar.get(Calendar.DAY_OF_YEAR)
    val yesterdayYear = calendar.get(Calendar.YEAR)

    val itemCalendar = Calendar.getInstance()
    val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

    val grouped = linkedMapOf<String, MutableList<MediaItem>>()

    for (item in items) {
      itemCalendar.timeInMillis = item.dateAdded * 1000L
      val itemDay = itemCalendar.get(Calendar.DAY_OF_YEAR)
      val itemYear = itemCalendar.get(Calendar.YEAR)

      val sectionTitle = when {
        itemYear == thisYear && itemDay == today -> "Today"
        itemYear == yesterdayYear && itemDay == yesterday -> "Yesterday"
        itemYear == thisYear -> SimpleDateFormat("MMMM d", Locale.getDefault()).format(itemCalendar.time)
        else -> monthYearFormat.format(itemCalendar.time)
      }

      grouped.getOrPut(sectionTitle) { mutableListOf() }.add(item)
    }

    return grouped.map { MediaDateSection(it.key, it.value) }
  }

  // Search
  fun onSearchQueryChange(query: String) {
    _searchQuery.value = query
  }

  fun toggleSearch() {
    val next = !_isSearchActive.value
    _isSearchActive.value = next
    if (!next) _searchQuery.value = ""
  }

  // Category Filter
  fun setCategoryFilter(filter: CategoryFilter) {
    _categoryFilter.value = filter
  }

  // Selection Mode
  fun toggleSelection(itemId: Long) {
    val current = _selectedIds.value.toMutableSet()
    if (current.contains(itemId)) {
      current.remove(itemId)
    } else {
      current.add(itemId)
    }
    _selectedIds.value = current
    if (current.isEmpty()) {
      _isSelectionMode.value = false
    } else {
      _isSelectionMode.value = true
    }
  }

  fun enterSelectionMode(firstItemId: Long) {
    _isSelectionMode.value = true
    _selectedIds.value = setOf(firstItemId)
  }

  fun cancelSelectionMode() {
    _isSelectionMode.value = false
    _selectedIds.value = emptySet()
  }

  fun selectAll() {
    _selectedIds.value = filteredMedia.value.map { it.id }.toSet()
    _isSelectionMode.value = true
  }

  fun deselectAll() {
    _selectedIds.value = emptySet()
    _isSelectionMode.value = false
  }

  // Favorite toggle
  fun toggleFavorite(item: MediaItem) {
    viewModelScope.launch {
      if (item.isFavorite) {
        favDao.deleteFavorite(item.id)
      } else {
        favDao.insertFavorite(
          FavoriteEntity(
            mediaId = item.id,
            mediaUri = item.uri.toString()
          )
        )
      }
    }
  }

  fun favoriteSelected() {
    val selected = filteredMedia.value.filter { _selectedIds.value.contains(it.id) }
    viewModelScope.launch {
      val allFav = selected.all { it.isFavorite }
      if (allFav) {
        favDao.deleteFavorites(selected.map { it.id })
      } else {
        selected.forEach {
          favDao.insertFavorite(FavoriteEntity(it.id, it.uri.toString()))
        }
      }
      cancelSelectionMode()
    }
  }

  // Deletion
  fun deleteItems(items: List<MediaItem>) {
    viewModelScope.launch {
      when (val result = mediaRepo.deleteMediaItems(items)) {
        is MediaStoreRepository.DeleteResult.Success -> {
          // Remove deleted items from Room favorites
          favDao.deleteFavorites(items.map { it.id })
          cancelSelectionMode()
          loadMedia()
          _toastMessage.emit("Deleted successfully")
        }
        is MediaStoreRepository.DeleteResult.RequiresPermission -> {
          _deleteIntentEvent.emit(result.intentSenderRequest)
        }
        is MediaStoreRepository.DeleteResult.Failure -> {
          _toastMessage.emit(result.error)
        }
      }
    }
  }

  fun deleteSelected() {
    val selected = filteredMedia.value.filter { _selectedIds.value.contains(it.id) }
    deleteItems(selected)
  }

  // Saving edited image copy
  fun saveEditedCopy(bitmap: Bitmap, originalName: String, onFinished: (Boolean) -> Unit) {
    viewModelScope.launch {
      val savedUri = mediaRepo.saveEditedImageCopy(bitmap, originalName)
      if (savedUri != null) {
        loadMedia()
        _toastMessage.emit("Copy saved to Pictures/M-U-N Gallery")
        onFinished(true)
      } else {
        _toastMessage.emit("Failed to save edited copy")
        onFinished(false)
      }
    }
  }

  // Share Intent
  fun createShareIntent(items: List<MediaItem>): Intent {
    return mediaRepo.createShareIntent(items)
  }

  fun createShareIntentForSelected(): Intent {
    val selected = filteredMedia.value.filter { _selectedIds.value.contains(it.id) }
    return mediaRepo.createShareIntent(selected)
  }
}
