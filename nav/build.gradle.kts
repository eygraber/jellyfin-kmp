plugins {
  alias(libs.plugins.conventionsAndroidKmpLibrary)
  alias(libs.plugins.conventionsComposeMultiplatform)
  alias(libs.plugins.conventionsDetekt)
  alias(libs.plugins.conventionsKotlinMultiplatform)
  alias(libs.plugins.conventionsProjectCommon)
  alias(libs.plugins.dependencyAnalysis)
  alias(libs.plugins.kotlinxSerialization)
  alias(libs.plugins.metro)
}

kotlin {
  defaultKmpTargets(
    project = project,
    androidNamespace = "com.eygraber.jellyfin.nav",
  )

  sourceSets {
    commonMain.dependencies {
      api(projects.di)

      implementation(projects.screens.collectionItems)
      implementation(projects.screens.devSettings)
      implementation(projects.screens.episodeDetail)
      implementation(projects.screens.genreItems)
      implementation(projects.screens.home)
      implementation(projects.screens.libraryCollections)
      implementation(projects.screens.libraryGenres)
      implementation(projects.screens.libraryMovies)
      implementation(projects.screens.movieDetail)
      implementation(projects.screens.libraryMusic)
      implementation(projects.screens.libraryTvshows)
      implementation(projects.screens.musicAlbumTracks)
      implementation(projects.screens.musicArtistAlbums)
      implementation(projects.screens.root)
      implementation(projects.screens.search)
      implementation(projects.screens.tvshowDetail)
      implementation(projects.screens.tvshowEpisodes)
      implementation(projects.screens.tvshowSeasons)
      implementation(projects.screens.welcome)

      api(projects.services.deviceSensors.public)

      implementation(libs.compose.nav3.runtime)
      implementation(libs.compose.nav3.ui)

      implementation(libs.compose.animation)
      implementation(libs.compose.material3)
      implementation(libs.compose.runtime)
      implementation(libs.compose.ui)

      implementation(libs.kotlinx.coroutines.core)
      implementation(libs.kotlinx.serialization.core)

      implementation(libs.vice.nav3)
    }

    commonTest.dependencies {
      implementation(libs.test.kotest.assertions.core)
      implementation(kotlin("test"))
    }
  }
}
