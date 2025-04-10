package org.robsonribeiro.ppd.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.robsonribeiro.ppd.komms.*
import org.robsonribeiro.ppd.komms.model.ChatMessagePayload
import org.robsonribeiro.ppd.komms.model.KommData
import org.robsonribeiro.ppd.komms.model.decodeJson
import org.robsonribeiro.ppd.model.ChatMessage
import org.robsonribeiro.ppd.model.TypeMessage

class ClientViewModel : ViewModel() {

    private var serverSocket: KommServerSocket? = null
    private var clientSocket: KommClientSocket? = null

    private var host: String? = null
    private var port: Int? = null
    private var clientId: String? = null

    private val _serverState = MutableStateFlow<Pair<Boolean, String?>>(false to null)
    val serverState = _serverState.asStateFlow()

    private val _clientState = MutableStateFlow<Pair<Boolean, String?>>(false to null)
    val clientState = _clientState.asStateFlow()

    private val _chatlogs = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatlogs = _chatlogs.asStateFlow()

    private val _chatIsEnabled = MutableStateFlow(false)
    val chatIsEnabled = _chatIsEnabled.asStateFlow()

    fun startServer(host: String, port: Int) {
        serverSocket = KommServerSocket(host, port)
        serverSocket?.start()
        _serverState.value = true to "$host:$port"
        this.host = host
        this.port = port
    }

    fun killServer(){
        _serverState.value = false to null
        _clientState.value = false to null
        serverSocket?.killServer()
    }

    fun registerClient(clientId: String) {
        clientSocket = KommClientSocket(host!!, port!!)
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
                else -> {

                }
            }
        }
        _clientState.value = true to clientId
        _chatIsEnabled.value = true
        this.clientId = clientId
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