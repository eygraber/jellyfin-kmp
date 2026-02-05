package com.eygraber.jellyfin.konsist

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.declaration.KoAnnotationDeclaration
import com.lemonappdev.konsist.api.declaration.KoClassDeclaration
import com.lemonappdev.konsist.api.declaration.KoInterfaceDeclaration
import com.lemonappdev.konsist.api.provider.KoAnnotationProvider
import com.lemonappdev.konsist.api.provider.KoNameProvider
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.IntoMap
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import io.kotest.matchers.collections.shouldContainExactly
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import java.util.stream.Stream
import kotlin.reflect.KClass

class MetroAnnotationOrderKonsistTest {
  @TestFactory
  fun `The Inject`(): Stream<DynamicTest> =
    Konsist
      .scopeFromProject()
      .run {
        classesAndInterfacesAndObjects() + functions()
      }
      .filter { it.hasAnnotationOf(Inject::class) }
      .assertRelativeOrder(
        desiredOrder = listOf(Inject::class, SingleIn::class, ContributesBinding::class),
      )

  @TestFactory
  fun `The Provides`(): Stream<DynamicTest> =
    Konsist
      .scopeFromProject()
      .run {
        functions() + properties()
      }
      .filter { it.hasAnnotationOf(Provides::class) }
      .assertRelativeOrder(
        desiredOrder = listOf(Provides::class, SingleIn::class, IntoMap::class, IntoSet::class),
      )

  @TestFactory
  fun `The DependencyGraph`(): Stream<DynamicTest> =
    Konsist
      .scopeFromProject()
      .classesAndInterfaces()
      .filter { it is KoInterfaceDeclaration || it is KoClassDeclaration && it.hasAbstractModifier }
      .filter { it.hasAnnotationOf(DependencyGraph::class) }
      .assertRelativeOrder(
        desiredOrder = listOf(DependencyGraph::class),
      )

  @TestFactory
  fun `The GraphExtension`(): Stream<DynamicTest> =
    Konsist
      .scopeFromProject()
      .interfaces()
      .filter { it.hasAnnotationOf(GraphExtension::class) }
      .assertRelativeOrder(
        desiredOrder = listOf(GraphExtension::class),
      )

  private fun List<KoAnnotationProvider>.assertRelativeOrder(
    desiredOrder: List<KClass<*>>,
  ) =
    stream()
      .flatMap { provider ->
        val name = (provider as KoNameProvider).name
        Stream.of(
          dynamicTest(
            "annotation for $name follows the correct order",
          ) {
            (provider as KoAnnotationProvider).annotations.assertRelativeOrder(desiredOrder)
          },
        )
      }
}

private fun List<KoAnnotationDeclaration>.assertRelativeOrder(desiredOrder: List<KClass<*>>) {
  val indices = desiredOrder.associateWith { kClass -> indexOf(kClass) }
  val present = indices.filterValues { it != -1 }
  val presentDesired = desiredOrder.filter { it in present }
  val actual = present.entries.sortedBy { it.value }.map { it.key }
  actual shouldContainExactly presentDesired
}

private fun List<KoAnnotationDeclaration>.indexOf(kClass: KClass<*>): Int =
  indexOfFirst { it.fullyQualifiedName == kClass.qualifiedName }
