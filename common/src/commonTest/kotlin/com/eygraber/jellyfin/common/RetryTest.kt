package com.eygraber.jellyfin.common

import io.kotest.matchers.longs.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class RetryTest {
  @Test
  fun returns_success_without_retrying() {
    runTest {
      var callCount = 0
      val result = retryWithBackoff(
        config = RetryConfig(initialDelayMs = 1),
      ) {
        callCount++
        JellyfinResult.Success("hello")
      }

      result.shouldBeInstanceOf<JellyfinResult.Success<String>>()
      result.value shouldBe "hello"
      callCount shouldBe 1
    }
  }

  @Test
  fun retries_on_error_up_to_max() {
    runTest {
      var callCount = 0
      val result = retryWithBackoff(
        config = RetryConfig(maxRetries = 3, initialDelayMs = 1),
      ) {
        callCount++
        JellyfinResult.Error.Generic(message = "fail", isEphemeral = true)
      }

      result.isError() shouldBe true
      callCount shouldBe 4 // 1 initial + 3 retries
    }
  }

  @Test
  fun succeeds_on_second_attempt() {
    runTest {
      var callCount = 0
      val result = retryWithBackoff(
        config = RetryConfig(maxRetries = 3, initialDelayMs = 1),
      ) {
        callCount++
        if(callCount < 2) {
          JellyfinResult.Error.Generic(message = "fail", isEphemeral = true)
        }
        else {
          JellyfinResult.Success("recovered")
        }
      }

      result.shouldBeInstanceOf<JellyfinResult.Success<String>>()
      result.value shouldBe "recovered"
      callCount shouldBe 2
    }
  }

  @Test
  fun stops_retrying_when_shouldRetry_returns_false() {
    runTest {
      var callCount = 0
      val result = retryWithBackoff(
        config = RetryConfig(
          maxRetries = 5,
          initialDelayMs = 1,
          shouldRetry = { false },
        ),
      ) {
        callCount++
        JellyfinResult.Error.Generic(message = "non-retryable", isEphemeral = false)
      }

      result.isError() shouldBe true
      callCount shouldBe 1
    }
  }

  @Test
  fun zero_retries_executes_once() {
    runTest {
      var callCount = 0
      val result = retryWithBackoff(
        config = RetryConfig(maxRetries = 0),
      ) {
        callCount++
        JellyfinResult.Error.Generic(message = "fail", isEphemeral = true)
      }

      result.isError() shouldBe true
      callCount shouldBe 1
    }
  }

  @Test
  fun calculateDelay_returns_correct_exponential_values() {
    calculateDelay(
      attempt = 0,
      initialDelayMs = 1_000,
      maxDelayMs = 30_000,
      multiplier = 2.0,
    ) shouldBe 1_000

    calculateDelay(
      attempt = 1,
      initialDelayMs = 1_000,
      maxDelayMs = 30_000,
      multiplier = 2.0,
    ) shouldBe 2_000

    calculateDelay(
      attempt = 2,
      initialDelayMs = 1_000,
      maxDelayMs = 30_000,
      multiplier = 2.0,
    ) shouldBe 4_000

    calculateDelay(
      attempt = 3,
      initialDelayMs = 1_000,
      maxDelayMs = 30_000,
      multiplier = 2.0,
    ) shouldBe 8_000
  }

  @Test
  fun calculateDelay_respects_max_delay() {
    val delay = calculateDelay(
      attempt = 10,
      initialDelayMs = 1_000,
      maxDelayMs = 30_000,
      multiplier = 2.0,
    )
    delay shouldBeLessThanOrEqual 30_000
  }
}
