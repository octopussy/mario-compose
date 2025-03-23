package dev.octo.mario

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(input: PlayerInput) {
    MaterialTheme {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            WorldView(input)
            DebugInputView(input)
        }
    }
}
