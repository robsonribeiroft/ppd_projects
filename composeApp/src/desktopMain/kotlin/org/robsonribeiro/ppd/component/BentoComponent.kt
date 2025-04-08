package org.robsonribeiro.ppd.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import org.robsonribeiro.ppd.values.ColorResources
import org.robsonribeiro.ppd.values.Padding

@Composable
fun BentoComponent(
    modifier: Modifier,
    content: @Composable BoxScope.()->Unit
) {
    Card(
        modifier = modifier,
        elevation = Padding.small,
        shape = RoundedCornerShape(Padding.regular)
    ) {
        Box { content() }
    }
}