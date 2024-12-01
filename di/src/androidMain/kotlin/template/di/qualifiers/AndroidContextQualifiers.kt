package template.di.qualifiers

import me.tatarka.inject.annotations.Qualifier

@Qualifier
@Target(
  AnnotationTarget.PROPERTY,
  AnnotationTarget.PROPERTY_GETTER,
  AnnotationTarget.FUNCTION,
  AnnotationTarget.VALUE_PARAMETER,
  AnnotationTarget.TYPE,
)
annotation class ActivityContext

@Qualifier
@Target(
  AnnotationTarget.PROPERTY,
  AnnotationTarget.PROPERTY_GETTER,
  AnnotationTarget.FUNCTION,
  AnnotationTarget.VALUE_PARAMETER,
  AnnotationTarget.TYPE,
)
annotation class AppContext
