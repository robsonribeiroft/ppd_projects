package org.robsonribeiro.ppd

import org.robsonribeiro.ppd.helper.isServerRmiLive
import org.robsonribeiro.ppd.komms.CHANNEL_CHAT
import org.robsonribeiro.ppd.komms.SERVER_HOSTNAME
import org.robsonribeiro.ppd.komms.SERVER_PORT
import org.robsonribeiro.ppd.komms.model.ChatMessagePayload
import org.robsonribeiro.ppd.komms.model.KommData
import org.robsonribeiro.ppd.komms.model.decodeJson
import org.robsonribeiro.ppd.komms.model.toJson
import org.robsonribeiro.ppd.komms.rmi.KommClient
import org.robsonribeiro.ppd.komms.rmi.KommClientRmi
import org.robsonribeiro.ppd.komms.rmi.KommServer
import org.robsonribeiro.ppd.komms.rmi.KommServerRmi

fun main() {
    val server = KommServer(SERVER_HOSTNAME, SERVER_PORT)
    server.start()

    if (isServerRmiLive(SERVER_HOSTNAME, SERVER_PORT, KommServerRmi.serverName)) {
        println("Server is live and listening on $SERVER_HOSTNAME:$SERVER_PORT/${KommServerRmi.serverName}.")
    } else {
        println("Server was not initialized!")
    }

    val clientOne = KommClient(
        hostname = SERVER_HOSTNAME,
        port = SERVER_PORT,
        serverName = KommServerRmi.serverName,
    )
    clientOne.handshake("clientOne", KommClient.ClientCallbackHandler(
        onChat = { clientId, channel, message ->
            println("[${clientId}] > $message")
        },
        onPiece = {  },
        onPlayersConnected = {  },
        onBoard = {  },
        onScore = {  },
        onOutcome = {  },
        onNewGame = {  }
    ))



    val clientTwo = KommClient(
        hostname = SERVER_HOSTNAME,
        port = SERVER_PORT,
        serverName = KommServerRmi.serverName
    )
    clientTwo.handshake("clientTwo", KommClient.ClientCallbackHandler(
        onChat = { clientId, channel, message ->
            println("[${clientId}] > $message")
        },
        onPiece = {  },
        onPlayersConnected = {  },
        onBoard = {  },
        onScore = {  },
        onOutcome = {  },
        onNewGame = {  }
    ))

    clientOne.sendChatMessage("Hi 2")
    clientTwo.sendChatMessage("Hey 1")
    clientOne.disconnect()
    clientTwo.disconnect()

}