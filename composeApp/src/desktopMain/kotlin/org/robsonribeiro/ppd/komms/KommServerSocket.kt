package org.robsonribeiro.ppd.komms

import kotlinx.coroutines.Runnable
import kotlinx.serialization.SerializationException
import org.robsonribeiro.ppd.komms.model.*
import java.io.*
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors

class KommServerSocket(
    private val host: String,
    private val port: Int,
    private val requiredPlayers: Int
): Thread() {

    private lateinit var serverSocket: ServerSocket
    private val clients = CopyOnWriteArrayList<ClientHandler>()
    private val executor = Executors.newCachedThreadPool()

    override fun run() {
        try {
            serverSocket = ServerSocket(port, 10, InetAddress.getByName(host))
            println("ApplicationServer started on $host:$port")

            while (true) {
                val socket = serverSocket.accept()
                val clientHandler = ClientHandler(socket = socket, server = this)

                clients.add(clientHandler)
                executor.execute(clientHandler)
            }
        } catch (e: IOException) {
            println("Error starting the server: ${e.message}")
            e.printStackTrace()
        }
    }

    fun broadcastMessage(senderId: String, message: String) {
        clients.forEach { client ->
            if (client.clientId != senderId) {
                client.output.println(message)
            }
        }
    }

    fun removeClient(clientHandler: ClientHandler) {
        clients.remove(clientHandler)
        if (clients.isEmpty()) {
            println("ApplicationServer has stopped")
            killServer()
        }
    }

    fun killServer() {
        executor.shutdownNow()
        serverSocket.close()
    }


    inner class ClientHandler(
        private val socket: Socket,
        private val server: KommServerSocket
    ): Runnable {
        private val input = BufferedReader(InputStreamReader(socket.getInputStream()))
        val output = PrintWriter(OutputStreamWriter(socket.getOutputStream()), true)
        var clientId: String? = null

        override fun run() {
            try {
                clientId = input.readLine()
                sendClientWelcomeMessage(clientId!!)
                notifyPartyClientHasJoinedTheServer(clientId!!)
                sendClientAmountPlayer(clientId!!, server.clients.size)

                var message: String?
                while (true) {
                    message = input.readLine()
                    val receivedKommData = message.decodeJson()
                    when(val payload = receivedKommData.data) {
                        is CommandPayload -> {
                            if (payload.command == COMMAND_QUIT) {
                                notifyPartyClientHasLeftTheServer(clientId!!)
                                break
                            }
                        }
                        else -> {
                            server.broadcastMessage(senderId = clientId!!, message = message)
                        }
                    }
                }

            } catch (e: IOException) {
                println("IO Error handling client ${clientId ?: "UNKNOWN"}:")
                e.printStackTrace()
            } catch (e: SerializationException) {
                println("Server failed to deserialize JSON from $clientId: ${e.message}")
                e.printStackTrace()
            }
            catch (e: Exception) {
                println("Unexpected Error handling client ${clientId ?: "UNKNOWN"}:")
                e.printStackTrace()
            } finally {
                socket.close()
                removeClient(clientHandler = this)
            }

        }

        private fun sendClientAmountPlayer(clientId: String, numberOfClients: Int) {
            val kommData = KommData(clientId, CHANNEL_GAME, PlayersConnectedPayload(numberOfClients))
            output.println(kommData.toJson())
        }

        private fun sendClientWelcomeMessage(clientId: String) {
            val kommData = KommData(clientId, CHANNEL_CHAT_SYSTEM, ChatMessagePayload("Welcome $clientId!"))
            output.println(kommData.toJson())
        }

        private fun notifyPartyClientHasLeftTheServer(clientId: String) {
            val message = "$clientId has left the server."
            println(message)
            val kommData = KommData(
                clientId,
                CHANNEL_CHAT_SYSTEM,
                ChatMessagePayload(message)
            )
            broadcastMessage(senderId = clientId, message = kommData.toJson())
        }

        private fun notifyPartyClientHasJoinedTheServer(clientId: String) {
            val sayHelloPayload = ChatMessagePayload("say hello to $clientId who joined the server")
            val sayHelloKommData = KommData(clientId, CHANNEL_CHAT_SYSTEM, sayHelloPayload)
            broadcastMessage(senderId = clientId, sayHelloKommData.toJson())
        }
    }
}