package org.robsonribeiro.ppd.component.game

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.robsonribeiro.ppd.component.game.logic.PlayerPiece
import org.robsonribeiro.ppd.values.Padding

@Composable
fun CoinFlipComponent(
    playerPiece: PlayerPiece?,
    modifier: Modifier = Modifier,
    colorFront: Color = PlayerPiece.PLAYER_ONE.color,
    colorBack: Color = PlayerPiece.PLAYER_TWO.color,
    animationDurationMillis: Int = 1000
) {

    var targetRotation by remember { mutableStateOf(0f) }

    LaunchedEffect(playerPiece) {
        delay(800)
        targetRotation += 360f
    }

    val rotationAnim by animateFloatAsState(
        targetValue = targetRotation,
        animationSpec = tween(durationMillis = animationDurationMillis),
        label = "RotationAnimation"
    )


    val isTargetLogicallyFlipped = remember(targetRotation) {
        (targetRotation / 360f).toInt() % 2 != 0
    }


    val restingColor = remember(isTargetLogicallyFlipped, playerPiece, colorFront, colorBack) {
        if (isTargetLogicallyFlipped) {
            if (playerPiece == PlayerPiece.PLAYER_ONE) colorFront else colorBack
        } else {
            colorFront
        }
    }

    val currentBgColor = remember(rotationAnim, restingColor, colorFront, colorBack) {

        val displayAngle = rotationAnim % 360f
        val normalizedAngle = if (displayAngle < 0) displayAngle + 360f else displayAngle


        val cycleVisualFrontColor = restingColor
        val cycleVisualBackColor = if (restingColor == colorFront) colorBack else colorFront

        if (normalizedAngle <= 90f || normalizedAngle > 270f) {
            cycleVisualFrontColor
        } else {
            cycleVisualBackColor
        }
    }

    Box(
        modifier = modifier
            .size(200.dp)
            .graphicsLayer {
                rotationY = rotationAnim
                cameraDistance = 8 * density
            }
            .clickable {
                targetRotation += 720f
            }
            .background(
                color = currentBgColor,
                shape = RoundedCornerShape(Padding.regular)
            )
    )
}