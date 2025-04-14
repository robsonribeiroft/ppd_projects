package org.robsonribeiro.ppd.window

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.window.*
import org.jetbrains.compose.resources.painterResource
import org.robsonribeiro.ppd.component.WindowBarComponent
import org.robsonribeiro.ppd.dialog.ConfirmationDialog
import org.robsonribeiro.ppd.helper.screenDimensions
import org.robsonribeiro.ppd.helper.toDpSize
import org.robsonribeiro.ppd.model.ConfirmationDialogInfo
import org.robsonribeiro.ppd.values.StringResources
import org.robsonribeiro.ppd.values.empty
import org.robsonribeiro.ppd.viewmodel.MainViewModel
import ppd.composeapp.generated.resources.Res
import ppd.composeapp.generated.resources.background_noise
import ppd.composeapp.generated.resources.ic_game_joystick

@Composable
fun ClientWindow(
    mainViewModel: MainViewModel,
    exitApplication: () -> Unit,
) {
    var windowState by remember {
        mutableStateOf(
            WindowState(
                position = WindowPosition(Alignment.TopCenter),
                placement = WindowPlacement.Floating,
                size = screenDimensions(verticalWeight = 1f, horizontalWeight = 0.5f).toDpSize()
            )
        )
    }

    var showConfirmationDialog by remember { mutableStateOf<ConfirmationDialogInfo?>(null) }

    Window(
        onCloseRequest = {
            showConfirmationDialog = ConfirmationDialogInfo(
                title = "Quit Application",
                description = "Close this windows will make your opponent win\nAre you sure to quit?"
            ) {
                mainViewModel.leaveServer()
                exitApplication()
            }
        },
        title = StringResources.APPLICATION_NAME,
        icon = painterResource(Res.drawable.ic_game_joystick),
        resizable = true,
        undecorated = true,
        state = windowState,
        onPreviewKeyEvent = { keyEvent ->
            when {
                keyEvent.isCtrlPressed && keyEvent.key == Key.DirectionLeft -> {
                    windowState = WindowState(
                        position = WindowPosition(Alignment.TopStart),
                        placement = WindowPlacement.Floating,
                        size = screenDimensions(horizontalWeight = 0.5f).toDpSize()
                    )
                    return@Window true
                }
                keyEvent.isCtrlPressed && keyEvent.key == Key.DirectionRight -> {
                    windowState = WindowState(
                        position = WindowPosition(Alignment.TopEnd),
                        placement = WindowPlacement.Floating,
                        size = screenDimensions(horizontalWeight = 0.5f).toDpSize()
                    )
                    return@Window true
                }
                keyEvent.isCtrlPressed && keyEvent.key == Key.F -> {
                    windowState = WindowState(
                        position = WindowPosition(Alignment.TopCenter),
                        placement = WindowPlacement.Maximized,
                        size = screenDimensions().toDpSize()
                    )
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
                    showConfirmationDialog = ConfirmationDialogInfo(
                        title = "Quit Application",
                        description = "Close this windows will make your opponent win\nAre you sure to quit?"
                    ) {
                        mainViewModel.leaveServer()
                        exitApplication()
                    }
                }
                ClientContentWindow(mainViewModel)
            }

            showConfirmationDialog?.let {
                ConfirmationDialog(
                    content = showConfirmationDialog!!
                ) {
                    showConfirmationDialog = null
                }
            }
        }
    }
}

@Composable
private fun WindowScope.windowTitleBar(onExitApplication: ()-> Unit) = WindowDraggableArea {
    WindowBarComponent { onExitApplication() }
}

