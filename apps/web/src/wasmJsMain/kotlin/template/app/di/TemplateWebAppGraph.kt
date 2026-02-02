package template.app.di

import dev.zacsweers.metro.DependencyGraph
import template.apps.shared.TemplateInitializer
import template.di.scopes.AppScope

@DependencyGraph(AppScope::class)
interface TemplateWebAppGraph : TemplateWebSessionGraph.Factory {
  val initializer: TemplateInitializer

  @DependencyGraph.Factory
  interface Factory {
    fun create(): TemplateWebAppGraph
  }
}
