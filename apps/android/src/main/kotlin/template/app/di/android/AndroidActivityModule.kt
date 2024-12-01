package template.app.di.android

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import me.tatarka.inject.annotations.Provides
import template.di.qualifiers.ActivityContext

interface AndroidActivityModule {
  @Provides @ActivityContext fun provideContext(activity: Activity): Context = activity
  @Provides fun provideAppCompatActivity(activity: Activity): AppCompatActivity = activity as AppCompatActivity
}
