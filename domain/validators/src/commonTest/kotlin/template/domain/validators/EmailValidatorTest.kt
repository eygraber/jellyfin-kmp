package template.domain.validators

import io.kotest.matchers.shouldBe
import org.junit.Test
import kotlin.test.Ignore

// TODO: @Burst https://github.com/cashapp/burst/issues/72
class EmailValidatorTest {
  @Test
  fun `if an empty email is validated, then the result is Required`() {
    val validator = EmailValidator()
    validator.validate("") shouldBe EmailValidator.Result.Required
  }

  @Test
  fun `if a blank email is validated, then the result is Required`() {
    val validator = EmailValidator()
    validator.validate(" ") shouldBe EmailValidator.Result.Required
  }

  @Ignore("https://github.com/cashapp/burst/issues/72")
  @Test
  fun `if an invalid email is validated, then the result is Invalid`(
    /*invalidEmail: String = burstValues(
      "me",
      "me@",
      "example.com",
      "example.",
      "@example.com",
      "me@example.",
      "me.example.com",
      "me@examplecom",
      "me@.com",
    )*/
  ) {
    val validator = EmailValidator()
    validator.validate("me") shouldBe EmailValidator.Result.Invalid
  }

  @Test
  fun `if a valid email is validated, then the result is Valid`() {
    val validator = EmailValidator()
    validator.validate(VALID_EMAIL) shouldBe EmailValidator.Result.Valid
  }
}

private const val VALID_EMAIL = "me@example.com"
