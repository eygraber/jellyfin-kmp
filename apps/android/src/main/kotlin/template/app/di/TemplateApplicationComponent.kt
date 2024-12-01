package template.app.di

import android.app.Application
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.MergeComponent
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import template.app.di.android.AndroidAppModule
import template.apps.shared.TemplateInitializer

@MergeComponent(AppScope::class)
@SingleIn(AppScope::class)
abstract class TemplateApplicationComponent(
  override val application: Application,
) : AndroidAppModule, TemplateActivityComponent.Factory {
  abstract val initializer: TemplateInitializer
}
