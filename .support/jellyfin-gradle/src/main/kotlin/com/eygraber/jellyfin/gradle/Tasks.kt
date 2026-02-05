package com.eygraber.jellyfin.gradle

import com.android.build.api.variant.ApplicationVariant
import com.android.build.api.variant.ApplicationVariantBuilder
import com.android.build.api.variant.LibraryVariant
import com.android.build.api.variant.LibraryVariantBuilder
import com.android.build.api.variant.Variant
import java.util.Locale

val ApplicationVariantBuilder.nameForTasks get() = name.replaceFirstChar { it.uppercase(Locale.ROOT) }
val LibraryVariantBuilder.nameForTasks get() = name.replaceFirstChar { it.uppercase(Locale.ROOT) }

val ApplicationVariant.nameForTasks get() = name.replaceFirstChar { it.uppercase(Locale.ROOT) }
val Variant.nameForTasks get() = name.replaceFirstChar { it.uppercase(Locale.ROOT) }
val LibraryVariant.nameForTasks get() = name.replaceFirstChar { it.uppercase(Locale.ROOT) }
