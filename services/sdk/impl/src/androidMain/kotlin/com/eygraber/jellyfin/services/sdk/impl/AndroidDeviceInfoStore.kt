package com.eygraber.jellyfin.services.sdk.impl

import android.content.Context
import android.os.Build
import com.eygraber.jellyfin.di.qualifiers.AppContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
internal class AndroidDeviceInfoStore(
  @param:AppContext private val context: Context,
) : DeviceInfoStore {
  private val prefs by lazy {
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
  }

  override val deviceName: String = "Jellyfin KMP (${Build.MANUFACTURER} ${Build.MODEL})"

  override fun readDeviceId(): String? = prefs.getString(KEY_DEVICE_ID, null)

  override fun writeDeviceId(value: String) {
    prefs.edit().putString(KEY_DEVICE_ID, value).apply()
  }

  private companion object {
    const val PREFS_NAME = "jellyfin_kmp_device"
    const val KEY_DEVICE_ID = "device_id"
  }
}
