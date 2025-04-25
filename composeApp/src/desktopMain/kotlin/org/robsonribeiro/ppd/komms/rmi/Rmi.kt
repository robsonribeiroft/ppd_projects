package org.robsonribeiro.ppd.komms.rmi

import org.robsonribeiro.ppd.komms.model.KommData
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


interface IKommServer : Remote {
    @Throws(RemoteException::class)
    fun sendChatMessagePayload(payload: KommData)
    @Throws(RemoteException::class)
    fun sendPlayerPiecePayload(payload: KommData)
    @Throws(RemoteException::class)
    fun sendPlayersConnectedPayload(payload: KommData)
    @Throws(RemoteException::class)
    fun sendSeegaBoardPayload(payload: KommData)
    @Throws(RemoteException::class)
    fun sendScoreBoardPayload(payload: KommData)
    @Throws(RemoteException::class)
    fun sendGameOutcomePayload(payload: KommData)
    @Throws(RemoteException::class)
    fun sendNewGamePayload(payload: KommData)
    @Throws(RemoteException::class)
    fun sendCommandPayload(payload: KommData)
    @Throws(RemoteException::class, IllegalArgumentException::class)
    fun registerClient(clientId: String, clientCallback: IKommClient)
    @Throws(RemoteException::class)
    fun unregisterClient(clientId: String?)

}

interface IKommClient : Remote {
    @Throws(RemoteException::class)
    fun onChatMessagePayload(payload: KommData)
    @Throws(RemoteException::class)
    fun onPlayerPiecePayload(payload: KommData)
    @Throws(RemoteException::class)
    fun onPlayersConnectedPayload(payload: KommData)
    @Throws(RemoteException::class)
    fun onSeegaBoardPayload(payload: KommData)
    @Throws(RemoteException::class)
    fun onScoreBoardPayload(payload: KommData)
    @Throws(RemoteException::class)
    fun onGameOutcomePayload(payload: KommData)
    @Throws(RemoteException::class)
    fun onNewGamePayload(payload: KommData)
}

interface IKommRmiConfig {
    val hostname: String
    val port: Int
    val serverName: String
    val serviceAddress: String get() = "rmi://$hostname:$port/$serverName"
}