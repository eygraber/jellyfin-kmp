package com.eygraber.jellyfin.sdk.core

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.js.Js

actual fun createPlatformHttpEngine(): HttpClientEngine = Js.create()
