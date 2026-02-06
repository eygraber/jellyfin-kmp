package com.eygraber.jellyfin.services.logging.impl

import com.eygraber.jellyfin.services.logging.LogSanitizer
import com.juul.khronicle.ConsoleLogger
import com.juul.khronicle.Log
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlin.test.BeforeTest
import kotlin.test.Test

class KhronicleLoggerTest {
  private val capturedMessages = mutableListOf<String>()

  private val capturingSanitizer = object : LogSanitizer {
    override fun sanitize(message: String): String {
      capturedMessages.add(message)
      return message.replace("secret", "[REDACTED]")
    }
  }

  private val logger = KhronicleLogger(sanitizer = capturingSanitizer)

  @BeforeTest
  fun setUp() {
    capturedMessages.clear()
    Log.dispatcher.install(ConsoleLogger)
  }

  @Test
  fun verbose_passes_message_through_sanitizer() {
    logger.verbose(tag = "Test", message = "my secret data")
    capturedMessages shouldHaveSize 1
    capturedMessages[0] shouldBe "my secret data"
  }

  @Test
  fun debug_passes_message_through_sanitizer() {
    logger.debug(tag = "Test", message = "debug secret info")
    capturedMessages shouldHaveSize 1
    capturedMessages[0] shouldBe "debug secret info"
  }

  @Test
  fun info_passes_message_through_sanitizer() {
    logger.info(tag = "Test", message = "info secret stuff")
    capturedMessages shouldHaveSize 1
    capturedMessages[0] shouldBe "info secret stuff"
  }

  @Test
  fun warn_passes_message_through_sanitizer() {
    logger.warn(tag = "Test", message = "warn secret warning")
    capturedMessages shouldHaveSize 1
    capturedMessages[0] shouldBe "warn secret warning"
  }

  @Test
  fun error_passes_message_through_sanitizer() {
    logger.error(tag = "Test", message = "error secret failure")
    capturedMessages shouldHaveSize 1
    capturedMessages[0] shouldBe "error secret failure"
  }

  @Test
  fun all_log_levels_use_sanitizer() {
    logger.verbose(tag = "T", message = "v")
    logger.debug(tag = "T", message = "d")
    logger.info(tag = "T", message = "i")
    logger.warn(tag = "T", message = "w")
    logger.error(tag = "T", message = "e")
    capturedMessages shouldHaveSize 5
  }
}
