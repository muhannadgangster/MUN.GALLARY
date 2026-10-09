package com.example.data.model

enum class SortOrder(val label: String) {
  NEWEST("Newest first"),
  OLDEST("Oldest first"),
  LARGEST("Largest first"),
  SMALLEST("Smallest first"),
  NAME_AZ("Name A-Z"),
  NAME_ZA("Name Z-A")
}

enum class CategoryFilter(val label: String) {
  ALL("All"),
  PHOTOS("Photos"),
  VIDEOS("Videos"),
  ALBUMS("Albums"),
  FAVORITES("Favorites")
}

enum class ViewMode(val label: String) {
  GRID("Grid"),
  LIST("List")
}

enum class AppThemeMode(val label: String) {
  DARK("Dark"),
  LIGHT("Light"),
  SYSTEM("System")
}
