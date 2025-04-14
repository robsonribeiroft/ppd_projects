package org.robsonribeiro.ppd.component.game

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import org.robsonribeiro.ppd.component.game.logic.PlayerPiece
import org.robsonribeiro.ppd.values.ColorResources
import org.robsonribeiro.ppd.values.Padding


private const val YOUR_PIECE_INFO = "Your piece"

@Composable
fun PieceInfoComponent(
    modifier: Modifier = Modifier,
    piece: PlayerPiece
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
            Text(
                text = YOUR_PIECE_INFO
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(Padding.regular))
                    .background(piece.color)
                    .border(
                        BorderStroke(
                            Padding.single,
                            ColorResources.BlackRich.copy(alpha = 0.6f)),
                        RoundedCornerShape(Padding.regular)
                    )
            )
        }
    }
}