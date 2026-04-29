package com.eygraber.jellyfin.screens.settings.compose

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.eygraber.jellyfin.screens.settings.Res
import com.eygraber.jellyfin.screens.settings.SettingsCategory
import com.eygraber.jellyfin.screens.settings.settings_category_bug_report
import com.eygraber.jellyfin.screens.settings.settings_category_bug_report_summary
import com.eygraber.jellyfin.screens.settings.settings_category_display
import com.eygraber.jellyfin.screens.settings.settings_category_display_summary
import com.eygraber.jellyfin.screens.settings.settings_category_playback
import com.eygraber.jellyfin.screens.settings.settings_category_playback_summary
import com.eygraber.jellyfin.screens.settings.settings_category_subtitles
import com.eygraber.jellyfin.screens.settings.settings_category_subtitles_summary
import com.eygraber.jellyfin.ui.icons.BugReport
import com.eygraber.jellyfin.ui.icons.ChevronRight
import com.eygraber.jellyfin.ui.icons.ClosedCaption
import com.eygraber.jellyfin.ui.icons.JellyfinIcons
import com.eygraber.jellyfin.ui.icons.Palette
import com.eygraber.jellyfin.ui.icons.PlayCircle
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

internal data class SettingsCategoryUi(
  val category: SettingsCategory,
  val icon: ImageVector,
  val label: StringResource,
  val summary: StringResource,
)

internal fun SettingsCategory.toUi(): SettingsCategoryUi = when(this) {
  SettingsCategory.Playback -> SettingsCategoryUi(
    category = this,
    icon = JellyfinIcons.PlayCircle,
    label = Res.string.settings_category_playback,
    summary = Res.string.settings_category_playback_summary,
  )
  SettingsCategory.Subtitles -> SettingsCategoryUi(
    category = this,
    icon = JellyfinIcons.ClosedCaption,
    label = Res.string.settings_category_subtitles,
    summary = Res.string.settings_category_subtitles_summary,
  )
  SettingsCategory.Display -> SettingsCategoryUi(
    category = this,
    icon = JellyfinIcons.Palette,
    label = Res.string.settings_category_display,
    summary = Res.string.settings_category_display_summary,
  )
  SettingsCategory.BugReport -> SettingsCategoryUi(
    category = this,
    icon = JellyfinIcons.BugReport,
    label = Res.string.settings_category_bug_report,
    summary = Res.string.settings_category_bug_report_summary,
  )
}

@Composable
internal fun SettingsCategoryItem(
  category: SettingsCategoryUi,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Surface(
    onClick = onClick,
    modifier = modifier.fillMaxWidth(),
    color = ListItemDefaults.containerColor,
  ) {
    ListItem(
      leadingContent = {
        Icon(
          imageVector = category.icon,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      },
      headlineContent = {
        Text(stringResource(category.label))
      },
      supportingContent = {
        Text(stringResource(category.summary))
      },
      trailingContent = {
        Icon(
          imageVector = JellyfinIcons.ChevronRight,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      },
    )
  }
}
