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

val TemplateIcons.ShowPassword: ImageVector
  get() {
    if(internalShowPassword != null) {
      return requireNotNull(internalShowPassword)
    }
    internalShowPassword = Builder(
      name = "ShowPassword",
      defaultWidth = 20.0.dp,
      defaultHeight = 12.0.dp,
      viewportWidth = 20.0f,
      viewportHeight = 12.0f,
    ).apply {
      path(
        fill = SolidColor(Color(0xFF3C3C43)),
        stroke = null,
        fillAlpha = 0.6f,
        strokeLineWidth = 0.0f,
        strokeLineCap = Butt,
        strokeLineJoin = Miter,
        strokeLineMiter = 4.0f,
        pathFillType = NonZero,
      ) {
        moveTo(9.7393f, 11.9297f)
        curveTo(8.8278f, 11.9297f, 7.9642f, 11.818f, 7.1484f, 11.5947f)
        curveTo(6.3372f, 11.376f, 5.583f, 11.0866f, 4.8857f, 10.7266f)
        curveTo(4.193f, 10.362f, 3.571f, 9.9609f, 3.0195f, 9.5234f)
        curveTo(2.4681f, 9.0859f, 1.9964f, 8.6484f, 1.6045f, 8.2109f)
        curveTo(1.2126f, 7.7689f, 0.9118f, 7.361f, 0.7021f, 6.9873f)
        curveTo(0.4971f, 6.609f, 0.3945f, 6.3037f, 0.3945f, 6.0713f)
        curveTo(0.3945f, 5.8389f, 0.4971f, 5.5358f, 0.7021f, 5.1621f)
        curveTo(0.9118f, 4.7839f, 1.2126f, 4.376f, 1.6045f, 3.9385f)
        curveTo(1.9964f, 3.4964f, 2.4681f, 3.0566f, 3.0195f, 2.6191f)
        curveTo(3.5755f, 2.1816f, 4.1999f, 1.7829f, 4.8926f, 1.4229f)
        curveTo(5.5898f, 1.0583f, 6.3441f, 0.7666f, 7.1553f, 0.5479f)
        curveTo(7.971f, 0.3245f, 8.8324f, 0.2129f, 9.7393f, 0.2129f)
        curveTo(10.6553f, 0.2129f, 11.5212f, 0.3245f, 12.3369f, 0.5479f)
        curveTo(13.1572f, 0.7666f, 13.9137f, 1.0583f, 14.6064f, 1.4229f)
        curveTo(15.3037f, 1.7829f, 15.9258f, 2.1816f, 16.4727f, 2.6191f)
        curveTo(17.0241f, 3.0566f, 17.4935f, 3.4964f, 17.8809f, 3.9385f)
        curveTo(18.2682f, 4.376f, 18.5645f, 4.7839f, 18.7695f, 5.1621f)
        curveTo(18.9746f, 5.5358f, 19.0771f, 5.8389f, 19.0771f, 6.0713f)
        curveTo(19.0771f, 6.3037f, 18.9746f, 6.609f, 18.7695f, 6.9873f)
        curveTo(18.5645f, 7.361f, 18.2682f, 7.7689f, 17.8809f, 8.2109f)
        curveTo(17.4935f, 8.6484f, 17.0241f, 9.0859f, 16.4727f, 9.5234f)
        curveTo(15.9258f, 9.9609f, 15.306f, 10.362f, 14.6133f, 10.7266f)
        curveTo(13.9206f, 11.0866f, 13.1641f, 11.376f, 12.3438f, 11.5947f)
        curveTo(11.528f, 11.818f, 10.6598f, 11.9297f, 9.7393f, 11.9297f)
        close()
        moveTo(9.7393f, 10.8496f)
        curveTo(10.4867f, 10.8496f, 11.2044f, 10.7562f, 11.8926f, 10.5693f)
        curveTo(12.5853f, 10.3825f, 13.2347f, 10.1364f, 13.8408f, 9.831f)
        curveTo(14.4469f, 9.5212f, 14.9961f, 9.1839f, 15.4883f, 8.8193f)
        curveTo(15.985f, 8.4548f, 16.4111f, 8.0947f, 16.7666f, 7.7393f)
        curveTo(17.1221f, 7.3792f, 17.3955f, 7.0511f, 17.5869f, 6.7549f)
        curveTo(17.7829f, 6.4587f, 17.8809f, 6.2308f, 17.8809f, 6.0713f)
        curveTo(17.8809f, 5.9346f, 17.7829f, 5.7249f, 17.5869f, 5.4424f)
        curveTo(17.3955f, 5.1598f, 17.1221f, 4.8385f, 16.7666f, 4.4785f)
        curveTo(16.4111f, 4.1139f, 15.985f, 3.7471f, 15.4883f, 3.3779f)
        curveTo(14.9961f, 3.0088f, 14.4469f, 2.667f, 13.8408f, 2.3525f)
        curveTo(13.2347f, 2.0335f, 12.5853f, 1.7783f, 11.8926f, 1.5869f)
        curveTo(11.2044f, 1.3909f, 10.4867f, 1.293f, 9.7393f, 1.293f)
        curveTo(8.9873f, 1.293f, 8.265f, 1.3909f, 7.5723f, 1.5869f)
        curveTo(6.8796f, 1.7783f, 6.2301f, 2.0335f, 5.624f, 2.3525f)
        curveTo(5.0179f, 2.667f, 4.4688f, 3.0088f, 3.9766f, 3.3779f)
        curveTo(3.4844f, 3.7471f, 3.0583f, 4.1139f, 2.6982f, 4.4785f)
        curveTo(2.3428f, 4.8385f, 2.0693f, 5.1598f, 1.8779f, 5.4424f)
        curveTo(1.6865f, 5.7249f, 1.5908f, 5.9346f, 1.5908f, 6.0713f)
        curveTo(1.5908f, 6.2308f, 1.6865f, 6.4587f, 1.8779f, 6.7549f)
        curveTo(2.0693f, 7.0511f, 2.3428f, 7.3792f, 2.6982f, 7.7393f)
        curveTo(3.0583f, 8.0947f, 3.4844f, 8.4548f, 3.9766f, 8.8193f)
        curveTo(4.4688f, 9.1839f, 5.0179f, 9.5212f, 5.624f, 9.831f)
        curveTo(6.2301f, 10.1364f, 6.8796f, 10.3825f, 7.5723f, 10.5693f)
        curveTo(8.265f, 10.7562f, 8.9873f, 10.8496f, 9.7393f, 10.8496f)
        close()
        moveTo(9.7393f, 9.8994f)
        curveTo(9.2015f, 9.8994f, 8.7002f, 9.7992f, 8.2353f, 9.5986f)
        curveTo(7.7705f, 9.3981f, 7.3626f, 9.1224f, 7.0117f, 8.7715f)
        curveTo(6.6608f, 8.416f, 6.3851f, 8.0081f, 6.1846f, 7.5479f)
        curveTo(5.9886f, 7.083f, 5.8906f, 6.5908f, 5.8906f, 6.0713f)
        curveTo(5.8906f, 5.5381f, 5.9886f, 5.0391f, 6.1846f, 4.5742f)
        curveTo(6.3851f, 4.1094f, 6.6608f, 3.7038f, 7.0117f, 3.3574f)
        curveTo(7.3626f, 3.0065f, 7.7705f, 2.7331f, 8.2353f, 2.5371f)
        curveTo(8.7002f, 2.3412f, 9.2015f, 2.2432f, 9.7393f, 2.2432f)
        curveTo(10.2679f, 2.2432f, 10.7646f, 2.3412f, 11.2295f, 2.5371f)
        curveTo(11.6943f, 2.7331f, 12.1022f, 3.0065f, 12.4531f, 3.3574f)
        curveTo(12.804f, 3.7038f, 13.0775f, 4.1094f, 13.2734f, 4.5742f)
        curveTo(13.474f, 5.0391f, 13.5742f, 5.5381f, 13.5742f, 6.0713f)
        curveTo(13.5742f, 6.5908f, 13.474f, 7.083f, 13.2734f, 7.5479f)
        curveTo(13.0775f, 8.0081f, 12.804f, 8.416f, 12.4531f, 8.7715f)
        curveTo(12.1022f, 9.1224f, 11.6943f, 9.3981f, 11.2295f, 9.5986f)
        curveTo(10.7646f, 9.7992f, 10.2679f, 9.8994f, 9.7393f, 9.8994f)
        close()
        moveTo(9.7393f, 7.3428f)
        curveTo(10.0902f, 7.3428f, 10.391f, 7.2175f, 10.6416f, 6.9668f)
        curveTo(10.8923f, 6.7161f, 11.0176f, 6.4176f, 11.0176f, 6.0713f)
        curveTo(11.0176f, 5.7204f, 10.8923f, 5.4219f, 10.6416f, 5.1758f)
        curveTo(10.391f, 4.9251f, 10.0902f, 4.7998f, 9.7393f, 4.7998f)
        curveTo(9.3838f, 4.7998f, 9.0807f, 4.9251f, 8.8301f, 5.1758f)
        curveTo(8.5794f, 5.4219f, 8.4541f, 5.7204f, 8.4541f, 6.0713f)
        curveTo(8.4541f, 6.4176f, 8.5794f, 6.7161f, 8.8301f, 6.9668f)
        curveTo(9.0807f, 7.2175f, 9.3838f, 7.3428f, 9.7393f, 7.3428f)
        close()
      }
    }
      .build()
    return requireNotNull(internalShowPassword)
  }

private var internalShowPassword: ImageVector? = null
