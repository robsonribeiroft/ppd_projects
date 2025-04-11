package org.robsonribeiro.ppd.component.game

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import org.robsonribeiro.ppd.component.game.logic.PlayerPiece
import org.robsonribeiro.ppd.values.ColorResources
import org.robsonribeiro.ppd.values.Padding

@Composable
fun GridCellComponent(
    modifier: Modifier = Modifier,
    row: Int,
    col: Int,
    piece: PlayerPiece?,
    isCenter: Boolean,
    onClick: (row: Int, col: Int) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .border(BorderStroke(Padding.single, ColorResources.BlackRich))
            .background(ColorResources.White.copy(alpha = 0.3f))
            .clickable { onClick(row, col) },
        contentAlignment = Alignment.Center
    ) {

        if (isCenter) {
            // handle center logic
        }

        // --- Display the Piece ---
        if (piece != null) {
            val pieceColor = when (piece) {
                PlayerPiece.PLAYER_ONE -> ColorResources.BlueRoyal
                PlayerPiece.PLAYER_TWO -> ColorResources.RedPantone
            }
            Box(
                modifier = Modifier
                    .fillMaxSize(0.5f)
                    .clip(RoundedCornerShape(Padding.regular))
                    .background(pieceColor)
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