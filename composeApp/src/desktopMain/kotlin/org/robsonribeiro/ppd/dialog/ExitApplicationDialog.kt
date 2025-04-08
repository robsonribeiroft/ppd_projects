package org.robsonribeiro.ppd.dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.DialogWindow
import org.robsonribeiro.ppd.component.BentoComponent
import org.robsonribeiro.ppd.component.WindowBarComponent
import org.robsonribeiro.ppd.values.ColorResources
import org.robsonribeiro.ppd.values.Padding
import org.robsonribeiro.ppd.values.TextSize

@Composable
fun ExitApplicationDialog(
    modifier: Modifier = Modifier,
    exitApplication: () -> Unit,
    onDismiss: (Boolean) -> Unit
) {
    DialogWindow(
        undecorated = true,
        transparent = true,
        onPreviewKeyEvent = { keyEvent ->
            when (keyEvent.key) {
                Key.Escape -> {
                    onDismiss(false)
                    return@DialogWindow true
                }
                Key.Enter, Key.NumPadEnter -> {
                    exitApplication()
                    return@DialogWindow true
                }
                else -> false
            }
        },
        onCloseRequest = {
            onDismiss(false)
        }
    ) {

        BentoComponent(modifier.fillMaxSize()) {
            Column{
                WindowDraggableArea {
                    WindowBarComponent { onDismiss(false) }
                }

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Quit Application?",
                        style = TextStyle(
                            fontSize = TextSize.largeExtra,
                            lineHeight = TextSize.large,
                            color = ColorResources.BlackRich,
                            fontWeight = FontWeight.W700
                        )
                    )

                    Text(
                        text = "Close this windows will make your opponent win\nAre you sure to quit?",
                        style = TextStyle(
                            fontSize = TextSize.regular,
                            lineHeight = TextSize.regular,
                            color = ColorResources.BlackRich,
                            fontWeight = FontWeight.W500
                        )
                    )

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
                            onClick = { onDismiss(false) }) {
                            Text("Cancel")
                        }
                        Button(
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = ColorResources.RedPantoneDarker,
                                contentColor = ColorResources.White
                            ),
                            onClick = { exitApplication() }) {
                            Text("Quit")
                        }

                    }
                }
            }
        }
    }
}