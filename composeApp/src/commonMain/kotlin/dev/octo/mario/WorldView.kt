package dev.octo.mario

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import co.touchlab.kermit.Logger
import dev.octo.mario.util.currentTimeMillis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import mario_compose.composeapp.generated.resources.Res
import mario_compose.composeapp.generated.resources.dirt
import org.jetbrains.compose.resources.imageResource
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.microseconds
import kotlin.time.Duration.Companion.milliseconds

private val ColorSky = Color(0xff5c94fc)

const val WorldCellSize = 16
const val WorldHeightInCells = 15

const val LevelWidth = 211
const val LevelHeight = 30

const val DesiredFrameTime = 1 / 60.0
const val DesiredFrameTimeMs = ((1 / 60.0) * 1000).toLong()

fun createLevel(): Map<IntOffset, Boolean> {
    val level = mutableMapOf<IntOffset, Boolean>()
    (0 until LevelWidth).forEach { x ->
        level[IntOffset(x, 13)] = true
        level[IntOffset(x, 14)] = true
    }

    return level
}

class WorldTick {
    val scrollX = mutableStateOf(0f)

    private val scrollSpeed = 50
    private var lastInput = PlayerInput()

    fun handleInput(input: PlayerInput) {
        lastInput = input
    }

    fun tick(deltaTime: Double) {
        val dir = if (lastInput.rightPressed) {
            1
        } else if (lastInput.leftPressed){
            -1
        } else {
            0
        }

        scrollX.value += (scrollSpeed * deltaTime).toFloat() * dir
    }
}

@Composable
fun BoxWithConstraintsScope.WorldView(input: PlayerInput) {
    val level = remember { createLevel() }

    val ticker = remember { WorldTick() }

    val image = imageResource(Res.drawable.dirt)
    val density = LocalDensity.current

    val updateScope = rememberCoroutineScope {
        Dispatchers.Default
    }

    DisposableEffect(Unit) {
        val job = updateScope.launch {
            var lastFrameTimeMs = currentTimeMillis()
            while (isActive) {
                val timeNowMs = currentTimeMillis()
                val deltaTimeMs = timeNowMs - lastFrameTimeMs
                val deltaTimeSec = deltaTimeMs / 1000.0
                Logger.d { "TIME: $deltaTimeSec" }
                ticker.tick(deltaTimeSec)
                lastFrameTimeMs = timeNowMs

                val freeTime = (DesiredFrameTimeMs - deltaTimeMs).coerceAtLeast(0)
                delay(freeTime.milliseconds)
            }
        }
        onDispose {
            job.cancel()
        }
    }

    LaunchedEffect(input) {
        ticker.handleInput(input)
    }

    val (_, windowHeight) = with(density) {
        this@WorldView.maxWidth.toPx() to this@WorldView.maxHeight.toPx()
    }

    val scale = remember(windowHeight) { windowHeight / (WorldHeightInCells * WorldCellSize).toFloat() }

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(color = ColorSky)

        scale(scale, pivot = Offset(0f, 0f)) {
            level.forEach { cell ->
                if (cell.value) {
                    val x = cell.key.x * WorldCellSize
                    val y = cell.key.y * WorldCellSize
                    drawImage(
                        image = image,
                        filterQuality = FilterQuality.None,
                        dstOffset = IntOffset(x - ticker.scrollX.value.roundToInt(), y),
                        dstSize = IntSize(WorldCellSize, WorldCellSize)
                    )
                }
            }
        }
    }
}
