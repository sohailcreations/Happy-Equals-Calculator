
package com.sohailcreations.happyequals

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.sohailcreations.happyequals.ui.screens.MainHomeScreen
import com.sohailcreations.happyequals.ui.screens.SplashScreen

@Composable
fun HappyEqualsApp() {

    var splashFinished by rememberSaveable {
        mutableStateOf(false)
    }

    Crossfade(
        targetState = splashFinished,
        animationSpec = tween(
            durationMillis = 300
        ),
        label = "SplashToHome"
    ) { finished ->

        if (finished) {

            MainHomeScreen()

        } else {

            SplashScreen(
                onFinished = {
                    splashFinished = true
                }
            )
        }
    }
}