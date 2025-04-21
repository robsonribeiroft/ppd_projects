package org.robsonribeiro.ppd.helper

import org.robsonribeiro.ppd.komms.rmi.KommServerRmi
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketTimeoutException
import java.rmi.RemoteException
import java.rmi.registry.LocateRegistry

fun isServerLive(host: String, port: Int, timeoutMillis: Int = 100): Boolean {
    val socket = Socket()
    try {
        println("Attempting self-connect check to $host:$port with timeout $timeoutMillis ms")

        val socketAddress = InetSocketAddress(host, port)
        socket.connect(socketAddress, timeoutMillis)
        println("Self-connect check successful.")
        return true
    } catch (e: SocketTimeoutException) {
        println("Self-connect check timed out.")
        return false
    } catch (e: IOException) {
        println("Self-connect check failed: ${e.message}")
        return false
    } catch (e: Exception) {
        println("Self-connect check encountered unexpected error: ${e.message}")
        return false
    }
    finally {
        try {
            socket.close()
        } catch (_: Exception) { }
    }
}


fun isServerRmiLive(host: String, port: Int, serviceName: String = KommServerRmi.serverName): Boolean {
    return try {
        val registry = LocateRegistry.getRegistry(host, port)
        registry.lookup(serviceName)
        true
    } catch (e: RemoteException) {
        e.printStackTrace()
        false
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}