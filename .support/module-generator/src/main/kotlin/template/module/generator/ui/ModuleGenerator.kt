package template.module.generator.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.singleWindowApplication
import javax.swing.UIManager

fun main() {
  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

  singleWindowApplication(title = "Module Generator") {
    val viewModel = remember { ModuleGeneratorViewModel() }

    MaterialTheme(
      colorScheme = if(isSystemInDarkTheme()) darkColorScheme() else lightColorScheme(),
    ) {
      Surface(modifier = Modifier.fillMaxSize()) {
        if(viewModel.isProgressShowing) {
          Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
          ) {
            Text(viewModel.progressText)
            CircularProgressIndicator()
          }
        }
        else if(viewModel.isProjectDirValid) {
          Generator(viewModel)
        }
        else {
          GeneratorErrorDialog(
            text = "Please run the generator in the root of the project.",
          )
        }
      }
    }
  }
}

@Composable
private fun Generator(viewModel: ModuleGeneratorViewModel) {
  val initialFocusRequester = remember { FocusRequester() }

  LaunchedEffect(Unit) {
    initialFocusRequester.requestFocus()
  }

  Column(
    modifier = Modifier.padding(horizontal = 16.dp).verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    GeneratorCheckbox(
      isChecked = viewModel.shouldGenerateViceEffects,
      title = "Generate ViceEffects",
      onCheckedChange = viewModel::onGenerateViceEffectsChange,
    )

    Row {
      GeneratorCheckbox(
        isChecked = viewModel.shouldInferPackageName,
        title = "Infer package name",
        onCheckedChange = viewModel::onInferPackageNameChange,
      )

      GeneratorCheckbox(
        isChecked = viewModel.shouldInferModuleName,
        title = "Infer module name",
        onCheckedChange = viewModel::onInferModuleNameChange,
      )
    }

    Row {
      GeneratorCheckbox(
        isChecked = viewModel.shouldGeneratePreview,
        title = "Generate a preview",
        onCheckedChange = viewModel::onGeneratePreviewChange,
      )

      GeneratorCheckbox(
        isChecked = viewModel.shouldGeneratePreviewParameterProvider,
        title = "Generate a preview parameter provider",
        isEnabled = viewModel.shouldGeneratePreview,
        onCheckedChange = viewModel::onGeneratePreviewParameterProviderChange,
      )
    }

    FeatureNameTextField(
      featureName = viewModel.featureName,
      featureNameError = viewModel.featureNameError,
      onValueChange = viewModel::onFeatureNameChange,
      modifier = Modifier.focusRequester(initialFocusRequester),
    )

    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      ModuleNameTextField(
        moduleNamePrefix = viewModel.moduleNamePrefix,
        moduleName = viewModel.moduleName,
        moduleNameError = viewModel.moduleNameError,
        isModuleEnabled = !viewModel.shouldInferModuleName,
        onValueChange = viewModel::onModuleNameChange,
      )

      if(viewModel.doesModuleAlreadyExist) {
        Text(
          text = "Generating into an existing module",
          color = MaterialTheme.colorScheme.primary,
          style = MaterialTheme.typography.labelMedium,
        )
      }
    }

    PackageNameTextField(
      packageName = viewModel.packageName,
      packageNameError = viewModel.packageNameError,
      isPackageEnabled = !viewModel.shouldInferPackageName,
      onValueChange = viewModel::onPackageNameChange,
    )

    Button(
      onClick = { viewModel.generate() },
      enabled = viewModel.isGenerationEnabled,
    ) {
      Text(
        text = "Generate",
      )
    }
  }
}

@Composable
private fun FeatureNameTextField(
  featureName: String,
  featureNameError: String?,
  onValueChange: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  GeneratorTextField(
    value = featureName,
    error = featureNameError,
    label = {
      Text("Feature name:")
    },
    onValueChange = onValueChange,
    modifier = modifier,
  )
}

@Composable
private fun ModuleNameTextField(
  moduleNamePrefix: String,
  moduleName: String,
  moduleNameError: String?,
  isModuleEnabled: Boolean,
  onValueChange: (String) -> Unit,
) {
  GeneratorTextField(
    value = moduleName,
    error = moduleNameError,
    enabled = isModuleEnabled,
    label = {
      Text("Module name:")
    },
    prefix = moduleNamePrefix,
    onValueChange = onValueChange,
  )
}

@Composable
private fun PackageNameTextField(
  packageName: String,
  packageNameError: String?,
  isPackageEnabled: Boolean,
  onValueChange: (String) -> Unit,
) {
  GeneratorTextField(
    value = packageName,
    error = packageNameError,
    enabled = isPackageEnabled,
    label = {
      Text("Package name:")
    },
    onValueChange = onValueChange,
  )
}
