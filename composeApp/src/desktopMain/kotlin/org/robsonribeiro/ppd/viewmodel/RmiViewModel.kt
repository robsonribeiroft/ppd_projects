package org.robsonribeiro.ppd.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.robsonribeiro.ppd.component.game.logic.*
import org.robsonribeiro.ppd.helper.isServerLive
import org.robsonribeiro.ppd.helper.isServerRmiLive
import org.robsonribeiro.ppd.komms.*
import org.robsonribeiro.ppd.komms.model.*
import org.robsonribeiro.ppd.komms.rmi.KommClientRmi
import org.robsonribeiro.ppd.komms.rmi.KommServerRmi
import org.robsonribeiro.ppd.model.*

class RmiViewModel : ViewModel() {

    private var server: KommServerRmi? = null
    private var client: KommClientRmi? = null

    private val _applicationState = MutableStateFlow(ApplicationState.WAITING_PLAYERS)
    val applicationState = _applicationState.asStateFlow()

    private val _serverState = MutableStateFlow(ServerState())
    val serverState = _serverState.asStateFlow()

    private val _clientState = MutableStateFlow(ClientState())
    val clientState = _clientState.asStateFlow()

    private val _chatlogs = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatlogs = _chatlogs.asStateFlow()

    private val _gameState = MutableStateFlow(GameState())
    val gameState = _gameState.asStateFlow()

    private val _scoredBoardOpponent = MutableStateFlow(OpponentScoreBoard())
    val scoredBoardOpponent = _scoredBoardOpponent.asStateFlow()

    private val _chatIsEnabled = MutableStateFlow(false)

    fun chatIsEnabled(): Boolean {
        return _serverState.value.isRunning && _clientState.value.isConnected
    }

    fun startServer(host: String, port: Int) {
        if (!isServerRmiLive(host, port)) {
            server = KommServerRmi(host, port)
            server?.start()
        }
        _serverState.value = ServerState(
            host = host,
            port = port,
            isRunning = true
        )
    }

    fun killServer(){
        _serverState.value = ServerState()
        _clientState.value = ClientState()
        server?.shutdown()
    }

    fun registerClient(clientId: String) {
        client = KommClientRmi(_serverState.value.host!!, _serverState.value.port!!)
        client?.handshake(clientId) { json ->
            val receivedKommData = json.decodeJson()
            when(val payload = receivedKommData.data) {
                is ChatMessagePayload -> {
                    _chatlogs.value += ChatMessage(
                        sender = receivedKommData.clientId,
                        message = payload.message,
                        messageOwner = handleChatTypeMessage(receivedKommData)
                    )
                }
                is PlayerPiecePayload -> {
                    println("PlayerPiecePayload: $receivedKommData")
                    _applicationState.value = ApplicationState.GET_PIECE
                    _gameState.setPlayerPiece(payload.piece)
                }
                is PlayersConnectedPayload -> {
                    if (payload.amountOfPlayersConnected == 2) {
                        allPlayerAreConnected()
                    }
                }
                is SeegaBoardPayload -> {
                    _gameState.value = _gameState.value.copy(board = payload.seegaBoard)
                    _gameState.value = _gameState.value.checkAllPiecesArePlaced()
                }
                is ScoreBoardPayload -> {
                    _scoredBoardOpponent.value = payload.opponentScoreBoard
                }
                is GameOutcomePayload -> {
                    _applicationState.value = ApplicationState.READY_TO_PLAY
                    _gameState.value = _gameState.value.copy(
                        gameOutcome = payload.gameOutcome
                    )
                }
                is NewGamePayload -> {
                    _gameState.value = GameState()
                }
                else -> Unit
            }
        }
        _clientState.value = ClientState(
            clientId = clientId,
            isConnected = true
        )
        _chatIsEnabled.value = true
    }

    fun sendMessage(message: String) {
        client?.sendChatMessage(message)
        _chatlogs.value += ChatMessage(
            sender = _clientState.value.clientId!!,
            message = message,
            messageOwner = TypeMessage.OWNER
        )
    }

    private fun allPlayerAreConnected() {
        val playerPiece = randomPlayerPiece()
        val opponentPiece = playerPiece.getOpponentPiece()
        println("The ${_clientState.value.clientId} sorted the $playerPiece")
        client?.sendPlayerPiece(opponentPiece)
        _applicationState.value = ApplicationState.GET_PIECE
        _gameState.setPlayerPiece(playerPiece)
    }

    private fun handleChatTypeMessage(kommData: KommData) : TypeMessage {
        if (kommData.channel == CHANNEL_CHAT_SYSTEM)
            return TypeMessage.SYSTEM
        return TypeMessage.FOREIGNER
    }

    fun onBoardCellClick(row: Int, column: Int) {
        _gameState.handleOnClickGridCell(row, column) { error ->
            _chatlogs.value += ChatMessage(
                sender = _clientState.value.clientId!!,
                message = error,
                messageOwner = TypeMessage.SYSTEM
            )
        }
        with(_gameState.value) {
            client?.sendSeegaBoard(board)
            client?.sendScoreBoard(playerPiece!!, amountPiecesCaptured)
        }
        if (_gameState.value.allPiecesPlaced && _gameState.value.gameAction == GameAction.CAPTURE) {
            val gameOutComeAfterPieceMoved = _gameState.value.board.checkGameOutComeAfterCapture()
            if (gameOutComeAfterPieceMoved != GameOutcome.Ongoing) {
                _gameState.value = _gameState.value.copy(
                    gameOutcome = gameOutComeAfterPieceMoved
                )
                client?.sendGameOutCome(gameOutComeAfterPieceMoved)
            }
        }
    }

    fun movePiece(fromRow: Int, fromColumn: Int, toRow: Int, toColumn: Int) {
        _gameState.handleMovePieceOnGridCell(fromRow, fromColumn, toRow, toColumn)
        client?.sendSeegaBoard(_gameState.value.board)
        val gameOutComeAfterPieceMoved = _gameState.value.board.checkGameOutComeAfterMove()
        if (gameOutComeAfterPieceMoved != GameOutcome.Ongoing) {
            _gameState.value = _gameState.value.copy(
                gameOutcome = gameOutComeAfterPieceMoved
            )
            client?.sendGameOutCome(gameOutComeAfterPieceMoved)
        }
    }

    fun leaveServer() {
        client?.sendGameOutCome(GameOutcome.OpponentConcede)
        client?.sendCommand(Command.LEAVE_SERVER)
    }

    fun showSeegaBoard() {
        _applicationState.value = ApplicationState.READY_TO_PLAY
        client?.sendScoreBoard(_gameState.value.playerPiece!!, _gameState.value.amountPiecesCaptured)
    }

    fun setGameAction(gameAction: GameAction) {
        _gameState.setGameAction(gameAction)
    }

    fun concede() {
        _gameState.value = _gameState.value.copy(
            gameOutcome = GameOutcome.Defeat
        )
        client?.sendGameOutCome(GameOutcome.OpponentConcede)
    }

    fun newGame() {
        _gameState.value = GameState()
        client?.sendNewGame()
        allPlayerAreConnected()
    }
}