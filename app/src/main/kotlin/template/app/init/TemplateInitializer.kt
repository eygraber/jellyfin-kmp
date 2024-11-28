package template.app.init

import me.tatarka.inject.annotations.Inject

@Inject
class TemplateInitializer {
  fun initialize() {
    initializeEnvironment()
  }
}
