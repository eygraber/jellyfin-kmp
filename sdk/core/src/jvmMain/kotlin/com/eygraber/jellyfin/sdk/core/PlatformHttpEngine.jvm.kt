package com.eygraber.jellyfin.sdk.core

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.java.Java

actual fun createPlatformHttpEngine(): HttpClientEngine = Java.create()
