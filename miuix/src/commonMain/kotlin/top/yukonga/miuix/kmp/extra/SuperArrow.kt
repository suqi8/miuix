package top.yukonga.miuix.kmp.extra

import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.BasicComponentColors
import top.yukonga.miuix.kmp.basic.BasicComponentDefaults
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.basic.ArrowRight
import top.yukonga.miuix.kmp.interfaces.HoldDownInteraction
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * A arrow with a title and a summary.
 *
 * @param title The title of the [SuperArrow].
 * @param modifier The modifier to be applied to the [SuperArrow].
 * @param titleColor The color of the title.
 * @param summary The summary of the [SuperArrow].
 * @param summaryColor The color of the summary.
 * @param leftAction The [Composable] content that on the left side of the [SuperArrow].
 * @param rightText The text on the right side of the [SuperArrow].
 * @param rightActionColor The color of the right action.
 * @param onClick The callback when the [SuperArrow] is clicked.
 * @param holdDownState Used to determine whether it is in the pressed state.
 * @param insideMargin The margin inside the [SuperArrow].
 * @param enabled Whether the [SuperArrow] is clickable.
 */
@Composable
fun SuperArrow(
    title: String,
    modifier: Modifier = Modifier,
    titleColor: BasicComponentColors = BasicComponentDefaults.titleColor(),
    summary: String? = null,
    summaryColor: BasicComponentColors = BasicComponentDefaults.summaryColor(),
    leftAction: @Composable (() -> Unit)? = null,
    rightText: String? = null,
    rightActionColor: RightActionColors = SuperArrowDefaults.rightActionColors(),
    onClick: (() -> Unit)? = null,
    holdDownState: Boolean = false,
    insideMargin: PaddingValues = BasicComponentDefaults.InsideMargin,
    enabled: Boolean = true
) {
    val updatedOnClick by rememberUpdatedState(onClick)
    val coroutineScope = rememberCoroutineScope()
    val interactionSource = remember { MutableInteractionSource() }
    val holdDown = remember { mutableStateOf<HoldDownInteraction.HoldDown?>(null) }

    if (!holdDownState) {
        holdDown.value?.let { oldValue ->
            coroutineScope.launch {
                interactionSource.emit(HoldDownInteraction.Release(oldValue))
            }
            holdDown.value = null
        }
    }

    BasicComponent(
        modifier = modifier,
        insideMargin = insideMargin,
        title = title,
        titleColor = titleColor,
        summary = summary,
        summaryColor = summaryColor,
        leftAction = leftAction,
        rightActions = {
            if (rightText != null) {
                Text(
                    modifier = Modifier.widthIn(max = 130.dp),
                    text = rightText,
                    fontSize = MiuixTheme.textStyles.body2.fontSize,
                    color = rightActionColor.color(enabled),
                    textAlign = TextAlign.End,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )
            }
            Image(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(10.dp, 16.dp),
                imageVector = MiuixIcons.Basic.ArrowRight,
                contentDescription = null,
                colorFilter = ColorFilter.tint(rightActionColor.color(enabled)),
            )
        },
        onClick = {
            if (enabled) {
                updatedOnClick?.invoke()
                coroutineScope.launch {
                    interactionSource.emit(HoldDownInteraction.HoldDown().also {
                        holdDown.value = it
                    })
                }
            }
        },
        enabled = enabled,
        interactionSource = interactionSource
    )
}

object SuperArrowDefaults {

    /**
     * The default color of the arrow.
     */
    @Composable
    fun rightActionColors() = RightActionColors(
        color = MiuixTheme.colorScheme.onSurfaceVariantActions,
        disabledColor = MiuixTheme.colorScheme.disabledOnSecondaryVariant
    )

}


@Immutable
class RightActionColors(
    private val color: Color,
    private val disabledColor: Color
) {
    @Stable
    internal fun color(enabled: Boolean): Color = if (enabled) color else disabledColor
}
