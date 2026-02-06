package com.eygraber.jellyfin.domain.server.impl

import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

@Suppress("MagicNumber")
internal actual fun currentTimeMillis(): Long =
  (NSDate().timeIntervalSince1970 * 1000).toLong()
