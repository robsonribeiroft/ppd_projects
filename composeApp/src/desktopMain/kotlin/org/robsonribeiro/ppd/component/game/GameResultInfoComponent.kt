package org.robsonribeiro.ppd.component.game

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.delay
import org.robsonribeiro.ppd.component.game.logic.GameOutcome
import org.robsonribeiro.ppd.component.game.logic.GameState
import org.robsonribeiro.ppd.values.ColorResources
import org.robsonribeiro.ppd.values.Padding
import org.robsonribeiro.ppd.values.TextSize
import org.robsonribeiro.ppd.values.empty

const val WINNER = "You Win!"
const val LOSER = "You Lose!"
const val DRAW = "Draw!"
const val NEW_GAME = "New Game"

@Composable
fun GameResultInfoComponent(
    modifier: Modifier,
    gameState: GameState,
    onClick: ()->Unit = {}
) {

    var buttonVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(500)
        buttonVisible = true
    }

    val buttonAlpha by animateFloatAsState(
        targetValue = if (buttonVisible) 1.0f else 0.0f,
        animationSpec = tween(durationMillis = 500),
        label = "ButtonAlphaAnimation"
    )

    val (text, back) = when(val outcome = gameState.gameOutcome) {
        GameOutcome.Draw -> DRAW to ColorResources.background_gradient
        GameOutcome.Ongoing -> String.empty to ColorResources.background_gradient
        GameOutcome.OpponentConcede -> WINNER to ColorResources.win_gradient
        GameOutcome.Defeat ->LOSER to ColorResources.defeat_gradient
        is GameOutcome.Win -> {
            if (gameState.playerPiece == outcome.winner) {
                WINNER to ColorResources.win_gradient
            } else {
                LOSER to ColorResources.defeat_gradient
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ColorResources.White.copy(alpha = 0.8f)),
        verticalArrangement = Arrangement.spacedBy(Padding.large, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Padding.large),
            shape = CircleShape,
            elevation = Padding.large
        ) {
            Text(
                modifier = Modifier
                    .background(
                        brush = Brush.linearGradient(
                            back
                        ),
                        shape = CircleShape,
                    ),
                text = text,
                textAlign = TextAlign.Center,
                fontSize = TextSize.veryExtraLarge,
                color = ColorResources.White
            )
        }

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
            Text(NEW_GAME)
        }
    }
}