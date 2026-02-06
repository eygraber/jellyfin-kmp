package com.eygraber.jellyfin.sdk.core

import io.ktor.client.engine.HttpClientEngine

expect fun createPlatformHttpEngine(): HttpClientEngine
