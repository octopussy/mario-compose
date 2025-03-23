package dev.octo.mario

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import co.touchlab.kermit.Logger

fun main() = application {
    val input = remember { mutableStateOf(PlayerInput()) }
    val keyMap = remember { mutableMapOf<Key, Boolean>() }

    val windowState = rememberWindowState()

    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        resizable = true,
        onPreviewKeyEvent = { key ->
            if (key.type == KeyEventType.KeyDown) {
                keyMap[key.key] = true
            } else if (key.type == KeyEventType.KeyUp){
                keyMap[key.key] = false
            }

            input.value = input.value.copy(
                leftPressed = keyMap[Key.A] ?: false,
                upPressed = keyMap[Key.W] ?: false,
                rightPressed = keyMap[Key.D] ?: false,
                downPressed = keyMap[Key.S] ?: false,
            )
            true
        },
        title = "mario-compose",
    ) {
        App(input = input.value)
    }
}
