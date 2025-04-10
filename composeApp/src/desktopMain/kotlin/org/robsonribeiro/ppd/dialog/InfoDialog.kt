package org.robsonribeiro.ppd.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.window.DialogState
import androidx.compose.ui.window.DialogWindow
import org.robsonribeiro.ppd.component.BentoComponent
import org.robsonribeiro.ppd.component.WindowBarComponent
import org.robsonribeiro.ppd.helper.screenDimensions
import org.robsonribeiro.ppd.helper.toDpSize
import org.robsonribeiro.ppd.values.ColorResources
import org.robsonribeiro.ppd.values.Padding
import org.robsonribeiro.ppd.values.empty

private const val BUTTON_CONFIRM = "Ok"
private const val DIALOG_WINDOW_BAR = "Warning"


@Composable
fun InfoDialog(
    content: Pair<String, String>? = null,
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit
) {

    DialogWindow(
        onCloseRequest = onDismissRequest,
        undecorated = true,
        transparent = true,
        state = DialogState(
            size = screenDimensions(
                verticalWeight = 0.3f,
                horizontalWeight = 0.4f
            ).toDpSize()
        ),
        onPreviewKeyEvent = { keyEvent ->
            when (keyEvent.key) {
                Key.Escape -> {
                    onDismissRequest()
                    true
                }
                Key.Enter, Key.NumPadEnter -> {
                    onDismissRequest()
                    true
                }
                else -> false
            }
        }
    ) {
        BentoComponent(
            modifier
            .fillMaxSize()
            .border(
                width = Padding.single,
                brush = Brush.linearGradient(
                    colors = listOf(Color.Gray, Color.Transparent),
                ),
                shape = RoundedCornerShape(Padding.regular)
            )
        ) {
            Column {
                WindowDraggableArea {
                    WindowBarComponent(
                        title = DIALOG_WINDOW_BAR,
                        barGradientColors = ColorResources.background_gradient
                    ) { onDismissRequest() }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterHorizontally)
                        .padding(Padding.large),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier,
                        text = content?.first ?: String.empty,
                        style = MaterialTheme.typography.h6.copy(color = ColorResources.BlackRich),
                    )

                    Text(
                        modifier = Modifier,
                        text = content?.second ?: String.empty,
                        color = ColorResources.BlackRich,
                        style = MaterialTheme.typography.body1,
                    )
                }

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(Padding.large),
                    horizontalArrangement = Arrangement.spacedBy(Padding.regular, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onDismissRequest,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = ColorResources.GreenEmerald,
                            contentColor = ColorResources.White
                        )
                    ) {
                        Text(BUTTON_CONFIRM)
                    }
                }
            }
        }
    }
}