package com.eygraber.jellyfin.services.logging.impl

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotContain
import kotlin.test.Test

class DefaultLogSanitizerTest {
  private val sanitizer = DefaultLogSanitizer()

  @Test
  fun sanitizes_access_token_with_equals() {
    val input = "Request with access_token=abc123def456"
    val result = sanitizer.sanitize(input)
    result shouldNotContain "abc123def456"
    result shouldBe "Request with [REDACTED_TOKEN]"
  }

  @Test
  fun sanitizes_token_with_colon() {
    val input = "Header token: mySecretToken123"
    val result = sanitizer.sanitize(input)
    result shouldNotContain "mySecretToken123"
  }

  @Test
  fun sanitizes_authorization_header() {
    val input = "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.abc.xyz"
    val result = sanitizer.sanitize(input)
    result shouldNotContain "eyJhbGciOiJIUzI1NiJ9"
  }

  @Test
  fun sanitizes_emby_authorization() {
    val input = "X-Emby-Authorization: MediaBrowser Token=secret123"
    val result = sanitizer.sanitize(input)
    result shouldNotContain "secret123"
  }

  @Test
  fun sanitizes_password_with_equals() {
    val input = "Login with password=superSecret123"
    val result = sanitizer.sanitize(input)
    result shouldNotContain "superSecret123"
  }

  @Test
  fun sanitizes_api_key() {
    val input = "Using api_key=ak_12345abcde"
    val result = sanitizer.sanitize(input)
    result shouldNotContain "ak_12345abcde"
  }

  @Test
  fun sanitizes_email_addresses() {
    val input = "User logged in: user@example.com"
    val result = sanitizer.sanitize(input)
    result shouldNotContain "user@example.com"
    result shouldBe "User logged in: [REDACTED_EMAIL]"
  }

  @Test
  fun preserves_non_sensitive_messages() {
    val input = "Fetching items from library with limit=50"
    val result = sanitizer.sanitize(input)
    result shouldBe input
  }

  @Test
  fun sanitizes_multiple_patterns_in_same_message() {
    val input = "Request token=abc123 for user@example.com with api_key=key456"
    val result = sanitizer.sanitize(input)
    result shouldNotContain "abc123"
    result shouldNotContain "user@example.com"
    result shouldNotContain "key456"
  }

  @Test
  fun case_insensitive_token_matching() {
    val input = "TOKEN=mySecret ACCESS_TOKEN=otherSecret"
    val result = sanitizer.sanitize(input)
    result shouldNotContain "mySecret"
    result shouldNotContain "otherSecret"
  }
}
