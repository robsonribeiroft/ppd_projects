package org.robsonribeiro.ppd.dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.window.DialogState
import androidx.compose.ui.window.DialogWindow
import org.robsonribeiro.ppd.component.BentoComponent
import org.robsonribeiro.ppd.component.WindowBarComponent
import org.robsonribeiro.ppd.helper.screenDimensions
import org.robsonribeiro.ppd.helper.toDpSize
import org.robsonribeiro.ppd.model.ConfirmationDialogInfo
import org.robsonribeiro.ppd.values.ColorResources
import org.robsonribeiro.ppd.values.Padding

private const val BUTTON_CANCEL = "Cancel"
private const val BUTTON_CONFIRM = "Confirm"
private const val DIALOG_WINDOW_BAR = "Confirm Action"

@Composable
fun ConfirmationDialog(
    content: ConfirmationDialogInfo,
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
                    content.onDismiss
                    true
                }
                Key.Enter, Key.NumPadEnter -> {
                    content.onDismiss
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
                color = Color.Gray,
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
                        text = content.title,
                        style = MaterialTheme.typography.h6.copy(color = ColorResources.BlackRich),
                    )

                    Text(
                        modifier = Modifier,
                        text = content.description,
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
                    OutlinedButton(
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = ColorResources.White,
                            contentColor = ColorResources.BlackRich
                        ),
                        border = BorderStroke(
                            Padding.single,
                            ColorResources.BlackRich
                        ),
                        onClick = {
                            onDismissRequest()
                            content.onDismiss()
                        }
                    ) {
                        Text(BUTTON_CANCEL)
                    }
                    Button(
                        onClick = {
                            onDismissRequest()
                            content.onConfirm()
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = ColorResources.RedPantoneDarker,
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