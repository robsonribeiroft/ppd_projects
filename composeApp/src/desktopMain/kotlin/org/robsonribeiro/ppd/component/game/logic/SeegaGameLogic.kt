package org.robsonribeiro.ppd.component.game.logic

import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import org.robsonribeiro.ppd.values.ColorResources
import kotlin.random.Random

typealias SeegaBoard = List<List<PlayerPiece?>>

enum class PlayerPiece(
    val color: Color
) {
    PLAYER_ONE(ColorResources.RedPantone),
    PLAYER_TWO(ColorResources.BlueRoyal)
}

fun randomPlayerPiece(): PlayerPiece {
    val pieces = PlayerPiece.entries
    val randomIndex = Random.nextInt(pieces.size)
    return pieces[randomIndex]
}

enum class ApplicationState {
    WAITING_PLAYERS, GET_PIECE, READY_TO_PLAY
}

enum class GameAction {
    PLACE_PIECE, REMOVE_PIECE, MOVE_PIECE
}

data class GameState(
    val board: SeegaBoard = initialBoardState(),
    val playerPiece: PlayerPiece? = null,
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

        else -> value
    }
}

fun MutableStateFlow<GameState>.handleMovePieceOnGridCell(fromRow: Int, fromColumn: Int, toRow: Int, toColumn: Int) {
    value = value.handleMovePieceOnGridCell(fromRow, fromColumn, toRow, toColumn)
}

fun MutableStateFlow<GameState>.setPlayerPiece(piece: PlayerPiece) {
    value = value.copy(playerPiece = piece)
}

fun MutableStateFlow<GameState>.setGameAction(action: GameAction) {
    value = value.copy(gameAction = action)
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

fun GameState.handleMovePieceOnGridCell(fromRow: Int, fromColumn: Int, toRow: Int, toColumn: Int): GameState {
    val piece = this.board[fromRow][fromColumn] ?: return this
    val newBoard = board
        .updateCell(fromRow, fromColumn, null)
        .updateCell(toRow, toColumn, piece)
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



