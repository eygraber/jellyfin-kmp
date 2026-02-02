package template.app

import android.app.Application
import dev.zacsweers.metro.createGraphFactory
import template.app.di.TemplateApplicationGraph

class TemplateApplication : Application() {
  val graph by lazy {
    createGraphFactory<TemplateApplicationGraph.Factory>().create(
      application = this,
    )
  }
}

internal val Application.templateApplicationGraph get() = (this as TemplateApplication).graph
