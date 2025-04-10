package org.robsonribeiro.ppd.component.server

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import org.jetbrains.compose.resources.vectorResource
import org.robsonribeiro.ppd.model.ServerState
import org.robsonribeiro.ppd.values.ColorResources
import org.robsonribeiro.ppd.values.Padding
import org.robsonribeiro.ppd.values.StringResources.SERVER_BUTTON_CLICK_SETUP
import org.robsonribeiro.ppd.values.StringResources.SERVER_BUTTON_SERVER_DISABLED
import org.robsonribeiro.ppd.values.StringResources.SERVER_BUTTON_SERVER_RUNNING
import org.robsonribeiro.ppd.values.empty
import ppd.composeapp.generated.resources.Res
import ppd.composeapp.generated.resources.ic_server_host

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun StartServerButtonComponent(
    modifier: Modifier = Modifier,
    serverState: ServerState,
    onClick: ()->Unit
) {

    val infiniteTransition = rememberInfiniteTransition()
    val animatedColor by infiniteTransition.animateColor(
        initialValue = Color.Transparent,
        targetValue = ColorResources.GreenEmerald,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    var isHoveringOver by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .onPointerEvent(eventType = PointerEventType.Enter){ isHoveringOver = true }
            .onPointerEvent(eventType = PointerEventType.Exit){ isHoveringOver = false }
            .clickable {
                onClick()
                isHoveringOver = !isHoveringOver
            }
            .border(
                width = Padding.tiny,
                color = if (serverState.isRunning) animatedColor else Color.Transparent,
                shape = RoundedCornerShape(Padding.regular)
            ),
        elevation = if (isHoveringOver) Padding.large else Padding.tiny,
        shape = RoundedCornerShape(Padding.regular)
    ) {
        Row (
            Modifier.padding(
                vertical = Padding.regular,
                horizontal = Padding.regular
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Padding.regular)
        ) {
            Icon(
                imageVector = vectorResource(Res.drawable.ic_server_host),
                contentDescription = String.empty,
                tint = ColorResources.BlackRich,
            )
            Column {
                Text(
                    text = if (serverState.isRunning) SERVER_BUTTON_SERVER_RUNNING else SERVER_BUTTON_SERVER_DISABLED,
                    style = MaterialTheme.typography.body1
                )
                Text(
                    text = if (serverState.isRunning) "${serverState.host}:${serverState.port}" else SERVER_BUTTON_CLICK_SETUP,
                    style = MaterialTheme.typography.body2
                )
            }
        }
    }
}