package com.eygraber.jellyfin.data.search.history.impl

private fun dateNow(): Double = js("Date.now()")

internal actual fun currentTimeMillis(): Long = dateNow().toLong()
