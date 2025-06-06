package org.robsonribeiro.ppd.komms

import kotlinx.coroutines.Runnable
import kotlinx.serialization.json.Json
import org.robsonribeiro.ppd.component.game.logic.GameOutcome
import org.robsonribeiro.ppd.component.game.logic.PlayerPiece
import org.robsonribeiro.ppd.component.game.logic.SeegaBoard
import org.robsonribeiro.ppd.komms.model.*
import org.robsonribeiro.ppd.model.OpponentScoreBoard
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

object Command{
    const val LEAVE_SERVER = "/quit"
}

class KommClientSocket(
    private val serverAddress: String = SERVER_HOSTNAME,
    private val port: Int = SERVER_PORT
) : Runnable {

    private lateinit var socket: Socket
    private lateinit var input: BufferedReader
    private lateinit var output: PrintWriter
    private lateinit var messageCallback: (String) -> Unit
    private var clientId: String? = null

    init {
        connect()
    }

    private fun connect() {
        try {
            socket = Socket(serverAddress, port)
            input = BufferedReader(InputStreamReader(socket.getInputStream()))
            output = PrintWriter(socket.getOutputStream(), true)
        } catch (e: IOException) {
            println("Could not connet to server: ${e.message}")
            e.printStackTrace()
        }
    }

    fun handshake(clientId: String, onMessageReceived: (String) -> Unit) {
        this.clientId = clientId
        this.messageCallback = onMessageReceived
        Thread(this@KommClientSocket).start()
        output.println(clientId)
    }

    fun sendChatMessage(message: String) {
        val dataToSend = KommData(
            clientId = clientId!!,
            channel = CHANNEL_CHAT,
            data = ChatMessagePayload(message = message)
        )
        val jsonString = Json.encodeToString(dataToSend)
        sendJson(jsonString)
    }

    fun sendCommand(command: String, args: List<String> = emptyList()) {
        val actualCommand = if (command.startsWith("/")) command else "/$command"
        val dataToSend = KommData(
            clientId = clientId!!,
            channel = CHANNEL_COMMAND,
            data = CommandPayload(command = actualCommand)
        )
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

    private fun sendJson(json: String){
        output.println(json)
    }

    private fun listenForMessages(onMessageReceived: (String) -> Unit) {
        try {
            lateinit var message: String
            while (input.readLine().also { message = it } != null) {
                onMessageReceived(message)
            }
        } catch (e: IOException) {
            println("Connection to server lost: ${e.message}")
            e.printStackTrace()
        } catch (e: Exception) {
            println("Unexpected error on listenForMessages: ${e.message}")
            e.printStackTrace()
        }
    }

    override fun run() = listenForMessages(messageCallback)

}

