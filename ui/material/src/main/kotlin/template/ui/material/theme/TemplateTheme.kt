package template.ui.material.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ElevatedSuggestionChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import template.ui.icons.Add
import template.ui.icons.ArrowBack
import template.ui.icons.Barbell
import template.ui.icons.Check
import template.ui.icons.Close
import template.ui.icons.Delete
import template.ui.icons.TemplateIcons

@Composable
fun TemplateTheme(
  isDarkMode: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = when {
      isDarkMode -> templateDarkColorScheme()
      else -> templateLightColorScheme()
    },
  ) {
    content()
  }
}

@Composable
fun TemplateDarkTheme(
  shouldSetSystemOverride: Boolean = true,
  content: @Composable () -> Unit,
) {
  if(shouldSetSystemOverride) {
    DisposableEffect(Unit) {
      SystemDarkModeOverride.pushOverride(SystemDarkModeOverride.Dark)

      onDispose {
        SystemDarkModeOverride.popOverride()
      }
    }
  }

  TemplateTheme(
    isDarkMode = true,
    content = content,
  )
}

@Composable
fun TemplateLightTheme(
  shouldSetSystemOverride: Boolean = true,
  content: @Composable () -> Unit,
) {
  if(shouldSetSystemOverride) {
    DisposableEffect(Unit) {
      SystemDarkModeOverride.pushOverride(SystemDarkModeOverride.Light)

      onDispose {
        SystemDarkModeOverride.popOverride()
      }
    }
  }

  TemplateTheme(
    isDarkMode = false,
    content = content,
  )
}

@PreviewLightDark
@Composable
private fun TemplateThemeButtonPreview() {
  TemplatePreviewScaffold(
    title = "Buttons",
    fab = {
      FloatingActionButton(
        onClick = {},
      ) {
        Icon(TemplateIcons.Check, contentDescription = null)
      }
    },
  ) {
    listOf(true, false).forEach { enabled ->
      TextButton(
        onClick = {},
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled,
      ) {
        Text("TextButton")
      }

      Button(
        onClick = {},
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled,
      ) {
        Text("Button")
      }

      FilledTonalButton(
        onClick = {},
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled,
      ) {
        Text("FilledTonalButton")
      }

      ElevatedButton(
        onClick = {},
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled,
      ) {
        Text("ElevatedButton")
      }

      OutlinedButton(
        onClick = {},
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled,
      ) {
        Text("OutlinedButton")
      }

      IconButton(
        onClick = {},
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled,
      ) {
        Icon(imageVector = TemplateIcons.Delete, contentDescription = null)
      }
    }
  }
}

@PreviewLightDark
@Composable
private fun TemplateThemeSlidersAndProgressPreview() {
  TemplatePreviewScaffold(
    title = "Sliders and Progress",
  ) {
    LinearProgressIndicator(
      progress = { .33F },
    )

    LinearProgressIndicator()

    Row(
      horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      CircularProgressIndicator(
        progress = { .33F },
      )

      CircularProgressIndicator()
    }

    Slider(
      value = 0.5F,
      onValueChange = {},
    )

    Slider(
      value = 0.5F,
      onValueChange = {},
      enabled = false,
    )

    RangeSlider(
      value = .25F..0.75F,
      onValueChange = {},
    )

    RangeSlider(
      value = .25F..0.75F,
      onValueChange = {},
      enabled = false,
    )
  }
}

@PreviewLightDark
@Composable
private fun TemplateThemeTextFieldsPreview() {
  TemplatePreviewScaffold(
    title = "TextFields",
  ) {
    listOf(true, false).forEach { enabled ->
      TextField(
        value = "",
        onValueChange = {},
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled,
        label = {
          Text(text = "Enter text")
        },
      )

      TextField(
        value = "Lorem ipsum",
        onValueChange = {},
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled,
        label = {
          Text(text = "Enter text")
        },
      )

      if(enabled) {
        TextField(
          value = "Lorem ipsum",
          onValueChange = {},
          modifier = Modifier.fillMaxWidth(),
          label = {
            Text(text = "Enter text")
          },
          supportingText = {
            Text("Stop using lorem ipsum")
          },
          isError = true,
        )
      }

      OutlinedTextField(
        value = "",
        onValueChange = {},
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled,
        label = {
          Text(text = "Enter text")
        },
      )

      OutlinedTextField(
        value = "Lorem ipsum",
        onValueChange = {},
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled,
        label = {
          Text(text = "Enter text")
        },
      )
    }
  }
}

