package com.eygraber.jellyfin.screens.settings

/**
 * Top-level groupings rendered on the settings screen.
 *
 * Each category will eventually navigate to its own dedicated sub-settings screen. Until those
 * screens are implemented, selecting a category surfaces a "coming soon" placeholder via the
 * navigator.
 */
enum class SettingsCategory {
  Playback,
  Subtitles,
  Display,
  BugReport,
}
