package template.lint

import com.android.tools.lint.client.api.JavaEvaluator
import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.SourceCodeScanner
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiElement
import org.jetbrains.uast.UElement
import org.jetbrains.uast.USimpleNameReferenceExpression
import org.jetbrains.uast.UVariable
import org.jetbrains.uast.kotlin.parentAs

class ForbiddenMaterialIconsDetector : Detector(), SourceCodeScanner {
  override fun createUastHandler(context: JavaContext) =
    object : UElementHandler() {
      override fun visitSimpleNameReferenceExpression(node: USimpleNameReferenceExpression) {
        val resolvedNode by lazy {
          node.resolve() as? PsiClass
        }

        val nodeExpressionType = when(val expressionType = node.getExpressionType()) {
          null -> when(resolvedNode) {
            null -> null
            else -> context.evaluator.getClassType(resolvedNode)
          }

          else -> expressionType as? PsiClassType
        }

        val referenced = resolvedNode
        if(nodeExpressionType != null && referenced != null) {
          visitReference(context, node, referenced, nodeExpressionType.name)
        }
      }

      private val variableCache = HashSet<PsiElement?>()

      override fun visitVariable(node: UVariable) {
        if(!variableCache.add(node.sourcePsi)) return

        (node.type as? PsiClassType)?.let { type ->
          context.evaluator.getTypeClass(type)?.let { referenced ->
            visitReference(context, node, referenced, type.name)
          }
        }
      }
    }

  override fun getApplicableUastTypes() = listOf(
    USimpleNameReferenceExpression::class.java,
    UVariable::class.java,
  )

  private fun PsiClass.findIconParent(evaluator: JavaEvaluator): Boolean {
    var parent = parentAs<PsiClass>()
    while(parent != null) {
      if(evaluator.getClassType(parent)?.name == "Icons") return true
      parent = parent.parentAs<PsiClass>()
    }

    return false
  }

  private fun visitReference(
    context: JavaContext,
    reference: UElement,
    referenced: PsiClass,
    typeName: String,
  ) {
    val pkg = context.evaluator.getPackage(referenced)
    if(pkg?.qualifiedName == "androidx.compose.material.icons") {
      if(typeName == "Icons" || referenced.findIconParent(context.evaluator)) {
        context.report(
          issue = ISSUE,
          scope = reference,
          location = context.getLocation(reference),
          message = errorMessage,
        )
      }
    }
  }

  private val errorMessage =
    "Usage of Icons from material-icons-core is not supported in Template. Use TemplateIcons instead."

  companion object {
    @JvmField
    val ISSUE: Issue =
      Issue.create(
        id = "ForbiddenMaterialIcons",
        briefDescription = "Usage of forbidden material Icons",
        explanation = "Flags usage of material Icons.",
        category = Category.CUSTOM_LINT_CHECKS,
        priority = 7,
        severity = Severity.ERROR,
        implementation = Implementation(ForbiddenMaterialIconsDetector::class.java, Scope.JAVA_FILE_SCOPE),
      )
  }
}
