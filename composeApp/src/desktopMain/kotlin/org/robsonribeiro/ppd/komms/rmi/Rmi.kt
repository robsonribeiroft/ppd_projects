package org.robsonribeiro.ppd.komms.rmi

import java.rmi.Remote
import java.rmi.RemoteException

/**
 * Defines the remote methods the server exposes to clients.
 */
interface KommServerRmiInterface : Remote {
    /**
     * Registers a new client with the server.
     * @param clientId A unique identifier for the client.
     * @param clientCallback The remote object the server can use to send messages back to the client.
     * @throws RemoteException If a communication error occurs.
     * @throws IllegalArgumentException If the clientId is already registered.
     */
    @Throws(RemoteException::class, IllegalArgumentException::class)
    fun registerClient(clientId: String, clientCallback: KommClientRmiInterface)

    /**
     * Unregisters an existing client from the server.
     * @param clientId The unique identifier of the client to remove.
     * @throws RemoteException If a communication error occurs.
     */
    @Throws(RemoteException::class)
    fun unregisterClient(clientId: String)

    /**
     * Sends a message from a client to be broadcast to other clients.
     * @param senderId The unique identifier of the client sending the message.
     * @param message The content of the message.
     * @throws RemoteException If a communication error occurs.
     */
    @Throws(RemoteException::class)
    fun broadcastMessage(senderId: String, message: String)
}

/**
 * Defines the remote methods the client exposes to the server (for callbacks).
 */
fun interface KommClientRmiInterface : Remote {
    /**
     * Called by the server to deliver a message to this client.
     * @param senderId The unique identifier of the client who sent the original message.
     * @param message The content of the message.
     * @throws RemoteException If a communication error occurs during the callback.
     */
    @Throws(RemoteException::class)
    fun receiveMessage(message: String)
}

interface KommRmiConfigInterface {
    val hostname: String
    val port: Int
    val serverName: String
    val serviceAddress: String get() = "rmi://$hostname:$port/$serverName"
}