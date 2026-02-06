package com.eygraber.jellyfin.common

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class JellyfinResultFlowExtensionsTest {
  @Test
  fun mapSuccess_transforms_success_values() {
    runTest {
      val results = flowOf(
        JellyfinResult.Success(1),
        JellyfinResult.Success(2),
        JellyfinResult.Success(3),
      ).mapSuccess { it * 10 }.toList()

      results.size shouldBe 3
      results[0].successOrNull shouldBe 10
      results[1].successOrNull shouldBe 20
      results[2].successOrNull shouldBe 30
    }
  }

  @Test
  fun mapSuccess_passes_through_errors() {
    runTest {
      val error = JellyfinResult.Error.Generic(message = "fail", isEphemeral = true)
      val results = flowOf<JellyfinResult<Int>>(
        JellyfinResult.Success(1),
        error,
        JellyfinResult.Success(3),
      ).mapSuccess { it * 10 }.toList()

      results.size shouldBe 3
      results[0].successOrNull shouldBe 10
      results[1].isError() shouldBe true
      results[2].successOrNull shouldBe 30
    }
  }

  @Test
  fun flatMapSuccess_chains_results() {
    runTest {
      val results = flowOf(
        JellyfinResult.Success(42),
      ).flatMapSuccess { value ->
        JellyfinResult.Success("Number: $value")
      }.toList()

      results.size shouldBe 1
      results[0].successOrNull shouldBe "Number: 42"
    }
  }

  @Test
  fun flatMapSuccess_can_produce_error() {
    runTest {
      val results = flowOf(
        JellyfinResult.Success(42),
      ).flatMapSuccess {
        JellyfinResult.Error.Generic(message = "mapped to error", isEphemeral = true)
      }.toList()

      results.size shouldBe 1
      results[0].isError() shouldBe true
    }
  }

  @Test
  fun filterSuccess_extracts_success_values() {
    runTest {
      val values = flowOf<JellyfinResult<String>>(
        JellyfinResult.Success("a"),
        JellyfinResult.Error.Generic(message = "fail", isEphemeral = true),
        JellyfinResult.Success("b"),
      ).filterSuccess().toList()

      values shouldContainExactly listOf("a", "b")
    }
  }

  @Test
  fun filterErrors_extracts_error_values() {
    runTest {
      val errors = flowOf<JellyfinResult<String>>(
        JellyfinResult.Success("a"),
        JellyfinResult.Error.Generic(message = "fail1", isEphemeral = true),
        JellyfinResult.Success("b"),
        JellyfinResult.Error.Generic(message = "fail2", isEphemeral = false),
      ).filterErrors().toList()

      errors.size shouldBe 2
      errors[0].message shouldBe "fail1"
      errors[1].message shouldBe "fail2"
    }
  }

  @Test
  fun onEachSuccess_invokes_action_for_successes() {
    runTest {
      val captured = mutableListOf<String>()

      flowOf<JellyfinResult<String>>(
        JellyfinResult.Success("a"),
        JellyfinResult.Error.Generic(message = "fail", isEphemeral = true),
        JellyfinResult.Success("b"),
      ).onEachSuccess { captured.add(it) }.toList()

      captured shouldContainExactly listOf("a", "b")
    }
  }

  @Test
  fun onEachError_invokes_action_for_errors() {
    runTest {
      val captured = mutableListOf<String?>()

      flowOf<JellyfinResult<String>>(
        JellyfinResult.Success("a"),
        JellyfinResult.Error.Generic(message = "fail1", isEphemeral = true),
        JellyfinResult.Success("b"),
        JellyfinResult.Error.Generic(message = "fail2", isEphemeral = false),
      ).onEachError { captured.add(it.message) }.toList()

      captured shouldContainExactly listOf("fail1", "fail2")
    }
  }

  @Test
  fun catchAsResult_converts_exceptions_to_errors() {
    runTest {
      val results = flow<JellyfinResult<String>> {
        emit(JellyfinResult.Success("before"))
        throw RuntimeException("flow failed")
      }.catchAsResult().toList()

      results.size shouldBe 2
      results[0].successOrNull shouldBe "before"
      results[1].isError() shouldBe true
      results[1].errorMessageOrNull shouldBe "flow failed"
    }
  }

  @Test
  fun asResultFlow_wraps_values_in_success() {
    runTest {
      val results = flowOf("a", "b", "c").asResultFlow().toList()

      results.size shouldBe 3
      results.forEach {
        it.shouldBeInstanceOf<JellyfinResult.Success<String>>()
      }
      results.map { it.successOrNull } shouldContainExactly listOf("a", "b", "c")
    }
  }

  @Test
  fun asResultFlow_catches_upstream_exceptions() {
    runTest {
      val results = flow {
        emit("ok")
        throw RuntimeException("oops")
      }.asResultFlow().toList()

      results.size shouldBe 2
      results[0].successOrNull shouldBe "ok"
      results[1].isError() shouldBe true
      results[1].errorMessageOrNull shouldBe "oops"
    }
  }
}
