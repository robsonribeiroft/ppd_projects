package org.robsonribeiro.ppd.model

data class ServerState(
    val host: String? = null,
    val port: Int? = null,
    val isRunning: Boolean = false
)
