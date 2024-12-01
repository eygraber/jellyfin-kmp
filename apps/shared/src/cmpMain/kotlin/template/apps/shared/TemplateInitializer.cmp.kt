package template.apps.shared

import com.juul.khronicle.ConsoleLogger
import com.juul.khronicle.Log

internal actual fun TemplateInitializer.initializeEnvironment() {
  Log.dispatcher.install(ConsoleLogger)
}
