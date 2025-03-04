package top.yukonga.miuix.kmp.basic

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.ColorUtils
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape

/**
 * A [ColorPicker] component with Miuix style.
 *
 * @param initialColor The initial color of the picker.
 * @param onColorChanged The callback to be called when the color changes.
 * @param modifier The modifier to be applied to the color picker.
 */
@Composable
fun ColorPicker(
    initialColor: Color = MiuixTheme.colorScheme.primary,
    onColorChanged: (Color) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var initialSetup by remember { mutableStateOf(true) }
    var currentHue by remember { mutableStateOf(0f) }
    var currentSaturation by remember { mutableStateOf(0f) }
    var currentValue by remember { mutableStateOf(0f) }
    var currentAlpha by remember { mutableStateOf(1f) }

    // Set initial HSV values only once
    LaunchedEffect(initialColor, initialSetup) {
        if (initialSetup) {
            val hsv = FloatArray(3)
            ColorUtils.rgbToHsv(
                (initialColor.red * 255).toInt(),
                (initialColor.green * 255).toInt(),
                (initialColor.blue * 255).toInt(),
                hsv
            )
            currentHue = hsv[0]
            currentSaturation = hsv[1]
            currentValue = hsv[2]
            currentAlpha = initialColor.alpha
            initialSetup = false
        }
    }

    // Current selected color
    val selectedColor = Color.hsv(currentHue, currentSaturation, currentValue, currentAlpha)

    // Track previous color to prevent recomposition loops
    var previousColor by remember { mutableStateOf(selectedColor) }

    // Only trigger callback when colors actually change from user interaction
    LaunchedEffect(currentHue, currentSaturation, currentValue, currentAlpha) {
        if (!initialSetup && selectedColor != previousColor) {
            previousColor = selectedColor
            onColorChanged(selectedColor)
        }
    }

    // Color preview
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(26.dp)
            .clip(SmoothRoundedCornerShape(13.dp))
            .background(selectedColor)
    )

    Spacer(modifier = Modifier.height(12.dp))

    // Hue selection
    HueSlider(
        currentHue = currentHue,
        onHueChanged = { newHue -> currentHue = newHue * 360f }
    )

    Spacer(modifier = Modifier.height(12.dp))

    // Saturation selection
    SaturationSlider(
        currentHue = currentHue,
        currentSaturation = currentSaturation,
        onSaturationChanged = { currentSaturation = it }
    )

    Spacer(modifier = Modifier.height(12.dp))

    // Value selection
    ValueSlider(
        currentHue = currentHue,
        currentSaturation = currentSaturation,
        currentValue = currentValue,
        onValueChanged = { currentValue = it }
    )

    Spacer(modifier = Modifier.height(12.dp))

    // Alpha selection
    AlphaSlider(
        currentHue = currentHue,
        currentSaturation = currentSaturation,
        currentValue = currentValue,
        currentAlpha = currentAlpha,
        onAlphaChanged = { currentAlpha = it }
    )
}


/**
 * A [HueSlider] component for selecting the hue of a color.
 *
 * @param currentHue The current hue value.
 * @param onHueChanged The callback to be called when the hue changes.
 */
@Composable
fun HueSlider(
    currentHue: Float,
    onHueChanged: (Float) -> Unit,
) {
    ColorSlider(
        value = currentHue / 360f,
        onValueChanged = onHueChanged,
        drawBrush = {
            val width = size.width
            for (i in 0 until width.toInt()) {
                val hue = i / width * 360f
                drawLine(
                    color = Color.hsv(hue, 1f, 1f),
                    start = Offset(i.toFloat(), 0f),
                    end = Offset(i.toFloat(), size.height),
                    strokeWidth = 1f
                )
            }
        },
        indicatorColor = { Color.hsv(currentHue, 1f, 1f) },
        modifier = Modifier.fillMaxWidth()
    )
}

/**
 * A [SaturationSlider] component for selecting the saturation of a color.
 *
 * @param currentHue The current hue value.
 * @param currentSaturation The current saturation value.
 * @param onSaturationChanged The callback to be called when the saturation changes.
 */
@Composable
fun SaturationSlider(
    currentHue: Float,
    currentSaturation: Float,
    onSaturationChanged: (Float) -> Unit,
) {
    ColorSlider(
        value = currentSaturation,
        onValueChanged = onSaturationChanged,
        drawBrush = {
            val brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.hsv(currentHue, 0f, 1f, 1f),
                    Color.hsv(currentHue, 1f, 1f, 1f)
                )
            )
            drawRect(brush = brush)
        },
        indicatorColor = { Color.hsv(currentHue, currentSaturation, 1f) },
        modifier = Modifier.fillMaxWidth()
    )
}


