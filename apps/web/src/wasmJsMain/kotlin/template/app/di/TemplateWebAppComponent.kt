package template.app.di

import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.MergeComponent
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import template.apps.shared.TemplateInitializer

@MergeComponent(AppScope::class)
@SingleIn(AppScope::class)
abstract class TemplateWebAppComponent : TemplateWebSessionComponent.Factory {
  abstract val initializer: TemplateInitializer
}
