package com.eygraber.jellyfin.common

import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.Test

class JellyfinResultTest {
  @Test
  fun success_isSuccess_returns_true() {
    val result: JellyfinResult<String> = JellyfinResult.Success("hello")
    result.isSuccess() shouldBe true
  }

  @Test
  fun success_isError_returns_false() {
    val result: JellyfinResult<String> = JellyfinResult.Success("hello")
    result.isError() shouldBe false
  }

  @Test
  fun error_isError_returns_true() {
    val result: JellyfinResult<String> = JellyfinResult.Error.Generic(
      message = "failure",
      isEphemeral = false,
    )
    result.isError() shouldBe true
  }

  @Test
  fun error_isSuccess_returns_false() {
    val result: JellyfinResult<String> = JellyfinResult.Error.Generic(
      message = "failure",
      isEphemeral = false,
    )
    result.isSuccess() shouldBe false
  }

  @Test
  fun successOrNull_returns_value_for_success() {
    val result: JellyfinResult<String> = JellyfinResult.Success("hello")
    result.successOrNull shouldBe "hello"
  }

  @Test
  fun successOrNull_returns_null_for_error() {
    val result: JellyfinResult<String> = JellyfinResult.Error()
    result.successOrNull.shouldBeNull()
  }

  @Test
  fun throwableOrNull_returns_throwable_for_detailed_error() {
    val exception = RuntimeException("oops")
    val result: JellyfinResult<String> = JellyfinResult.Error.Detailed(
      details = exception,
      message = "oops",
      isEphemeral = true,
    )
    result.throwableOrNull shouldBe exception
  }

  @Test
  fun throwableOrNull_returns_null_for_generic_error() {
    val result: JellyfinResult<String> = JellyfinResult.Error.Generic(
      message = "oops",
      isEphemeral = true,
    )
    result.throwableOrNull.shouldBeNull()
  }

  @Test
  fun throwableOrNull_returns_null_for_success() {
    val result: JellyfinResult<String> = JellyfinResult.Success("hello")
    result.throwableOrNull.shouldBeNull()
  }

  @Test
  fun errorMessageOrNull_returns_message_for_error() {
    val result: JellyfinResult<String> = JellyfinResult.Error.Generic(
      message = "something went wrong",
      isEphemeral = false,
    )
    result.errorMessageOrNull shouldBe "something went wrong"
  }

  @Test
  fun errorMessageOrNull_returns_null_for_success() {
    val result: JellyfinResult<String> = JellyfinResult.Success("hello")
    result.errorMessageOrNull.shouldBeNull()
  }

  @Test
  fun errorDetailOrNull_returns_details_for_detailed_error() {
    val result: JellyfinResult<String> = JellyfinResult.Error.Detailed(
      details = "some details",
      message = "oops",
      isEphemeral = true,
    )
    result.errorDetailOrNull shouldBe "some details"
  }

  @Test
  fun errorDetailOrNull_returns_null_for_generic_error() {
    val result: JellyfinResult<String> = JellyfinResult.Error.Generic(
      message = "oops",
      isEphemeral = true,
    )
    result.errorDetailOrNull.shouldBeNull()
  }

  @Test
  fun mapToUnit_converts_success_to_unit_success() {
    val result = JellyfinResult.Success("hello").mapToUnit()
    result.shouldBeInstanceOf<JellyfinResult.Success<Unit>>()
    result.value shouldBe Unit
  }

  @Test
  fun mapToUnit_passes_through_error() {
    val error = JellyfinResult.Error.Generic(message = "fail", isEphemeral = false)
    val result = error.mapToUnit()
    result shouldBe error
  }

  @Test
  fun mapSuccessTo_transforms_success_value() {
    val result = JellyfinResult.Success(42).mapSuccessTo { toString() }
    result.shouldBeInstanceOf<JellyfinResult.Success<String>>()
    result.value shouldBe "42"
  }

  @Test
  fun mapSuccessTo_passes_through_error() {
    val error: JellyfinResult<Int> = JellyfinResult.Error.Generic(
      message = "fail",
      isEphemeral = false,
    )
    val result = error.mapSuccessTo { toString() }
    result.isError() shouldBe true
  }

  @Test
  fun flatMapSuccessTo_chains_success() {
    val result = JellyfinResult.Success(42).flatMapSuccessTo {
      JellyfinResult.Success(this * 2)
    }
    result.shouldBeInstanceOf<JellyfinResult.Success<Int>>()
    result.value shouldBe 84
  }