/**
 * A [ValueSlider] component for selecting the value of a color.
 *
 * @param currentHue The current hue value.
 * @param currentSaturation The current saturation value.
 * @param currentValue The current value value.
 * @param onValueChanged The callback to be called when the value changes.
 */
@Composable
fun ValueSlider(
    currentHue: Float,
    currentSaturation: Float,
    currentValue: Float,
    onValueChanged: (Float) -> Unit,
) {
    ColorSlider(
        value = currentValue,
        onValueChanged = onValueChanged,
        drawBrush = {
            val brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.Black,
                    Color.hsv(currentHue, currentSaturation, 1f)
                )
            )
            drawRect(brush = brush)
        },
        indicatorColor = { Color.hsv(currentHue, currentSaturation, currentValue) },
        modifier = Modifier.fillMaxWidth()
    )
}

/**
 * A [AlphaSlider] component for selecting the alpha of a color.
 *
 * @param currentHue The current hue value.
 * @param currentSaturation The current saturation value.
 * @param currentValue The current value value.
 * @param currentAlpha The current alpha value.
 * @param onAlphaChanged The callback to be called when the alpha changes.
 */
@Composable
fun AlphaSlider(
    currentHue: Float,
    currentSaturation: Float,
    currentValue: Float,
    currentAlpha: Float,
    onAlphaChanged: (Float) -> Unit,
) {
    ColorSlider(
        value = currentAlpha,
        onValueChanged = onAlphaChanged,
        drawBrush = {
            val brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.hsv(currentHue, currentSaturation, currentValue, 0f),
                    Color.hsv(currentHue, currentSaturation, currentValue, 1f)
                )
            )
            drawRect(brush = brush)
        },
        indicatorColor = { Color.hsv(currentHue, currentSaturation, currentValue, currentAlpha) },
        modifier = Modifier.fillMaxWidth()
    )
}

/**
 * Generic slider component for color selection.
 */
@Composable
private fun ColorSlider(
    value: Float,
    onValueChanged: (Float) -> Unit,
    drawBrush: DrawScope.() -> Unit,
    indicatorColor: () -> Color,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    var sliderWidth by remember { mutableStateOf(0.dp) }
    val indicatorSizeDp = 20.dp
    val sliderSizePx = with(density) { 26.dp.toPx() }

    Box(
        modifier = modifier
            .height(26.dp)
            .clip(SmoothRoundedCornerShape(13.dp))
    ) {
        // Draw gradient
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned { coordinates ->
                    sliderWidth = with(density) { coordinates.size.width.toDp() }
                }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { change, _ ->
                        change.consume()
                        handleSliderInteraction(change.position.x, size.width.toFloat(), sliderSizePx, onValueChanged)
                    }
                }
        ) {
            drawBrush()
        }

        // Current value indicator
        SliderIndicator(
            modifier = Modifier.align(Alignment.CenterStart),
            value = value,
            sliderWidth = sliderWidth,
            sliderSizePx = sliderSizePx,
            indicatorSize = indicatorSizeDp,
            indicatorColor = indicatorColor()
        )
    }
}

@Composable
private fun SliderIndicator(
    modifier: Modifier,
    value: Float,
    sliderWidth: androidx.compose.ui.unit.Dp,
    sliderSizePx: Float,
    indicatorSize: androidx.compose.ui.unit.Dp,
    indicatorColor: Color
) {
    val density = LocalDensity.current
    Box(
        modifier = modifier
            .offset(
                x = with(density) {
                    val effectiveWidth = sliderWidth.toPx() - sliderSizePx
                    ((value * effectiveWidth) + sliderSizePx / 2).toDp() - (indicatorSize / 2)
                }
            )
            .size(indicatorSize)
            .clip(RoundedCornerShape(50.dp))
            .border(6.dp, Color.White, RoundedCornerShape(50.dp))
            .background(indicatorColor, RoundedCornerShape(50.dp))
    )
}

/**
 * Handle slider interaction and calculate new value.
 */
private fun handleSliderInteraction(
    positionX: Float,
    totalWidth: Float,
    sliderSizePx: Float,
    onValueChanged: (Float) -> Unit
) {
    val sliderHalfSizePx = sliderSizePx / 2
    val effectiveWidth = totalWidth - sliderSizePx
    val constrainedX = positionX.coerceIn(sliderHalfSizePx, totalWidth - sliderHalfSizePx)
    val newPosition = (constrainedX - sliderHalfSizePx) / effectiveWidth
    onValueChanged(newPosition.coerceIn(0f, 1f))
}