package org.robsonribeiro.ppd.component.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.robsonribeiro.ppd.component.game.logic.GameAction
import org.robsonribeiro.ppd.component.game.logic.GameState


@Composable
fun SeegaBoardComponent(
    modifier: Modifier = Modifier,
    gameState: GameState,
    onCellClick: (row: Int, col: Int) -> Unit,
    onMovePiece: (fromRow: Int, fromColumn: Int, toRow: Int, toColumn: Int) -> Unit
) {

    var originMoveCell by remember { mutableStateOf<Pair<Int, Int>>(-1 to -1) }

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
                        onClick = { row, column ->
                            if (gameState.gameAction == GameAction.MOVE_PIECE) {
                                if (originMoveCell.first < 0){
                                    originMoveCell = row to column
                                    println("ORIGIN CELL IS SET: $originMoveCell")
                                } else {
                                    println("DESTINY CELL IS : $row, $column")
                                    onMovePiece(originMoveCell.first, originMoveCell.second, row, column)
                                    originMoveCell = -1 to -1
                                }
                            } else {
                                onCellClick(row, column)
                            }
                        }
                    )
                }
            }
        }
    }
}