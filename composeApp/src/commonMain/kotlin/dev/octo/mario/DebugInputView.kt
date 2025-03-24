package dev.octo.mario

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DebugInputView(input: PlayerInput) {
    @Composable
    fun Btn(modifier: Modifier, text: String, pressed: Boolean) {
        Box(
            modifier = modifier
                .size(35.dp)
                .drawBehind {
                    drawRect(color = if (pressed) Color.Green else Color.LightGray)
                },
            contentAlignment = Alignment.Center
        ) {
            Text(text = text, fontSize = 9.sp)
        }
    }

    Row {
        Box(modifier = Modifier.size(110.dp).padding(4.dp)) {
            Btn(
                modifier = Modifier.align(Alignment.CenterStart),
                text = "LEFT",
                pressed = input.leftPressed
            )
            Btn(
                modifier = Modifier.align(Alignment.TopCenter),
                text = "UP",
                pressed = input.upPressed
            )
            Btn(
                modifier = Modifier.align(Alignment.CenterEnd),
                text = "RIGHT",
                pressed = input.rightPressed
            )
            Btn(
                modifier = Modifier.align(Alignment.BottomCenter),
                text = "DOWN",
                pressed = input.downPressed
            )
        }

        Btn(
            modifier = Modifier,
            text = "JUMP",
            pressed = input.jumpPressed
        )
        Btn(
            modifier = Modifier,
            text = "FIRE",
            pressed = input.firePressed
        )

    }
}
