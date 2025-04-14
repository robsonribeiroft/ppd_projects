package org.robsonribeiro.ppd.komms.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.robsonribeiro.ppd.component.game.logic.PlayerPiece

@Serializable
sealed interface MessagePayload

@Serializable
@SerialName("CHAT_MESSAGE")
data class ChatMessagePayload(
    val message: String
) : MessagePayload


@Serializable
@SerialName("COMMAND")
data class CommandPayload(
    val command: String,
    val args: List<String> = emptyList()
) : MessagePayload

@Serializable
@SerialName("PLAYER_PIECE")
data class PlayerPiecePayload(
    val piece: PlayerPiece
) : MessagePayload

@Serializable
@SerialName("PLAYERS_CONNECTED")
data class PlayersConnectedPayload(
    val amountOfPlayersConnected: Int
) : MessagePayload

@Serializable
data class KommData(
    val clientId: String,
    val channel: String,
    val data: MessagePayload
)

fun KommData.toJson() = Json.encodeToString(this)

fun String.decodeJson(): KommData = Json.decodeFromString<KommData>(this)
