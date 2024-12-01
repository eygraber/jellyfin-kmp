package template.services.splash.screen

interface SplashScreenController {
  fun init(isAppRestoring: Boolean)

  suspend fun awaitSystemSplashRemoved()
}
