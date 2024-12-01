package template.ui.icons

import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

val TemplateIcons.ArrowBack: ImageVector
  get() {
    if(internalArrowBack != null) {
      return requireNotNull(internalArrowBack)
    }
    internalArrowBack = materialIcon(name = "AutoMirrored.Filled.ArrowBack", autoMirror = true) {
      materialPath {
        moveTo(20.0f, 11.0f)
        horizontalLineTo(7.83f)
        lineToRelative(5.59f, -5.59f)
        lineTo(12.0f, 4.0f)
        lineToRelative(-8.0f, 8.0f)
        lineToRelative(8.0f, 8.0f)
        lineToRelative(1.41f, -1.41f)
        lineTo(7.83f, 13.0f)
        horizontalLineTo(20.0f)
        verticalLineToRelative(-2.0f)
        close()
      }
    }
    return requireNotNull(internalArrowBack)
  }

private var internalArrowBack: ImageVector? = null
