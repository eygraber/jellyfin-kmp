@file:Suppress("NOTHING_TO_INLINE")

package com.eygraber.jellyfin.android

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast

@ChecksSdkIntAtLeast(parameter = 0)
inline fun androidVersionIsAtLeast(minVersion: Int) = Build.VERSION.SDK_INT >= minVersion

inline fun androidVersionIsLessThan(minVersion: Int) = Build.VERSION.SDK_INT < minVersion
