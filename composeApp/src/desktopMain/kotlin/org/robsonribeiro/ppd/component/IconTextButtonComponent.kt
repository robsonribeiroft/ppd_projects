package org.robsonribeiro.ppd.component

import androidx.compose.material.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.vectorResource
import org.robsonribeiro.ppd.values.ColorResources
import org.robsonribeiro.ppd.values.empty

@Composable
fun IconTextButtonComponent(
    iconResource: DrawableResource,
    iconColor: Color = ColorResources.BlackRich,
    buttonColor: Color = Color.LightGray,
    modifier: Modifier = Modifier,
    onClick: ()->Unit
) {
    var isHoveringOver by remember { mutableStateOf(false) }
    ButtonComponent(
        modifier = modifier,
        color = buttonColor,
        onClick = onClick
    ) {
        Icon(
            vectorResource(iconResource),
            String.empty,
            modifier,
            iconColor
        )
    }
}