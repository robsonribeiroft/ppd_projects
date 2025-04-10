package org.robsonribeiro.ppd.window

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.robsonribeiro.ppd.component.BentoComponent
import org.robsonribeiro.ppd.component.chat.ChatComponent
import org.robsonribeiro.ppd.component.game.GameBoard
import org.robsonribeiro.ppd.component.server.ConnectClientButtonComponent
import org.robsonribeiro.ppd.component.server.StartServerButtonComponent
import org.robsonribeiro.ppd.dialog.ConnectToServerDialog
import org.robsonribeiro.ppd.dialog.InfoDialog
import org.robsonribeiro.ppd.dialog.JoinClientToServerDialog
import org.robsonribeiro.ppd.values.Padding
import org.robsonribeiro.ppd.values.StringResources
import org.robsonribeiro.ppd.viewmodel.ClientViewModel

@Composable
@Preview
fun ClientContentWindow(
    clientViewModel: ClientViewModel,
    onExitApplication: ()->Unit
) {

    val serverState by clientViewModel.serverState.collectAsState()
    val clientState by clientViewModel.clientState.collectAsState()
    val chatIsEnabled by clientViewModel.chatIsEnabled.collectAsState()

    var showConnectToServerDialog by remember { mutableStateOf(false) }
    var showJoinClientToServerDialog by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf<Pair<String, String>?>(null) }

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
                        messages = chatlogs,
                        chatIsEnabled = chatIsEnabled
                    ) { sendMessage ->
                        clientViewModel.sendMessage(sendMessage)
                    }
                }
                Column (
                    Modifier.weight(2f),
                    verticalArrangement = Arrangement.spacedBy(Padding.large)
                ) {
                    BentoComponent(Modifier.weight(8f)) {
                        GameBoard(Modifier.fillMaxSize())
                    }
                    Row(
                        modifier = Modifier.weight(2f),
                        horizontalArrangement = Arrangement.spacedBy(Padding.large)
                    ) {
                        BentoComponent(
                            Modifier.weight(1f)
                        ) {
                            Column (
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(Padding.regular),
                                verticalArrangement = Arrangement.spacedBy(Padding.small)
                            ) {
                                StartServerButtonComponent(
                                    modifier = Modifier.fillMaxWidth(),
                                    serverIsRunning = serverState
                                ) {
                                    if (serverState.first) {
                                        clientViewModel.killServer()
                                    } else {
                                        showConnectToServerDialog = true
                                    }
                                }
                                ConnectClientButtonComponent(
                                    modifier = Modifier.fillMaxWidth(),
                                    clientIsConnected = clientState
                                ) {
                                    if (serverState.first) {
                                        showJoinClientToServerDialog = true
                                    } else {
                                        showInfoDialog =
                                            StringResources.INFO_DIALOG_TITLE_JOIN_WITH_DISABLED_SERVER to StringResources.INFO_DIALOG_DESCRIPTION_JOIN_WITH_DISABLED_SERVER
                                    }
                                }
                            }
                        }

                        BentoComponent(
                            Modifier.weight(2f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(Padding.large)
                            ) {

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

        if (showJoinClientToServerDialog) {
            JoinClientToServerDialog(
                modifier = Modifier,
                onDismissRequest = {
                    showJoinClientToServerDialog = false
                },
                onConnect = { clientId ->
                    clientViewModel.registerClient(clientId)
                    showJoinClientToServerDialog = false
                }
            )
        }

        if (showInfoDialog != null) {
            InfoDialog(
                content = showInfoDialog
            ) {
                showInfoDialog = null
            }
        }
    }
}