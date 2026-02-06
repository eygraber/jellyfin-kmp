package com.eygraber.jellyfin.sdk.core

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.Test
import kotlin.test.assertFailsWith

class SdkResultTest {
  @Test
  fun success_contains_value() {
    val result = SdkResult.Success("test")
    result.isSuccess.shouldBeTrue()
    result.isFailure.shouldBeFalse()
    result.getOrNull() shouldBe "test"
    result.getOrThrow() shouldBe "test"
    result.errorOrNull().shouldBeNull()
  }

  @Test
  fun failure_contains_error() {
    val error = JellyfinSdkError.Http(statusCode = 404, message = "Not found")
    val result = SdkResult.Failure(error)
    result.isSuccess.shouldBeFalse()
    result.isFailure.shouldBeTrue()
    result.getOrNull().shouldBeNull()
    result.errorOrNull() shouldBe error
  }

  @Test
  fun failure_getOrThrow_throws() {
    val error = JellyfinSdkError.Http(statusCode = 404, message = "Not found")
    val result: SdkResult<String> = SdkResult.Failure(error)
    assertFailsWith<JellyfinSdkError.Http> {
      result.getOrThrow()
    }
  }

  @Test
  fun map_transforms_success() {
    val result = SdkResult.Success(42)
    val mapped = result.map { it.toString() }
    mapped.getOrNull() shouldBe "42"
  }

  @Test
  fun map_passes_through_failure() {
    val error = JellyfinSdkError.Http(statusCode = 500, message = "Server error")
    val result: SdkResult<Int> = SdkResult.Failure(error)
    val mapped = result.map { it.toString() }
    mapped.errorOrNull() shouldBe error
  }

  @Test
  fun flatMap_transforms_success() {
    val result = SdkResult.Success(42)
    val flatMapped = result.flatMap { SdkResult.Success(it.toString()) }
    flatMapped.getOrNull() shouldBe "42"
  }

  @Test
  fun flatMap_can_return_failure() {
    val result = SdkResult.Success(42)
    val error = JellyfinSdkError.Http(statusCode = 400, message = "Bad request")
    val flatMapped = result.flatMap<String> { SdkResult.Failure(error) }
    flatMapped.errorOrNull() shouldBe error
  }

  @Test
  fun onSuccess_called_for_success() {
    var wasCalled = false
    SdkResult.Success("test").onSuccess { wasCalled = true }
    wasCalled.shouldBeTrue()
  }

  @Test
  fun onSuccess_not_called_for_failure() {
    var wasCalled = false
    val result: SdkResult<String> = SdkResult.Failure(
      JellyfinSdkError.Http(statusCode = 404, message = "Not found"),
    )
    result.onSuccess { wasCalled = true }
    wasCalled.shouldBeFalse()
  }

  @Test
  fun onFailure_called_for_failure() {
    var wasCalled = false
    val result: SdkResult<String> = SdkResult.Failure(
      JellyfinSdkError.Http(statusCode = 404, message = "Not found"),
    )
    result.onFailure { wasCalled = true }
    wasCalled.shouldBeTrue()
  }

  @Test
  fun onFailure_not_called_for_success() {
    var wasCalled = false
    SdkResult.Success("test").onFailure { wasCalled = true }
    wasCalled.shouldBeFalse()
  }

  @Test
  fun error_types_are_correct() {
    JellyfinSdkError.Http(statusCode = 404, message = "Not found")
      .shouldBeInstanceOf<JellyfinSdkError.Http>()

    JellyfinSdkError.Network(cause = RuntimeException("timeout"))
      .shouldBeInstanceOf<JellyfinSdkError.Network>()

    JellyfinSdkError.Serialization(cause = RuntimeException("bad json"))
      .shouldBeInstanceOf<JellyfinSdkError.Serialization>()

    JellyfinSdkError.Authentication(message = "expired")
      .shouldBeInstanceOf<JellyfinSdkError.Authentication>()

    JellyfinSdkError.Unknown(cause = RuntimeException("wat"))
      .shouldBeInstanceOf<JellyfinSdkError.Unknown>()
  }
}
