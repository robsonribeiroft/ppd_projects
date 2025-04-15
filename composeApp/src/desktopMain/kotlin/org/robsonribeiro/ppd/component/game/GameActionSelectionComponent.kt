package org.robsonribeiro.ppd.component.game

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.robsonribeiro.ppd.component.game.logic.GameAction
import org.robsonribeiro.ppd.values.ColorResources
import org.robsonribeiro.ppd.values.Padding

const val CONCEDE = "Concede"

@Composable
fun GameActionSelectionComponent(
    modifier: Modifier,
    currentAction: GameAction,
    concede: () -> Unit,
    onClick: (GameAction) -> Unit
) {
    Card(
        modifier = modifier,
        elevation = Padding.tiny,
        shape = RoundedCornerShape(Padding.regular)
    ) {
        Column (
            Modifier.padding(Padding.small),
            verticalArrangement = Arrangement.spacedBy(Padding.small),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GameAction.entries.forEach { action ->
                OutlinedButton(
                    modifier = Modifier.weight(1f).fillMaxSize(),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = ColorResources.White,
                        contentColor = ColorResources.BlackRich
                    ),
                    border = BorderStroke(
                        Padding.single,
                        if (action == currentAction) ColorResources.BlackRich else Color.Transparent
                    ),
                    onClick = {
                        onClick(action)
                    }
                ) {
                    Text(text = action.toString())
                }
            }
            Button(
                modifier = Modifier.weight(1f).fillMaxSize(),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = ColorResources.RedPantoneDarker,
                    contentColor = ColorResources.White
                ),
                onClick = concede
            ) {
                Text(
                    text = CONCEDE,
                    style = MaterialTheme.typography.button
                )
            }

        }
    }
}