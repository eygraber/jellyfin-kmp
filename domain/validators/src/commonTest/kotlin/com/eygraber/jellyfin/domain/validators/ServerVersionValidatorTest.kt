package com.eygraber.jellyfin.domain.validators

import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.Test

class ServerVersionValidatorTest {
  private val validator = ServerVersionValidator()

  @Test
  fun null_version_returns_unknown() {
    validator.validate(null) shouldBe ServerVersionValidator.Result.Unknown
  }

  @Test
  fun empty_version_returns_unknown() {
    validator.validate("") shouldBe ServerVersionValidator.Result.Unknown
  }

  @Test
  fun invalid_version_format_returns_unknown() {
    validator.validate("not-a-version") shouldBe ServerVersionValidator.Result.Unknown
  }

  @Test
  fun version_with_only_major_returns_unknown() {
    validator.validate("10") shouldBe ServerVersionValidator.Result.Unknown
  }

  @Test
  fun version_10_8_0_is_compatible() {
    validator.validate("10.8.0").shouldBeInstanceOf<ServerVersionValidator.Result.Compatible>()
  }

  @Test
  fun version_10_9_0_is_compatible() {
    validator.validate("10.9.0").shouldBeInstanceOf<ServerVersionValidator.Result.Compatible>()
  }

  @Test
  fun version_10_10_0_is_compatible() {
    validator.validate("10.10.0").shouldBeInstanceOf<ServerVersionValidator.Result.Compatible>()
  }

  @Test
  fun version_11_0_0_is_compatible() {
    validator.validate("11.0.0").shouldBeInstanceOf<ServerVersionValidator.Result.Compatible>()
  }

  @Test
  fun version_10_7_9_is_incompatible() {
    validator.validate("10.7.9").shouldBeInstanceOf<ServerVersionValidator.Result.Incompatible>()
  }

  @Test
  fun version_10_0_0_is_incompatible() {
    validator.validate("10.0.0").shouldBeInstanceOf<ServerVersionValidator.Result.Incompatible>()
  }

  @Test
  fun version_9_99_99_is_incompatible() {
    validator.validate("9.99.99").shouldBeInstanceOf<ServerVersionValidator.Result.Incompatible>()
  }

  @Test
  fun version_with_extra_parts_is_compatible() {
    validator.validate("10.9.0.1234").shouldBeInstanceOf<ServerVersionValidator.Result.Compatible>()
  }
}
