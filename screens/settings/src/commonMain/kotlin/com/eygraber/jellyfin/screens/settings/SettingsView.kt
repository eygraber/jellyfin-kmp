package com.eygraber.jellyfin.screens.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.eygraber.jellyfin.screens.settings.compose.SettingsCategoryItem
import com.eygraber.jellyfin.screens.settings.compose.toUi
import com.eygraber.jellyfin.ui.compose.PreviewJellyfinScreen
import com.eygraber.jellyfin.ui.icons.ArrowBack
import com.eygraber.jellyfin.ui.icons.JellyfinIcons
import com.eygraber.jellyfin.ui.icons.Logout
import com.eygraber.jellyfin.ui.icons.Person
import com.eygraber.jellyfin.ui.material.theme.JellyfinPreviewTheme
import com.eygraber.jellyfin.ui.material.theme.JellyfinTheme
import com.eygraber.vice.ViceView
import org.jetbrains.compose.resources.stringResource

internal typealias SettingsView = ViceView<SettingsIntent, SettingsViewState>

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsView(
  state: SettingsViewState,
  onIntent: (SettingsIntent) -> Unit,
) {
  JellyfinTheme {
    Scaffold(
      topBar = {
        TopAppBar(
          title = { Text(stringResource(Res.string.settings_title)) },
          navigationIcon = {
            IconButton(onClick = { onIntent(SettingsIntent.BackClicked) }) {
              Icon(
                imageVector = JellyfinIcons.ArrowBack,
                contentDescription = stringResource(Res.string.settings_cd_back),
              )
            }
          },
        )
      },
    ) { contentPadding ->
      val preferenceCategories = state.categories.filter { it != SettingsCategory.BugReport }
      val helpCategories = state.categories.filter { it == SettingsCategory.BugReport }

      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(contentPadding),
        contentPadding = PaddingValues(vertical = 8.dp),
      ) {
        if(state.userInfo != null) {
          item(key = "account-section") {
            SettingsSectionHeader(text = stringResource(Res.string.settings_section_account))
          }

          item(key = "user-info") {
            UserInfoSection(userInfo = state.userInfo)
          }

          item(key = "user-info-divider") {
            SectionDivider()
          }
        }

        if(preferenceCategories.isNotEmpty()) {
          item(key = "preferences-section") {
            SettingsSectionHeader(text = stringResource(Res.string.settings_section_preferences))
          }

          items(
            items = preferenceCategories,
            key = { it.name },
          ) { category ->
            SettingsCategoryItem(
              category = category.toUi(),
              onClick = { onIntent(SettingsIntent.CategoryClicked(category)) },
            )
          }
        }

        if(helpCategories.isNotEmpty()) {
          item(key = "preferences-divider") {
            SectionDivider()
          }

          item(key = "help-section") {
            SettingsSectionHeader(text = stringResource(Res.string.settings_section_help))
          }

          items(
            items = helpCategories,
            key = { it.name },
          ) { category ->
            SettingsCategoryItem(
              category = category.toUi(),
              onClick = { onIntent(SettingsIntent.CategoryClicked(category)) },
            )
          }
        }

        if(state.userInfo != null) {
          item(key = "sign-out-divider") {
            SectionDivider()
          }

          item(key = "sign-out") {
            SignOutItem(
              isSigningOut = state.isSigningOut,
              onClick = { onIntent(SettingsIntent.SignOutClicked) },
            )
          }
        }
      }
    }

    if(state.isSignOutDialogVisible) {
      SignOutConfirmationDialog(
        isSigningOut = state.isSigningOut,
        onConfirm = { onIntent(SettingsIntent.SignOutConfirmed) },
        onDismiss = { onIntent(SettingsIntent.SignOutDismissed) },
      )
    }
  }
}

@Composable
private fun SettingsSectionHeader(text: String) {
  Text(
    text = text,
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 12.dp),
    style = MaterialTheme.typography.labelLarge,
    color = MaterialTheme.colorScheme.primary,
  )
}

@Composable
private fun SectionDivider() {
  HorizontalDivider(
    modifier = Modifier.padding(vertical = 8.dp),
  )
}

