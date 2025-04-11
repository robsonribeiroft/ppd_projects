package org.robsonribeiro.ppd.komms

import kotlinx.coroutines.Runnable
import kotlinx.serialization.json.Json
import org.robsonribeiro.ppd.komms.model.ChatMessagePayload
import org.robsonribeiro.ppd.komms.model.CommandPayload
import org.robsonribeiro.ppd.komms.model.KommData
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
        val jsonString = Json.encodeToString(dataToSend)
        sendJson(jsonString)
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

