package com.eygraber.jellyfin.domain.server.impl

internal actual fun currentTimeMillis() = jsDateNow().toLong()

@OptIn(ExperimentalWasmJsInterop::class)
private fun jsDateNow(): Double = js("Date.now()")
