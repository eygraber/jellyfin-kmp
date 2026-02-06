package com.eygraber.jellyfin.services.api.impl

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.java.Java

internal actual fun platformHttpEngineFactory(): HttpClientEngineFactory<*> = Java