  @Test
  fun flatMapSuccessTo_chains_to_error() {
    val result = JellyfinResult.Success(42).flatMapSuccessTo {
      JellyfinResult.Error.Generic(message = "failed", isEphemeral = true)
    }
    result.isError() shouldBe true
  }

  @Test
  fun flatMapSuccessTo_passes_through_error() {
    val error: JellyfinResult<Int> = JellyfinResult.Error.Generic(
      message = "fail",
      isEphemeral = false,
    )
    val result = error.flatMapSuccessTo { JellyfinResult.Success(99) }
    result.isError() shouldBe true
    result.errorMessageOrNull shouldBe "fail"
  }

  @Test
  fun andThen_executes_block_on_success() {
    var wasExecuted = false
    val result = JellyfinResult.Success("hello").andThen { wasExecuted = true }
    wasExecuted shouldBe true
    result.isSuccess() shouldBe true
  }

  @Test
  fun andThen_does_not_execute_block_on_error() {
    var wasExecuted = false
    val error: JellyfinResult<String> = JellyfinResult.Error.Generic(
      message = "fail",
      isEphemeral = false,
    )
    error.andThen { wasExecuted = true }
    wasExecuted shouldBe false
  }

  @Test
  fun andThen_catches_block_exceptions() {
    val result = JellyfinResult.Success("hello").andThen {
      error("intentional failure")
    }
    result.isError() shouldBe true
  }

  @Test
  fun doOnSuccess_executes_block_on_success() {
    var captured: String? = null
    JellyfinResult.Success("hello").doOnSuccess { captured = it }
    captured shouldBe "hello"
  }

  @Test
  fun doOnSuccess_does_not_execute_block_on_error() {
    var captured: String? = null
    val error: JellyfinResult<String> = JellyfinResult.Error()
    error.doOnSuccess { captured = it }
    captured.shouldBeNull()
  }

  @Test
  fun doOnError_executes_block_on_error() {
    var captured: JellyfinResult.Error? = null
    val error: JellyfinResult<String> = JellyfinResult.Error.Generic(
      message = "fail",
      isEphemeral = false,
    )
    error.doOnError { captured = it }
    captured.shouldNotBeNull()
    captured.message shouldBe "fail"
  }

  @Test
  fun doOnError_does_not_execute_block_on_success() {
    var captured: JellyfinResult.Error? = null
    JellyfinResult.Success("hello").doOnError { captured = it }
    captured.shouldBeNull()
  }

  @Test
  fun runResult_wraps_success() {
    val result = runResult { "hello" }
    result.shouldBeInstanceOf<JellyfinResult.Success<String>>()
    result.value shouldBe "hello"
  }

  @Test
  fun runResult_wraps_exception_as_error() {
    val result = runResult { error("oops") }
    result.isError() shouldBe true
    result.errorMessageOrNull shouldBe "oops"
    result.throwableOrNull.shouldNotBeNull()
  }

  @Test
  fun unwrap_flattens_nested_success() {
    val nested: JellyfinResult<JellyfinResult<String>> =
      JellyfinResult.Success(JellyfinResult.Success("hello"))
    val result = nested.unwrap()
    result.shouldBeInstanceOf<JellyfinResult.Success<String>>()
    result.value shouldBe "hello"
  }

  @Test
  fun unwrap_flattens_nested_error() {
    val nested: JellyfinResult<JellyfinResult<String>> =
      JellyfinResult.Success(JellyfinResult.Error.Generic(message = "inner fail", isEphemeral = true))
    val result = nested.unwrap()
    result.isError() shouldBe true
    result.errorMessageOrNull shouldBe "inner fail"
  }

  @Test
  fun unwrap_passes_through_outer_error() {
    val nested: JellyfinResult<JellyfinResult<String>> =
      JellyfinResult.Error.Generic(message = "outer fail", isEphemeral = false)
    val result = nested.unwrap()
    result.isError() shouldBe true
    result.errorMessageOrNull shouldBe "outer fail"
  }

  @Test
  fun success_empty_returns_unit() {
    val result = JellyfinResult.Success()
    result.shouldBeInstanceOf<JellyfinResult.Success<Unit>>()
    result.value shouldBe Unit
  }

  @Test
  fun error_empty_has_null_message() {
    val result: JellyfinResult<String> = JellyfinResult.Error()
    result.isError() shouldBe true
    result.errorMessageOrNull.shouldBeNull()
  }
}
