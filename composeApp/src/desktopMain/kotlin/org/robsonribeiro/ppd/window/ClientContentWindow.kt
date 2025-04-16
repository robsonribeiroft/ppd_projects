package org.robsonribeiro.ppd.window

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.robsonribeiro.ppd.component.BentoComponent
import org.robsonribeiro.ppd.component.chat.ChatComponent
import org.robsonribeiro.ppd.component.game.*
import org.robsonribeiro.ppd.component.game.logic.ApplicationState
import org.robsonribeiro.ppd.component.game.logic.GameAction
import org.robsonribeiro.ppd.component.game.logic.GameOutcome
import org.robsonribeiro.ppd.component.game.logic.isReadyToPlay
import org.robsonribeiro.ppd.component.server.ConnectClientButtonComponent
import org.robsonribeiro.ppd.component.server.StartServerButtonComponent
import org.robsonribeiro.ppd.dialog.ConfirmationDialog
import org.robsonribeiro.ppd.dialog.ConnectToServerDialog
import org.robsonribeiro.ppd.dialog.InfoDialog
import org.robsonribeiro.ppd.dialog.JoinClientToServerDialog
import org.robsonribeiro.ppd.model.ConfirmationDialogInfo
import org.robsonribeiro.ppd.model.ScoreBoardInfo
import org.robsonribeiro.ppd.values.Padding
import org.robsonribeiro.ppd.values.StringResources
import org.robsonribeiro.ppd.viewmodel.MainViewModel

@Composable
@Preview
fun ClientContentWindow(
    viewModel: MainViewModel
) {

    val applicationState by viewModel.applicationState.collectAsState()
    val serverState by viewModel.serverState.collectAsState()
    val clientState by viewModel.clientState.collectAsState()
    val chatlogs by viewModel.chatlogs.collectAsState()
    val gameState by viewModel.gameState.collectAsState()
    val scoredBoardOpponent by viewModel.scoredBoardOpponent.collectAsState()

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
                    ) {
                        when (applicationState) {
                            ApplicationState.WAITING_PLAYERS -> {
                                WaitingPlayersComponent(
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            ApplicationState.GET_PIECE -> {
                                SortPieceComponent(
                                    modifier = Modifier,
                                    gameState.playerPiece
                                ) {
                                    viewModel.showSeegaBoard()
                                }
                            }
                            ApplicationState.READY_TO_PLAY -> {
                                SeegaBoardComponent(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(Padding.large),
                                    gameState = gameState,
                                    onCellClick = viewModel::onBoardCellClick,
                                    onMovePiece = viewModel::movePiece
                                )
                                if (gameState.gameOutcome != GameOutcome.Ongoing) {
                                    GameResultInfoComponent(
                                        modifier = Modifier,
                                        gameState = gameState
                                    ) {
                                        viewModel.newGame()
                                    }
                                }
                            }
                        }
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
                                    modifier = Modifier.weight(1f).fillMaxWidth(),
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
                                    modifier = Modifier.weight(1f).fillMaxWidth(),
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
                                    .fillMaxSize()
                                    .padding(Padding.small),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(Padding.small)
                            ) {
                                if (applicationState.isReadyToPlay()) {
                                    ScoreBoardComponent(
                                        modifier = Modifier.weight(1f),
                                        scoreBoardInfo = ScoreBoardInfo(
                                            clientId = clientState.clientId,
                                            playerPiece = gameState.playerPiece,
                                            capturedPiecesAmount = gameState.amountPiecesCaptured,
                                            opponentPlayerPiece = scoredBoardOpponent.opponentPiece,
                                            opponentClientId = scoredBoardOpponent.opponentClientId,
                                            opponentCapturedPiecesAmount = scoredBoardOpponent.opponentAmountCaptured
                                        )
                                    )
                                    GameActionSelectionComponent(
                                        modifier = Modifier.weight(1f),
                                        currentAction = gameState.gameAction,
                                        concede = {
                                            showConfirmationDialog = ConfirmationDialogInfo(
                                                title = "Concede",
                                                description = "Confirm this action will make your opponent win.\nAre you sure?"
                                            ) {
                                                viewModel.concede()
                                            }
                                        }
                                    ) { gameAction ->
                                        viewModel.setGameAction(gameAction)
                                    }
                                } else {
                                    Text(
                                        modifier = Modifier.fillMaxSize(),
                                        text = "Waiting for the start of the game",
                                        textAlign = TextAlign.Center
                                    )
                                }
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