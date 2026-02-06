package com.eygraber.jellyfin.services.api.impl

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttp

internal actual fun platformHttpEngineFactory(): HttpClientEngineFactory<*> = OkHttp
