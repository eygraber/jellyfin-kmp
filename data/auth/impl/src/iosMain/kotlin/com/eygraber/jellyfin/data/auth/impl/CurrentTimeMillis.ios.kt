package com.eygraber.jellyfin.data.auth.impl

import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

internal actual fun currentTimeMillis(): Long =
  (NSDate().timeIntervalSince1970 * MILLIS_PER_SECOND).toLong()

private const val MILLIS_PER_SECOND = 1_000
