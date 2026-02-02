package template.app.init

import android.content.Context
import androidx.startup.Initializer
import template.app.TemplateApplication

class TemplateAndroidXInitializer : Initializer<Unit> {
  override fun create(context: Context) {
    (context as TemplateApplication).graph.initializer.initialize()
  }

  override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
