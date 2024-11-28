package template.domain.validators

import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import com.google.testing.junit.testparameterinjector.TestParameterValuesProvider
import io.kotest.matchers.shouldBe
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
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

  @Test
  fun `if an invalid email is validated, then the result is Invalid`(
    @TestParameter(valuesProvider = InvalidEmailParameterProvider::class)
    invalidEmail: String,
  ) {
    val validator = EmailValidator()
    validator.validate(invalidEmail) shouldBe EmailValidator.Result.Invalid
  }

  @Test
  fun `if a valid email is validated, then the result is Valid`() {
    val validator = EmailValidator()
    validator.validate(VALID_EMAIL) shouldBe EmailValidator.Result.Valid
  }
}

private object InvalidEmailParameterProvider : TestParameterValuesProvider() {
  override fun provideValues(context: Context?) = listOf(
    "me",
    "me@",
    "example.com",
    "example.",
    "@example.com",
    "me@example.",
    "me.example.com",
    "me@examplecom",
    "me@.com",
  )
}

private const val VALID_EMAIL = "me@example.com"
