package com.eygraber.jellyfin.data.auth.impl

@OptIn(ExperimentalWasmJsInterop::class)
private fun dateNow(): Double = js("Date.now()")

internal actual fun currentTimeMillis(): Long = dateNow().toLong()
