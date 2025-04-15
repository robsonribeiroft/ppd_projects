package org.robsonribeiro.ppd.model

import kotlinx.serialization.Serializable
import org.robsonribeiro.ppd.component.game.logic.PlayerPiece

@Serializable
data class OpponentScoreBoard(
    val opponentClientId: String? = null,
    val opponentPiece: PlayerPiece? = null,
    val opponentAmountCaptured: Int = 0
)
