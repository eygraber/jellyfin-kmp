package template.app.di

import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.MergeComponent
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import template.apps.shared.TemplateInitializer

@MergeComponent(AppScope::class)
@SingleIn(AppScope::class)
abstract class TemplateDesktopAppComponent : TemplateDesktopSessionComponent.Factory {
  abstract val initializer: TemplateInitializer
}
