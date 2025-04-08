package org.robsonribeiro.ppd.component.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.robsonribeiro.ppd.values.ColorResources

@Composable
fun GameBoard(modifier: Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ColorResources.RedPantoneDarker)
    )
}