package com.eygraber.jellyfin.services.sdk.impl

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import java.io.File

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
internal class JvmDeviceInfoStore : DeviceInfoStore {
  private val deviceIdFile: File by lazy {
    val dir = File(System.getProperty("user.home"), APP_DIR).apply { mkdirs() }
    File(dir, FILE_NAME)
  }

  override val deviceName: String =
    "Jellyfin KMP (${System.getProperty("os.name").orEmpty().ifEmpty { "Desktop" }})"

  override fun readDeviceId(): String? =
    if(deviceIdFile.exists()) deviceIdFile.readText().trim().ifEmpty { null } else null

  override fun writeDeviceId(value: String) {
    deviceIdFile.writeText(value)
  }

  private companion object {
    const val APP_DIR = ".jellyfin-kmp"
    const val FILE_NAME = "device.id"
  }
}
