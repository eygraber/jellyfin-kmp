package template.ui.icons

import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

val TemplateIcons.Delete: ImageVector
  get() {
    if(internalDelete != null) {
      return requireNotNull(internalDelete)
    }
    internalDelete = materialIcon(name = "Filled.Delete") {
      materialPath {
        moveTo(6.0f, 19.0f)
        curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
        horizontalLineToRelative(8.0f)
        curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
        verticalLineTo(7.0f)
        horizontalLineTo(6.0f)
        verticalLineToRelative(12.0f)
        close()
        moveTo(19.0f, 4.0f)
        horizontalLineToRelative(-3.5f)
        lineToRelative(-1.0f, -1.0f)
        horizontalLineToRelative(-5.0f)
        lineToRelative(-1.0f, 1.0f)
        horizontalLineTo(5.0f)
        verticalLineToRelative(2.0f)
        horizontalLineToRelative(14.0f)
        verticalLineTo(4.0f)
        close()
      }
    }
    return requireNotNull(internalDelete)
  }

private var internalDelete: ImageVector? = null
