package org.robsonribeiro.ppd.model

import org.robsonribeiro.ppd.component.game.logic.PlayerPiece

data class ScoreBoardInfo(
    val clientId: String? = null,
    val opponentClientId: String? = null,
    val playerPiece: PlayerPiece? = null,
    val opponentPlayerPiece: PlayerPiece? = null,
    val capturedPiecesAmount: Int = 0,
    val opponentCapturedPiecesAmount: Int = 0
)