package com.eygraber.jellyfin.domain.server.impl

internal actual fun currentTimeMillis(): Long = js("Date.now()").toLong()
