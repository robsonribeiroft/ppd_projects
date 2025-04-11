package org.robsonribeiro.ppd

import androidx.compose.ui.window.*
import org.robsonribeiro.ppd.viewmodel.MainViewModel
import org.robsonribeiro.ppd.window.ClientWindow

fun main() = application {
    ClientWindow(MainViewModel()) {
        exitApplication()
    }
}