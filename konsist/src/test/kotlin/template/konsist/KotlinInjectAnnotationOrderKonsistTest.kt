package template.konsist

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.declaration.KoAnnotationDeclaration
import com.lemonappdev.konsist.api.declaration.KoClassDeclaration
import com.lemonappdev.konsist.api.declaration.KoInterfaceDeclaration
import com.lemonappdev.konsist.api.provider.KoAnnotationProvider
import com.lemonappdev.konsist.api.provider.KoNameProvider
import io.kotest.matchers.collections.shouldContainExactly
import me.tatarka.inject.annotations.Inject
import me.tatarka.inject.annotations.IntoMap
import me.tatarka.inject.annotations.IntoSet
import me.tatarka.inject.annotations.Provides
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.ContributesSubcomponent
import software.amazon.lastmile.kotlin.inject.anvil.MergeComponent
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import java.util.stream.Stream
import kotlin.reflect.KClass

class KotlinInjectAnnotationOrderKonsistTest {
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
  fun `The MergeComponent`(): Stream<DynamicTest> =
    Konsist
      .scopeFromProject()
      .classesAndInterfaces()
      .filter { it is KoInterfaceDeclaration || it is KoClassDeclaration && it.hasAbstractModifier }
      .filter { it.hasAnnotationOf(MergeComponent::class) }
      .assertRelativeOrder(
        desiredOrder = listOf(MergeComponent::class, SingleIn::class),
      )

  @TestFactory
  fun `The ContributesSubcomponent`(): Stream<DynamicTest> =
    Konsist
      .scopeFromProject()
      .interfaces()
      .filter { it.hasAnnotationOf(ContributesSubcomponent::class) }
      .assertRelativeOrder(
        desiredOrder = listOf(ContributesSubcomponent::class, SingleIn::class),
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
