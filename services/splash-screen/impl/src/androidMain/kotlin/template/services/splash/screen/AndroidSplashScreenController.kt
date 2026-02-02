package template.services.splash.screen

import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.window.SplashScreenView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider
import androidx.core.view.children
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import template.android.androidVersionIsAtLeast
import template.di.scopes.SessionScope
import java.lang.ref.WeakReference
import kotlin.time.Duration.Companion.seconds

@SingleIn(SessionScope::class)
@ContributesBinding(SessionScope::class)
class AndroidSplashScreenController(
  private val activity: AppCompatActivity,
) : SplashScreenController {
  private var splashScreenViewRef: WeakReference<SplashScreenViewProvider>? = null
  private val systemDeferred = CompletableDeferred<Unit>()

  private var shouldKeepSplashOnScreen = true

  override fun init(isAppRestoring: Boolean) {
    if(androidVersionIsAtLeast(31)) {
      val splashScreen = activity.installSplashScreen()

      if(isAppRestoring) {
        systemDeferred.complete(Unit)
      }
      else {
        splashScreen.setKeepOnScreenCondition { shouldKeepSplashOnScreen }

        splashScreen.setOnExitAnimationListener { systemSplashView ->
          // if we don't keep some type of reference to the
          // SplashScreenViewProvider, Android acts weird and never removes the splash icon
          splashScreenViewRef = WeakReference(systemSplashView)
          systemDeferred.complete(Unit)
        }
      }
    }
    else {
      systemDeferred.complete(Unit)
    }
  }

  override suspend fun awaitSystemSplashRemoved() {
    if(androidVersionIsAtLeast(31)) {
      shouldKeepSplashOnScreen = false

      // https://issuetracker.google.com/issues/216136037
      // On Android 12+ in certain scenarios the SplashScreenView won't get shown
      // which means that the OnExitAnimationListener will never fire and we'll hang indefinitely
      // Because of that we look for the SplashScreenView after waiting some time and if it still isn't there
      // we assume that OnExitAnimationListener will never get called and so we just complete and move forward
      withTimeoutOrNull(1.seconds) {
        systemDeferred.await()
      }

      if(!systemDeferred.isCompleted) {
        if(!findSplashScreenView()) {
          systemDeferred.complete(Unit)
        }
      }

      // This is just a fail-safe for any unknown issues with OnExitAnimationListener
      // because otherwise the user would just get stuck at the splash screen forever
      withTimeoutOrNull(3.seconds) {
        systemDeferred.await()
      }

      withContext(Dispatchers.Main) {
        splashScreenViewRef = splashScreenViewRef?.let { ref ->
          ref.get()?.remove()
          ref.clear()
          null
        }
      }
    }
  }

  private fun findSplashScreenView() =
    if(androidVersionIsAtLeast(31)) {
      activity
        .findViewById<ViewGroup>(android.R.id.content)
        .rootView
        .findSplashScreenView()
    }
    else {
      true
    }

  @RequiresApi(Build.VERSION_CODES.S)
  private fun View.findSplashScreenView(): Boolean {
    if(this is ViewGroup) {
      for(child in children) {
        if(child is SplashScreenView) return true
        child.findSplashScreenView()
      }
    }

    return false
  }
}
