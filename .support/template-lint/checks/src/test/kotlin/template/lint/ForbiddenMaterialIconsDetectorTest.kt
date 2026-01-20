package template.lint

import com.android.tools.lint.checks.infrastructure.TestFiles.kotlin
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import org.intellij.lang.annotations.Language
import org.junit.Test

@Suppress("TrimMultilineRawString")
class ForbiddenMaterialIconsDetectorTest {
  private val iconsFile = kotlin(
    """
      package androidx.compose.material.icons

      @Suppress("ForbiddenMaterialIcons")
      object Icons {
        @Suppress("ForbiddenMaterialIcons")
        object Defaults
      }
    """,
  ).indented()

  @Test
  fun singleForbiddenMaterialIcons() {
    runTest(
      code = """
          import androidx.compose.material.icons.Icons
          import androidx.compose.material.icons.Icons.Defaults

          fun test() {
            Icons
            Icons.Defaults
            Defaults
          }
          """,
      expectedOutput = """
        src/test.kt:5: Error: Usage of Icons from material-icons-core is not supported in Template. Use TemplateIcons instead. [ForbiddenMaterialIcons]
          Icons
          ~~~~~
        src/test.kt:6: Error: Usage of Icons from material-icons-core is not supported in Template. Use TemplateIcons instead. [ForbiddenMaterialIcons]
          Icons.Defaults
                ~~~~~~~~
        src/test.kt:7: Error: Usage of Icons from material-icons-core is not supported in Template. Use TemplateIcons instead. [ForbiddenMaterialIcons]
          Defaults
          ~~~~~~~~
        3 errors, 0 warnings
        """,
    )
  }

  @Test
  fun singleForbiddenMaterialIconsLocalVariable() {
    runTest(
      code = """
          import androidx.compose.material.icons.Icons
          import androidx.compose.material.icons.Icons.Defaults

          fun test() {
            val icons: Icons
            val iconsDefaults: Icons.Defaults
            val defaults: Defaults
          }
          """,
      expectedOutput = """
        src/test.kt:5: Error: Usage of Icons from material-icons-core is not supported in Template. Use TemplateIcons instead. [ForbiddenMaterialIcons]
          val icons: Icons
          ~~~~~~~~~~~~~~~~
        src/test.kt:6: Error: Usage of Icons from material-icons-core is not supported in Template. Use TemplateIcons instead. [ForbiddenMaterialIcons]
          val iconsDefaults: Icons.Defaults
          ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        src/test.kt:7: Error: Usage of Icons from material-icons-core is not supported in Template. Use TemplateIcons instead. [ForbiddenMaterialIcons]
          val defaults: Defaults
          ~~~~~~~~~~~~~~~~~~~~~~
        3 errors, 0 warnings
        """,
    )
  }

  @Test
  fun singleForbiddenMaterialIconsCtorProperties() {
    runTest(
      code = """
          import androidx.compose.material.icons.Icons
          import androidx.compose.material.icons.Icons.Defaults

          class Test(
            val icons: Icons,
            val iconsDefaults: Icons.Defaults,
            val defaults: Defaults
          )
          """,
      expectedOutput = """
        src/Test.kt:5: Error: Usage of Icons from material-icons-core is not supported in Template. Use TemplateIcons instead. [ForbiddenMaterialIcons]
          val icons: Icons,
          ~~~~~~~~~~~~~~~~
        src/Test.kt:6: Error: Usage of Icons from material-icons-core is not supported in Template. Use TemplateIcons instead. [ForbiddenMaterialIcons]
          val iconsDefaults: Icons.Defaults,
          ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        src/Test.kt:7: Error: Usage of Icons from material-icons-core is not supported in Template. Use TemplateIcons instead. [ForbiddenMaterialIcons]
          val defaults: Defaults
          ~~~~~~~~~~~~~~~~~~~~~~
        3 errors, 0 warnings
        """,
    )
  }

  @Test
  fun singleForbiddenMaterialIconsProperties() {
    runTest(
      code = """
          import androidx.compose.material.icons.Icons
          import androidx.compose.material.icons.Icons.Defaults

          class Test {
            val icons: Icons
            val iconsDefaults: Icons.Defaults
            val defaults: Defaults
          }
          """,
      expectedOutput = """
        src/Test.kt:5: Error: Usage of Icons from material-icons-core is not supported in Template. Use TemplateIcons instead. [ForbiddenMaterialIcons]
          val icons: Icons
          ~~~~~~~~~~~~~~~~
        src/Test.kt:6: Error: Usage of Icons from material-icons-core is not supported in Template. Use TemplateIcons instead. [ForbiddenMaterialIcons]
          val iconsDefaults: Icons.Defaults
          ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        src/Test.kt:7: Error: Usage of Icons from material-icons-core is not supported in Template. Use TemplateIcons instead. [ForbiddenMaterialIcons]
          val defaults: Defaults
          ~~~~~~~~~~~~~~~~~~~~~~
        3 errors, 0 warnings
        """,
    )
  }

  @Test
  fun singleForbiddenMaterialIconsParameter() {
    runTest(
      code = """
          import androidx.compose.material.icons.Icons
          import androidx.compose.material.icons.Icons.Defaults

          fun test(icons: Icons) {}
          fun test(icons: Icons.Defaults) {}
          fun test(defaults: Defaults) {}
          """,
      expectedOutput = """
        src/test.kt:4: Error: Usage of Icons from material-icons-core is not supported in Template. Use TemplateIcons instead. [ForbiddenMaterialIcons]
        fun test(icons: Icons) {}
                 ~~~~~~~~~~~~
        src/test.kt:5: Error: Usage of Icons from material-icons-core is not supported in Template. Use TemplateIcons instead. [ForbiddenMaterialIcons]
        fun test(icons: Icons.Defaults) {}
                 ~~~~~~~~~~~~~~~~~~~~~
        src/test.kt:6: Error: Usage of Icons from material-icons-core is not supported in Template. Use TemplateIcons instead. [ForbiddenMaterialIcons]
        fun test(defaults: Defaults) {}
                 ~~~~~~~~~~~~~~~~~~
        3 errors, 0 warnings
        """,
    )
  }

  private fun runTest(
    @Language("kotlin") code: String,
    expectedOutput: String,
  ) {
    lint()
      .files(
        iconsFile,
        kotlin(code).indented(),
      )
      .issues(ForbiddenMaterialIconsDetector.ISSUE)
      .allowMissingSdk()
      .run()
      .expect(expectedOutput)
  }
}
