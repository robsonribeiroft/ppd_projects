package org.robsonribeiro.ppd.komms.rmi

import org.robsonribeiro.ppd.component.game.logic.GameOutcome
import org.robsonribeiro.ppd.component.game.logic.PlayerPiece
import org.robsonribeiro.ppd.component.game.logic.SeegaBoard
import org.robsonribeiro.ppd.komms.CHANNEL_CHAT
import org.robsonribeiro.ppd.komms.CHANNEL_COMMAND
import org.robsonribeiro.ppd.komms.CHANNEL_GAME
import org.robsonribeiro.ppd.komms.COMMAND_QUIT
import org.robsonribeiro.ppd.komms.model.*
import org.robsonribeiro.ppd.model.OpponentScoreBoard
import java.rmi.RemoteException
import java.rmi.server.UnicastRemoteObject

typealias ChatMessageHandler = (String, String, String) -> Unit
typealias PlayerPieceHandler = (PlayerPiece) -> Unit
typealias PlayersConnectedHandler = (Int) -> Unit
typealias SeegaBoardHandler = (SeegaBoard) -> Unit
typealias ScoreBoardHandler = (OpponentScoreBoard) -> Unit
typealias GameOutcomeHandler = (GameOutcome) -> Unit
typealias NewGameHandler = () -> Unit

class KommClient(
    override val hostname: String,
    override val port: Int,
    override val serverName: String = KommServerRmi.serverName
) : IKommRmiConfig {

    lateinit var clientId: String
    private lateinit var server: IKommServer
    private lateinit var clientHandler: IKommClient

    fun handshake(clientId: String, clientHandler: IKommClient){
        this.clientId = clientId
        this.clientHandler = clientHandler
        connect(clientId)
    }

    private fun connect(clientId: String) {
        try {
            server = java.rmi.Naming.lookup(serviceAddress) as IKommServer
            server.registerClient(clientId, clientHandler)
            Runtime.getRuntime().addShutdownHook(Thread {
                this.disconnect()
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun disconnect() {
        try {
            server.unregisterClient(clientId)
        } catch (e: RemoteException) {
            e.printStackTrace()
        } finally {
            try {
                UnicastRemoteObject.unexportObject(clientHandler, true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun sendChatMessage(message: String) {
        val dataToSend = KommData(
            clientId = clientId,
            channel = CHANNEL_CHAT,
            data = ChatMessagePayload(message = message)
        )
        server.sendChatMessagePayload(dataToSend)
    }

    fun sendCommand(command: String, args: List<String> = emptyList()) {
        val actualCommand = if (command.startsWith("/")) command else "/$command"
        if (actualCommand == COMMAND_QUIT) {
            disconnect()
            return
        }
        val kommData = KommData(
            clientId = clientId,
            channel = CHANNEL_COMMAND,
            data = CommandPayload(command = actualCommand)
        )
        server.sendCommandPayload(kommData)
    }

    fun sendPlayerPiece(playerPiece: PlayerPiece) {
        val kommData = KommData(
            clientId = clientId,
            channel = CHANNEL_GAME,
            data = PlayerPiecePayload(piece = playerPiece)
        )
        server.sendPlayerPiecePayload(kommData)
//        sendJson(pieceKommData.toJson())
    }

    fun sendGameOutCome(gameOutcome: GameOutcome){
        val kommData = KommData(
            clientId = clientId,
            channel = CHANNEL_GAME,
            data = GameOutcomePayload(gameOutcome)
        )
        server.sendGameOutcomePayload(kommData)
//        sendJson(kommData.toJson())
    }

    fun sendNewGame(){
        val kommData = KommData(
            clientId = clientId,
            channel = CHANNEL_GAME,
            data = NewGamePayload
        )
        server.sendNewGamePayload(kommData)
//        sendJson(kommData.toJson())
    }

    fun sendSeegaBoard(seegaBoard: SeegaBoard) {
        val kommData = KommData(
            clientId = clientId,
            channel = CHANNEL_GAME,
            data = SeegaBoardPayload(seegaBoard)
        )
        server.sendSeegaBoardPayload(kommData)
//        sendJson(kommData.toJson())
    }

    fun sendScoreBoard(piece: PlayerPiece, amountCapturedPieces: Int) {
        val kommData = KommData(
            clientId = clientId,
            channel = CHANNEL_GAME,
            data = ScoreBoardPayload(OpponentScoreBoard(clientId, piece, amountCapturedPieces))
        )
        server.sendScoreBoardPayload(kommData)
    }



    class ClientCallbackHandler(
        private val onChat: ChatMessageHandler,
        private val onPiece: PlayerPieceHandler,
        private val onPlayersConnected: PlayersConnectedHandler,
        private val onBoard: SeegaBoardHandler,
        private val onScore: ScoreBoardHandler,
        private val onOutcome: GameOutcomeHandler,
        private val onNewGame: NewGameHandler
    ): UnicastRemoteObject(), IKommClient {

        @Throws(RemoteException::class)
        override fun onChatMessagePayload(payload: KommData) {
            payload<ChatMessagePayload> { (clientId, channel, data) ->
                onChat(clientId, channel, data.message)
            }
        }

        @Throws(RemoteException::class)
        override fun onPlayerPiecePayload(payload: KommData) {
            payload<PlayerPiecePayload> { (_, _, data) ->
                onPiece(data.piece)
            }
        }

        @Throws(RemoteException::class)
        override fun onPlayersConnectedPayload(payload: KommData) {
            payload<PlayersConnectedPayload> { (_, _, data) ->
                onPlayersConnected(data.amountOfPlayersConnected)
            }
        }

        @Throws(RemoteException::class)
        override fun onSeegaBoardPayload(payload: KommData) {
            payload<SeegaBoardPayload> { (_, _, data) ->
                onBoard(data.seegaBoard)
            }
        }

        @Throws(RemoteException::class)
        override fun onScoreBoardPayload(payload: KommData) {
            payload<ScoreBoardPayload> { (_, _, data) ->
                onScore(data.opponentScoreBoard)
            }
        }

        @Throws(RemoteException::class)
        override fun onGameOutcomePayload(payload: KommData) {
            payload<GameOutcomePayload> { (_, _, data) ->
                onOutcome(data.gameOutcome)
            }
        }

        @Throws(RemoteException::class)
        override fun onNewGamePayload(payload: KommData) {
            onNewGame()
        }
    }

}