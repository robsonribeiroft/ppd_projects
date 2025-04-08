package org.robsonribeiro.ppd.dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.*
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.window.DialogWindow
import org.robsonribeiro.ppd.component.BentoComponent
import org.robsonribeiro.ppd.component.WindowBarComponent
import org.robsonribeiro.ppd.helper.NetworkInputValidator
import org.robsonribeiro.ppd.komms.SERVER_HOSTNAME
import org.robsonribeiro.ppd.komms.SERVER_PORT
import org.robsonribeiro.ppd.values.ColorResources
import org.robsonribeiro.ppd.values.Padding
import org.robsonribeiro.ppd.values.empty

@Composable
fun ConnectToServerDialog(
    modifier: Modifier = Modifier,
    initialValue: String = "$SERVER_HOSTNAME:$SERVER_PORT",
    onConnect: (host: String, port: Int) -> Unit,
    onDismissRequest: () -> Unit
) {
    var textFieldHasFocus by remember { mutableStateOf(false) }
    var textFieldValue by remember { mutableStateOf(String.empty) }
    var validationResult by remember { mutableStateOf(NetworkInputValidator.validateServerAddress(String.empty)) }

    val attemptConnection = {
        val selectAddress = textFieldValue.ifEmpty { initialValue }
        val result = NetworkInputValidator.validateServerAddress(selectAddress)
        validationResult = result
        if (result.isValid) {
            onConnect(result.host!!, result.port!!)
        }
    }

    DialogWindow(
        onCloseRequest = onDismissRequest,
        undecorated = true,
        transparent = true,
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
        BentoComponent(modifier.fillMaxSize()) {
            Column {
                WindowDraggableArea {
                    WindowBarComponent(title = "Connect to Server") { onDismissRequest() }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(Padding.large),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Enter Server Address",
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier.padding(bottom = Padding.regular)
                    )

                    OutlinedTextField(
                        value = textFieldValue,
                        onValueChange = {
                            textFieldValue = it
                            validationResult = NetworkInputValidator.validateServerAddress(it)
                        },
                        label = { Text(if (textFieldHasFocus) String.empty else "Default Address on localhost:12345") },
                        isError = !validationResult.isValid && textFieldValue.isNotEmpty(), // Show error only if invalid and not empty
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Uri,
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
                        shape = RoundedCornerShape(Padding.large),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            backgroundColor = ColorResources.White,
                            focusedBorderColor = ColorResources.BlueRoyal,
                            unfocusedBorderColor = ColorResources.BaseBackground,
                            errorBorderColor = ColorResources.RedPantoneDarker
                        )
                    )

                    if (!validationResult.isValid && textFieldValue.isNotEmpty()) {
                        Text(
                            text = validationResult.errorMessage ?: String.empty,
                            color = MaterialTheme.colors.error, // Standard error text color
                            style = MaterialTheme.typography.caption,
                            modifier = Modifier.padding(top = Padding.small)
                                .align(Alignment.Start) // Align error text left
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
                        enabled = validationResult.isValid || textFieldValue.isEmpty(),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = ColorResources.GreenEmerald,
                            contentColor = ColorResources.White
                        )
                    ) {
                        Text("Connect")
                    }
                }
            }
        }
    }
}