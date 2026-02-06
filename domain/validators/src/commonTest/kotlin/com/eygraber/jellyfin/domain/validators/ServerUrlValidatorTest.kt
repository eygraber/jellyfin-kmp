package com.eygraber.jellyfin.domain.validators

import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ServerUrlValidatorTest {
  private val validator = ServerUrlValidator()

  @Test
  fun empty_url_returns_empty() {
    validator.validate("") shouldBe ServerUrlValidator.Result.Empty
  }

  @Test
  fun blank_url_returns_empty() {
    validator.validate("   ") shouldBe ServerUrlValidator.Result.Empty
  }

  @Test
  fun valid_https_url_returns_valid() {
    validator.validate("https://jellyfin.example.com") shouldBe ServerUrlValidator.Result.Valid
  }

  @Test
  fun valid_url_with_port_returns_valid() {
    validator.validate("https://jellyfin.example.com:8096") shouldBe ServerUrlValidator.Result.Valid
  }

  @Test
  fun valid_url_with_path_returns_valid() {
    validator.validate("https://jellyfin.example.com/jellyfin") shouldBe ServerUrlValidator.Result.Valid
  }

  @Test
  fun url_without_scheme_returns_valid() {
    validator.validate("jellyfin.example.com") shouldBe ServerUrlValidator.Result.Valid
  }

  @Test
  fun http_url_returns_insecure() {
    validator.validate("http://jellyfin.example.com") shouldBe ServerUrlValidator.Result.InsecureProtocol
  }

  @Test
  fun invalid_url_returns_invalid_format() {
    validator.validate("not a url at all!") shouldBe ServerUrlValidator.Result.InvalidFormat
  }

  @Test
  fun normalize_adds_https_scheme() {
    val result = validator.normalize("jellyfin.example.com")
    result.shouldNotBeNull()
    result shouldBe "https://jellyfin.example.com"
  }

  @Test
  fun normalize_preserves_https_scheme() {
    val result = validator.normalize("https://jellyfin.example.com")
    result.shouldNotBeNull()
    result shouldBe "https://jellyfin.example.com"
  }

  @Test
  fun normalize_trims_trailing_slash() {
    val result = validator.normalize("https://jellyfin.example.com/")
    result.shouldNotBeNull()
    result shouldBe "https://jellyfin.example.com"
  }

  @Test
  fun normalize_preserves_port() {
    val result = validator.normalize("jellyfin.example.com:8096")
    result.shouldNotBeNull()
    result shouldBe "https://jellyfin.example.com:8096"
  }

  @Test
  fun normalize_returns_null_for_blank() {
    validator.normalize("").shouldBeNull()
    validator.normalize("  ").shouldBeNull()
  }

  @Test
  fun normalize_returns_null_for_invalid() {
    validator.normalize("not a url at all!").shouldBeNull()
  }

  @Test
  fun url_with_ip_address_returns_valid() {
    validator.validate("https://192.168.1.100:8096") shouldBe ServerUrlValidator.Result.Valid
  }

  @Test
  fun localhost_url_returns_valid() {
    validator.validate("https://localhost:8096") shouldBe ServerUrlValidator.Result.Valid
  }
}
