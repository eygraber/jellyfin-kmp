package com.eygraber.jellyfin.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val JellyfinIcons.HidePassword: ImageVector
  get() {
    if(internalHidePassword != null) {
      return requireNotNull(internalHidePassword)
    }
    internalHidePassword = Builder(
      name = "HidePassword",
      defaultWidth = 20.0.dp,
      defaultHeight = 14.0.dp,
      viewportWidth = 20.0f,
      viewportHeight = 14.0f,
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
        moveTo(9.7393f, 12.9297f)
        curveTo(8.8278f, 12.9297f, 7.9642f, 12.818f, 7.1484f, 12.5947f)
        curveTo(6.3372f, 12.376f, 5.583f, 12.0866f, 4.8857f, 11.7266f)
        curveTo(4.193f, 11.362f, 3.571f, 10.9609f, 3.0195f, 10.5234f)
        curveTo(2.4681f, 10.0859f, 1.9964f, 9.6484f, 1.6045f, 9.2109f)
        curveTo(1.2126f, 8.7689f, 0.9118f, 8.361f, 0.7021f, 7.9873f)
        curveTo(0.4971f, 7.609f, 0.3945f, 7.3037f, 0.3945f, 7.0713f)
        curveTo(0.3945f, 6.8662f, 0.4766f, 6.6042f, 0.6406f, 6.2852f)
        curveTo(0.8047f, 5.9616f, 1.0417f, 5.6084f, 1.3516f, 5.2256f)
        curveTo(1.666f, 4.8428f, 2.0466f, 4.4554f, 2.4932f, 4.0635f)
        curveTo(2.9443f, 3.667f, 3.4548f, 3.2933f, 4.0244f, 2.9424f)
        lineTo(4.8721f, 3.79f)
        curveTo(4.3662f, 4.0908f, 3.9105f, 4.4075f, 3.5049f, 4.7402f)
        curveTo(3.1038f, 5.0729f, 2.7598f, 5.3942f, 2.4727f, 5.7041f)
        curveTo(2.1901f, 6.014f, 1.9713f, 6.2897f, 1.8164f, 6.5313f)
        curveTo(1.666f, 6.7728f, 1.5908f, 6.9528f, 1.5908f, 7.0713f)
        curveTo(1.5908f, 7.2308f, 1.6865f, 7.4587f, 1.8779f, 7.7549f)
        curveTo(2.0693f, 8.0511f, 2.3428f, 8.3792f, 2.6982f, 8.7393f)
        curveTo(3.0583f, 9.0947f, 3.4844f, 9.4548f, 3.9766f, 9.8193f)
        curveTo(4.4688f, 10.1839f, 5.0179f, 10.5212f, 5.624f, 10.8311f)
        curveTo(6.2301f, 11.1364f, 6.8796f, 11.3825f, 7.5723f, 11.5693f)
        curveTo(8.265f, 11.7562f, 8.9873f, 11.8496f, 9.7393f, 11.8496f)
        curveTo(10.1995f, 11.8496f, 10.6462f, 11.8132f, 11.0791f, 11.7402f)
        curveTo(11.512f, 11.6673f, 11.9313f, 11.5693f, 12.3369f, 11.4463f)
        lineTo(13.2324f, 12.3418f)
        curveTo(12.7038f, 12.5241f, 12.1478f, 12.6676f, 11.5645f, 12.7725f)
        curveTo(10.9811f, 12.8773f, 10.3727f, 12.9297f, 9.7393f, 12.9297f)
        close()
        moveTo(9.7393f, 1.2129f)
        curveTo(10.6598f, 1.2129f, 11.528f, 1.3245f, 12.3438f, 1.5479f)
        curveTo(13.1641f, 1.7666f, 13.9206f, 2.0583f, 14.6133f, 2.4228f)
        curveTo(15.306f, 2.7829f, 15.9258f, 3.1816f, 16.4727f, 3.6191f)
        curveTo(17.0241f, 4.0566f, 17.4935f, 4.4964f, 17.8809f, 4.9385f)
        curveTo(18.2682f, 5.376f, 18.5645f, 5.7839f, 18.7695f, 6.1621f)
        curveTo(18.9746f, 6.5358f, 19.0771f, 6.8389f, 19.0771f, 7.0713f)
        curveTo(19.0771f, 7.2718f, 18.9974f, 7.5293f, 18.8379f, 7.8438f)
        curveTo(18.6829f, 8.1582f, 18.4574f, 8.5023f, 18.1611f, 8.876f)
        curveTo(17.8649f, 9.2451f, 17.5026f, 9.6234f, 17.0742f, 10.0107f)
        curveTo(16.6504f, 10.3981f, 16.1696f, 10.7673f, 15.6318f, 11.1182f)
        lineTo(14.791f, 10.2773f)
        curveTo(15.265f, 9.9857f, 15.6911f, 9.6826f, 16.0693f, 9.3682f)
        curveTo(16.4476f, 9.0537f, 16.7712f, 8.7484f, 17.04f, 8.4522f)
        curveTo(17.3135f, 8.1514f, 17.5208f, 7.8825f, 17.6621f, 7.6455f)
        curveTo(17.8079f, 7.404f, 17.8809f, 7.2126f, 17.8809f, 7.0713f)
        curveTo(17.8809f, 6.9346f, 17.7829f, 6.7249f, 17.5869f, 6.4424f)
        curveTo(17.3955f, 6.1598f, 17.1221f, 5.8385f, 16.7666f, 5.4785f)
        curveTo(16.4111f, 5.1139f, 15.9873f, 4.7471f, 15.4951f, 4.3779f)
        curveTo(15.0029f, 4.0088f, 14.4538f, 3.667f, 13.8477f, 3.3525f)
        curveTo(13.2415f, 3.0335f, 12.5921f, 2.7783f, 11.8994f, 2.5869f)
        curveTo(11.2067f, 2.3909f, 10.4867f, 2.293f, 9.7393f, 2.293f)
        curveTo(9.3154f, 2.293f, 8.9075f, 2.3249f, 8.5156f, 2.3887f)
        curveTo(8.1283f, 2.4525f, 7.7454f, 2.5391f, 7.3672f, 2.6484f)
        lineTo(6.4648f, 1.7529f)
        curveTo(6.9707f, 1.5843f, 7.4925f, 1.4521f, 8.0303f, 1.3564f)
        curveTo(8.5726f, 1.2607f, 9.1423f, 1.2129f, 9.7393f, 1.2129f)
        close()
        moveTo(9.7393f, 10.8994f)
        curveTo(9.2015f, 10.8994f, 8.7002f, 10.7992f, 8.2353f, 10.5986f)
        curveTo(7.7705f, 10.3981f, 7.3626f, 10.1224f, 7.0117f, 9.7715f)
        curveTo(6.6608f, 9.416f, 6.3874f, 9.0081f, 6.1914f, 8.5478f)
        curveTo(5.9954f, 8.083f, 5.8952f, 7.5908f, 5.8906f, 7.0713f)
        curveTo(5.8906f, 6.766f, 5.9248f, 6.472f, 5.9932f, 6.1894f)
        curveTo(6.0615f, 5.9023f, 6.1595f, 5.6312f, 6.2871f, 5.376f)
        lineTo(11.4141f, 10.5029f)
        curveTo(11.1589f, 10.626f, 10.89f, 10.724f, 10.6074f, 10.7969f)
        curveTo(10.3294f, 10.8652f, 10.04f, 10.8994f, 9.7393f, 10.8994f)
        close()
        moveTo(13.2461f, 8.5684f)
        lineTo(8.2353f, 3.5576f)
        curveTo(8.4632f, 3.4574f, 8.7025f, 3.3799f, 8.9531f, 3.3252f)
        curveTo(9.2083f, 3.2705f, 9.4704f, 3.2432f, 9.7393f, 3.2432f)
        curveTo(10.2679f, 3.2432f, 10.7646f, 3.3412f, 11.2295f, 3.5371f)
        curveTo(11.6943f, 3.7331f, 12.1022f, 4.0065f, 12.4531f, 4.3574f)
        curveTo(12.804f, 4.7038f, 13.0775f, 5.1094f, 13.2734f, 5.5742f)
        curveTo(13.474f, 6.0391f, 13.5742f, 6.5381f, 13.5742f, 7.0713f)
        curveTo(13.5742f, 7.3356f, 13.5446f, 7.5954f, 13.4854f, 7.8506f)
        curveTo(13.4307f, 8.1012f, 13.3509f, 8.3405f, 13.2461f, 8.5684f)
        close()
        moveTo(15.0576f, 12.9912f)
        lineTo(3.6211f, 1.5684f)
        curveTo(3.5208f, 1.4681f, 3.4707f, 1.345f, 3.4707f, 1.1992f)
        curveTo(3.4707f, 1.0488f, 3.5208f, 0.9235f, 3.6211f, 0.8232f)
        curveTo(3.7259f, 0.7184f, 3.8512f, 0.6683f, 3.9971f, 0.6729f)
        curveTo(4.1475f, 0.6729f, 4.2728f, 0.723f, 4.3731f, 0.8232f)
        lineTo(15.8027f, 12.2461f)
        curveTo(15.9076f, 12.3509f, 15.9622f, 12.4717f, 15.9668f, 12.6084f)
        curveTo(15.9714f, 12.7497f, 15.9167f, 12.8773f, 15.8027f, 12.9912f)
        curveTo(15.6979f, 13.1051f, 15.5726f, 13.1598f, 15.4268f, 13.1553f)
        curveTo(15.2855f, 13.1507f, 15.1624f, 13.096f, 15.0576f, 12.9912f)
        close()
      }
    }
      .build()
    return requireNotNull(internalHidePassword)
  }

private var internalHidePassword: ImageVector? = null
