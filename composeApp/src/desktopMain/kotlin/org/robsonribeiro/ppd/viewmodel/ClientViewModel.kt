package org.robsonribeiro.ppd.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import org.robsonribeiro.ppd.komms.*
import org.robsonribeiro.ppd.komms.model.ChatMessagePayload
import org.robsonribeiro.ppd.komms.model.KommData
import org.robsonribeiro.ppd.model.ChatMessage
import org.robsonribeiro.ppd.model.TypeMessage

class ClientViewModel(
    startServer: Boolean,
    private val clientId: String
): ViewModel() {

    private val server = KommServerSocket(SERVER_HOSTNAME, SERVER_PORT)
    private var clientSocket: KommClientSocket? = null

    private val _chatlogs = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatlogs = _chatlogs.asStateFlow()


    init {
        if (startServer) {
            server.start()
        }
        Thread.sleep(1000)
        registerClient(clientId)
    }

    fun startServer(host: String, port: Int) {

    }

    fun registerClient(clientId: String) {
        clientSocket = KommClientSocket(SERVER_HOSTNAME, SERVER_PORT)
        clientSocket?.handshake(clientId) { json ->
            val receivedKommData = Json.decodeFromString<KommData>(json)
            when(val payload = receivedKommData.data) {
                is ChatMessagePayload -> {
                    _chatlogs.value += ChatMessage(
                        sender = receivedKommData.clientId,
                        message = payload.message,
                        messageOwner = handleChatTypeMessage(receivedKommData)
                    )
                }
                else -> {

                }
            }
        }
    }

    fun sendMessage(message: String) {
        clientSocket?.sendChatMessage(message)
        _chatlogs.value += ChatMessage(
            sender = clientId!!,
            message = message,
            messageOwner = TypeMessage.OWNER
        )
    }

    private fun handleChatTypeMessage(kommData: KommData) : TypeMessage{
        if (kommData.channel == CHANNEL_CHAT_SYSTEM)
            return TypeMessage.SYSTEM
        return TypeMessage.FOREIGNER
    }
}