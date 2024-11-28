package template.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val TemplateIcons.Barbell: ImageVector
  get() {
    if(internalBarbell != null) {
      return requireNotNull(internalBarbell)
    }
    internalBarbell = ImageVector.Builder(
      name = "Barbell",
      defaultWidth = 22.dp,
      defaultHeight = 22.dp,
      viewportWidth = 22f,
      viewportHeight = 22f,
    ).apply {
      path(
        fill = SolidColor(Color(0xFFFFFFFF)),
        fillAlpha = 1.0f,
        stroke = null,
        strokeAlpha = 1.0f,
        strokeLineWidth = 1.0f,
        strokeLineCap = StrokeCap.Butt,
        strokeLineJoin = StrokeJoin.Miter,
        strokeLineMiter = 1.0f,
        pathFillType = PathFillType.NonZero,
      ) {
        moveTo(21.3125f, 10.3125f)
        horizontalLineTo(20.625f)
        verticalLineTo(7.5625f)
        curveTo(20.6250f, 7.19780f, 20.48010f, 6.84810f, 20.22230f, 6.59020f)
        curveTo(19.96440f, 6.33240f, 19.61470f, 6.18750f, 19.250f, 6.18750f)
        horizontalLineTo(17.875f)
        verticalLineTo(5.5f)
        curveTo(17.8750f, 5.13530f, 17.73010f, 4.78560f, 17.47230f, 4.52770f)
        curveTo(17.21440f, 4.26990f, 16.86470f, 4.1250f, 16.50f, 4.1250f)
        horizontalLineTo(14.4375f)
        curveTo(14.07280f, 4.1250f, 13.72310f, 4.26990f, 13.46520f, 4.52770f)
        curveTo(13.20740f, 4.78560f, 13.06250f, 5.13530f, 13.06250f, 5.50f)
        verticalLineTo(10.3125f)
        horizontalLineTo(8.9375f)
        verticalLineTo(5.5f)
        curveTo(8.93750f, 5.13530f, 8.79260f, 4.78560f, 8.53480f, 4.52770f)
        curveTo(8.27690f, 4.26990f, 7.92720f, 4.1250f, 7.56250f, 4.1250f)
        horizontalLineTo(5.5f)
        curveTo(5.13530f, 4.1250f, 4.78560f, 4.26990f, 4.52770f, 4.52770f)
        curveTo(4.26990f, 4.78560f, 4.1250f, 5.13530f, 4.1250f, 5.50f)
        verticalLineTo(6.1875f)
        horizontalLineTo(2.75f)
        curveTo(2.38530f, 6.18750f, 2.03560f, 6.33240f, 1.77770f, 6.59020f)
        curveTo(1.51990f, 6.84810f, 1.3750f, 7.19780f, 1.3750f, 7.56250f)
        verticalLineTo(10.3125f)
        horizontalLineTo(0.6875f)
        curveTo(0.50520f, 10.31250f, 0.33030f, 10.38490f, 0.20140f, 10.51390f)
        curveTo(0.07240f, 10.64280f, 00f, 10.81770f, 00f, 110f)
        curveTo(00f, 11.18230f, 0.07240f, 11.35720f, 0.20140f, 11.48610f)
        curveTo(0.33030f, 11.61510f, 0.50520f, 11.68750f, 0.68750f, 11.68750f)
        horizontalLineTo(1.375f)
        verticalLineTo(14.4375f)
        curveTo(1.3750f, 14.80220f, 1.51990f, 15.15190f, 1.77770f, 15.40980f)
        curveTo(2.03560f, 15.66760f, 2.38530f, 15.81250f, 2.750f, 15.81250f)
        horizontalLineTo(4.125f)
        verticalLineTo(16.5f)
        curveTo(4.1250f, 16.86470f, 4.26990f, 17.21440f, 4.52770f, 17.47230f)
        curveTo(4.78560f, 17.73010f, 5.13530f, 17.8750f, 5.50f, 17.8750f)
        horizontalLineTo(7.5625f)
        curveTo(7.92720f, 17.8750f, 8.27690f, 17.73010f, 8.53480f, 17.47230f)
        curveTo(8.79260f, 17.21440f, 8.93750f, 16.86470f, 8.93750f, 16.50f)
        verticalLineTo(11.6875f)
        horizontalLineTo(13.0625f)
        verticalLineTo(16.5f)
        curveTo(13.06250f, 16.86470f, 13.20740f, 17.21440f, 13.46520f, 17.47230f)
        curveTo(13.72310f, 17.73010f, 14.07280f, 17.8750f, 14.43750f, 17.8750f)
        horizontalLineTo(16.5f)
        curveTo(16.86470f, 17.8750f, 17.21440f, 17.73010f, 17.47230f, 17.47230f)
        curveTo(17.73010f, 17.21440f, 17.8750f, 16.86470f, 17.8750f, 16.50f)
        verticalLineTo(15.8125f)
        horizontalLineTo(19.25f)
        curveTo(19.61470f, 15.81250f, 19.96440f, 15.66760f, 20.22230f, 15.40980f)
        curveTo(20.48010f, 15.15190f, 20.6250f, 14.80220f, 20.6250f, 14.43750f)
        verticalLineTo(11.6875f)
        horizontalLineTo(21.3125f)
        curveTo(21.49480f, 11.68750f, 21.66970f, 11.61510f, 21.79860f, 11.48610f)
        curveTo(21.92760f, 11.35720f, 220f, 11.18230f, 220f, 110f)
        curveTo(220f, 10.81770f, 21.92760f, 10.64280f, 21.79860f, 10.51390f)
        curveTo(21.66970f, 10.38490f, 21.49480f, 10.31250f, 21.31250f, 10.31250f)
        close()
        moveTo(2.75f, 14.4375f)
        verticalLineTo(7.5625f)
        horizontalLineTo(4.125f)
        verticalLineTo(14.4375f)
        horizontalLineTo(2.75f)
        close()
        moveTo(7.5625f, 16.5f)
        horizontalLineTo(5.5f)
        verticalLineTo(5.5f)
        horizontalLineTo(7.5625f)
        verticalLineTo(16.5f)
        close()
        moveTo(16.5f, 16.5f)
        horizontalLineTo(14.4375f)
        verticalLineTo(5.5f)
        horizontalLineTo(16.5f)
        verticalLineTo(15.1095f)
        curveTo(16.50f, 15.11470f, 16.50f, 15.11980f, 16.50f, 15.1250f)
        curveTo(16.50f, 15.13020f, 16.50f, 15.13530f, 16.50f, 15.14050f)
        verticalLineTo(16.5f)
        close()
        moveTo(19.25f, 14.4375f)
        horizontalLineTo(17.875f)
        verticalLineTo(7.5625f)
        horizontalLineTo(19.25f)
        verticalLineTo(14.4375f)
        close()
      }
    }.build()
    return requireNotNull(internalBarbell)
  }

private var internalBarbell: ImageVector? = null
