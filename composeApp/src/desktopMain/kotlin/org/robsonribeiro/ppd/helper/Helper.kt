package org.robsonribeiro.ppd.helper

import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.WindowScope
import org.jetbrains.skia.FontWeight
import org.robsonribeiro.ppd.component.WindowBarComponent
import java.awt.GraphicsEnvironment

@Composable
fun WindowScope.windowTitleBar(onExitApplication: ()-> Unit) = WindowDraggableArea {
    WindowBarComponent { onExitApplication() }
}

fun screenDimensions(
    verticalPadding: Int = 40,
    horizontalPadding: Int = 40,
): Pair<Int, Int> {
    val displayMode = GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice.displayMode
    val width = (displayMode.width/2) - horizontalPadding
    val height = displayMode.height - verticalPadding
    return width to height
}

fun screenDimensions(
    verticalWeight: Float = 1f,
    horizontalWeight: Float = 1f,
    verticalPadding: Int = 40,
    horizontalPadding: Int = 40,
): Pair<Int, Int> {
    val displayMode = GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice.displayMode
    val width = (displayMode.width*horizontalWeight).toInt() - horizontalPadding
    val height = (displayMode.height*verticalWeight).toInt() - verticalPadding
    return width to height
}