@Composable
private fun UserInfoSection(userInfo: SettingsUserInfo) {
  ListItem(
    leadingContent = {
      Surface(
        modifier = Modifier
          .size(40.dp)
          .clip(CircleShape),
        color = MaterialTheme.colorScheme.primaryContainer,
      ) {
        Box(
          modifier = Modifier.fillMaxSize(),
          contentAlignment = Alignment.Center,
        ) {
          Icon(
            imageVector = JellyfinIcons.Person,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
          )
        }
      }
    },
    headlineContent = {
      Text(stringResource(Res.string.settings_signed_in_as, userInfo.username))
    },
    supportingContent = {
      Column {
        if(userInfo.serverName.isNotEmpty()) {
          Text(stringResource(Res.string.settings_connected_to, userInfo.serverName))
        }
        if(userInfo.serverUrl.isNotEmpty()) {
          Text(
            text = userInfo.serverUrl,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }
    },
  )
}

@Composable
private fun SignOutItem(
  isSigningOut: Boolean,
  onClick: () -> Unit,
) {
  Surface(
    onClick = onClick,
    enabled = !isSigningOut,
    modifier = Modifier.fillMaxWidth(),
    color = ListItemDefaults.containerColor,
  ) {
    ListItem(
      leadingContent = {
        Icon(
          imageVector = JellyfinIcons.Logout,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.error,
        )
      },
      headlineContent = {
        Text(
          text = stringResource(Res.string.settings_sign_out),
          color = MaterialTheme.colorScheme.error,
        )
      },
      trailingContent = if(isSigningOut) {
        {
          CircularProgressIndicator(
            modifier = Modifier.size(20.dp),
            strokeWidth = 2.dp,
          )
        }
      }
      else {
        null
      },
    )
  }
}

@Composable
private fun SignOutConfirmationDialog(
  isSigningOut: Boolean,
  onConfirm: () -> Unit,
  onDismiss: () -> Unit,
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(stringResource(Res.string.settings_sign_out_dialog_title)) },
    text = { Text(stringResource(Res.string.settings_sign_out_dialog_message)) },
    confirmButton = {
      TextButton(
        onClick = onConfirm,
        enabled = !isSigningOut,
      ) {
        if(isSigningOut) {
          CircularProgressIndicator(
            modifier = Modifier.size(18.dp),
            strokeWidth = 2.dp,
            color = MaterialTheme.colorScheme.error,
          )
        }
        else {
          Text(
            text = stringResource(Res.string.settings_sign_out_dialog_confirm),
            color = MaterialTheme.colorScheme.error,
          )
        }
      }
    },
    dismissButton = {
      TextButton(
        onClick = onDismiss,
        enabled = !isSigningOut,
      ) {
        Text(stringResource(Res.string.settings_sign_out_dialog_dismiss))
      }
    },
  )
}

@PreviewJellyfinScreen
@Composable
private fun SettingsAuthenticatedPreview() {
  JellyfinPreviewTheme {
    SettingsView(
      state = SettingsViewState(
        userInfo = SettingsUserInfo(
          username = "alice",
          serverName = "Home Server",
          serverUrl = "https://jellyfin.example.com",
        ),
      ),
      onIntent = {},
    )
  }
}

@PreviewJellyfinScreen
@Composable
private fun SettingsNoUserPreview() {
  JellyfinPreviewTheme {
    SettingsView(
      state = SettingsViewState(),
      onIntent = {},
    )
  }
}

@PreviewJellyfinScreen
@Composable
private fun SettingsSignOutDialogPreview() {
  JellyfinPreviewTheme {
    SettingsView(
      state = SettingsViewState(
        userInfo = SettingsUserInfo(
          username = "alice",
          serverName = "Home Server",
          serverUrl = "https://jellyfin.example.com",
        ),
        isSignOutDialogVisible = true,
      ),
      onIntent = {},
    )
  }
}

@PreviewJellyfinScreen
@Composable
private fun SettingsSigningOutPreview() {
  JellyfinPreviewTheme {
    SettingsView(
      state = SettingsViewState(
        userInfo = SettingsUserInfo(
          username = "alice",
          serverName = "Home Server",
          serverUrl = "https://jellyfin.example.com",
        ),
        isSignOutDialogVisible = true,
        isSigningOut = true,
      ),
      onIntent = {},
    )
  }
}
