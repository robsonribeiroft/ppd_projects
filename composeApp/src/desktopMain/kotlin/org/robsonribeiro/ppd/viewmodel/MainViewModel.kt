package org.robsonribeiro.ppd.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.robsonribeiro.ppd.component.game.logic.*
import org.robsonribeiro.ppd.helper.isServerLive
import org.robsonribeiro.ppd.komms.*
import org.robsonribeiro.ppd.komms.model.*
import org.robsonribeiro.ppd.model.*

class MainViewModel : ViewModel() {

    private var serverSocket: KommServerSocket? = null
    private var clientSocket: KommClientSocket? = null

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
        if (!isServerLive(host, port)) {
            serverSocket = KommServerSocket(host, port, requiredPlayers = 2)
            serverSocket?.start()
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
        serverSocket?.killServer()
    }

    fun registerClient(clientId: String) {
        clientSocket = KommClientSocket(_serverState.value.host!!, _serverState.value.port!!)
        clientSocket?.handshake(clientId) { json ->
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
                    _gameState.checkGameOutcome()
                }
                is ScoreBoardPayload -> {
                    _scoredBoardOpponent.value = payload.opponentScoreBoard
                    _gameState.checkGameOutcome()
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
        clientSocket?.sendChatMessage(message)
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
        clientSocket?.sendPlayerPiece(opponentPiece)
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
            clientSocket?.sendSeegaBoard(board)
            clientSocket?.sendScoreBoard(playerPiece!!, amountPiecesCaptured)
        }
        _gameState.checkGameOutcome()
    }

    fun movePiece(fromRow: Int, fromColumn: Int, toRow: Int, toColumn: Int) {
        _gameState.handleMovePieceOnGridCell(fromRow, fromColumn, toRow, toColumn)
        clientSocket?.sendSeegaBoard(_gameState.value.board)
    }

    fun leaveServer() {
        clientSocket?.sendCommand(Command.LEAVE_SERVER)
    }

    fun showSeegaBoard() {
        _applicationState.value = ApplicationState.READY_TO_PLAY
        clientSocket?.sendScoreBoard(_gameState.value.playerPiece!!, _gameState.value.amountPiecesCaptured)
    }

    fun setGameAction(gameAction: GameAction) {
        _gameState.setGameAction(gameAction)
    }

    fun concede() {
        clientSocket?.sendCommand(COMMAND_QUIT)
    }

    fun newGame() {
        _gameState.value = GameState()
        clientSocket?.sendNewGame()
        allPlayerAreConnected()
    }
}