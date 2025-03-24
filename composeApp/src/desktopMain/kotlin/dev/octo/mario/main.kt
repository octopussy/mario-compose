package dev.octo.mario

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import co.touchlab.kermit.Logger

fun main() = application {
    val input = remember { mutableStateOf(PlayerInput()) }
    val keyMap = remember { mutableMapOf<Key, Boolean>() }

    val windowState = rememberWindowState(
        size = DpSize(1280.dp, 720.dp)
    )

    fun isKeyPressed(k: Key): Boolean {
        return keyMap[k] ?: false
    }

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
                leftPressed = isKeyPressed(Key.A) || isKeyPressed(Key.DirectionLeft),
                upPressed = isKeyPressed(Key.W) || isKeyPressed(Key.DirectionUp),
                rightPressed = isKeyPressed(Key.D) || isKeyPressed(Key.DirectionRight),
                downPressed = isKeyPressed(Key.S) || isKeyPressed(Key.DirectionDown),
                jumpPressed = isKeyPressed(Key.Spacebar) || isKeyPressed(Key.J),
                firePressed = isKeyPressed(Key.Z) || isKeyPressed(Key.K),
            )
            true
        },
        title = "mario-compose",
    ) {
        App(input = input.value)
    }
}
