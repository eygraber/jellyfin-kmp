package template.app.init

import com.juul.khronicle.ConsoleLogger
import com.juul.khronicle.Log

internal fun TemplateInitializer.initializeEnvironment() {
  Log.dispatcher.install(ConsoleLogger)
}
