package org.robsonribeiro.ppd

import org.robsonribeiro.ppd.helper.isServerLive
import org.robsonribeiro.ppd.komms.*

fun main() {
    val server = KommServerSocket(SERVER_HOSTNAME, SERVER_PORT)
    server.start()
    Thread.sleep(1000)

    if (isServerLive(SERVER_HOSTNAME, SERVER_PORT)) {
        println("Server is live and listening on $SERVER_HOSTNAME:$SERVER_PORT.")
    } else {
        println("Server is NOT listening on $SERVER_HOSTNAME:$SERVER_PORT.")
    }


    val clientPrimary = KommClientSocket(SERVER_HOSTNAME, SERVER_PORT)
    clientPrimary.handshake("PRIMAL") { newMessage ->
        println("PRIMARY: $newMessage")
    }

    val clientSecondary = KommClientSocket(SERVER_HOSTNAME, SERVER_PORT)
    clientSecondary.handshake("LEGEND") { newMessage ->
        println("secondary: $newMessage")
    }

    // -- chat --

//    clientPrimary.sendChatMessage("Hello from the $clientPrimary!")
//    clientSecondary.sendChatMessage("Hello from secondary $clientSecondary!")
//    clientSecondary.sendChatMessage("Want have some fun?")
//    clientSecondary.sendChatMessage("lets play a game")
//    clientPrimary.sendChatMessage("Sure, which game?")
//    clientSecondary.sendChatMessage("A mortal one")
//    clientPrimary.sendChatMessage("Nope, bye")
    Thread.sleep(3000)
    clientPrimary.sendCommand("/quit")
    clientSecondary.sendCommand("/quit")

}