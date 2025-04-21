package org.robsonribeiro.ppd.komms.rmi

import org.robsonribeiro.ppd.komms.CHANNEL_CHAT_SYSTEM
import org.robsonribeiro.ppd.komms.CHANNEL_GAME
import org.robsonribeiro.ppd.komms.model.ChatMessagePayload
import org.robsonribeiro.ppd.komms.model.KommData
import org.robsonribeiro.ppd.komms.model.PlayersConnectedPayload
import org.robsonribeiro.ppd.komms.model.toJson
import java.rmi.RemoteException
import java.rmi.registry.LocateRegistry
import java.rmi.registry.Registry
import java.rmi.server.UnicastRemoteObject
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.thread
import kotlin.system.exitProcess

class KommServerRmi(
    override val hostname: String,
    override val port: Int,
    override val serverName: String = KommServerRmi.serverName,
) : UnicastRemoteObject(), KommServerRmiInterface, KommRmiConfigInterface {

    companion object {
        val serverName: String get() = KommServerRmi::class.java.simpleName
    }
    private val clients = ConcurrentHashMap<String, KommClientRmiInterface>()
    private lateinit var registry: Registry

    @Throws(RemoteException::class, IllegalArgumentException::class)
    override fun registerClient(clientId: String, clientCallback: KommClientRmiInterface) {
        if (clients.containsKey(clientId)) {
            // clientId is already on logged
            return
        }
        clients[clientId] = clientCallback

        try {
            notifyClientJoined(clientId, clientCallback)
            notifyClientAmountPlayers(clientId, clientCallback)
        } catch (re: RemoteException) {
            unregisterClientGracefully(clientId, "SERVER: Error sending welcome notification to '$clientId'. Removing.")
        }
    }

    @Throws(RemoteException::class)
    override fun unregisterClient(clientId: String) {
        val removedClient = clients.remove(clientId)
        if (removedClient != null) {
            notifyClientLeftServer(clientId)
            if (clients.isEmpty()) {
                shutdown()
            }
        }
    }

    @Throws(RemoteException::class)
    override fun broadcastMessage(senderId: String, message: String) {
        val clientsToRemove = mutableListOf<String>()
        clients.forEach { (clientId, clientCallback) ->
            if (clientId != senderId) {
                try {
                    clientCallback.receiveMessage(message)
                } catch (e: RemoteException) {
                    println("SERVER: Error sending message to '$clientId'. Assuming disconnected: ${e.message}")
                    clientsToRemove.add(clientId)
                }
            }
        }
        clientsToRemove.forEach { unregisterClientGracefully(it, "disconnected abruptly") }
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
            java.rmi.Naming.rebind(serviceAddress, this@KommServerRmi)
            Runtime.getRuntime().addShutdownHook(thread(start = false) {
                this.shutdown()
            })
        } catch (e: Exception) {
            println("SERVER: Failed to start server: ${e.message}")
            e.printStackTrace()
            exitProcess(1)
        }
    }

    private fun notifyClientJoined(clientId: String,  clientCallback: KommClientRmiInterface) {
        broadcastMessage(
            senderId = clientId,
            message = KommData(
                clientId = clientId,
                channel = CHANNEL_CHAT_SYSTEM,
                data = ChatMessagePayload("Say hello to $clientId")
            ).toJson()
        )
        clientCallback.receiveMessage(
            message = KommData(
                clientId = clientId,
                channel = CHANNEL_CHAT_SYSTEM,
                data = ChatMessagePayload("Welcome $clientId!")
            ).toJson()
        )
    }

    private fun notifyClientLeftServer(clientId: String) {
        broadcastMessage(
            senderId = clientId,
            message = KommData(
                clientId = clientId,
                channel = CHANNEL_CHAT_SYSTEM,
                data = ChatMessagePayload("$clientId has left the server!")
            ).toJson()
        )
    }

    private fun notifyClientAmountPlayers(clientId: String, clientCallback: KommClientRmiInterface) {
        val kommData = KommData(clientId, CHANNEL_GAME, PlayersConnectedPayload(clients.size))
        clientCallback.receiveMessage(kommData.toJson())
    }
}