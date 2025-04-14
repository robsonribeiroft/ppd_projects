package org.robsonribeiro.ppd.component.game

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import kotlinx.coroutines.delay
import org.robsonribeiro.ppd.component.game.logic.PlayerPiece
import org.robsonribeiro.ppd.values.ColorResources
import org.robsonribeiro.ppd.values.Padding

const val YOUR_PIECE = "You Piece color is..."
const val RED_PLAY_FIRST = "The red pieces plays first!"
const val BLUE_PLAY_SECOND = "The blue pieces plays second!"
const val START_GAME = "Start Game"

@Composable
fun SortPieceComponent(
    modifier: Modifier,
    piece: PlayerPiece?,
    onClick: ()->Unit = {}
) {

    var buttonVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(500)
        buttonVisible = true
    }

    val buttonAlpha by animateFloatAsState(
        targetValue = if (buttonVisible) 1.0f else 0.0f,
        animationSpec = tween(durationMillis = 1000),
        label = "ButtonAlphaAnimation"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ColorResources.White),
        verticalArrangement = Arrangement.spacedBy(Padding.large, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = YOUR_PIECE
        )

        CoinFlipComponent(
            playerPiece = piece
        )

        Text(
            modifier = Modifier.alpha(buttonAlpha),
            text = if (piece == PlayerPiece.PLAYER_ONE) RED_PLAY_FIRST else BLUE_PLAY_SECOND
        )

        OutlinedButton(
            modifier = Modifier.alpha(buttonAlpha),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = ColorResources.White,
                contentColor = ColorResources.BlackRich
            ),
            border = BorderStroke(
                Padding.single,
                ColorResources.BlackRich
            ),
            onClick = onClick
        ) {
            Text(START_GAME)
        }
    }
}