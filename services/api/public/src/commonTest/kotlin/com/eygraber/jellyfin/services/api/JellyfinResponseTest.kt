package com.eygraber.jellyfin.services.api

import com.eygraber.jellyfin.common.isError
import com.eygraber.jellyfin.common.isSuccess
import com.eygraber.jellyfin.common.successOrNull
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.Test

class JellyfinResponseTest {
  @Test
  fun success_toResult_returns_success() {
    val response: JellyfinResponse<String> = JellyfinResponse.Success(
      body = "hello",
      statusCode = 200,
    )

    val result = response.toResult()
    result.isSuccess().shouldBeTrue()
    result.successOrNull shouldBe "hello"
  }

  @Test
  fun success_toResult_with_mapper_transforms_body() {
    val response: JellyfinResponse<String> = JellyfinResponse.Success(
      body = "42",
      statusCode = 200,
    )

    val result = response.toResult { it.toInt() }
    result.isSuccess().shouldBeTrue()
    result.successOrNull shouldBe 42
  }

  @Test
  fun error_toResult_returns_error() {
    val response: JellyfinResponse<String> = JellyfinResponse.Error(
      statusCode = 404,
      message = "Not Found",
    )

    val result = response.toResult()
    result.isError().shouldBeTrue()
  }

  @Test
  fun error_toResult_with_cause_preserves_exception() {
    val cause = RuntimeException("connection failed")
    val response: JellyfinResponse<String> = JellyfinResponse.Error(
      message = "connection failed",
      cause = cause,
    )

    val result = response.toResult()
    result.isError().shouldBeTrue()
    result.shouldBeInstanceOf<com.eygraber.jellyfin.common.JellyfinResult.Error.Detailed<*>>()
    result.details shouldBe cause
  }

  @Test
  fun error_toResult_without_cause_creates_apiException() {
    val response: JellyfinResponse<String> = JellyfinResponse.Error(
      statusCode = 500,
      message = "Internal Server Error",
    )

    val result = response.toResult()
    result.isError().shouldBeTrue()
    result.shouldBeInstanceOf<com.eygraber.jellyfin.common.JellyfinResult.Error.Detailed<*>>()
    result.details.shouldBeInstanceOf<ApiException>()
  }
}
