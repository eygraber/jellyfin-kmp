@file:Suppress("MissingPackageDeclaration")

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController
import template.app.di.TemplateIosAppComponent
import template.app.di.createKmp
import template.apps.shared.TemplateAppSession

class TemplateIosApplication {
  private val appComponent = TemplateIosAppComponent::class.createKmp().apply {
    initializer.initialize()
  }

  fun createViewController(): UIViewController {
    val viewControllerComponent = appComponent.createTemplateIosViewControllerComponent()
    val navComponent = viewControllerComponent.createTemplateNavComponent()

    return ComposeUIViewController {
      TemplateAppSession(
        onDarkMode = {},
        navComponent = navComponent,
      )
    }
  }
}
