package org.robsonribeiro.ppd.dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.window.DialogState
import androidx.compose.ui.window.DialogWindow
import org.robsonribeiro.ppd.component.BentoComponent
import org.robsonribeiro.ppd.component.WindowBarComponent
import org.robsonribeiro.ppd.helper.NetworkInputValidator
import org.robsonribeiro.ppd.helper.screenDimensions
import org.robsonribeiro.ppd.helper.toDpSize
import org.robsonribeiro.ppd.values.ColorResources
import org.robsonribeiro.ppd.values.Padding
import org.robsonribeiro.ppd.values.empty


private const val TEXT_FIELD_HINT = "e.g., your name, 1_bit_2_win"
private const val BUTTON_CONFIRM = "Join"
private const val DIALOG_TITLE = "Enter Your Nickname"
private const val DIALOG_WINDOW_BAR = "Join to Server"
private const val INVALID_ID = "The nickname is invalid! $TEXT_FIELD_HINT"


@Composable
fun JoinClientToServerDialog(
    modifier: Modifier = Modifier,
    onConnect: (clientId: String) -> Unit,
    onDismissRequest: () -> Unit
) {
    var textFieldHasFocus by remember { mutableStateOf(false) }
    var textFieldValue by remember { mutableStateOf(String.empty) }
    var validationResult by remember { mutableStateOf(NetworkInputValidator.validateClientId(String.empty)) }
    val textFieldHintText = {
        if (textFieldHasFocus || textFieldValue.isNotEmpty())
            String.empty
        else
            TEXT_FIELD_HINT
    }

    val attemptConnection = {
        val result = NetworkInputValidator.validateClientId(textFieldValue)
        if (result) {
            onConnect(textFieldValue)
        }
    }

    DialogWindow(
        onCloseRequest = onDismissRequest,
        undecorated = true,
        transparent = true,
        state = DialogState(
            size = screenDimensions(
                verticalWeight = 0.4f,
                horizontalWeight = 0.5f
            ).toDpSize()
        ),
        onPreviewKeyEvent = { keyEvent ->
            when (keyEvent.key) {
                Key.Escape -> {
                    onDismissRequest()
                    true
                }
                Key.Enter, Key.NumPadEnter -> {
                    attemptConnection()
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
                        .padding(Padding.large),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = DIALOG_TITLE,
                        style = MaterialTheme.typography.h6.copy(color = ColorResources.BlackRich),
                    )

                    OutlinedTextField(
                        value = textFieldValue,
                        onValueChange = {
                            textFieldValue = it
                            validationResult = NetworkInputValidator.validateClientId(it)
                        },
                        label = { Text(textFieldHintText()) },
                        isError = !validationResult && textFieldValue.isNotEmpty(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        ),
                        visualTransformation = VisualTransformation.None,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState ->
                                textFieldHasFocus = focusState.hasFocus
                            }
                            .onKeyEvent { event ->
                                when (event.key) {
                                    Key.Enter, Key.NumPadEnter -> {
                                        attemptConnection()
                                        true
                                    }
                                    else -> false
                                }
                            },
                        shape = CircleShape,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            backgroundColor = ColorResources.White,
                            focusedBorderColor = ColorResources.BlueRoyal,
                            unfocusedBorderColor = ColorResources.BaseBackground,
                            errorBorderColor = ColorResources.RedPantoneDarker
                        )
                    )

                    if (!validationResult) {
                        Text(
                            text = INVALID_ID,
                            color = MaterialTheme.colors.error,
                            style = MaterialTheme.typography.caption,
                            modifier = Modifier.padding(top = Padding.small)
                                .align(Alignment.Start)
                        )
                    }
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
                        onClick = onDismissRequest) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = attemptConnection,
                        enabled = validationResult,
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