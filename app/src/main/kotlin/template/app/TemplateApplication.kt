package template.app

import android.app.Application
import template.app.di.TemplateApplicationComponent
import template.app.di.create

class TemplateApplication : Application() {
  val component by lazy {
    TemplateApplicationComponent::class.create(
      applicationDelegate = this,
    )
  }
}

internal val Application.templateApplicationComponent get() = (this as TemplateApplication).component
