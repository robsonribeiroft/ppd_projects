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

fun PlayerPiece.getOpponentPiece(): PlayerPiece {
    return if (this == PlayerPiece.PLAYER_TWO) PlayerPiece.PLAYER_ONE else PlayerPiece.PLAYER_TWO
}

fun randomPlayerPiece(): PlayerPiece {
    val pieces = PlayerPiece.entries
    val randomIndex = Random.nextInt(pieces.size)
    return pieces[randomIndex]
}

enum class ApplicationState {
    WAITING_PLAYERS, GET_PIECE, READY_TO_PLAY
}

fun ApplicationState.isReadyToPlay(): Boolean {
    return this == ApplicationState.READY_TO_PLAY
}

enum class GameAction {
    PLACE, REMOVE, MOVE, CAPTURE
}

data class GameState(
    val board: SeegaBoard = initialBoardState(),
    val playerPiece: PlayerPiece? = null,
    val gameAction: GameAction = GameAction.PLACE,
    val amountPiecesCaptured: Int = 0,
    val gameOutcome: GameOutcome = GameOutcome.Ongoing
)

sealed interface GameOutcome {
    data object Ongoing : GameOutcome
    data class Win(val winner: PlayerPiece) : GameOutcome
    data object Draw : GameOutcome
}

fun MutableStateFlow<GameState>.handleOnClickGridCell(row: Int, column: Int, onError: (String) -> Unit = {}) {
    value = when(value.gameAction) {
        GameAction.PLACE -> {
            value.handlePlacementPieceOnGridCell(row, column, onError)
        }
        GameAction.REMOVE -> {
            value.handleRemovePieceOnGridCell(row, column, onError)
        }
        GameAction.CAPTURE -> {
            value.handleCaptureOpponentPieceOnGridCell(row, column, onError)
        }
        else -> value
    }
}

fun SeegaBoard.checkBarrierWin(): PlayerPiece? {
    for (row in 0..4) {
        if (this[row].all { it == PlayerPiece.PLAYER_ONE }) return PlayerPiece.PLAYER_ONE
        if (this[row].all { it == PlayerPiece.PLAYER_TWO }) return PlayerPiece.PLAYER_TWO
    }
    // Check columns
    for (col in 0..4) {
        if ((0..4).all { row -> this[row][col] == PlayerPiece.PLAYER_ONE }) return PlayerPiece.PLAYER_ONE
        if ((0..4).all { row -> this[row][col] == PlayerPiece.PLAYER_TWO }) return PlayerPiece.PLAYER_TWO
    }
    return null
}

fun SeegaBoard.countPieces(): Pair<Int, Int> {
    var p1Count = 0
    var p2Count = 0
    for (row in this) {
        for (cell in row) {
            when (cell) {
                PlayerPiece.PLAYER_ONE -> p1Count++
                PlayerPiece.PLAYER_TWO -> p2Count++
                null -> Unit
            }
        }
    }
    return p1Count to p2Count
}

fun MutableStateFlow<GameState>.checkGameOutcome() {
    if (value.gameAction != GameAction.CAPTURE)
        return
    val board =  value.board
    val (p1count, p2count) = board.countPieces()
    if (p1count == 0) {
        value = value.copy(gameOutcome = GameOutcome.Win(PlayerPiece.PLAYER_TWO))
    }
    if (p2count == 0) {
        value = value.copy(gameOutcome = GameOutcome.Win(PlayerPiece.PLAYER_ONE))
    }

    val barrierWinner = board.checkBarrierWin()
    barrierWinner?.let { winner ->
        value = value.copy(gameOutcome = GameOutcome.Win(winner))
    }

    if (p1count <= 3 && p2count <= 3) {
        value = value.copy(gameOutcome = GameOutcome.Draw)
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

fun GameState.handlePlacementPieceOnGridCell(row: Int, column: Int, onError: (String)->Unit): GameState {
    if (this.board[row][column] != null) {
        onError("Invalid Placement: Cell ($row, $column) is already occupied.")
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

fun GameState.handleRemovePieceOnGridCell(row: Int, column: Int, onError: (String)->Unit): GameState {
    val piece = this.board[row][column] ?: return this
    if (piece != this.playerPiece) {
        onError("You can only remove your own pieces!")
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

fun GameState.handleCaptureOpponentPieceOnGridCell(row: Int, column: Int, onError: (String)->Unit): GameState {
    val piece = this.board[row][column] ?: return this
    if (piece == this.playerPiece) {
        onError("You can only capture your opponent pieces!")
        return this
    }

    println("Remove on ($row, $column) by ${this.playerPiece}")
    val newBoard = this.board.updateCell(
        row = row,
        column = column,
        piece = null
    )
    return this.copy(board = newBoard, amountPiecesCaptured = this.amountPiecesCaptured+1)
}

fun GameState.handleMovePieceOnGridCell(fromRow: Int, fromColumn: Int, toRow: Int, toColumn: Int): GameState {
    val piece = this.board[fromRow][fromColumn] ?: return this
    if (this.board[toRow][toColumn] != null) return this
    if (piece != this.playerPiece) return this

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



