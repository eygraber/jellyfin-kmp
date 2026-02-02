package template.app.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import template.di.scopes.SessionScope
import template.nav.TemplateNavGraph
import template.services.splash.screen.SplashScreenController

@GraphExtension(SessionScope::class)
interface TemplateDesktopSessionGraph : TemplateNavGraph.Factory {
  val splashScreenController: SplashScreenController

  @ContributesTo(AppScope::class)
  @GraphExtension.Factory
  interface Factory {
    fun createTemplateDesktopSessionGraph(): TemplateDesktopSessionGraph
  }
}
