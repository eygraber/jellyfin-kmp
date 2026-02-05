package com.eygraber.jellyfin.app.di.android

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.eygraber.jellyfin.di.qualifiers.ActivityContext
import com.eygraber.jellyfin.di.scopes.SessionScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@ContributesTo(SessionScope::class)
interface AndroidActivityProviders {
  @Provides @ActivityContext fun provideContext(activity: Activity): Context = activity
  @Provides fun provideAppCompatActivity(activity: Activity): AppCompatActivity = activity as AppCompatActivity
}
