package template.app.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import template.apps.shared.TemplateInitializer

@DependencyGraph(AppScope::class)
interface TemplateIosAppGraph : TemplateIosViewControllerGraph.Factory {
  val initializer: TemplateInitializer

  @DependencyGraph.Factory
  interface Factory {
    fun create(): TemplateIosAppGraph
  }
}
