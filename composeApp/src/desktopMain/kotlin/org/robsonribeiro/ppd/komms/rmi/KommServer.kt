package org.robsonribeiro.ppd.komms.rmi

import org.robsonribeiro.ppd.komms.CHANNEL_CHAT_SYSTEM
import org.robsonribeiro.ppd.komms.CHANNEL_GAME
import org.robsonribeiro.ppd.komms.COMMAND_QUIT
import org.robsonribeiro.ppd.komms.model.ChatMessagePayload
import org.robsonribeiro.ppd.komms.model.CommandPayload
import org.robsonribeiro.ppd.komms.model.KommData
import org.robsonribeiro.ppd.komms.model.PlayersConnectedPayload
import java.rmi.RemoteException
import java.rmi.registry.LocateRegistry
import java.rmi.registry.Registry
import java.rmi.server.UnicastRemoteObject
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.thread
import kotlin.system.exitProcess

class KommServer(
    override val hostname: String,
    override val port: Int,
    override val serverName: String = KommServerRmi.serverName,
) : UnicastRemoteObject(), IKommServer, IKommRmiConfig{

    private val clients = ConcurrentHashMap<String, IKommClient>()
    private lateinit var registry: Registry

    override fun sendChatMessagePayload(payload: KommData) {
        safeBroadcast(payload.clientId) {
            onChatMessagePayload(payload)
        }
    }

    override fun sendPlayerPiecePayload(payload: KommData) {
        safeBroadcast(payload.clientId) {
            onPlayerPiecePayload(payload)
        }
    }

    override fun sendPlayersConnectedPayload(payload: KommData) {
        safeBroadcast(payload.clientId) {
            onPlayersConnectedPayload(payload)
        }
    }

    override fun sendSeegaBoardPayload(payload: KommData) {
        safeBroadcast(payload.clientId) {
            onSeegaBoardPayload(payload)
        }
    }

    override fun sendScoreBoardPayload(payload: KommData) {
        safeBroadcast(payload.clientId) {
            onScoreBoardPayload(payload)
        }
    }

    override fun sendGameOutcomePayload(payload: KommData) {
        safeBroadcast(payload.clientId) {
            onGameOutcomePayload(payload)
        }
    }

    override fun sendNewGamePayload(payload: KommData) {
        safeBroadcast(payload.clientId) {
            onNewGamePayload(payload)
        }
    }

    override fun sendCommandPayload(payload: KommData) {
        val command = payload.data as CommandPayload
        if (command.command == COMMAND_QUIT) {
            unregisterClient(payload.clientId)
        }
    }

    @Throws(RemoteException::class, IllegalArgumentException::class)
    override fun registerClient(clientId: String, clientCallback: IKommClient) {
        if (clients.containsKey(clientId)) {
            // clientId is already on logged
            return
        }
        clients[clientId] = clientCallback

        try {
            notifyClientJoined(clientId)
            notifyClientAmountPlayers(clientId)
        } catch (re: RemoteException) {
            unregisterClientGracefully(clientId, "SERVER: Error sending welcome notification to '$clientId'. Removing.")
        }
    }

    @Throws(RemoteException::class)
    override fun unregisterClient(clientId: String?) {
        if (clientId.isNullOrBlank()) return
        val removedClient = clients.remove(clientId)
        if (removedClient != null) {
            notifyClientLeftServer(clientId)
            if (clients.isEmpty()) {
                shutdown()
            }
        }
    }

    private fun unregisterClientGracefully(clientId: String, reason: String) {
        println("SERVER: Unregistering '$clientId' because they $reason.")
        clients.remove(clientId)
    }

    fun shutdown() {
        clients.clear()
        try {
            java.rmi.Naming.unbind(serviceAddress)
            unexportObject(this, true)
        } catch (e: Exception) {
            println("SERVER: Error during RMI cleanup: ${e.message}")
            e.printStackTrace()
        }
        println("SERVER: Shutdown complete.")
    }

    fun start() {
        try {
            this.registry = LocateRegistry.createRegistry(port)
            println("Registry created on port $port. Attempting rebind using name '$serverName' only.")
            java.rmi.Naming.rebind(serviceAddress, this@KommServer)
            println("Rebind successful using name only.")
            Runtime.getRuntime().addShutdownHook(thread(start = false) {
                this.shutdown()
            })
        } catch (e: Exception) {
            println("SERVER: Failed to start server: ${e.message}")
            e.printStackTrace()
            exitProcess(1)
        }
    }

    private fun notifyClientJoined(clientId: String) {
        safeBroadcast(clientId) {
            onChatMessagePayload(
                KommData(
                    clientId = clientId,
                    channel = CHANNEL_CHAT_SYSTEM,
                    data = ChatMessagePayload("Say hello to $clientId")
                )
            )
        }
        notifyClientById(clientId) {
            onChatMessagePayload(
                KommData(
                    clientId = clientId,
                    channel = CHANNEL_CHAT_SYSTEM,
                    data = ChatMessagePayload("Welcome $clientId!")
                )
            )
        }
    }

    private fun notifyClientLeftServer(clientId: String) {
        safeBroadcast(clientId) {
            sendChatMessagePayload(
                KommData(
                    clientId = clientId,
                    channel = CHANNEL_CHAT_SYSTEM,
                    data = ChatMessagePayload("$clientId has left the server!")
                )
            )
        }
    }

    private fun notifyClientAmountPlayers(clientId: String) {
        val kommData = KommData(clientId, CHANNEL_GAME, PlayersConnectedPayload(clients.size))
        notifyClientById(clientId) {
            onPlayersConnectedPayload(kommData)
        }
    }


    private fun notifyClientById(clientId: String?, block: IKommClient.() -> Unit) {
        if (clientId.isNullOrBlank()) return
        clients[clientId]?.let { client: IKommClient ->
            try {
                client.block()
            } catch (e: RemoteException) {
                println("SERVER: Error sending message to '$clientId'. Assuming disconnected: ${e.message}")
                unregisterClientGracefully(clientId, "disconnected abruptly")
            }
        }
    }

    private fun safeBroadcast(senderId: String?, block: IKommClient.() ->Unit) {
        if (senderId.isNullOrBlank()) return
        val clientsToRemove = mutableListOf<String>()
        clients.forEach { (clientId, clientCallback) ->
            if (clientId != senderId) {
                try {
                    clientCallback.block()
                } catch (e: RemoteException) {
                    println("SERVER: Error sending message to '$clientId'. Assuming disconnected: ${e.message}")
                    clientsToRemove.add(clientId)
                }
            }
        }
        clientsToRemove.forEach { unregisterClientGracefully(it, "disconnected abruptly") }
    }
}