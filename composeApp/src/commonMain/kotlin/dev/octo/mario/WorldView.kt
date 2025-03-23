package dev.octo.mario

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import mario_compose.composeapp.generated.resources.Res
import mario_compose.composeapp.generated.resources.dirt
import mario_compose.composeapp.generated.resources.mario
import org.jetbrains.compose.resources.imageResource
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sign

private val ColorSky = Color(0xff5c94fc)

const val WorldCellSize = 16
const val WorldHeightInCells = 15

const val LevelWidth = 211
const val LevelHeight = 30

const val DesiredFrameTime = 1 / 60.0
const val DesiredFrameTimeMs = (DesiredFrameTime * 1000).toLong()

const val Gravity = 500.0
const val PlayerStartWalkAcceleration = 700.0
const val PlayerWalkSpeed = 120.0

fun createLevel(): Map<IntOffset, Boolean> {
    val level = mutableMapOf<IntOffset, Boolean>()
    (0 until LevelWidth).forEach { x ->
        level[IntOffset(x, 13)] = true
        level[IntOffset(x, 14)] = true
    }

    level[IntOffset(12, 13)] = false
    level[IntOffset(12, 14)] = false
    level[IntOffset(13, 13)] = false
    level[IntOffset(13, 14)] = false
    level[IntOffset(14, 13)] = false
    level[IntOffset(14, 14)] = false

    return level
}

@Composable
fun BoxWithConstraintsScope.WorldView(input: PlayerInput, ticker: GameTicker) {
    val density = LocalDensity.current

    val level = remember { createLevel() }

    val dirtImage = imageResource(Res.drawable.dirt)
    val marioImage = imageResource(Res.drawable.mario)

    var worldScrollX by remember { mutableStateOf(0) }

    var playerPosition by remember { mutableStateOf(IntOffset(64, 0)) }
    var playerVelocityX by remember { mutableStateOf(0.0) }
    var playerVelocityY by remember { mutableStateOf(0.0) }

    val (windowWidthPx, windowHeightPx) = with(density) {
        this@WorldView.maxWidth.toPx() to this@WorldView.maxHeight.toPx()
    }

    val scale = remember(windowHeightPx) { windowHeightPx / (WorldHeightInCells * WorldCellSize).toFloat() }

    // scaled
    val (windowWidthScaled, windowHeightScaled) = windowWidthPx / scale to windowHeightPx / scale

    fun checkOnGround(x: Int, y: Int, w: Int, h: Int): Int? {
        for (cell in level) {
            if (!cell.value) continue
            val co = cell.key
            val coX = co.x * WorldCellSize
            val coY = co.y * WorldCellSize
            val onVerticalLine = !((x < coX && x + w < coX) || (x > coX + WorldCellSize && x + w > coX + WorldCellSize))
            if (y + h > coY && y < coY && onVerticalLine) {
                return coY
            }
        }
        return null
    }

    remember {
        ticker.onTick { dt ->
            // handle input
            val currentInput = ticker.lastInput.value

            val inputDir = when {
                currentInput.rightPressed -> 1
                currentInput.leftPressed -> -1
                else -> 0
            }
            val currentDir = sign(playerVelocityX).toInt()
            var currentVelXAbs = abs(playerVelocityX)

            if (inputDir != currentDir && inputDir != 0 && currentDir != 0) {
                currentVelXAbs = 0.0
            }

            if (currentInput.rightPressed || currentInput.leftPressed) {
                val newVelAbs = (currentVelXAbs + PlayerStartWalkAcceleration * dt).coerceAtMost(PlayerWalkSpeed)
                playerVelocityX = newVelAbs * inputDir
            } else {
                val newVelAbs = (currentVelXAbs - PlayerStartWalkAcceleration * dt).coerceAtLeast(0.0)
                playerVelocityX = newVelAbs * currentDir
            }

            // apply gravity
            val groundLevel = checkOnGround(playerPosition.x, playerPosition.y, 16, 16)
            if (groundLevel == null) {
                playerVelocityY += Gravity * dt
            } else {
                playerVelocityY = 0.0
            }

            val newX = (playerPosition.x + playerVelocityX * dt).roundToInt()
            var newY = groundLevel ?: (playerPosition.y + playerVelocityY * dt).roundToInt()
            if (groundLevel != null) {
                newY -= 16
            }
            playerPosition = playerPosition.copy(x = newX, y = newY)

            worldScrollX = (newX - windowWidthScaled / 2.0)
                .roundToInt()
                .coerceAtLeast(0)
        }
    }

    val cellSize = IntSize(WorldCellSize, WorldCellSize)

    fun isCellVisible(cx: Int, cy: Int): Boolean = !(cx > windowWidthScaled ||
            cy > windowHeightScaled ||
            (cx + WorldCellSize) < 0 ||
            (cy + WorldCellSize) < 0)

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(color = ColorSky)

        var imagesDrawn = 0
        val scrollX = worldScrollX

        scale(scale, pivot = Offset(0f, 0f)) {
            level.forEach { cell ->
                val cellX = cell.key.x * WorldCellSize - scrollX
                val cellY = cell.key.y * WorldCellSize

                if (cell.value && isCellVisible(cellX, cellY)) {
                    drawImage(
                        image = dirtImage,
                        filterQuality = FilterQuality.None,
                        dstOffset = IntOffset(cellX, cellY),
                        dstSize = cellSize
                    )

                    ++imagesDrawn
                }
            }

            val playerX = playerPosition.x - scrollX
            val playerY = playerPosition.y
            drawImage(
                image = marioImage,
                filterQuality = FilterQuality.None,
                srcSize = cellSize,
                dstOffset = IntOffset(playerX, playerY),
                dstSize = IntSize(WorldCellSize, WorldCellSize)
            )

        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        // Text("Vel: $playerVelocityX $playerVelocityY", color = Color.White)
    }
}
