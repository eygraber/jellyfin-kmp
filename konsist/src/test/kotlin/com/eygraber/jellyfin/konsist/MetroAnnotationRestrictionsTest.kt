package com.eygraber.jellyfin.konsist

import com.lemonappdev.konsist.api.Konsist
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import java.util.stream.Stream

class MetroAnnotationRestrictionsTest {

  @TestFactory
  fun `ContributesBinding classes should not have Inject annotation`(): Stream<DynamicTest> =
    Konsist
      .scopeFromProject()
      .classes()
      .filter { it.hasAnnotationOf(ContributesBinding::class) }
      .stream()
      .map { clazz ->
        dynamicTest("${clazz.name} should not have @Inject annotation") {
          clazz.hasAnnotationOf(Inject::class) shouldBe false
        }
      }

  @TestFactory
  fun `DependencyGraph interfaces should not have SingleIn annotation`(): Stream<DynamicTest> =
    Konsist
      .scopeFromProject()
      .interfaces()
      .filter { it.hasAnnotationOf(DependencyGraph::class) }
      .stream()
      .map { iface ->
        dynamicTest("${iface.name} should not have @SingleIn annotation") {
          iface.hasAnnotationOf(SingleIn::class) shouldBe false
        }
      }

  @TestFactory
  fun `GraphExtension interfaces should not have SingleIn annotation`(): Stream<DynamicTest> =
    Konsist
      .scopeFromProject()
      .interfaces()
      .filter { it.hasAnnotationOf(GraphExtension::class) }
      .stream()
      .map { iface ->
        dynamicTest("${iface.name} should not have @SingleIn annotation") {
          iface.hasAnnotationOf(SingleIn::class) shouldBe false
        }
      }
}
