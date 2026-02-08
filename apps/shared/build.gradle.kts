import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.BOOLEAN

plugins {
  alias(libs.plugins.buildKonfig)
  alias(libs.plugins.conventionsAndroidKmpLibrary)
  alias(libs.plugins.conventionsComposeMultiplatform)
  alias(libs.plugins.conventionsDetekt)
  alias(libs.plugins.conventionsKotlinMultiplatform)
  alias(libs.plugins.licensee)
  alias(libs.plugins.metro)
}

buildkonfig {
  packageName = "com.eygraber.jellyfin.apps.shared"

  defaultConfigs {
    buildConfigField(BOOLEAN, "isDev", "false")
  }

  defaultConfigs("dev") {
    buildConfigField(BOOLEAN, "isDev", "true")
  }
}

kotlin {
  defaultKmpTargets(
    project = project,
    androidNamespace = "com.eygraber.jellyfin.apps.shared",
  )

  sourceSets {
    commonMain.dependencies {
      api(projects.di)

      api(projects.data.auth.impl)
      api(projects.data.auth.public)
      api(projects.data.items.impl)
      api(projects.data.items.public)
      api(projects.data.playback.impl)
      api(projects.data.playback.public)
      api(projects.data.search.impl)
      api(projects.data.search.public)
      api(projects.data.searchHistory.impl)
      api(projects.data.searchHistory.public)
      api(projects.data.server.impl)
      api(projects.data.server.public)

      api(projects.domain.server.impl)
      api(projects.domain.server.public)
      api(projects.domain.session.impl)
      api(projects.domain.session.public)

      api(projects.nav)

      api(projects.screens.collectionItems)
      api(projects.screens.devSettings)
      api(projects.screens.episodeDetail)
      api(projects.screens.genreItems)
      api(projects.screens.home)
      api(projects.screens.libraryCollections)
      api(projects.screens.libraryGenres)
      api(projects.screens.libraryMovies)
      api(projects.screens.movieDetail)
      api(projects.screens.libraryMusic)
      api(projects.screens.libraryTvshows)
      api(projects.screens.musicAlbumTracks)
      api(projects.screens.musicArtistAlbums)
      api(projects.screens.root)
      api(projects.screens.search)
      api(projects.screens.tvshowDetail)
      api(projects.screens.videoPlayer)
      api(projects.screens.tvshowEpisodes)
      api(projects.screens.tvshowSeasons)
      api(projects.screens.welcome)

      api(projects.services.database.impl)
      api(projects.services.database.public)
      api(projects.services.deviceSensors.impl)
      api(projects.services.deviceSensors.public)
      api(projects.services.logging.impl)
      api(projects.services.logging.public)
      api(projects.services.player.impl)
      api(projects.services.player.public)
      api(projects.services.sdk.impl)
      api(projects.services.sdk.public)
      api(projects.services.splashScreen.impl)
      api(projects.services.splashScreen.public)

      api(projects.ui.material)

      api(libs.compose.foundation)
      api(libs.compose.material3)
      api(libs.compose.runtime)
      api(libs.compose.ui)

      api(libs.khronicle)

      implementation(libs.kotlinx.coroutines.core)

      api(libs.vice.nav3)
    }
  }
}

// whitelist which licenses are able to be used in the app
// new licenses can be added as long as they are permissive
// see https://github.com/cashapp/licensee for configuration options
licensee {
  allow("Apache-2.0")
  allow("MIT")

  // de.drick.compose:edge-to-edge-preview
  allow("Unlicense")

  // slf4j uses a non-standard MIT license URL
  allowUrl("https://opensource.org/license/mit")

  allowDependency(libs.jellyfinLint)
}
