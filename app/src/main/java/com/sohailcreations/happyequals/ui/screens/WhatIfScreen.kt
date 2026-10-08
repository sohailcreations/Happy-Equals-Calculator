package com.sohailcreations.happyequals.ui.screens

import com.sohailcreations.happyequals.data.HistoryRepository
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll

import androidx.compose.material3.*

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.composables.icons.lucide.*
import com.sohailcreations.happyequals.domain.CalculatorEngine
import com.sohailcreations.happyequals.ui.theme.*

import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

import kotlin.math.abs
import kotlin.math.roundToInt

import kotlinx.coroutines.delay

// ==========================================
// HAPPY EQUALS - LIVE WHAT-IF MODE
// ==========================================

@Composable
fun WhatIfScreen() {

    val context = LocalContext.current

    var expression by rememberSaveable {
        mutableStateOf("1250×x%")
    }

    var originalValue by rememberSaveable {
        mutableStateOf("18")
    }

    var trialValue by rememberSaveable {
        mutableStateOf("18")
    }

    var isDegrees by rememberSaveable {
        mutableStateOf(true)
    }


    var lastSavedKey by rememberSaveable {
        mutableStateOf("")
    }

    val currentSaveKey = "$expression|$trialValue|$isDegrees"

    val originalResult = remember(
        expression,
        originalValue,
        isDegrees
    ) {

        runCatching {

            evaluateWhatIf(
                expression = expression,
                x = originalValue.toDouble(),
                isDegrees = isDegrees
            )

        }.getOrNull()
    }

    val trialResult = remember(
        expression,
        trialValue,
        isDegrees
    ) {

        runCatching {

            evaluateWhatIf(
                expression = expression,
                x = trialValue.toDouble(),
                isDegrees = isDegrees
            )

        }.getOrNull()
    }

    val difference = if (
        originalResult != null &&
        trialResult != null
    ) {

        trialResult - originalResult

    } else {

        null
    }

    val sliderValue = trialValue
        .toFloatOrNull()
        ?.takeIf { it.isFinite() }
        ?.coerceIn(0f, 100f)
        ?: 0f

    val scrollState = rememberScrollState()

    // No imePadding here.
    // Android adjustResize handles keyboard resizing.

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .verticalScroll(scrollState)
            .padding(
                horizontal = 18.dp,
                vertical = 16.dp
            ),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {

        // ==================================
        // HEADER
        // ==================================

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(14.dp),
                color = AccentBlue.copy(alpha = 0.08f)
            ) {

                Box(
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Lucide.SlidersHorizontal,
                        contentDescription = null,
                        tint = AccentBlue,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Live What-if",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = "Explore different outcomes",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            IconButton(
                onClick = {

                    expression = "1250×x%"
                    originalValue = "18"
                    trialValue = "18"
                    isDegrees = true
                }
            ) {

                Icon(
                    imageVector = Lucide.RotateCcw,
                    contentDescription = "Reset What-if",
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // ==================================
        // EXPRESSION CARD
        // ==================================

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            color = AppSurface,
            border = BorderStroke(
                width = 1.dp,
                color = AppBorder
            )
        ) {

            Column(
                modifier = Modifier.padding(17.dp),
                verticalArrangement = Arrangement.spacedBy(13.dp)
            ) {

                HappyFloatingInput(
                    label = "Expression",
                    value = expression,
                    onValueChange = {
                        expression = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardType = KeyboardType.Text
                )

                Text(
                    text = "Use x for the number you want to change.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {

                    WhatIfPreset(
                        title = "Percent",
                        selected = expression == "1250×x%",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            expression = "1250×x%"
                        }
                    )

                    WhatIfPreset(
                        title = "Addition",
                        selected = expression == "500+x",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            expression = "500+x"
                        }
                    )

                    WhatIfPreset(
                        title = "Power",
                        selected = expression == "x^2",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            expression = "x^2"
                        }
                    )
                }

                HorizontalDivider(
                    color = AppBorder
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Text(
                        text = "Angle mode",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )

                    SingleChoiceSegmentedButtonRow {

                        SegmentedButton(
                            selected = isDegrees,
                            onClick = {
                                isDegrees = true
                            },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = 0,
                                count = 2
                            ),
                            label = {
                                Text("DEG")
                            }
                        )

                        SegmentedButton(
                            selected = !isDegrees,
                            onClick = {
                                isDegrees = false
                            },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = 1,
                                count = 2
                            ),
                            label = {
                                Text("RAD")
                            }
                        )
                    }
                }
            }
        }

        // ==================================
        // ORIGINAL AND TRIAL VALUES
        // ==================================

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            color = AppSurface,
            border = BorderStroke(
                width = 1.dp,
                color = AppBorder
            )
        ) {

            Column(
                modifier = Modifier.padding(17.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Text(
                    text = "ADJUST VARIABLE",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = TextSecondary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    HappyFloatingInput(
                        label = "Original x",
                        value = originalValue,
                        onValueChange = {
                            originalValue = it
                        },
                        modifier = Modifier.weight(1f),
                        keyboardType = KeyboardType.Decimal
                    )

                    HappyFloatingInput(
                        label = "Try x",
                        value = trialValue,
                        onValueChange = {
                            trialValue = it
                        },
                        modifier = Modifier.weight(1f),
                        keyboardType = KeyboardType.Decimal
                    )
                }

                HorizontalDivider(
                    color = AppBorder
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "Live adjustment",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )

                    Surface(
                        shape = CircleShape,
                        color = AccentBlue.copy(alpha = 0.08f)
                    ) {

                        Text(
                            text = trialValue,
                            modifier = Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 6.dp
                            ),
                            color = AccentBlue,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }

                Slider(
                    value = sliderValue,
                    onValueChange = { value ->

                        trialValue = value
                            .roundToInt()
                            .toString()
                    },
                    valueRange = 0f..100f,
                    steps = 99,
                    colors = SliderDefaults.colors(
                        thumbColor = AccentBlue,
                        activeTrackColor = AccentBlue,
                        inactiveTrackColor = AppBorder
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Text(
                        text = "0",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )

                    Text(
                        text = "100",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }

                Text(
                    text = "Use the slider or enter a value manually.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        // ==================================
        // LIVE RESULTS
        // ==================================

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(),
            shape = RoundedCornerShape(22.dp),
            color = AppSurface,
            border = BorderStroke(
                width = 1.dp,
                color = AppBorder
            )
        ) {

            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "LIVE RESULTS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = TextSecondary
                    )

                    Surface(
                        shape = CircleShape,
                        color = AccentTeal.copy(alpha = 0.10f)
                    ) {

                        Text(
                            text = "● LIVE",
                            modifier = Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 6.dp
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            color = AccentTeal,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = trialResult?.let {
                        formatWhatIfNumber(it)
                    } ?: "—",
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.displayMedium,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentBlue,
                    textAlign = TextAlign.End
                )

                HorizontalDivider(
                    color = AppBorder
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    WhatIfResultCard(
                        title = "Original",
                        value = originalResult?.let {
                            formatWhatIfNumber(it)
                        } ?: "—",
                        modifier = Modifier.weight(1f)
                    )

                    WhatIfResultCard(
                        title = "New result",
                        value = trialResult?.let {
                            formatWhatIfNumber(it)
                        } ?: "—",
                        modifier = Modifier.weight(1f)
                    )
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(15.dp),
                    color = AppSurfaceSoft
                ) {

                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = "Difference",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )

                        Text(
                            text = difference?.let {

                                val prefix = if (it > 0) {
                                    "+"
                                } else {
                                    ""
                                }

                                prefix + formatWhatIfNumber(it)

                            } ?: "—",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = when {

                                difference == null -> {
                                    TextSecondary
                                }

                                difference > 0 -> {
                                    AccentTeal
                                }

                                difference < 0 -> {
                                    AccentPurple
                                }

                                else -> {
                                    TextPrimary
                                }
                            }
                        )
                    }
                }

                if (
                    originalResult == null ||
                    trialResult == null
                ) {

                    Text(
                        text = when {

                            !expression.contains("x") -> {
                                "Add x to your expression."
                            }

                            originalValue.toDoubleOrNull() == null ||
                                    trialValue.toDoubleOrNull() == null -> {
                                "Enter valid numbers for x."
                            }

                            else -> {
                                "Check your expression and values."
                            }
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = ErrorColor
                    )
                }
            }
        }


// ==========================================
// SAVE WHAT-IF RESULT TO HISTORY
// ==========================================

        val canSave = originalResult != null &&
                trialResult != null &&
                lastSavedKey != currentSaveKey

        OutlinedButton(
            onClick = {

                val result = trialResult

                if (
                    result != null &&
                    originalResult != null &&
                    lastSavedKey != currentSaveKey
                ) {

                    // Convert x into its selected numeric value.
                    // This preserves a calculable expression
                    // in the history rather than saving
                    // an unresolved variable.

                    val normalizedX = BigDecimal.valueOf(
                        trialValue.toDouble()
                    )
                        .stripTrailingZeros()
                        .toPlainString()

                    val savedExpression = expression.replace(
                        "x",
                        "($normalizedX)"
                    )

                    HistoryRepository.add(
                        context = context,
                        expression = savedExpression,
                        result = formatWhatIfNumber(result),
                        source = if (isDegrees) {
                            "What-if · DEG"
                        } else {
                            "What-if · RAD"
                        }
                    )

                    lastSavedKey = currentSaveKey

                    Toast.makeText(
                        context,
                        "Saved to history",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            enabled = canSave,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(
                width = 1.dp,
                color = if (canSave) {
                    AccentBlue.copy(alpha = 0.35f)
                } else {
                    AppBorder
                }
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = if (canSave) {
                    AccentBlue.copy(alpha = 0.05f)
                } else {
                    AppSurfaceSoft
                },
                contentColor = AccentBlue,
                disabledContentColor = TextMuted
            )
        ) {

            Icon(
                imageVector = if (canSave) {
                    Lucide.Save
                } else {
                    Lucide.Check
                },
                contentDescription = null,
                modifier = Modifier.size(19.dp)
            )

            Spacer(
                modifier = Modifier.width(10.dp)
            )

            Text(
                text = if (canSave) {
                    "Save to History"
                } else if (lastSavedKey == currentSaveKey) {
                    "Saved to History"
                } else {
                    "Save to History"
                },
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.labelLarge
            )
        }

        // ==================================
        // COPY RESULT
        // ==================================

        Button(
            onClick = {

                if (trialResult != null) {

                    val clipboard = context.getSystemService(
                        Context.CLIPBOARD_SERVICE
                    ) as ClipboardManager

                    clipboard.setPrimaryClip(
                        ClipData.newPlainText(
                            "Happy Equals Result",
                            formatWhatIfNumber(trialResult)
                        )
                    )

                    Toast.makeText(
                        context,
                        "Result copied",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            enabled = trialResult != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentBlue,
                contentColor = Color.White
            )
        ) {

            Icon(
                imageVector = Lucide.Copy,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "Copy Result",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelLarge
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

// ==========================================
// SMOOTH PLAIN FLOATING INPUT
// ==========================================

@Composable
private fun HappyFloatingInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text
) {

    var isFocused by remember {
        mutableStateOf(false)
    }

    var labelWidthPx by remember(label) {
        mutableIntStateOf(0)
    }

    val focusManager = LocalFocusManager.current

    val keyboardController =
        LocalSoftwareKeyboardController.current

    val bringIntoViewRequester = remember {
        BringIntoViewRequester()
    }

    val keyboardBottom = WindowInsets.ime.getBottom(
        LocalDensity.current
    )

    // Bring only the focused input into view.
    // No artificial spacer or keyboard padding.


    val keyboardVisible =
        WindowInsets.ime.getBottom(
            LocalDensity.current
        ) > 0

    LaunchedEffect(
        isFocused,
        keyboardVisible
    ) {

        if (isFocused) {

            // Wait for the keyboard and layout to settle.
            delay(
                if (keyboardVisible) 220L else 320L
            )

            // Scroll the focused input above the keyboard.
            bringIntoViewRequester.bringIntoView()
        }
    }


    val hasValue = value.isNotEmpty()

    // An existing value keeps the label above
    // the value while unfocused.
    // Focus moves the label onto the top border.

    val labelY by animateDpAsState(
        targetValue = when {

            isFocused -> 3.dp

            hasValue -> 17.dp

            else -> 32.dp
        },
        animationSpec = tween(
            durationMillis = 220,
            easing = FastOutSlowInEasing
        ),
        label = "FloatingLabelY"
    )

    val labelSize by animateFloatAsState(
        targetValue = if (
            isFocused || hasValue
        ) {
            12f
        } else {
            14f
        },
        animationSpec = tween(
            durationMillis = 220,
            easing = FastOutSlowInEasing
        ),
        label = "FloatingLabelSize"
    )

    val labelColor by animateColorAsState(
        targetValue = if (isFocused) {
            AccentBlue
        } else {
            TextSecondary
        },
        animationSpec = tween(
            durationMillis = 220
        ),
        label = "FloatingLabelColor"
    )

    val borderGapProgress by animateFloatAsState(
        targetValue = if (isFocused) {
            1f
        } else {
            0f
        },
        animationSpec = tween(
            durationMillis = 220,
            easing = FastOutSlowInEasing
        ),
        label = "FloatingBorderGap"
    )

    val textTopPadding by animateDpAsState(
        targetValue = if (
            hasValue && !isFocused
        ) {
            14.dp
        } else {
            0.dp
        },
        animationSpec = tween(
            durationMillis = 220,
            easing = FastOutSlowInEasing
        ),
        label = "InputTextPosition"
    )

    val fieldShape = RoundedCornerShape(14.dp)

    Box(
        modifier = modifier
            .height(76.dp)
            .bringIntoViewRequester(
                bringIntoViewRequester
            )
    ) {

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .height(58.dp)
                .clip(fieldShape)
                .background(AppSurfaceSoft)
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            ),
            cursorBrush = SolidColor(AccentBlue),
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {

                    keyboardController?.hide()

                    focusManager.clearFocus()
                }
            ),
            decorationBox = { innerTextField ->

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            start = 16.dp,
                            end = 12.dp,
                            top = textTopPadding
                        ),
                    contentAlignment = Alignment.CenterStart
                ) {

                    innerTextField()
                }
            }
        )

        // Draw a real outline gap for the label.
        // No white overlay or background chip.

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = 12.dp)
                .height(58.dp)
        ) {

            val strokeInset = 1.dp.toPx()

            val radius = 14.dp.toPx()

            val left = strokeInset

            val top = strokeInset

            val right = size.width - strokeInset

            val bottom = size.height - strokeInset

            val gapStart = radius

            val fullGapEnd = (
                    16.dp.toPx() +
                            labelWidthPx.toFloat() +
                            5.dp.toPx()
                    ).coerceAtMost(right - radius)

            val gapEnd = gapStart +
                    (fullGapEnd - gapStart) *
                    borderGapProgress

            val outline = Path().apply {

                moveTo(gapEnd, top)

                lineTo(right - radius, top)

                quadraticBezierTo(
                    right,
                    top,
                    right,
                    top + radius
                )

                lineTo(right, bottom - radius)

                quadraticBezierTo(
                    right,
                    bottom,
                    right - radius,
                    bottom
                )

                lineTo(left + radius, bottom)

                quadraticBezierTo(
                    left,
                    bottom,
                    left,
                    bottom - radius
                )

                lineTo(left, top + radius)

                quadraticBezierTo(
                    left,
                    top,
                    left + radius,
                    top
                )

                lineTo(gapStart, top)
            }

            drawPath(
                path = outline,
                color = if (isFocused) {
                    AccentBlue
                } else {
                    AppBorder
                },
                style = Stroke(
                    width = if (isFocused) {
                        1.5.dp.toPx()
                    } else {
                        1.dp.toPx()
                    }
                )
            )
        }

        // Plain text only.
        // No Surface, white background or label chip.

        Text(
            text = label,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(
                    x = 16.dp,
                    y = labelY
                )
                .onSizeChanged { size ->
                    labelWidthPx = size.width
                },
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = labelSize.sp,
                fontWeight = if (isFocused) {
                    FontWeight.SemiBold
                } else {
                    FontWeight.Medium
                }
            ),
            color = labelColor,
            maxLines = 1
        )
    }
}

// ==========================================
// PRESET BUTTON
// ==========================================

@Composable
private fun WhatIfPreset(
    title: String,
    selected: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {

    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        contentPadding = PaddingValues(
            horizontal = 4.dp,
            vertical = 9.dp
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) {
                AccentBlue
            } else {
                AppBorder
            }
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (selected) {
                AccentBlue.copy(alpha = 0.07f)
            } else {
                AppSurface
            }
        )
    ) {

        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (selected) {
                AccentBlue
            } else {
                TextSecondary
            },
            maxLines = 1
        )
    }
}

