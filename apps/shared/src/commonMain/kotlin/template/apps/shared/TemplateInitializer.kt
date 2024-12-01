package template.apps.shared

import me.tatarka.inject.annotations.Inject

internal expect fun TemplateInitializer.initializeEnvironment()

@Inject
class TemplateInitializer {
  fun initialize() {
    initializeEnvironment()
  }
}
