package template.app.di.android

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import template.di.qualifiers.ActivityContext
import template.di.scopes.SessionScope

@ContributesTo(SessionScope::class)
interface AndroidActivityProviders {
  @Provides @ActivityContext fun provideContext(activity: Activity): Context = activity
  @Provides fun provideAppCompatActivity(activity: Activity): AppCompatActivity = activity as AppCompatActivity
}
