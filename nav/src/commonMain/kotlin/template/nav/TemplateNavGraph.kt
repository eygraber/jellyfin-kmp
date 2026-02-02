package template.nav

import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import template.di.scopes.NavScope
import template.di.scopes.SessionScope
import template.services.device.sensors.ShakeDetector

@GraphExtension(NavScope::class)
interface TemplateNavGraph {
  val shakeDetector: ShakeDetector
  val shortcutManager: NavShortcutManager

  @ContributesTo(SessionScope::class)
  @GraphExtension.Factory
  interface Factory {
    fun createTemplateNavGraph(): TemplateNavGraph
  }
}
