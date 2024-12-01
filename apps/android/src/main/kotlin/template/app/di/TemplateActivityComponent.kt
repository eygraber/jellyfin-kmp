package template.app.di

import android.app.Activity
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesSubcomponent
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import template.app.di.android.AndroidActivityModule
import template.di.scopes.SessionScope
import template.nav.TemplateNavComponent
import template.services.splash.screen.SplashScreenController

@ContributesSubcomponent(SessionScope::class)
@SingleIn(SessionScope::class)
interface TemplateActivityComponent : AndroidActivityModule, TemplateNavComponent.Factory {
  val splashScreenController: SplashScreenController

  @ContributesSubcomponent.Factory(AppScope::class)
  interface Factory {
    fun createTemplateActivityComponent(
      activity: Activity,
    ): TemplateActivityComponent
  }
}
