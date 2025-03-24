package dev.octo.mario

import androidx.compose.runtime.mutableStateOf

typealias TickListener = (dt: Double) -> Unit

class GameTicker {
    private val tickListeners = mutableSetOf<TickListener>()

    val lastInput = mutableStateOf(PlayerInput())
    val currentFrame = mutableStateOf(0)

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
