package dev.octo.mario

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import dev.octo.mario.util.currentTimeMillis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun App(input: PlayerInput) {
    val ticker = remember { GameTicker() }
    val updateScope = rememberCoroutineScope { Dispatchers.Default }

    DisposableEffect(Unit) {
        val job = updateScope.launch {
            var lastFrameTimeMs = currentTimeMillis()
            while (isActive) {
                val timeNowMs = currentTimeMillis()
                val deltaTimeMs = timeNowMs - lastFrameTimeMs
                val deltaTimeSec = deltaTimeMs / 1000.0
                ticker.tick(deltaTimeSec)
                lastFrameTimeMs = timeNowMs
                val idleTime = (DesiredFrameTimeMs - deltaTimeMs).coerceAtLeast(0)
                delay(idleTime.milliseconds)
            }
        }
        onDispose {
            job.cancel()
        }
    }

    LaunchedEffect(input) {
        ticker.handleInput(input)
    }

    MaterialTheme {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            WorldView(ticker)
            DebugInputView(input)
        }
    }
}
