package com.eygraber.jellyfin.data.search.history.impl

/**
 * Returns the current time in milliseconds since the Unix epoch.
 * Platform-specific implementation.
 */
internal expect fun currentTimeMillis(): Long
