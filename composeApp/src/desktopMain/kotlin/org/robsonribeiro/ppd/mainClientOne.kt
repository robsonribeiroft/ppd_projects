package org.robsonribeiro.ppd

import androidx.compose.ui.window.*
import org.robsonribeiro.ppd.viewmodel.ClientViewModel
import org.robsonribeiro.ppd.window.ClientWindow

fun main() = application {
    ClientWindow(
        clientViewModel = ClientViewModel(
            startServer = true,
            clientId = "Robson_One"
        )
    ) {
        exitApplication()
    }
}