package com.eygraber.jellyfin.services.api.impl

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.js.Js

internal actual fun platformHttpEngineFactory(): HttpClientEngineFactory<*> = Js
