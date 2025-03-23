package dev.octo.mario

data class PlayerInput(
    val leftPressed: Boolean = false,
    val rightPressed: Boolean = false,
    val upPressed: Boolean = false,
    val downPressed: Boolean = false
)
