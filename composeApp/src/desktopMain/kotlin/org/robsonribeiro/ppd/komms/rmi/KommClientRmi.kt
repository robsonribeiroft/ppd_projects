package org.robsonribeiro.ppd.komms.rmi

import kotlinx.serialization.json.Json
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

class KommClientRmi(
    override val hostname: String,
    override val port: Int,
    override val serverName: String = KommServerRmi.serverName
) : UnicastRemoteObject(), KommClientRmiInterface, KommRmiConfigInterface {

    var clientId: String? = null
    private var onMessageReceived: (String)->Unit = {}
    private lateinit var server: KommServerRmiInterface

    fun handshake(clientId: String,  onMessageReceived: (String)->Unit){
        this.clientId = clientId
        this.onMessageReceived = onMessageReceived
        connect(clientId)
    }

    private fun connect(clientId: String) {
        try {
            server = java.rmi.Naming.lookup(serviceAddress) as KommServerRmiInterface
            server.registerClient(clientId, this)
            Runtime.getRuntime().addShutdownHook(Thread {
                this.disconnect()
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun disconnect() {
        try {
            server.unregisterClient(clientId!!)
        } catch (e: RemoteException) {
            e.printStackTrace()
        } finally {
            try {
                unexportObject(this, true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun sendMessage(message: String) {
        try {
            server.broadcastMessage(clientId!!, message)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun receiveMessage(message: String) {
        onMessageReceived(message)
    }

    fun sendChatMessage(message: String) {
        val kommData = KommData(
            clientId = clientId!!,
            channel = CHANNEL_CHAT,
            data = ChatMessagePayload(message = message)
        )
        //server.sendChatMessagePayload(kommData)
    }

    fun sendCommand(command: String, args: List<String> = emptyList()) {
        val actualCommand = if (command.startsWith("/")) command else "/$command"
        if (actualCommand == COMMAND_QUIT) {
            disconnect()
            return
        }
        val dataToSend = KommData(
            clientId = clientId!!,
            channel = CHANNEL_COMMAND,
            data = CommandPayload(command = actualCommand)
        )
        //server.send
        sendJson(dataToSend.toJson())
    }

    fun sendPlayerPiece(playerPiece: PlayerPiece) {
        val pieceKommData = KommData(
            clientId = clientId!!,
            channel = CHANNEL_GAME,
            data = PlayerPiecePayload(piece = playerPiece)
        )
        sendJson(pieceKommData.toJson())
    }

    fun sendGameOutCome(gameOutcome: GameOutcome){
        val kommData = KommData(
            clientId = clientId!!,
            channel = CHANNEL_GAME,
            data = GameOutcomePayload(gameOutcome)
        )
        sendJson(kommData.toJson())
    }

    fun sendNewGame(){
        val kommData = KommData(
            clientId = clientId!!,
            channel = CHANNEL_GAME,
            data = NewGamePayload
        )
        sendJson(kommData.toJson())
    }

    fun sendSeegaBoard(seegaBoard: SeegaBoard) {
        val kommData = KommData(
            clientId = clientId!!,
            channel = CHANNEL_GAME,
            data = SeegaBoardPayload(seegaBoard)
        )
        sendJson(kommData.toJson())
    }

    fun sendScoreBoard(piece: PlayerPiece, amountCapturedPieces: Int) {
        val kommData = KommData(
            clientId = clientId!!,
            channel = CHANNEL_GAME,
            data = ScoreBoardPayload(OpponentScoreBoard(clientId!!, piece, amountCapturedPieces))
        )
        sendJson(kommData.toJson())
    }

    private fun sendJson(json: String) {
        server.broadcastMessage(senderId = clientId!!, json)
    }

}