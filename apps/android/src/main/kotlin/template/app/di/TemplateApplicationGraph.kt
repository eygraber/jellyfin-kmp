package template.app.di

import android.app.Application
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import template.app.di.android.AndroidAppProviders
import template.apps.shared.TemplateInitializer
import template.di.scopes.AppScope

@DependencyGraph(AppScope::class)
interface TemplateApplicationGraph : AndroidAppProviders, TemplateActivityGraph.Factory {
  val initializer: TemplateInitializer

  @DependencyGraph.Factory
  interface Factory {
    fun create(@Provides application: Application): TemplateApplicationGraph
  }
}
