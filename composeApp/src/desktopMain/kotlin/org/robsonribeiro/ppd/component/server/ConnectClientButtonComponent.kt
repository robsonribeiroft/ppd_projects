package org.robsonribeiro.ppd.component.server

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
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.vectorResource
import org.robsonribeiro.ppd.values.*
import org.robsonribeiro.ppd.values.StringResources.SERVER_BUTTON_CLICK_SETUP
import org.robsonribeiro.ppd.values.StringResources.SERVER_BUTTON_SERVER_DISABLED
import org.robsonribeiro.ppd.values.StringResources.SERVER_BUTTON_SERVER_RUNNING
import ppd.composeapp.generated.resources.Res
import ppd.composeapp.generated.resources.ic_server_host


private const val BUTTON_TITLE_CONNECT_CLIENT = "Join the server"
private const val BUTTON_CLICK_SETUP = "Click here to setup"

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ConnectClientButtonComponent(
    modifier: Modifier = Modifier,
    clientIsConnected: Pair<Boolean, String?> = false to null,
    onClick: ()->Unit
) {

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
                color = if (clientIsConnected.first) ColorResources.BlueCeltic else Color.Transparent,
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
                if (!clientIsConnected.first) {
                    Text(
                        text = BUTTON_TITLE_CONNECT_CLIENT,
                        style = TextStyle(
                            color = ColorResources.BlackRich,
                            fontSize = TextSize.small,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = TextSize.large
                        )
                    )
                    Text(
                        text = BUTTON_CLICK_SETUP,
                        style = TextStyle(
                            color = ColorResources.BlackRich,
                            fontSize = TextSize.tiny,
                            fontWeight = FontWeight.W400
                        )
                    )
                } else {
                    Text(
                        text =  "You join as ${clientIsConnected.second}",
                        style = TextStyle(
                            color = ColorResources.BlackRich,
                            fontSize = TextSize.small,
                            fontWeight = FontWeight.W400
                        )
                    )
                }
            }
        }
    }
}