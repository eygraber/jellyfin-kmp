package com.eygraber.jellyfin.services.api.impl

import io.ktor.client.engine.HttpClientEngineFactory

/**
 * Returns the platform-specific HTTP client engine factory.
 *
 * Each platform provides its own engine:
 * - Android: OkHttp
 * - iOS: Darwin
 * - Desktop/JVM: Java
 * - Web (WasmJs): Js
 */
internal expect fun platformHttpEngineFactory(): HttpClientEngineFactory<*>
