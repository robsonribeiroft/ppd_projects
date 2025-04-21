package org.robsonribeiro.ppd

import org.robsonribeiro.ppd.helper.isServerRmiLive
import org.robsonribeiro.ppd.komms.CHANNEL_CHAT
import org.robsonribeiro.ppd.komms.SERVER_HOSTNAME
import org.robsonribeiro.ppd.komms.SERVER_PORT
import org.robsonribeiro.ppd.komms.model.ChatMessagePayload
import org.robsonribeiro.ppd.komms.model.KommData
import org.robsonribeiro.ppd.komms.model.decodeJson
import org.robsonribeiro.ppd.komms.model.toJson
import org.robsonribeiro.ppd.komms.rmi.KommClientRmi
import org.robsonribeiro.ppd.komms.rmi.KommServerRmi

fun main() {
    val server = KommServerRmi(SERVER_HOSTNAME, SERVER_PORT)
    server.start()

    if (isServerRmiLive(SERVER_HOSTNAME, SERVER_PORT, KommServerRmi.serverName)) {
        println("Server is live and listening on $SERVER_HOSTNAME:$SERVER_PORT/${KommServerRmi.serverName}.")
    } else {
        println("Server was not initialized!")
    }

    val clientOne = KommClientRmi(
        hostname = SERVER_HOSTNAME,
        port = SERVER_PORT,
        serverName = KommServerRmi.serverName,
    )
    clientOne.handshake("clientOne") { json ->
        val payload = json.decodeJson()
        println("[${payload.clientId}] > ${(payload.data as ChatMessagePayload).message}")
    }

    val clientTwo = KommClientRmi(
        hostname = SERVER_HOSTNAME,
        port = SERVER_PORT,
        serverName = KommServerRmi.serverName
    )
    clientTwo.handshake("clientTwo") { json ->
        val payload = json.decodeJson()
        println("[${payload.clientId}] > ${(payload.data as ChatMessagePayload).message}")
    }

    clientOne.sendMessage(KommData("clientOne", CHANNEL_CHAT, ChatMessagePayload("Hi 2")).toJson())
    clientTwo.sendMessage(KommData("clientTwo", CHANNEL_CHAT, ChatMessagePayload("Hey 1")).toJson())
    clientOne.disconnect()
    clientTwo.disconnect()

}