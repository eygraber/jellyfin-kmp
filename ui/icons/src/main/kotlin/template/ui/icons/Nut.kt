package template.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val TemplateIcons.Nut: ImageVector
  get() {
    if(internalNut != null) {
      return requireNotNull(internalNut)
    }
    internalNut = Builder(
      name = "Nut",
      defaultWidth = 22.0.dp,
      defaultHeight = 22.0.dp,
      viewportWidth =
      22.0f,
      viewportHeight = 22.0f,
    ).apply {
      path(
        fill = SolidColor(Color(0xFF343330)),
        stroke = null,
        strokeLineWidth = 0.0f,
        strokeLineCap = Butt,
        strokeLineJoin = Miter,
        strokeLineMiter = 4.0f,
        pathFillType = NonZero,
      ) {
        moveTo(6.5313f, 11.0f)
        curveTo(6.5313f, 11.8838f, 6.7933f, 12.7478f, 7.2844f, 13.4827f)
        curveTo(7.7754f, 14.2176f, 8.4733f, 14.7904f, 9.2899f, 15.1286f)
        curveTo(10.1064f, 15.4668f, 11.005f, 15.5553f, 11.8718f, 15.3829f)
        curveTo(12.7387f, 15.2105f, 13.5349f, 14.7849f, 14.1599f, 14.1599f)
        curveTo(14.7848f, 13.5349f, 15.2105f, 12.7387f, 15.3829f, 11.8718f)
        curveTo(15.5553f, 11.005f, 15.4668f, 10.1065f, 15.1286f, 9.2899f)
        curveTo(14.7904f, 8.4733f, 14.2176f, 7.7754f, 13.4827f, 7.2844f)
        curveTo(12.7478f, 6.7934f, 11.8838f, 6.5313f, 11.0f, 6.5313f)
        curveTo(9.8152f, 6.5324f, 8.6792f, 7.0036f, 7.8414f, 7.8414f)
        curveTo(7.0036f, 8.6792f, 6.5324f, 9.8152f, 6.5313f, 11.0f)
        close()
        moveTo(13.4062f, 11.0f)
        curveTo(13.4062f, 11.4759f, 13.2651f, 11.9411f, 13.0007f, 12.3369f)
        curveTo(12.7363f, 12.7326f, 12.3605f, 13.041f, 11.9208f, 13.2231f)
        curveTo(11.4811f, 13.4052f, 10.9973f, 13.4529f, 10.5306f, 13.36f)
        curveTo(10.0638f, 13.2672f, 9.635f, 13.038f, 9.2985f, 12.7015f)
        curveTo(8.962f, 12.365f, 8.7328f, 11.9362f, 8.64f, 11.4694f)
        curveTo(8.5471f, 11.0027f, 8.5948f, 10.5189f, 8.7769f, 10.0792f)
        curveTo(8.959f, 9.6395f, 9.2675f, 9.2637f, 9.6632f, 8.9993f)
        curveTo(10.0589f, 8.7349f, 10.5241f, 8.5938f, 11.0f, 8.5938f)
        curveTo(11.6382f, 8.5938f, 12.2502f, 8.8473f, 12.7015f, 9.2985f)
        curveTo(13.1527f, 9.7498f, 13.4062f, 10.3618f, 13.4062f, 11.0f)
        close()
        moveTo(19.3875f, 5.3831f)
        lineTo(11.825f, 1.2435f)
        curveTo(11.5723f, 1.1044f, 11.2885f, 1.0315f, 11.0f, 1.0315f)
        curveTo(10.7115f, 1.0315f, 10.4277f, 1.1044f, 10.175f, 1.2435f)
        lineTo(2.6125f, 5.3831f)
        curveTo(2.3419f, 5.5312f, 2.1161f, 5.7494f, 1.9589f, 6.0147f)
        curveTo(1.8016f, 6.2801f, 1.7187f, 6.5829f, 1.7188f, 6.8913f)
        verticalLineTo(15.1087f)
        curveTo(1.7187f, 15.4171f, 1.8016f, 15.7199f, 1.9589f, 15.9853f)
        curveTo(2.1161f, 16.2507f, 2.3419f, 16.4688f, 2.6125f, 16.6169f)
        lineTo(10.175f, 20.7565f)
        curveTo(10.4277f, 20.8957f, 10.7115f, 20.9688f, 11.0f, 20.9688f)
        curveTo(11.2885f, 20.9688f, 11.5723f, 20.8957f, 11.825f, 20.7565f)
        lineTo(19.3875f, 16.6169f)
        curveTo(19.6581f, 16.4688f, 19.8839f, 16.2507f, 20.0411f, 15.9853f)
        curveTo(20.1984f, 15.7199f, 20.2813f, 15.4171f, 20.2812f, 15.1087f)
        verticalLineTo(6.8913f)
        curveTo(20.2813f, 6.5829f, 20.1984f, 6.2801f, 20.0411f, 6.0147f)
        curveTo(19.8839f, 5.7494f, 19.6581f, 5.5312f, 19.3875f, 5.3831f)
        close()
        moveTo(18.2188f, 14.905f)
        lineTo(11.0f, 18.8581f)
        lineTo(3.7813f, 14.905f)
        verticalLineTo(7.095f)
        lineTo(11.0f, 3.1419f)
        lineTo(18.2188f, 7.095f)
        verticalLineTo(14.905f)
        close()
      }
    }
      .build()
    return requireNotNull(internalNut)
  }

private var internalNut: ImageVector? = null
