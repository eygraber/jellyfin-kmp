package template.app.di

import android.app.Activity
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import template.app.di.android.AndroidActivityProviders
import template.di.scopes.AppScope
import template.di.scopes.SessionScope
import template.nav.TemplateNavGraph
import template.services.splash.screen.SplashScreenController

@GraphExtension(SessionScope::class)
interface TemplateActivityGraph : AndroidActivityProviders, TemplateNavGraph.Factory {
  val splashScreenController: SplashScreenController

  @ContributesTo(AppScope::class)
  @GraphExtension.Factory
  interface Factory {
    fun createTemplateActivityGraph(
      @Provides activity: Activity,
    ): TemplateActivityGraph
  }
}
