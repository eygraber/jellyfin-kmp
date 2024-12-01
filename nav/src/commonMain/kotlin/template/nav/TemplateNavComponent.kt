package template.nav

import software.amazon.lastmile.kotlin.inject.anvil.ContributesSubcomponent
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import template.di.scopes.NavScope
import template.di.scopes.SessionScope
import template.services.device.sensors.ShakeDetector

@ContributesSubcomponent(NavScope::class)
@SingleIn(NavScope::class)
interface TemplateNavComponent {
  val shakeDetector: ShakeDetector
  val shortcutManager: NavShortcutManager

  @ContributesSubcomponent.Factory(SessionScope::class)
  interface Factory {
    fun createTemplateNavComponent(): TemplateNavComponent
  }
}
