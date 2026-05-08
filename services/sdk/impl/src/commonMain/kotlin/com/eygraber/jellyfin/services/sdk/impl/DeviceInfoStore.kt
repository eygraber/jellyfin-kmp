package com.eygraber.jellyfin.services.sdk.impl

interface DeviceInfoStore {
  val deviceName: String
  fun readDeviceId(): String?
  fun writeDeviceId(value: String)
}
