package dev.octo.mario

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform