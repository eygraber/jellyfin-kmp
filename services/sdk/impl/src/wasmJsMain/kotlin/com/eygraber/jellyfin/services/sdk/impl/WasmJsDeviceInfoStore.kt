package com.eygraber.jellyfin.services.sdk.impl

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.browser.window

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
@Inject
internal class WasmJsDeviceInfoStore : DeviceInfoStore {
  override val deviceName: String = "Jellyfin KMP (Web)"

  override fun readDeviceId(): String? = window.localStorage.getItem(KEY_DEVICE_ID)

  override fun writeDeviceId(value: String) {
    window.localStorage.setItem(KEY_DEVICE_ID, value)
  }

  private companion object {
    const val KEY_DEVICE_ID = "jellyfin_kmp_device_id"
  }
}
