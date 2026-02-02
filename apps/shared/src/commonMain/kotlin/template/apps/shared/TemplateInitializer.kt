package template.apps.shared

import dev.zacsweers.metro.Inject

internal expect fun TemplateInitializer.initializeEnvironment()

@Inject
class TemplateInitializer {
  fun initialize() {
    initializeEnvironment()
  }
}
