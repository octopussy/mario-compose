package dev.octo.mario

import androidx.compose.runtime.mutableStateOf

typealias TickListener = (dt: Double) -> Unit

class GameTicker {
    val currentFrame = mutableStateOf(0)

    private val scrollSpeed = 100
    val lastInput = mutableStateOf(PlayerInput())

    private val tickListeners = mutableSetOf<TickListener>()

    fun handleInput(input: PlayerInput) {
        lastInput.value = input
    }

    fun tick(deltaTime: Double) {
        tickListeners.forEach { it(deltaTime) }
        ++currentFrame.value
    }

    fun onTick(block: TickListener) {
        tickListeners.add(block)
    }
}
