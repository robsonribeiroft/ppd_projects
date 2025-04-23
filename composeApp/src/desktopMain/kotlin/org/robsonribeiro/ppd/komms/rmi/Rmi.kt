package org.robsonribeiro.ppd.komms.rmi

import java.rmi.Remote
import java.rmi.RemoteException

interface KommServerRmiInterface : Remote {

    @Throws(RemoteException::class, IllegalArgumentException::class)
    fun registerClient(clientId: String, clientCallback: KommClientRmiInterface)

    @Throws(RemoteException::class)
    fun unregisterClient(clientId: String)

    @Throws(RemoteException::class)
    fun broadcastMessage(senderId: String, message: String)
}

fun interface KommClientRmiInterface : Remote {
    @Throws(RemoteException::class)
    fun receiveMessage(message: String)
}

interface KommRmiConfigInterface {
    val hostname: String
    val port: Int
    val serverName: String
    val serviceAddress: String get() = "rmi://$hostname:$port/$serverName"
}