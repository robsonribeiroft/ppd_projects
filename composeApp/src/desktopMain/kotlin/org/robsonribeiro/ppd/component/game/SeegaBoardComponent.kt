package org.robsonribeiro.ppd.component.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.robsonribeiro.ppd.component.game.logic.GameState


@Composable
fun SeegaBoardComponent(
    modifier: Modifier = Modifier,
    gameState: GameState,
    onCellClick: (row: Int, col: Int) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        repeat(5) { rowIndex ->
            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(5) { colIndex ->
                    val piece = gameState.board[rowIndex][colIndex]
                    val isCenter = rowIndex == 2 && colIndex == 2
                    GridCellComponent(
                        modifier = Modifier.weight(1f),
                        row = rowIndex,
                        col = colIndex,
                        piece = piece,
                        isCenter = isCenter,
                        onClick = onCellClick
                    )
                }
            }
        }
    }
}