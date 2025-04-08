package org.robsonribeiro.ppd.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import org.robsonribeiro.ppd.values.Padding
import org.robsonribeiro.ppd.values.ColorResources

@Composable
fun ButtonComponent(
    modifier: Modifier = Modifier,
    internalPadding: PaddingValues = PaddingValues(Padding.regular),
    color: Color = ColorResources.BaseBackground,
    shape: Shape = RoundedCornerShape(Padding.none),
    onClick: ()->Unit,
    content: @Composable BoxScope.()->Unit
) {
    Box(
        modifier = modifier
            .background(color = color, shape = shape)
            .clickable(enabled = true){ onClick() }
            .padding(internalPadding)
    ) {
        content()
    }
}