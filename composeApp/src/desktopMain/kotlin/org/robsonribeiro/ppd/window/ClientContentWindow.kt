package org.robsonribeiro.ppd.window

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.robsonribeiro.ppd.component.BentoComponent
import org.robsonribeiro.ppd.component.chat.ChatComponent
import org.robsonribeiro.ppd.component.game.GameBoard
import org.robsonribeiro.ppd.component.server.ServerButtonComponent
import org.robsonribeiro.ppd.dialog.ConnectToServerDialog
import org.robsonribeiro.ppd.model.MOCK_CHAT_MESSAGE
import org.robsonribeiro.ppd.values.Padding
import org.robsonribeiro.ppd.viewmodel.ClientViewModel

@Composable
@Preview
fun ClientContentWindow(
    clientViewModel: ClientViewModel,
    onExitApplication: ()->Unit
) {

    var showConnectToServerDialog by remember { mutableStateOf(false) }

    val chatlogs by clientViewModel.chatlogs.collectAsState()

    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = Padding.large,
                        vertical = Padding.large
                    ),
                horizontalArrangement = Arrangement.spacedBy(Padding.large)
            ) {
                BentoComponent(Modifier.weight(1f)){
                    ChatComponent(
                        Modifier,
                        messages = chatlogs
                    ) { sendMessage ->
                        clientViewModel.sendMessage(sendMessage)
                    }
                }
                Column (
                    Modifier.weight(2f),
                    verticalArrangement = Arrangement.spacedBy(Padding.large)
                ) {
                    BentoComponent(Modifier.weight(1f)) {
                        GameBoard(Modifier.fillMaxSize())
                    }
                    BentoComponent(
                        Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Padding.large)
                        ) {
                            ServerButtonComponent {
                                showConnectToServerDialog = true
                            }
                        }
                    }
                }
            }
        }


        if (showConnectToServerDialog) {
            ConnectToServerDialog(
                modifier = Modifier,
                onDismissRequest = {
                    showConnectToServerDialog = false
                },
                onConnect = { host, port ->
                    clientViewModel.startServer(host, port)
                    showConnectToServerDialog = false
                }
            )
        }
    }
}