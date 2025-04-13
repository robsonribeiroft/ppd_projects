package org.robsonribeiro.ppd.component.game.logic

import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import org.robsonribeiro.ppd.values.ColorResources

typealias SeegaBoard = List<List<PlayerPiece?>>

enum class PlayerPiece(
    val color: Color
) {
    PLAYER_ONE(ColorResources.RedPantone),
    PLAYER_TWO(ColorResources.BlueRoyal)
}

enum class GameAction {
    PLACE_PIECE, REMOVE_PIECE
}

data class GameState(
    val board: SeegaBoard = initialBoardState(),
    val playerPiece: PlayerPiece? = PlayerPiece.PLAYER_ONE,
    val gameAction: GameAction = GameAction.PLACE_PIECE
)

fun MutableStateFlow<GameState>.handleOnClickGridCell(row: Int, column: Int) {
    value = when(value.gameAction) {
        GameAction.PLACE_PIECE -> {
            value.handlePlacementPieceOnGridCell(row, column)
        }
        GameAction.REMOVE_PIECE -> {
            value.handleRemovePieceOnGridCell(row, column)
        }
    }
}

fun GameState.handlePlacementPieceOnGridCell(row: Int, column: Int): GameState {
    if (this.board[row][column] != null) {
        println("Invalid Placement: Cell ($row, $column) is already occupied.")
        return this
    }
    println("Placement on ($row, $column) by ${this.playerPiece}")
    val newBoard = this.board.updateCell(
        row = row,
        column = column,
        piece = this.playerPiece
    )
    return this.copy(board = newBoard)
}

fun GameState.handleRemovePieceOnGridCell(row: Int, column: Int): GameState {
    if (this.board[row][column] == null) {
        println("Cell ($row, $column) is already empty.")
        return this
    }
    println("Remove on ($row, $column) by ${this.playerPiece}")
    val newBoard = this.board.updateCell(
        row = row,
        column = column,
        piece = null
    )
    return this.copy(board = newBoard)
}

fun initialBoardState(): SeegaBoard = List(5) { List(5) { null } }

fun SeegaBoard.updateCell(
    row: Int,
    column: Int,
    piece: PlayerPiece?
): SeegaBoard {
    return mapIndexed { rowIndex, rowList ->
        if (row == rowIndex) {
            rowList.mapIndexed { columnIndex, currentPiece ->
                if (column == columnIndex) {
                    piece
                } else {
                    currentPiece
                }
            }
        } else rowList
    }
}



