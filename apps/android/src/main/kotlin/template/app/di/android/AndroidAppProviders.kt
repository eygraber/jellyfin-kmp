package template.app.di.android

import android.accounts.AccountManager
import android.app.ActivityManager
import android.app.AlarmManager
import android.app.AppOpsManager
import android.app.Application
import android.app.DownloadManager
import android.app.KeyguardManager
import android.app.NotificationManager
import android.app.SearchManager
import android.app.UiModeManager
import android.app.WallpaperManager
import android.app.admin.DevicePolicyManager
import android.app.job.JobScheduler
import android.app.usage.NetworkStatsManager
import android.app.usage.UsageStatsManager
import android.appwidget.AppWidgetManager
import android.bluetooth.BluetoothManager
import android.content.ClipboardManager
import android.content.ContentResolver
import android.content.Context
import android.content.RestrictionsManager
import android.content.pm.LauncherApps
import android.content.pm.PackageManager
import android.content.pm.ShortcutManager
import android.content.res.AssetManager
import android.content.res.Resources
import android.hardware.ConsumerIrManager
import android.hardware.SensorManager
import android.hardware.camera2.CameraManager
import android.hardware.display.DisplayManager
import android.hardware.input.InputManager
import android.hardware.usb.UsbManager
import android.location.LocationManager
import android.media.AudioManager
import android.media.MediaRouter
import android.media.midi.MidiManager
import android.media.projection.MediaProjectionManager
import android.media.session.MediaSessionManager
import android.media.tv.TvInputManager
import android.net.ConnectivityManager
import android.net.nsd.NsdManager
import android.net.wifi.WifiManager
import android.net.wifi.p2p.WifiP2pManager
import android.nfc.NfcManager
import android.os.BatteryManager
import android.os.DropBoxManager
import android.os.HardwarePropertiesManager
import android.os.Looper
import android.os.PowerManager
import android.os.UserManager
import android.os.Vibrator
import android.os.health.SystemHealthManager
import android.os.storage.StorageManager
import android.print.PrintManager
import android.telecom.TelecomManager
import android.telephony.CarrierConfigManager
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.accessibility.AccessibilityManager
import android.view.accessibility.CaptioningManager
import android.view.inputmethod.InputMethodManager
import android.view.textservice.TextServicesManager
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import template.di.qualifiers.AppContext

@ContributesTo(AppScope::class)
interface AndroidAppProviders {
  val application: Application

  @Provides @AppContext fun context(application: Application): Context = application.applicationContext

  @Provides fun assetManager(@AppContext context: Context): AssetManager = context.assets
  @Provides fun contentResolver(@AppContext context: Context): ContentResolver = context.contentResolver
  @Provides fun mainLooper(@AppContext context: Context): Looper = context.mainLooper
  @Provides fun packageManager(@AppContext context: Context): PackageManager = context.packageManager
  @Provides fun resources(@AppContext context: Context): Resources = context.resources

  @Provides fun accessibilityManager(@AppContext context: Context): AccessibilityManager =
    context.getSystemService(AccessibilityManager::class.java)

  @Provides fun accountManager(@AppContext context: Context): AccountManager =
    context.getSystemService(AccountManager::class.java)

  @Provides fun activityManager(@AppContext context: Context): ActivityManager =
    context.getSystemService(ActivityManager::class.java)

  @Provides fun alarmManager(@AppContext context: Context): AlarmManager =
    context.getSystemService(AlarmManager::class.java)

  @Provides fun appOpsManager(@AppContext context: Context): AppOpsManager =
    context.getSystemService(AppOpsManager::class.java)

  @Provides fun appWidgetManager(@AppContext context: Context): AppWidgetManager =
    context.getSystemService(AppWidgetManager::class.java)

  @Provides fun audioManager(@AppContext context: Context): AudioManager =
    context.getSystemService(AudioManager::class.java)

  @Provides fun batteryManager(@AppContext context: Context): BatteryManager =
    context.getSystemService(BatteryManager::class.java)

  @Provides fun bluetoothManager(@AppContext context: Context): BluetoothManager =
    context.getSystemService(BluetoothManager::class.java)

  @Provides fun cameraManager(@AppContext context: Context): CameraManager =
    context.getSystemService(CameraManager::class.java)

  @Provides fun captioningManager(@AppContext context: Context): CaptioningManager =
    context.getSystemService(CaptioningManager::class.java)

  @Provides fun clipboardManager(@AppContext context: Context): ClipboardManager =
    context.getSystemService(ClipboardManager::class.java)

  @Provides fun connectivityManager(@AppContext context: Context): ConnectivityManager =
    context.getSystemService(ConnectivityManager::class.java)

  @Provides fun consumerIrManager(@AppContext context: Context): ConsumerIrManager =
    context.getSystemService(ConsumerIrManager::class.java)

  @Provides fun devicePolicyManager(@AppContext context: Context): DevicePolicyManager =
    context.getSystemService(DevicePolicyManager::class.java)

  @Provides fun displayManager(@AppContext context: Context): DisplayManager =
    context.getSystemService(DisplayManager::class.java)

  @Provides fun downloadManager(@AppContext context: Context): DownloadManager =
    context.getSystemService(DownloadManager::class.java)

  @Provides fun dropBoxManager(@AppContext context: Context): DropBoxManager =
    context.getSystemService(DropBoxManager::class.java)

