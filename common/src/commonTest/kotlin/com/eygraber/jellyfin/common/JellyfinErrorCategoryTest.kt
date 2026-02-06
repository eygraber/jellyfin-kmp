package com.eygraber.jellyfin.common

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class JellyfinErrorCategoryTest {
  @Test
  fun status_401_maps_to_auth() {
    JellyfinErrorCategory.fromStatusCode(statusCode = 401) shouldBe JellyfinErrorCategory.Auth
  }

  @Test
  fun status_403_maps_to_auth() {
    JellyfinErrorCategory.fromStatusCode(statusCode = 403) shouldBe JellyfinErrorCategory.Auth
  }

  @Test
  fun status_400_maps_to_client() {
    JellyfinErrorCategory.fromStatusCode(statusCode = 400) shouldBe JellyfinErrorCategory.Client
  }

  @Test
  fun status_404_maps_to_client() {
    JellyfinErrorCategory.fromStatusCode(statusCode = 404) shouldBe JellyfinErrorCategory.Client
  }

  @Test
  fun status_500_maps_to_server() {
    JellyfinErrorCategory.fromStatusCode(statusCode = 500) shouldBe JellyfinErrorCategory.Server
  }

  @Test
  fun status_503_maps_to_server() {
    JellyfinErrorCategory.fromStatusCode(statusCode = 503) shouldBe JellyfinErrorCategory.Server
  }

  @Test
  fun null_status_maps_to_unknown() {
    JellyfinErrorCategory.fromStatusCode(statusCode = null) shouldBe JellyfinErrorCategory.Unknown
  }

  @Test
  fun unexpected_status_maps_to_unknown() {
    JellyfinErrorCategory.fromStatusCode(statusCode = 600) shouldBe JellyfinErrorCategory.Unknown
  }

  @Test
  fun timeout_exception_maps_to_network() {
    class TimeoutException : Exception("Request timed out")
    JellyfinErrorCategory.fromThrowable(TimeoutException()) shouldBe JellyfinErrorCategory.Network
  }

  @Test
  fun connect_exception_maps_to_network() {
    class ConnectException : Exception("Connection refused")
    JellyfinErrorCategory.fromThrowable(ConnectException()) shouldBe JellyfinErrorCategory.Network
  }

  @Test
  fun socket_exception_maps_to_network() {
    class SocketTimeoutException : Exception("Socket timeout")
    JellyfinErrorCategory.fromThrowable(SocketTimeoutException()) shouldBe JellyfinErrorCategory.Network
  }

  @Test
  fun unknown_host_exception_maps_to_network() {
    class UnknownHostException : Exception("Unknown host")
    JellyfinErrorCategory.fromThrowable(UnknownHostException()) shouldBe JellyfinErrorCategory.Network
  }

  @Test
  fun generic_exception_maps_to_unknown() {
    JellyfinErrorCategory.fromThrowable(RuntimeException("oops")) shouldBe JellyfinErrorCategory.Unknown
  }

  @Test
  fun network_category_is_retryable() {
    JellyfinErrorCategory.Network.isRetryable shouldBe true
  }

  @Test
  fun server_category_is_retryable() {
    JellyfinErrorCategory.Server.isRetryable shouldBe true
  }

  @Test
  fun auth_category_is_not_retryable() {
    JellyfinErrorCategory.Auth.isRetryable shouldBe false
  }

  @Test
  fun client_category_is_not_retryable() {
    JellyfinErrorCategory.Client.isRetryable shouldBe false
  }

  @Test
  fun unknown_category_is_not_retryable() {
    JellyfinErrorCategory.Unknown.isRetryable shouldBe false
  }

  @Test
  fun nested_network_exception_detected_via_cause() {
    class ConnectException : Exception("Connection refused")
    val wrapped = RuntimeException("Wrapper", ConnectException())
    JellyfinErrorCategory.fromThrowable(wrapped) shouldBe JellyfinErrorCategory.Network
  }
}
