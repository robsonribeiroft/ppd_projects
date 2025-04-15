package org.robsonribeiro.ppd.component.game

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import org.jetbrains.compose.resources.vectorResource
import org.robsonribeiro.ppd.component.game.logic.PlayerPiece
import org.robsonribeiro.ppd.values.ColorResources
import org.robsonribeiro.ppd.values.Padding
import org.robsonribeiro.ppd.values.empty
import ppd.composeapp.generated.resources.Res
import ppd.composeapp.generated.resources.ic_game_joystick

@Composable
fun GridCellComponent(
    modifier: Modifier = Modifier,
    row: Int,
    col: Int,
    piece: PlayerPiece?,
    isCenter: Boolean,
    isOriginMoveCell: Boolean,
    onClick: (row: Int, col: Int) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .border(BorderStroke(Padding.single, ColorResources.BlackRich))
            .background(if (isOriginMoveCell) ColorResources.GreenEmerald.copy(alpha = 0.4f) else ColorResources.White)
            .clickable { onClick(row, col) },
        contentAlignment = Alignment.Center
    ) {

        if (isCenter) {
            Icon(
                imageVector = vectorResource(Res.drawable.ic_game_joystick),
                contentDescription = String.empty,
                tint = ColorResources.BlackRich
            )
        }

        // --- Display the Piece ---
        if (piece != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize(0.5f)
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