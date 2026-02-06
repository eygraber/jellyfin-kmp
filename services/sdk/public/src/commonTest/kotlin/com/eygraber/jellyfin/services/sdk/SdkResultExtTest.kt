package com.eygraber.jellyfin.services.sdk

import com.eygraber.jellyfin.common.JellyfinResult
import com.eygraber.jellyfin.common.isError
import com.eygraber.jellyfin.common.isSuccess
import com.eygraber.jellyfin.sdk.core.JellyfinSdkError
import com.eygraber.jellyfin.sdk.core.SdkResult
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.Test

class SdkResultExtTest {
  @Test
  fun success_converts_to_jellyfin_success() {
    val sdkResult: SdkResult<String> = SdkResult.Success("hello")
    val jellyfinResult = sdkResult.toJellyfinResult()

    jellyfinResult.isSuccess() shouldBe true
    (jellyfinResult as JellyfinResult.Success).value shouldBe "hello"
  }

  @Test
  fun failure_network_converts_to_ephemeral_error() {
    val error = JellyfinSdkError.Network(cause = RuntimeException("timeout"))
    val sdkResult: SdkResult<String> = SdkResult.Failure(error)
    val jellyfinResult = sdkResult.toJellyfinResult()

    jellyfinResult.isError() shouldBe true
    val jellyfinError = jellyfinResult.shouldBeInstanceOf<JellyfinResult.Error.Detailed<*>>()
    jellyfinError.isEphemeral shouldBe true
    jellyfinError.details shouldBe error
  }

  @Test
  fun failure_auth_converts_to_non_ephemeral_error() {
    val error = JellyfinSdkError.Authentication(message = "Login required")
    val sdkResult: SdkResult<String> = SdkResult.Failure(error)
    val jellyfinResult = sdkResult.toJellyfinResult()

    jellyfinResult.isError() shouldBe true
    val jellyfinError = jellyfinResult.shouldBeInstanceOf<JellyfinResult.Error.Detailed<*>>()
    jellyfinError.isEphemeral shouldBe false
  }

  @Test
  fun failure_http_5xx_is_ephemeral() {
    val error = JellyfinSdkError.Http(statusCode = 500, message = "Internal Server Error")
    val sdkResult: SdkResult<String> = SdkResult.Failure(error)
    val jellyfinResult = sdkResult.toJellyfinResult()

    val jellyfinError = jellyfinResult.shouldBeInstanceOf<JellyfinResult.Error.Detailed<*>>()
    jellyfinError.isEphemeral shouldBe true
  }

  @Test
  fun failure_http_4xx_is_not_ephemeral() {
    val error = JellyfinSdkError.Http(statusCode = 404, message = "Not Found")
    val sdkResult: SdkResult<String> = SdkResult.Failure(error)
    val jellyfinResult = sdkResult.toJellyfinResult()

    val jellyfinError = jellyfinResult.shouldBeInstanceOf<JellyfinResult.Error.Detailed<*>>()
    jellyfinError.isEphemeral shouldBe false
  }

  @Test
  fun failure_serialization_is_ephemeral() {
    val error = JellyfinSdkError.Serialization(cause = RuntimeException("bad json"))
    val sdkResult: SdkResult<String> = SdkResult.Failure(error)
    val jellyfinResult = sdkResult.toJellyfinResult()

    val jellyfinError = jellyfinResult.shouldBeInstanceOf<JellyfinResult.Error.Detailed<*>>()
    jellyfinError.isEphemeral shouldBe true
  }

  @Test
  fun success_with_mapper_transforms_value() {
    val sdkResult: SdkResult<Int> = SdkResult.Success(42)
    val jellyfinResult = sdkResult.toJellyfinResult { it.toString() }

    jellyfinResult.isSuccess() shouldBe true
    (jellyfinResult as JellyfinResult.Success).value shouldBe "42"
  }

  @Test
  fun failure_with_mapper_preserves_error() {
    val error = JellyfinSdkError.Network(cause = RuntimeException("timeout"))
    val sdkResult: SdkResult<Int> = SdkResult.Failure(error)
    val jellyfinResult = sdkResult.toJellyfinResult { it.toString() }

    jellyfinResult.isError() shouldBe true
    val jellyfinError = jellyfinResult.shouldBeInstanceOf<JellyfinResult.Error.Detailed<*>>()
    jellyfinError.details shouldBe error
  }

  @Test
  fun error_message_is_preserved() {
    val error = JellyfinSdkError.Http(statusCode = 403, message = "Forbidden")
    val sdkResult: SdkResult<String> = SdkResult.Failure(error)
    val jellyfinResult = sdkResult.toJellyfinResult()

    val jellyfinError = jellyfinResult.shouldBeInstanceOf<JellyfinResult.Error.Detailed<*>>()
    jellyfinError.message shouldBe "Forbidden"
  }
}
