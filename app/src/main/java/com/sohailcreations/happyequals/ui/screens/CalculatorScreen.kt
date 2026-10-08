package com.sohailcreations.happyequals.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.border
import android.content.Context
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.*
import com.sohailcreations.happyequals.data.HistoryRepository
import com.sohailcreations.happyequals.domain.CalculatorEngine
import com.sohailcreations.happyequals.ui.theme.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.abs

/**
 * Happy Equals calculator screen.
 *
 * - Uses the new, accessible light theme (dark text on light keys).
 * - Saves successful '=' calculations when Auto-save is enabled.
 * - Respects the Haptic Feedback setting on every calculator key.
 * - Keeps the keypad scrollable on shorter devices; MainHomeScreen owns the bottom bar.
 * - Does NOT add IME padding, keyboard spacers or keyboard background panels.
 */
@Composable
fun CalculatorScreen() {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current
    val appPreferences = remember(context) {
        context.getSharedPreferences(HappyPreferences.PREFS, Context.MODE_PRIVATE)
    }

    var expression by rememberSaveable { mutableStateOf("") }
    var calculatedResult by rememberSaveable { mutableStateOf("") }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var isDegrees by rememberSaveable { mutableStateOf(true) }
    var justCalculated by rememberSaveable { mutableStateOf(false) }

    val livePreview = remember(expression, isDegrees) {
        if (expression.isBlank()) {
            null
        } else {
            runCatching {
                formatCalculatorResult(
                    CalculatorEngine.evaluate(expression = expression, isDegrees = isDegrees)
                )
            }.getOrNull()
        }
    }

    fun handleKey(key: String) {
        if (appPreferences.getBoolean(HappyPreferences.HAPTICS, true)) {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        }

        when (key) {
            "MODE" -> {
                isDegrees = !isDegrees
                calculatedResult = ""
                errorMessage = null
                justCalculated = false
                return
            }

            "AC" -> {
                expression = ""
                calculatedResult = ""
                errorMessage = null
                justCalculated = false
                return
            }

            "⌫" -> {
                expression = removeCalculatorToken(expression)
                calculatedResult = ""
                errorMessage = null
                justCalculated = false
                return
            }

            "=" -> {
                if (expression.isBlank()) return
                val completedExpression = expression
                runCatching {
                    formatCalculatorResult(
                        CalculatorEngine.evaluate(
                            expression = completedExpression,
                            isDegrees = isDegrees
                        )
                    )
                }.onSuccess { formattedResult ->
                    calculatedResult = formattedResult
                    errorMessage = null
                    justCalculated = true

                    if (appPreferences.getBoolean(HappyPreferences.AUTO_SAVE, true)) {
                        HistoryRepository.add(
                            context = context,
                            expression = completedExpression,
                            result = formattedResult,
                            source = "Calculator"
                        )
                    }
                }.onFailure { error ->
                    calculatedResult = ""
                    errorMessage = error.message ?: "Invalid expression"
                    justCalculated = false
                }
                return
            }
        }

        // Operators may continue the last answer; digits/functions begin a fresh expression.
        val continuationKeys = setOf("+", "−", "×", "÷", "xʸ", "x²", "!", "%")
        val current = if (justCalculated && calculatedResult.isNotBlank()) {
            if (key in continuationKeys) calculatedResult else ""
        } else {
            expression
        }

        expression = appendCalculatorInput(current, key)
        calculatedResult = ""
        errorMessage = null
        justCalculated = false
    }

    val displayResult = when {
        errorMessage != null -> errorMessage.orEmpty()
        calculatedResult.isNotBlank() -> calculatedResult
        livePreview != null -> livePreview
        else -> "0"
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize().background(AppBackground)
    ) {
        val compact = maxHeight < 600.dp
        val displayHeight = if (compact) 130.dp else 150.dp
        val scientificKeyHeight = if (compact) 36.dp else 41.dp
        val numberKeyHeight = if (compact) 43.dp else 50.dp
        val keyGap = if (compact) 6.dp else 8.dp
        val sectionGap = if (compact) 9.dp else 13.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(sectionGap)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Scientific calculator",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Calculate with confidence",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }

                Surface(
                    modifier = Modifier.clickable { handleKey("MODE") },
                    shape = RoundedCornerShape(12.dp),
                    color = AccentBlue.copy(alpha = 0.09f)
                ) {
                    Text(
                        text = if (isDegrees) "DEG" else "RAD",
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                        color = AccentBlue,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            CalculatorDisplay(
                expression = expression,
                result = displayResult,
                isDegrees = isDegrees,
                isPreview = errorMessage == null && calculatedResult.isBlank() && livePreview != null,
                hasError = errorMessage != null,
                height = displayHeight,
                compact = compact
            )

            Text(
                text = "SCIENTIFIC FUNCTIONS",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                color = TextSecondary,
                modifier = Modifier.padding(start = 2.dp)
            )

            val scientificRows = listOf(
                listOf("MODE", "sin", "cos", "tan", "ln"),
                listOf("log", "√", "x²", "xʸ", "!")
            )

            Column(verticalArrangement = Arrangement.spacedBy(keyGap)) {
                scientificRows.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        row.forEach { key ->
                            CalculatorKey(
                                key = key,
                                label = if (key == "MODE") {
                                    if (isDegrees) "DEG" else "RAD"
                                } else key,
                                modifier = Modifier.weight(1f),
                                height = scientificKeyHeight,
                                scientific = true,
                                onClick = { handleKey(key) }
                            )
                        }
                    }
                }
            }

            val numberRows = listOf(
                listOf("(", ")", "π", "e"),
                listOf("7", "8", "9", "÷"),
                listOf("4", "5", "6", "×"),
                listOf("1", "2", "3", "−"),
                listOf("AC", "0", ".", "+"),
                listOf("%", "⌫", "±", "=")
            )

            Column(verticalArrangement = Arrangement.spacedBy(keyGap)) {
                numberRows.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(keyGap)
                    ) {
                        row.forEach { key ->
                            CalculatorKey(
                                key = key,
                                label = key,
                                modifier = Modifier.weight(1f),
                                height = numberKeyHeight,
                                scientific = false,
                                onClick = { handleKey(key) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalculatorDisplay(
    expression: String,
    result: String,
    isDegrees: Boolean,
    isPreview: Boolean,
    hasError: Boolean,
    height: Dp,
    compact: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth().height(height),
        shape = RoundedCornerShape(22.dp),
        color = AppSurface,
        border = BorderStroke(1.dp, AppBorder),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 17.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "EXPRESSION",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = TextSecondary
                )
                Text(
                    text = if (isDegrees) "DEGREES" else "RADIANS",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = AccentBlue
                )
            }

            Text(
                text = expression.ifBlank { "0" },
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = if (compact) 22.sp else 26.sp,
                    lineHeight = if (compact) 27.sp else 31.sp
                ),
                color = TextPrimary,
                textAlign = TextAlign.End,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            HorizontalDivider(thickness = 1.dp, color = AppBorder)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when {
                        hasError -> "ERROR"
                        isPreview -> "LIVE PREVIEW"
                        else -> "RESULT"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
                Text(
                    text = result,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = when {
                            hasError -> 13.sp
                            compact -> 29.sp
                            else -> 34.sp
                        }
                    ),
                    color = if (hasError) ErrorColor else AccentBlue,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun CalculatorKey(
    key: String,
    label: String,
    modifier: Modifier,
    height: Dp,
    scientific: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.955f else 1f,
        animationSpec = spring(dampingRatio = 0.85f, stiffness = 750f),
        label = "CalculatorKeyPress"
    )

    val equals = key == "="
    val clear = key == "AC"
    val mode = key == "MODE"
    val operator = key in setOf("+", "−", "×", "÷", "xʸ")
    val backspace = key == "⌫"

    val fill = when {
        equals -> Brush.horizontalGradient(listOf(AccentTeal, AccentBlue))
        clear -> Brush.linearGradient(listOf(ErrorColor.copy(alpha = 0.09f), ErrorColor.copy(alpha = 0.09f)))
        mode -> Brush.linearGradient(listOf(AccentPurple.copy(alpha = 0.10f), AccentPurple.copy(alpha = 0.10f)))
        operator -> Brush.linearGradient(listOf(AccentBlue.copy(alpha = 0.09f), AccentBlue.copy(alpha = 0.09f)))
        scientific || backspace -> Brush.linearGradient(listOf(AppSurfaceSoft, AppSurfaceSoft))
        else -> Brush.linearGradient(listOf(AppSurface, AppSurface))
    }

    val foreground = when {
        equals -> Color.White
        clear -> ErrorColor
        mode -> AccentPurple
        operator -> AccentBlue
        scientific -> Color(0xFF385471)
        else -> TextPrimary
    }

    val shape = RoundedCornerShape(if (scientific) 12.dp else 16.dp)

    Box(
        modifier = modifier
            .height(height)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(shape)
            .background(brush = fill, shape = shape)
            .then(
                if (equals) Modifier else Modifier.border(
                    width = 1.dp,
                    color = AppBorder,
                    shape = shape
                )
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (backspace) {
            Icon(
                imageVector = Lucide.Delete,
                contentDescription = "Backspace",
                modifier = Modifier.size(21.dp),
                tint = TextPrimary
            )
        } else {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontSize = when {
                        scientific -> 14.sp
                        clear -> 16.sp
                        else -> 21.sp
                    }
                ),
                color = foreground,
                fontWeight = if (equals || operator || clear) {
                    FontWeight.Bold
                } else {
                    FontWeight.Medium
                },
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

private fun appendCalculatorInput(current: String, key: String): String {
    val last = current.lastOrNull()
    val endsWithValue = last?.isDigit() == true || last in listOf(')', 'π', 'e', '!', '%')

    fun appendImplicitlyMultiplied(token: String): String =
        if (endsWithValue) "$current×$token" else current + token

    return when (key) {
        "sin", "cos", "tan", "ln", "log" -> appendImplicitlyMultiplied("$key(")
        "√" -> appendImplicitlyMultiplied("√(")
        "π", "e", "(" -> appendImplicitlyMultiplied(key)
        ")" -> {
            val open = current.count { it == '(' }
            val closed = current.count { it == ')' }
            if (open > closed && endsWithValue) "$current)" else current
        }
        "x²" -> if (endsWithValue) "${current}^2" else current
        "xʸ" -> appendCalculatorOperator(current, "^")
        "!", "%" -> if (endsWithValue) current + key else current
        "±" -> when {
            current.isBlank() -> "−"
            current.startsWith("−(") && current.endsWith(")") ->
                current.substring(2, current.length - 1)
            else -> "−($current)"
        }
        "+", "−", "×", "÷" -> appendCalculatorOperator(current, key)
        "." -> {
            val number = current.takeLastWhile { it.isDigit() || it == '.' }
            when {
                number.contains('.') -> current
                current.isEmpty() || last in listOf('+', '−', '×', '÷', '^', '(') -> "${current}0."
                endsWithValue && last?.isDigit() != true -> "${current}×0."
                else -> "$current."
            }
        }
        else -> {
            if (key.length == 1 && key[0].isDigit()) {
                if (last in listOf(')', 'π', 'e', '!', '%')) "$current×$key" else current + key
            } else {
                current
            }
        }
    }
}

private fun appendCalculatorOperator(current: String, operator: String): String {
    if (current.isEmpty()) return if (operator == "−") "−" else ""
    val last = current.last()
    if (last == '(') return if (operator == "−") current + operator else current
    if (last == '.') return current
    if (last in "+−×÷^") {
        return if (operator == "−" && last in "×÷^") {
            current + operator
        } else {
            current.dropLast(1) + operator
        }
    }
    return current + operator
}

private fun removeCalculatorToken(expression: String): String {
    if (expression.isEmpty()) return ""
    val functionTokens = listOf("sin(", "cos(", "tan(", "log(", "ln(", "√(")
    val token = functionTokens.firstOrNull { expression.endsWith(it) }
    return if (token != null) expression.dropLast(token.length) else expression.dropLast(1)
}

private fun formatCalculatorResult(value: Double): String {
    require(value.isFinite()) { "Undefined result" }
    if (abs(value) < 1e-12) return "0"
    val absolute = abs(value)
    if (absolute >= 1e12 || absolute < 1e-8) {
        return DecimalFormat(
            "0.########E0",
            DecimalFormatSymbols(Locale.US)
        ).format(value)
    }
    return BigDecimal.valueOf(value)
        .setScale(10, RoundingMode.HALF_UP)
        .stripTrailingZeros()
        .toPlainString()
}