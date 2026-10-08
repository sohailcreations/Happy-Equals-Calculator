
package com.sohailcreations.happyequals.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.sohailcreations.happyequals.ui.theme.*

@Composable
fun PlainFloatingTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text
) {

    var isFocused by remember {
        mutableStateOf(false)
    }

    val isFloating = isFocused || value.isNotEmpty()

    val focusManager = LocalFocusManager.current

    val keyboardController =
        LocalSoftwareKeyboardController.current

    val bringIntoViewRequester = remember {
        BringIntoViewRequester()
    }

    val imeBottom = WindowInsets.ime.getBottom(
        LocalDensity.current
    )

    // Keep the focused field visible above the keyboard.
    LaunchedEffect(isFocused, imeBottom) {

        if (isFocused && imeBottom > 0) {

            bringIntoViewRequester.bringIntoView()
        }
    }

    // Smooth floating-label position.
    val labelOffset by animateDpAsState(
        targetValue = if (isFloating) {
            0.dp
        } else {
            41.dp
        },
        animationSpec = tween(
            durationMillis = 200,
            easing = FastOutSlowInEasing
        ),
        label = "FloatingLabelOffset"
    )

    // Smooth label size transition.
    val labelSize by animateFloatAsState(
        targetValue = if (isFloating) {
            12f
        } else {
            14f
        },
        animationSpec = tween(
            durationMillis = 200
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
            durationMillis = 200
        ),
        label = "FloatingLabelColor"
    )

    val fieldShape = RoundedCornerShape(14.dp)

    Box(
        modifier = modifier.height(82.dp)
    ) {

        BasicTextField(
            value = value,
            onValueChange = onValueChange,

            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
                .height(60.dp)
                .clip(fieldShape)
                .background(AppSurfaceSoft)
                .border(
                    width = if (isFocused) {
                        1.5.dp
                    } else {
                        1.dp
                    },
                    color = if (isFocused) {
                        AccentBlue
                    } else {
                        AppBorder
                    },
                    shape = fieldShape
                )
                .onFocusChanged { state ->
                    isFocused = state.isFocused
                }
                .bringIntoViewRequester(
                    bringIntoViewRequester
                ),

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
                        .padding(horizontal = 16.dp),

                    contentAlignment = Alignment.CenterStart
                ) {

                    innerTextField()
                }
            }
        )

        // Plain floating text, no background container.
        Text(
            text = label,

            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp)
                .offset(y = labelOffset),

            color = labelColor,

            fontSize = labelSize.sp,

            fontWeight = if (isFloating) {
                FontWeight.SemiBold
            } else {
                FontWeight.Medium
            },

            maxLines = 1
        )
    }
}