@PreviewLightDark
@Composable
private fun TemplateThemeCompoundButtonsPreview() {
  TemplatePreviewScaffold(
    title = "CompoundButtons",
  ) {
    listOf(
      true to true,
      true to false,
      false to true,
      false to false,
    ).forEach { state ->
      val (checked, enabled) = state

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        Checkbox(
          checked = checked,
          onCheckedChange = {},
          enabled = enabled,
        )

        Text("Checkbox")
      }
    }

    listOf(
      true to true,
      true to false,
      false to true,
      false to false,
    ).forEach { state ->
      val (selected, enabled) = state
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        RadioButton(
          selected = selected,
          onClick = {},
          enabled = enabled,
        )

        Text("RadioButton")
      }
    }

    listOf(
      true to true,
      true to false,
      false to true,
      false to false,
    ).forEach { state ->
      val (selected, enabled) = state
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        Switch(
          checked = selected,
          onCheckedChange = {},
          enabled = enabled,
        )

        Text("Switch")
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@PreviewLightDark
@Composable
private fun TemplateThemeChipPreview() {
  TemplatePreviewScaffold(
    title = "Chips",
  ) {
    FlowRow {
      AssistChip(
        onClick = {},
        label = {
          Text("I'm an AssistChip")
        },
        leadingIcon = {
          Icon(TemplateIcons.Barbell, contentDescription = null)
        },
      )

      ElevatedAssistChip(
        onClick = {},
        label = {
          Text("I'm an ElevatedAssistChip")
        },
        leadingIcon = {
          Icon(TemplateIcons.Barbell, contentDescription = null)
        },
      )

      FilterChip(
        selected = true,
        onClick = {},
        label = {
          Text("I'm a selected FilterChip")
        },
        leadingIcon = {
          Icon(TemplateIcons.Check, contentDescription = null)
        },
      )

      FilterChip(
        selected = false,
        onClick = {},
        label = {
          Text("I'm an unselected FilterChip")
        },
        leadingIcon = {
          Icon(TemplateIcons.Check, contentDescription = null)
        },
      )

      ElevatedFilterChip(
        selected = true,
        onClick = {},
        label = {
          Text("I'm a selected ElevatedFilterChip")
        },
        leadingIcon = {
          Icon(TemplateIcons.Check, contentDescription = null)
        },
      )

      ElevatedFilterChip(
        selected = false,
        onClick = {},
        label = {
          Text("I'm an unselected ElevatedFilterChip")
        },
        leadingIcon = {
          Icon(TemplateIcons.Check, contentDescription = null)
        },
      )

      InputChip(
        selected = true,
        onClick = {},
        label = {
          Text("I'm a selected InputChip")
        },
        leadingIcon = {
          Icon(TemplateIcons.Check, contentDescription = null)
        },
        trailingIcon = {
          Icon(TemplateIcons.Close, contentDescription = null)
        },
      )

      InputChip(
        selected = false,
        onClick = {},
        label = {
          Text("I'm an unselected InputChip")
        },
        leadingIcon = {
          Icon(TemplateIcons.Check, contentDescription = null)
        },
        trailingIcon = {
          Icon(TemplateIcons.Close, contentDescription = null)
        },
      )

      SuggestionChip(
        onClick = {},
        label = {
          Text("I'm a SuggestionChip")
        },
      )

      ElevatedSuggestionChip(
        onClick = {},
        label = {
          Text("I'm an ElevatedSuggestionChip")
        },
      )
    }
  }
}

@PreviewLightDark
@Composable
private fun TemplateThemeSurfacePreview() {
  TemplatePreviewScaffold(
    title = "Surfaces",
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .height(150.dp),
    ) {
      Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
      ) {
        Text("I'm a Card")
      }
    }

    ElevatedCard(
      modifier = Modifier
        .fillMaxWidth()
        .height(150.dp),
    ) {
      Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
      ) {
        Text("I'm an ElevatedCard")
      }
    }

    OutlinedCard(
      modifier = Modifier
        .fillMaxWidth()
        .height(150.dp),
    ) {
      Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
      ) {
        Text("I'm an OutlinedCard")
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TemplatePreviewScaffold(
  title: String,
  fab: (@Composable () -> Unit)? = null,
  content: @Composable ColumnScope.() -> Unit,
) {
  TemplateTheme {
    Scaffold(
      topBar = {
        TopAppBar(
          title = {
            Text(title)
          },
          navigationIcon = {
            IconButton(
              onClick = {},
            ) {
              Icon(TemplateIcons.ArrowBack, contentDescription = null)
            }
          },
          actions = {
            IconButton(
              onClick = {},
            ) {
              Icon(TemplateIcons.Add, contentDescription = null)
            }

            IconButton(
              onClick = {},
            ) {
              Icon(TemplateIcons.Delete, contentDescription = null)
            }
          },
        )
      },
      floatingActionButton = {
        fab?.invoke()
      },
    ) { contentPadding ->
      Column(
        modifier = Modifier
          .padding(contentPadding)
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        content()
      }
    }
  }
}
