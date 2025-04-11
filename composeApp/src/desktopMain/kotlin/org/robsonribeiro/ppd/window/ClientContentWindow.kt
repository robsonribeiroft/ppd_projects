package org.robsonribeiro.ppd.window

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.robsonribeiro.ppd.component.BentoComponent
import org.robsonribeiro.ppd.component.chat.ChatComponent
import org.robsonribeiro.ppd.component.game.SeegaBoardComponent
import org.robsonribeiro.ppd.component.server.ConnectClientButtonComponent
import org.robsonribeiro.ppd.component.server.StartServerButtonComponent
import org.robsonribeiro.ppd.dialog.ConfirmationDialog
import org.robsonribeiro.ppd.dialog.ConnectToServerDialog
import org.robsonribeiro.ppd.dialog.InfoDialog
import org.robsonribeiro.ppd.dialog.JoinClientToServerDialog
import org.robsonribeiro.ppd.model.ConfirmationDialogInfo
import org.robsonribeiro.ppd.values.Padding
import org.robsonribeiro.ppd.values.StringResources
import org.robsonribeiro.ppd.viewmodel.MainViewModel

@Composable
@Preview
fun ClientContentWindow(
    viewModel: MainViewModel
) {

    val serverState by viewModel.serverState.collectAsState()
    val clientState by viewModel.clientState.collectAsState()
    val chatlogs by viewModel.chatlogs.collectAsState()
    val gameState by viewModel.gameState.collectAsState()

    var showConnectToServerDialog by remember { mutableStateOf(false) }
    var showJoinClientToServerDialog by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf<Pair<String, String>?>(null) }
    var showConfirmationDialog by remember { mutableStateOf<ConfirmationDialogInfo?>(null) }

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
                        chatIsEnabled = viewModel.chatIsEnabled(),
                        sendMessage = viewModel::sendMessage
                    )
                }
                Column (
                    Modifier.weight(2f),
                    verticalArrangement = Arrangement.spacedBy(Padding.large)
                ) {
                    BentoComponent(
                        Modifier
                            .weight(8f)
                            .padding(Padding.regular)
                    ) {
                        SeegaBoardComponent(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(Padding.large),
                            gameState = gameState,
                            onCellClick = viewModel::onBoardCellClick
                        )
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
                                    .fillMaxSize()
                                    .padding(Padding.regular),
                                verticalArrangement = Arrangement.spacedBy(Padding.regular)
                            ) {
                                StartServerButtonComponent(
                                    modifier = Modifier.fillMaxWidth(),
                                    serverState = serverState
                                ) {
                                    if (serverState.isRunning) {
                                        showConfirmationDialog = ConfirmationDialogInfo(
                                            title = "Kill Server",
                                            description = "Are you sure that you want to finish server process?",
                                        ) {
                                            viewModel.killServer()
                                        }
                                    } else {
                                        showConnectToServerDialog = true
                                    }
                                }
                                ConnectClientButtonComponent(
                                    modifier = Modifier.fillMaxWidth(),
                                    clientState = clientState
                                ) {
                                    if (serverState.isRunning) {
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
                    viewModel.startServer(host, port)
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
                    showJoinClientToServerDialog = false
                    viewModel.registerClient(clientId)
                }
            )
        }

        showInfoDialog?.let {
            InfoDialog(
                content = it
            ) {
                showInfoDialog = null
            }
        }

        showConfirmationDialog?.let {
            ConfirmationDialog(
                content = it
            ) {
                showConfirmationDialog = null
            }
        }
    }
}