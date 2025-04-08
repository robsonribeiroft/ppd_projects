package org.robsonribeiro.ppd.helper

import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketTimeoutException
import java.io.IOException

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
