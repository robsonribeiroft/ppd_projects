package org.robsonribeiro.ppd.component.game

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.vectorResource
import org.robsonribeiro.ppd.values.ColorResources
import org.robsonribeiro.ppd.values.Padding
import org.robsonribeiro.ppd.values.TextSize
import org.robsonribeiro.ppd.values.empty
import ppd.composeapp.generated.resources.Res
import ppd.composeapp.generated.resources.ic_game_joystick

const val WAITING_PLAYERS = "Waiting players to connect..."

@Composable
fun WaitingPlayersComponent(
    modifier: Modifier = Modifier
) {

    val infiniteTransition = rememberInfiniteTransition()
    val rotationAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    LaunchedEffect(Unit) {
        delay(500)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(ColorResources.background_gradient)
            ),
        verticalArrangement = Arrangement.spacedBy(Padding.large, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            modifier = Modifier
                .size(200.dp)
                .graphicsLayer {
                    rotationY = rotationAnim
                    rotationZ = rotationAnim
                },
            imageVector = vectorResource(Res.drawable.ic_game_joystick),
            contentDescription = String.empty,
            tint = ColorResources.White
        )

        Text(
            modifier = Modifier,
            text = WAITING_PLAYERS,
            textAlign = TextAlign.Center,
            fontSize = TextSize.largeExtra,
            color = ColorResources.White
        )

    }
}