package com.eygraber.jellyfin.sdk.core.api

import com.eygraber.jellyfin.sdk.core.SdkResult
import io.ktor.client.request.parameter
import io.ktor.client.request.setBody
import io.ktor.http.HttpMethod

open class BaseApi(
  protected val apiClient: JellyfinApiClient,
) {
  protected suspend inline fun <reified T> get(
    path: String,
    queryParams: Map<String, Any?> = emptyMap(),
  ): SdkResult<T> = apiClient.request {
    url.pathSegments = path.trimStart('/').split('/')
    queryParams.forEach { (key, value) ->
      if(value != null) {
        parameter(key, value.toString())
      }
    }
    method = HttpMethod.Get
  }

  protected suspend inline fun <reified T, reified B> post(
    path: String,
    body: B? = null,
    queryParams: Map<String, Any?> = emptyMap(),
  ): SdkResult<T> = apiClient.request {
    url.pathSegments = path.trimStart('/').split('/')
    queryParams.forEach { (key, value) ->
      if(value != null) {
        parameter(key, value.toString())
      }
    }
    method = HttpMethod.Post
    if(body != null) {
      setBody(body)
    }
  }

  protected suspend inline fun <reified T> delete(
    path: String,
    queryParams: Map<String, Any?> = emptyMap(),
  ): SdkResult<T> = apiClient.request {
    url.pathSegments = path.trimStart('/').split('/')
    queryParams.forEach { (key, value) ->
      if(value != null) {
        parameter(key, value.toString())
      }
    }
    method = HttpMethod.Delete
  }
}
