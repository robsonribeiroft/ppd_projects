package org.robsonribeiro.ppd.component.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.robsonribeiro.ppd.component.game.logic.GameAction
import org.robsonribeiro.ppd.values.Padding

@Composable
fun GameActionSelectionComponent(
    modifier: Modifier,
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
                Button(
                    modifier = Modifier.weight(1f).fillMaxSize(),
                    onClick = {
                        onClick(action)
                    }
                ) {
                    Text(
                        text = action.name
                    )
                }
            }

        }
    }
}