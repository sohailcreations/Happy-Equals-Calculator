
package com.sohailcreations.happyequals.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sohailcreations.happyequals.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onFinished: () -> Unit
) {

    var animationStarted by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        animationStarted = true

        delay(1450)

        onFinished()
    }

    val contentAlpha by animateFloatAsState(
        targetValue = if (animationStarted) 1f else 0f,
        animationSpec = tween(
            durationMillis = 650,
            easing = FastOutSlowInEasing
        ),
        label = "SplashAlpha"
    )

    val contentOffset by animateDpAsState(
        targetValue = if (animationStarted) 0.dp else 12.dp,
        animationSpec = tween(
            durationMillis = 650,
            easing = FastOutSlowInEasing
        ),
        label = "SplashOffset"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .safeDrawingPadding(),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .offset(y = contentOffset)
                .graphicsLayer {
                    alpha = contentAlpha
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Image(
                painter = painterResource(
                    id = R.drawable.logo
                ),
                contentDescription = "Happy Equals Logo",
                modifier = Modifier.size(116.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Text(
                text = "Happy Equals",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 31.sp,
                    fontWeight = FontWeight.Bold,
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF12BDAE),
                            Color(0xFF1769E8),
                            Color(0xFF6747D9)
                        )
                    )
                ),
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}