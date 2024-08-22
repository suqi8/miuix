import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.MiuixBox
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.squircleshape.SquircleShape

@Composable
fun MiuixCheckbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = remember { MutableInteractionSource() }
) {
    val isChecked by rememberUpdatedState(checked)
    var isPressed by remember { mutableStateOf(false) }
    val backgroundColor by animateColorAsState(
        if (isChecked) MiuixTheme.colorScheme.primary else MiuixTheme.colorScheme.switchThumb,
        animationSpec = tween(durationMillis = 200)
    )
    val disabledBackgroundColor by rememberUpdatedState(
        if (isChecked) MiuixTheme.colorScheme.disabledBg else MiuixTheme.colorScheme.primaryContainer
    )
    val checkboxSize by animateDpAsState(if (isPressed) 20.dp else 22.dp)
    val checkmarkColor by animateColorAsState(if (checked) Color.White else Color.Transparent)
    val toggleableModifier = remember(onCheckedChange, isChecked, enabled) {
        if (onCheckedChange != null) {
            Modifier.toggleable(
                value = isChecked,
                onValueChange = {
                    onCheckedChange(it)
                },
                enabled = enabled,
                role = Role.Checkbox,
                interactionSource = interactionSource,
                indication = null
            )
        } else {
            Modifier
        }
    }

    MiuixBox(
        modifier = modifier
            .then(toggleableModifier)
            .wrapContentSize(Alignment.Center)
            .size(22.dp)
            .requiredSize(checkboxSize)
            .clip(SquircleShape(100.dp))
            .background(if (enabled) backgroundColor else disabledBackgroundColor)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                    },
                    onTap = {
                        isPressed = false
                        if (enabled) {
                            onCheckedChange?.invoke(!isChecked)
                        }
                    }
                )
            }
    ) {
        Canvas(
            modifier = Modifier.requiredSize(checkboxSize)
        ) {
            val svgPath =
                "m400-416 236-236q11-11 28-11t28 11q11 11 11 28t-11 28L428-332q-12 12-28 12t-28-12L268-436q-11-11-11-28t11-28q11-11 28-11t28 11l76 76Z"
            val path = PathParser().parsePathString(svgPath).toPath()
            val scaleFactor = size.minDimension / 960f
            path.transform(Matrix().apply {
                scale(scaleFactor, scaleFactor)
                translate(0f, 960f)
            })
            drawPath(path, checkmarkColor)
        }
    }
}