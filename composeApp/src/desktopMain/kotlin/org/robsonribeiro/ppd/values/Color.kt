package org.robsonribeiro.ppd.values

import androidx.compose.ui.graphics.Color

object ColorResources {
    val BlackRich = Color(0xFF041619)
    val BlueCeltic = Color(0xFF3870B2)
    val BluePrussian = Color(0xFF083042)
    val BlueRoyal = Color(0xFF0A2463)
    val BlueRoyalDarker = Color(0xFF081D4E)
    val GreenEmerald = Color(0xFF37BF6E)
    val RedPantone = Color(0xFFE63946)
    val RedPantoneDarker = Color(0xFFCB1928)
    val BaseBackground = Color(0xFFe8eaed)
    val White = Color(0xFFFFFFFF)

    val background_gradient = listOf(
        BlueRoyalDarker, BlueRoyalDarker,
        BlackRich, BlackRich, BlackRich,
        BlueRoyalDarker, BlueRoyalDarker,
        BlackRich, BlackRich,
        BlueRoyalDarker, BlueRoyalDarker,
        BlackRich, BlackRich
    )

//    val defeat_gradient = listOf(
//        RedPantone.darken(1.6f), RedPantone.darken(1.3f),
//        RedPantone, RedPantone.darken(0.9f),
//        RedPantone.darken(0.6f), RedPantone.darken(0.3f)
//    )

    val defeat_gradient = listOf(
        RedPantone.darken(1.3f),
        RedPantone,
        RedPantone.darken(0.3f)
    )

    val win_gradient = listOf(
        BlueCeltic.darken(1.3f),
        BlueCeltic,
        BlueCeltic.darken(0.3f)
    )


}

fun Color.darken(darkFactor: Float): Color {

    val clampedFactor = darkFactor.coerceIn(0.0f, 1.0f)

    val newRed = this.red * darkFactor
    val newGreen = this.green * darkFactor
    val newBlue = this.blue * darkFactor

    return Color(
        red = newRed,
        green = newGreen,
        blue = newBlue,
        alpha = this.alpha
    )
}
