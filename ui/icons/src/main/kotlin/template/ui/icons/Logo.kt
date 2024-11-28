package template.ui.icons

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val TemplateIcons.Logo: ImageVector
  get() {
    if(internalLogo != null) {
      return requireNotNull(internalLogo)
    }
    internalLogo = Builder(
      name = "Logo",
      defaultWidth = 108.0.dp,
      defaultHeight = 108.0.dp,
      viewportWidth = 108.0f,
      viewportHeight = 108.0f,
    ).apply {
      path(
        fill = linearGradient(
          0.0f to Color(0x44000000),
          1.0f to Color(0x00000000),
          start =
          Offset(42.9492f, 49.59793f),
          end = Offset(85.84757f, 92.4963f),
        ),
        stroke = null,
        strokeLineWidth = 0.0f,
        strokeLineCap = Butt,
        strokeLineJoin = Miter,
        strokeLineMiter = 4.0f,
        pathFillType = NonZero,
      ) {
        moveTo(31.0f, 63.928f)
        curveToRelative(0.0f, 0.0f, 6.4f, -11.0f, 12.1f, -13.1f)
        curveToRelative(7.2f, -2.6f, 26.0f, -1.4f, 26.0f, -1.4f)
        lineToRelative(38.1f, 38.1f)
        lineTo(107.0f, 108.928f)
        lineToRelative(-32.0f, -1.0f)
        lineTo(31.0f, 63.928f)
        close()
      }
      path(
        fill = SolidColor(Color(0xFFFFFFFF)),
        stroke = SolidColor(Color(0x00000000)),
        strokeLineWidth = 1.0f,
        strokeLineCap = Butt,
        strokeLineJoin = Miter,
        strokeLineMiter = 4.0f,
        pathFillType = NonZero,
      ) {
        moveTo(65.3f, 45.828f)
        lineToRelative(3.8f, -6.6f)
        curveToRelative(0.2f, -0.4f, 0.1f, -0.9f, -0.3f, -1.1f)
        curveToRelative(-0.4f, -0.2f, -0.9f, -0.1f, -1.1f, 0.3f)
        lineToRelative(-3.9f, 6.7f)
        curveToRelative(-6.3f, -2.8f, -13.4f, -2.8f, -19.7f, 0.0f)
        lineToRelative(-3.9f, -6.7f)
        curveToRelative(-0.2f, -0.4f, -0.7f, -0.5f, -1.1f, -0.3f)
        curveTo(38.8f, 38.328f, 38.7f, 38.828f, 38.9f, 39.228f)
        lineToRelative(3.8f, 6.6f)
        curveTo(36.2f, 49.428f, 31.7f, 56.028f, 31.0f, 63.928f)
        horizontalLineToRelative(46.0f)
        curveTo(76.3f, 56.028f, 71.8f, 49.428f, 65.3f, 45.828f)
        close()
        moveTo(43.4f, 57.328f)
        curveToRelative(-0.8f, 0.0f, -1.5f, -0.5f, -1.8f, -1.2f)
        curveToRelative(-0.3f, -0.7f, -0.1f, -1.5f, 0.4f, -2.1f)
        curveToRelative(0.5f, -0.5f, 1.4f, -0.7f, 2.1f, -0.4f)
        curveToRelative(0.7f, 0.3f, 1.2f, 1.0f, 1.2f, 1.8f)
        curveTo(45.3f, 56.528f, 44.5f, 57.328f, 43.4f, 57.328f)
        lineTo(43.4f, 57.328f)
        close()
        moveTo(64.6f, 57.328f)
        curveToRelative(-0.8f, 0.0f, -1.5f, -0.5f, -1.8f, -1.2f)
        reflectiveCurveToRelative(-0.1f, -1.5f, 0.4f, -2.1f)
        curveToRelative(0.5f, -0.5f, 1.4f, -0.7f, 2.1f, -0.4f)
        curveToRelative(0.7f, 0.3f, 1.2f, 1.0f, 1.2f, 1.8f)
        curveTo(66.5f, 56.528f, 65.6f, 57.328f, 64.6f, 57.328f)
        lineTo(64.6f, 57.328f)
        close()
      }
    }
      .build()
    return requireNotNull(internalLogo)
  }

private var internalLogo: ImageVector? = null
