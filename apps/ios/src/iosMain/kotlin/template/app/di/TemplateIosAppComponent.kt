package template.app.di

import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.MergeComponent
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import template.apps.shared.TemplateInitializer
import kotlin.reflect.KClass

@MergeComponent(AppScope::class)
@SingleIn(AppScope::class)
abstract class TemplateIosAppComponent : TemplateIosViewControllerComponent.Factory {
  abstract val initializer: TemplateInitializer
}

@MergeComponent.CreateComponent
expect fun KClass<TemplateIosAppComponent>.createKmp(): TemplateIosAppComponent
