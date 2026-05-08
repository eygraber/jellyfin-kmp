package com.eygraber.jellyfin.services.sdk.impl

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import platform.Foundation.NSUserDefaults
import platform.UIKit.UIDevice

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
internal class IosDeviceInfoStore : DeviceInfoStore {
  private val defaults = NSUserDefaults.standardUserDefaults

  override val deviceName: String = "Jellyfin KMP (${UIDevice.currentDevice.name})"

  override fun readDeviceId(): String? = defaults.stringForKey(KEY_DEVICE_ID)

  override fun writeDeviceId(value: String) {
    defaults.setObject(value, KEY_DEVICE_ID)
  }

  private companion object {
    const val KEY_DEVICE_ID = "jellyfin_kmp_device_id"
  }
}
