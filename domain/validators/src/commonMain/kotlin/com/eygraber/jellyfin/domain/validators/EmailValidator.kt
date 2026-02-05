package com.eygraber.jellyfin.domain.validators

import androidx.compose.runtime.Immutable
import dev.zacsweers.metro.Inject

@Inject
class EmailValidator {
  @Immutable
  enum class Result {
    Valid,
    Invalid,
    Required,
  }

  fun validate(email: CharSequence) =
    when {
      email.isBlank() -> Result.Required
      !email.matches(ValidEmailRegex) -> Result.Invalid
      else -> Result.Valid
    }

  companion object {
    /*
    Copied from android.util.Patterns.EMAIL_ADDRESS
     */
    private val ValidEmailRegex = Regex(
      """[a-zA-Z0-9+._%\-]{1,256}@[a-zA-Z0-9][a-zA-Z0-9\-]{0,64}(\.[a-zA-Z0-9][a-zA-Z0-9\-]{0,25})+""",
    )
  }
}
