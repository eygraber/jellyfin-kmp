package template.ui.icons

import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

val TemplateIcons.Check: ImageVector
  get() {
    if(internalCheck != null) {
      return requireNotNull(internalCheck)
    }
    internalCheck = materialIcon(name = "Filled.Check") {
      materialPath {
        moveTo(9.0f, 16.17f)
        lineTo(4.83f, 12.0f)
        lineToRelative(-1.42f, 1.41f)
        lineTo(9.0f, 19.0f)
        lineTo(21.0f, 7.0f)
        lineToRelative(-1.41f, -1.41f)
        close()
      }
    }
    return requireNotNull(internalCheck)
  }

private var internalCheck: ImageVector? = null
