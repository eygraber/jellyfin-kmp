package com.eygraber.jellyfin.sdk.core

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin

actual fun createPlatformHttpEngine(): HttpClientEngine = Darwin.create()
