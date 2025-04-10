package org.robsonribeiro.ppd.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.vectorResource
import ppd.composeapp.generated.resources.Res
import ppd.composeapp.generated.resources.ic_close
import ppd.composeapp.generated.resources.ic_minimize
import org.robsonribeiro.ppd.values.Padding
import org.robsonribeiro.ppd.values.TextSize
import org.robsonribeiro.ppd.values.ColorResources
import org.robsonribeiro.ppd.values.StringResources
import org.robsonribeiro.ppd.values.empty
import ppd.composeapp.generated.resources.ic_game_joystick

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WindowBarComponent(
    title: String = StringResources.APPLICATION_NAME,
    titleColor: Color = ColorResources.White,
    iconColor: Color = ColorResources.White,
    barColor: Color = ColorResources.BlackRich.copy(alpha = 0.5f),
    barGradientColors: List<Color>? = null,
    modifier: Modifier = Modifier,
    onMinimize: (()-> Unit)? = null,
    onExitWindow: ()-> Unit,
) {

    var isHoverOverMinimize by remember { mutableStateOf(false) }
    var isHoverOverClose by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    colors = if (barGradientColors.isNullOrEmpty()) listOf(barColor, barColor) else barGradientColors
                )
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Padding.regular),
            horizontalArrangement = Arrangement.spacedBy(Padding.regular),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                vectorResource(Res.drawable.ic_game_joystick),
                String.empty,
                modifier,
                iconColor
            )
            Text(
                modifier = modifier,
                text = title,

                style = TextStyle(
                    color = titleColor,
                    fontSize = TextSize.large,
                    lineHeight = TextSize.large,
                    fontWeight = FontWeight.W500
                )
            )
        }
        Row {
            onMinimize?.let {
                IconButtonComponent(
                    modifier = Modifier
                        .onPointerEvent(eventType = PointerEventType.Enter){ isHoverOverMinimize = true }
                        .onPointerEvent(eventType = PointerEventType.Exit){ isHoverOverMinimize = false },
                    buttonColor = if (isHoverOverMinimize) Color.White.copy(alpha = 0.2f) else Color.Transparent,
                    iconColor = Color.White,
                    iconResource = Res.drawable.ic_minimize
                ) {
                    onMinimize()
                }
            }
            IconButtonComponent(
                modifier = Modifier
                    .onPointerEvent(eventType = PointerEventType.Enter){ isHoverOverClose = true }
                    .onPointerEvent(eventType = PointerEventType.Exit){ isHoverOverClose = false },
                buttonColor = if (isHoverOverClose) ColorResources.RedPantoneDarker else Color.Transparent,
                iconColor = Color.White,
                iconResource = Res.drawable.ic_close,
            ){
                onExitWindow()
            }
        }
    }
}
