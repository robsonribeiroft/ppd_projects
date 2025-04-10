package org.robsonribeiro.ppd.window

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import org.jetbrains.compose.resources.painterResource
import org.robsonribeiro.ppd.component.WindowBarComponent
import org.robsonribeiro.ppd.dialog.ExitApplicationDialog
import org.robsonribeiro.ppd.helper.screenDimensions
import org.robsonribeiro.ppd.helper.toDpSize
import org.robsonribeiro.ppd.values.StringResources
import org.robsonribeiro.ppd.values.empty
import org.robsonribeiro.ppd.viewmodel.ClientViewModel
import ppd.composeapp.generated.resources.Res
import ppd.composeapp.generated.resources.background_noise
import ppd.composeapp.generated.resources.ic_game_joystick

@Composable
fun ClientWindow(
    clientViewModel: ClientViewModel,
    exitApplication: () -> Unit,
) {
    var alignmentState by remember { mutableStateOf(Alignment.Center) }
    var confirmCloseApplication by remember { mutableStateOf(false) }

    Window(
        onCloseRequest = {
            confirmCloseApplication = true
        },
        title = StringResources.APPLICATION_NAME,
        icon = painterResource(Res.drawable.ic_game_joystick),
        resizable = true,
        undecorated = true,
        state = WindowState(
            position = WindowPosition(alignmentState),
            placement = if (alignmentState == Alignment.TopCenter) WindowPlacement.Maximized else WindowPlacement.Floating,
            size = if (alignmentState == Alignment.TopCenter) {
                screenDimensions().toDpSize()
            } else {
                screenDimensions(0.7f, 0.8f).toDpSize()
            }
        ),
        onPreviewKeyEvent = { keyEvent ->
            when {
                keyEvent.isCtrlPressed && keyEvent.key == Key.DirectionLeft -> {
                    alignmentState = Alignment.CenterStart
                    return@Window true
                }
                keyEvent.isCtrlPressed && keyEvent.key == Key.DirectionRight -> {
                    alignmentState = Alignment.CenterEnd
                    return@Window true
                }
                keyEvent.isCtrlPressed && keyEvent.key == Key.F -> {
                    alignmentState = Alignment.TopCenter
                    return@Window true
                }
                else -> false
            }
        }
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(Res.drawable.background_noise),
                contentDescription = String.empty,
                contentScale = ContentScale.FillBounds
            )
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                windowTitleBar {
                    confirmCloseApplication = true
                }
                ClientContentWindow(clientViewModel) {
                    exitApplication()
                }
            }

            if (confirmCloseApplication) {
                ExitApplicationDialog(
                    onDismiss = { shouldDismiss ->
                        confirmCloseApplication = shouldDismiss
                    },
                    exitApplication = {
                        clientViewModel.killServer()
                        exitApplication()
                    }
                )
            }
        }
    }
}

@Composable
private fun WindowScope.windowTitleBar(onExitApplication: ()-> Unit) = WindowDraggableArea {
    WindowBarComponent { onExitApplication() }
}

