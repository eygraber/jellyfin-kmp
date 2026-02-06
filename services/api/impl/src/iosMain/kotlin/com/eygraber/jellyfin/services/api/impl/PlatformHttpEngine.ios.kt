package com.eygraber.jellyfin.services.api.impl

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.darwin.Darwin

internal actual fun platformHttpEngineFactory(): HttpClientEngineFactory<*> = Darwin
