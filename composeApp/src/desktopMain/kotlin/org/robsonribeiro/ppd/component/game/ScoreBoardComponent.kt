package org.robsonribeiro.ppd.component.game

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import org.robsonribeiro.ppd.component.game.logic.PlayerPiece
import org.robsonribeiro.ppd.model.ScoreBoardInfo
import org.robsonribeiro.ppd.values.ColorResources
import org.robsonribeiro.ppd.values.Padding
import org.robsonribeiro.ppd.values.empty


@Composable
fun ScoreBoardComponent(
    modifier: Modifier = Modifier,
    scoreBoardInfo: ScoreBoardInfo
) {

    Card(
        modifier = modifier,
        elevation = Padding.tiny,
        shape = RoundedCornerShape(Padding.regular)
    ) {
        Column (
            Modifier.padding(Padding.small),
            verticalArrangement = Arrangement.spacedBy(Padding.small),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ScoreInfo(
                modifier = Modifier.weight(1f).fillMaxSize(),
                clientId = scoreBoardInfo.clientId,
                playerPiece = scoreBoardInfo.playerPiece,
                count = scoreBoardInfo.capturedPiecesAmount
            )
            ScoreInfo(
                modifier = Modifier.weight(1f).fillMaxSize(),
                clientId = scoreBoardInfo.opponentClientId ?: "Opponent",
                playerPiece = scoreBoardInfo.opponentPlayerPiece,
                count = scoreBoardInfo.opponentCapturedPiecesAmount
            )
        }
    }
}

@Composable
fun ScoreInfo(
    modifier: Modifier,
    clientId: String?,
    playerPiece: PlayerPiece?,
    count: Int = 0
) {
    Row (
        modifier = modifier
            .fillMaxSize()
            .background(
                color = playerPiece?.color ?: PlayerPiece.PLAYER_ONE.color,
                shape = RoundedCornerShape(Padding.regular)
            )
            .border(
                BorderStroke(
                    Padding.single,
                    ColorResources.BlackRich.copy(alpha = 0.6f)),
                RoundedCornerShape(Padding.regular)
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Padding.regular, Alignment.CenterHorizontally)
    ){
        Text (
            modifier = Modifier,
            text = clientId ?: String.empty,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h6.copy(color = ColorResources.White),

        )

        Text(
            text = count.toString(),
            style = MaterialTheme.typography.h6.copy(color = ColorResources.White)
        )
    }
}

