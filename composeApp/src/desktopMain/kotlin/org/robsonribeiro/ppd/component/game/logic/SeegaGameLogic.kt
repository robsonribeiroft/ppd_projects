package org.robsonribeiro.ppd.component.game.logic

import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.Serializable
import org.robsonribeiro.ppd.values.ColorResources
import kotlin.random.Random

typealias SeegaBoard = List<List<PlayerPiece?>>

fun SeegaBoard.printSeegaBoard() {
    this.forEachIndexed { rowIndex, columns ->
        columns.forEachIndexed { colIndex, playerPiece ->
            print("[($rowIndex,$colIndex) $playerPiece] ")
        }
        println()
    }
}

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
    val board: SeegaBoard = initialBoardState,
    val playerPiece: PlayerPiece? = null,
    val gameAction: GameAction = GameAction.PLACE,
    val amountPiecesCaptured: Int = 0,
    val gameOutcome: GameOutcome = GameOutcome.Ongoing,
    val allPiecesPlaced: Boolean = false
)

@Serializable
sealed interface GameOutcome {
    @Serializable
    data object Ongoing : GameOutcome
    @Serializable
    data class Win(val winner: PlayerPiece) : GameOutcome
    @Serializable
    data object Draw : GameOutcome
    @Serializable
    data object Defeat : GameOutcome
    @Serializable
    data object OpponentConcede : GameOutcome
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

fun SeegaBoard.countPieces(): Pair<Int, Int> {
    var p1RemainingPieces = 0
    var p2RemainingPieces = 0
    for (row in this) {
        for (cell in row) {
            when (cell) {
                PlayerPiece.PLAYER_ONE -> p1RemainingPieces++
                PlayerPiece.PLAYER_TWO -> p2RemainingPieces++
                null -> Unit
            }
        }
    }
    return p1RemainingPieces to p2RemainingPieces
}

fun SeegaBoard.checkGameOutComeAfterCapture(): GameOutcome {
    val (p1RemainingPieces, p2RemainingPieces) = countPieces()
    if (p1RemainingPieces <= 1) {
        return GameOutcome.Win(PlayerPiece.PLAYER_TWO)
    }
    if (p2RemainingPieces <= 1) {
        return GameOutcome.Win(PlayerPiece.PLAYER_ONE)
    }
    return GameOutcome.Ongoing
}

private fun SeegaBoard.checkBarrierInColum(): Pair<PlayerPiece, Int>? {
    for (column in 1 .. 3) {
        if ((0..4).all { row -> this[row][column] == PlayerPiece.PLAYER_ONE }) return PlayerPiece.PLAYER_ONE to column
        if ((0..4).all { row -> this[row][column] == PlayerPiece.PLAYER_TWO }) return PlayerPiece.PLAYER_TWO to column
    }
    return null
}

private fun SeegaBoard.checkBarrierInRow(): Pair<PlayerPiece, Int>? {
    for (row in 1 .. 3) {
        if (this[row].all { piece -> piece == PlayerPiece.PLAYER_ONE }) return PlayerPiece.PLAYER_ONE to row
        if (this[row].all { piece -> piece == PlayerPiece.PLAYER_TWO }) return PlayerPiece.PLAYER_TWO to row
    }
    return null
}

private fun SeegaBoard.isOpponentIsolatedByHorizontalBarrier(
    barrierInRow: Int,
    barrierFormedByPiece: PlayerPiece
): Boolean {
    val opponentPiece = barrierFormedByPiece.getOpponentPiece()
    var thereIsOpponentAboveTheBarrier = false
    var thereIsOpponentBelowTheBarrier = false
    (0 until barrierInRow).forEach { row ->
        (0..4).forEach { col ->
            if (this[row][col] == opponentPiece) {
                thereIsOpponentAboveTheBarrier = true
            }
        }
    }
    (barrierInRow+1 .. 4).forEach { row ->
        (0..4).forEach { col ->
            if (this[row][col] == opponentPiece) {
                thereIsOpponentBelowTheBarrier = true
            }
        }
    }

    return thereIsOpponentAboveTheBarrier xor thereIsOpponentBelowTheBarrier
}

private fun SeegaBoard.isOpponentIsolatedByVerticalBarrier(
    barrierInColumn: Int,
    barrierFormedByPiece: PlayerPiece
): Boolean {
    val opponentPiece = barrierFormedByPiece.getOpponentPiece()
    var thereIsOpponentLeftTheBarrier = false
    var thereIsOpponentRightTheBarrier = false
    (0 .. 4).forEach { row ->
        (0 until barrierInColumn).forEach { col ->
            if (this[row][col] == opponentPiece) {
                thereIsOpponentLeftTheBarrier = true
            }
        }
    }
    (0 .. 4).forEach { row ->
        (barrierInColumn+1..4).forEach { col ->
            if (this[row][col] == opponentPiece) {
                thereIsOpponentRightTheBarrier = true
            }
        }
    }

    return thereIsOpponentLeftTheBarrier xor thereIsOpponentRightTheBarrier
}

fun SeegaBoard.checkGameOutComeAfterMove(): GameOutcome {
    checkBarrierInRow()?.let { (piece, barrierRowIndex) ->
        val opponentIsolated = isOpponentIsolatedByHorizontalBarrier(barrierInRow = barrierRowIndex, barrierFormedByPiece = piece)
        if (opponentIsolated) {
            return GameOutcome.Win(piece)
        }
    }
    checkBarrierInColum()?.let { (piece, barrierColumnIndex) ->
        val opponentIsolated = isOpponentIsolatedByVerticalBarrier(barrierInColumn = barrierColumnIndex, barrierFormedByPiece = piece)
        if (opponentIsolated) {
            return GameOutcome.Win(piece)
        }
    }
    return GameOutcome.Ongoing
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
    if (allPiecesPlaced) {
        onError("Invalid Placement: All pieces were placed. Move your piece or capture your opponent piece.")
        return this
    }
    if (row == 2 && column == 2 && !allPiecesPlaced) {
        onError("Invalid Placement: It is not possible to set a piece in the center of the board on placement stage")
        return this
    }
    if (this.board[row][column] != null) {
        onError("Invalid Placement: Cell ($row, $column) is already occupied.")
        return this
    }
    println("Placement on ($row, $column) by ${this.playerPiece}")
    val newBoard = this.board.updateCell(row = row, column = column, piece = this.playerPiece)

    if (!allPiecesPlaced) {
        val (p1, p2) = newBoard.countPieces()
        val totalPieces = p1 + p2
        println("placement total board: $totalPieces")
        if (totalPieces == 24) {
            return this.copy(
                allPiecesPlaced = true,
                board = newBoard
            )
        }
    }
    return this.copy(board = newBoard)
}

fun GameState.checkAllPiecesArePlaced(): GameState {
    if (allPiecesPlaced) return this
    val (p1, p2) = board.countPieces()
    val totalPieces = p1 + p2
    if (totalPieces == 24) {
        return this.copy(allPiecesPlaced = true)
    }
    return this
}

fun GameState.handleRemovePieceOnGridCell(row: Int, column: Int, onError: (String)->Unit): GameState {
    if (allPiecesPlaced) {
        onError("You can only remove the pieces on placement stage!")
        return this
    }
    val piece = this.board[row][column] ?: return this
    if (piece != this.playerPiece) {
        onError("You can only remove your own pieces!")
        return this
    }

    println("Remove on ($row, $column) by ${this.playerPiece}")
    val newBoard = this.board.updateCell(row = row, column = column, piece = null)
    return this.copy(board = newBoard)
}

fun GameState.handleCaptureOpponentPieceOnGridCell(row: Int, column: Int, onError: (String)->Unit): GameState {
    if (!allPiecesPlaced) {
        onError("You can only capture the opponent pieces after all pieces are placed!")
        return this
    }
    val piece = this.board[row][column] ?: return this
    if (piece == this.playerPiece) {
        onError("You can only capture your opponent pieces!")
        return this
    }

    println("Piece captured on ($row, $column)")
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

val initialBoardState: SeegaBoard get() = List(5) { List(5) { null } }

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