  @Provides fun carrierConfigManager(@AppContext context: Context): CarrierConfigManager =
    context.getSystemService(CarrierConfigManager::class.java)

  @Provides fun hardwarePropertiesManager(@AppContext context: Context): HardwarePropertiesManager =
    context.getSystemService(HardwarePropertiesManager::class.java)

  @Provides fun inputManager(@AppContext context: Context): InputManager =
    context.getSystemService(InputManager::class.java)

  @Provides fun inputMethodManager(@AppContext context: Context): InputMethodManager =
    context.getSystemService(InputMethodManager::class.java)

  @Provides fun jobScheduler(@AppContext context: Context): JobScheduler =
    context.getSystemService(JobScheduler::class.java)

  @Provides fun keyguardManager(@AppContext context: Context): KeyguardManager =
    context.getSystemService(KeyguardManager::class.java)

  @Provides fun launcherApps(@AppContext context: Context): LauncherApps =
    context.getSystemService(LauncherApps::class.java)

  @Provides fun layoutInflater(@AppContext context: Context): LayoutInflater =
    context.getSystemService(LayoutInflater::class.java)

  @Provides fun locationManager(@AppContext context: Context): LocationManager =
    context.getSystemService(LocationManager::class.java)

  @Provides fun midiManager(@AppContext context: Context): MidiManager =
    context.getSystemService(MidiManager::class.java)

  @Provides fun mediaProjectionManager(@AppContext context: Context): MediaProjectionManager =
    context.getSystemService(MediaProjectionManager::class.java)

  @Provides fun mediaRouter(@AppContext context: Context): MediaRouter =
    context.getSystemService(MediaRouter::class.java)

  @Provides fun mediaSessionManager(@AppContext context: Context): MediaSessionManager =
    context.getSystemService(MediaSessionManager::class.java)

  @Provides fun networkStatsManager(@AppContext context: Context): NetworkStatsManager =
    context.getSystemService(NetworkStatsManager::class.java)

  @Provides fun nfcManager(@AppContext context: Context): NfcManager = context.getSystemService(NfcManager::class.java)

  @Provides fun notificationManager(@AppContext context: Context): NotificationManager =
    context.getSystemService(NotificationManager::class.java)

  @Provides fun nsdManager(@AppContext context: Context): NsdManager = context.getSystemService(NsdManager::class.java)
  @Provides fun powerManager(@AppContext context: Context): PowerManager =
    context.getSystemService(PowerManager::class.java)

  @Provides fun printManager(@AppContext context: Context): PrintManager =
    context.getSystemService(PrintManager::class.java)

  @Provides fun restrictionsManager(@AppContext context: Context): RestrictionsManager =
    context.getSystemService(RestrictionsManager::class.java)

  @Provides fun searchManager(@AppContext context: Context): SearchManager =
    context.getSystemService(SearchManager::class.java)

  @Provides fun sensorManager(@AppContext context: Context): SensorManager =
    context.getSystemService(SensorManager::class.java)

  @Provides fun shortcutManager(@AppContext context: Context): ShortcutManager =
    context.getSystemService(ShortcutManager::class.java)

  @Provides fun storageManager(@AppContext context: Context): StorageManager =
    context.getSystemService(StorageManager::class.java)

  @Provides fun subscriptionManager(@AppContext context: Context): SubscriptionManager =
    context.getSystemService(SubscriptionManager::class.java)

  @Provides fun systemHealthManager(@AppContext context: Context): SystemHealthManager =
    context.getSystemService(SystemHealthManager::class.java)

  @Provides fun telecomManager(@AppContext context: Context): TelecomManager =
    context.getSystemService(TelecomManager::class.java)

  @Provides fun telephonyManager(@AppContext context: Context): TelephonyManager =
    context.getSystemService(TelephonyManager::class.java)

  @Provides fun textServicesManager(@AppContext context: Context): TextServicesManager =
    context.getSystemService(TextServicesManager::class.java)

  @Provides fun tvInputManager(@AppContext context: Context): TvInputManager =
    context.getSystemService(TvInputManager::class.java)

  @Provides fun uiModeManager(@AppContext context: Context): UiModeManager =
    context.getSystemService(UiModeManager::class.java)

  @Provides fun usageStatsManager(@AppContext context: Context): UsageStatsManager =
    context.getSystemService(UsageStatsManager::class.java)

  @Provides fun usbManager(@AppContext context: Context): UsbManager =
    context.getSystemService(UsbManager::class.java)

  @Provides fun userManager(@AppContext context: Context): UserManager =
    context.getSystemService(UserManager::class.java)

  @Provides fun vibrator(@AppContext context: Context): Vibrator = context.getSystemService(Vibrator::class.java)

  @Provides fun wallpaperManager(@AppContext context: Context): WallpaperManager =
    context.getSystemService(WallpaperManager::class.java)

  @Provides fun wifiP2pManager(@AppContext context: Context): WifiP2pManager =
    context.getSystemService(WifiP2pManager::class.java)

  @Provides fun wifiManager(@AppContext context: Context): WifiManager =
    context.getSystemService(WifiManager::class.java)

  @Provides fun windowManager(@AppContext context: Context): WindowManager =
    context.getSystemService(WindowManager::class.java)
}