// ==========================================
// RESULT CARD
// ==========================================

@Composable
private fun WhatIfResultCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {

    Surface(
        modifier = modifier,
        color = AppSurfaceSoft,
        shape = RoundedCornerShape(15.dp)
    ) {

        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {

            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 2
            )
        }
    }
}

// ==========================================
// WHAT-IF CALCULATION
// ==========================================

private fun evaluateWhatIf(
    expression: String,
    x: Double,
    isDegrees: Boolean
): Double {

    require(x.isFinite()) {
        "Invalid variable"
    }

    require(expression.contains("x")) {
        "Expression must contain x"
    }

    val replacement = BigDecimal.valueOf(x)
        .stripTrailingZeros()
        .toPlainString()

    val resolvedExpression = expression.replace(
        "x",
        "($replacement)"
    )

    return CalculatorEngine.evaluate(
        expression = resolvedExpression,
        isDegrees = isDegrees
    )
}

// ==========================================
// RESULT FORMATTING
// ==========================================

private fun formatWhatIfNumber(
    value: Double
): String {

    if (!value.isFinite()) {
        return "Undefined"
    }

    if (abs(value) < 1e-12) {
        return "0"
    }

    return DecimalFormat(
        "0.##########",
        DecimalFormatSymbols(Locale.US)
    ).format(value)
}