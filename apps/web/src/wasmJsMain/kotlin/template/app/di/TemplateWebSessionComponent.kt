package template.app.di

import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesSubcomponent
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import template.di.scopes.SessionScope
import template.nav.TemplateNavComponent
import template.services.splash.screen.SplashScreenController

@ContributesSubcomponent(SessionScope::class)
@SingleIn(SessionScope::class)
interface TemplateWebSessionComponent : TemplateNavComponent.Factory {
  val splashScreenController: SplashScreenController

  @ContributesSubcomponent.Factory(AppScope::class)
  interface Factory {
    fun createTemplateWebSessionComponent(): TemplateWebSessionComponent
  }
}
