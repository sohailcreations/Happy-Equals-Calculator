
package com.sohailcreations.happyequals.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.*

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.composables.icons.lucide.*
import com.sohailcreations.happyequals.R
import com.sohailcreations.happyequals.ui.theme.*

// ==========================================
// HAPPY EQUALS - NAVIGATION MODEL
// ==========================================

private data class HomeTab(
    val title: String,
    val icon: ImageVector
)

private val homeTabs = listOf(

    HomeTab(
        title = "Calculator",
        icon = Lucide.Calculator
    ),

    HomeTab(
        title = "What-if",
        icon = Lucide.SlidersHorizontal
    ),

    HomeTab(
        title = "History",
        icon = Lucide.History
    ),

    HomeTab(
        title = "Settings",
        icon = Lucide.Settings
    )
)

// ==========================================
// MAIN HOME SCREEN
// ==========================================

@Composable
fun MainHomeScreen() {

    var selectedTab by rememberSaveable {
        mutableIntStateOf(0)
    }

    val stateHolder = rememberSaveableStateHolder()

    val density = LocalDensity.current

    // Detect the software keyboard.
    // No additional IME padding is applied.

    val keyboardVisible =
        WindowInsets.ime.getBottom(density) > 0

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),

        containerColor = AppBackground,

        topBar = {

            HappyHomeHeader(
                onSettingsClick = {
                    selectedTab = 3
                }
            )
        },

        bottomBar = {

            // Hide navigation immediately when the
            // keyboard opens. No blank bottom panel.

            if (!keyboardVisible) {

                HappyBottomNavigation(
                    selectedTab = selectedTab,
                    onTabSelected = { index ->
                        selectedTab = index
                    }
                )
            }
        }

    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppBackground)
                .padding(innerPadding)
        ) {

            Crossfade(
                targetState = selectedTab,
                animationSpec = tween(
                    durationMillis = 220
                ),
                label = "HappyTabTransition"
            ) { tab ->

                stateHolder.SaveableStateProvider(tab) {

                    when (tab) {

                        0 -> {

                            CalculatorScreen()
                        }

                        1 -> {

                            WhatIfScreen()
                        }


                        2 -> {
                            HistoryScreen()
                        }


                        3 -> {
                            SettingsScreen()
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// HOME HEADER
// ==========================================

@Composable
private fun HappyHomeHeader(
    onSettingsClick: () -> Unit
) {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 0.dp
    ) {

        Column {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .height(68.dp)
                    .padding(
                        start = 17.dp,
                        end = 16.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Image(
                    painter = painterResource(
                        id = R.drawable.logo
                    ),
                    contentDescription = "Happy Equals Logo",
                    modifier = Modifier.size(39.dp)
                )

                Spacer(
                    modifier = Modifier.width(7.dp)
                )

                Text(
                    text = buildAnnotatedString {

                        append("Happy ")

                        withStyle(
                            SpanStyle(
                                color = AccentBlue
                            )
                        ) {

                            append("Equals")
                        }
                    },
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1
                )

                Surface(
                    modifier = Modifier.size(42.dp),
                    shape = CircleShape,
                    color = AppSurfaceSoft
                ) {

                    IconButton(
                        onClick = onSettingsClick
                    ) {

                        Icon(
                            imageVector = Lucide.Settings,
                            contentDescription = "Open Settings",
                            tint = AccentBlue,
                            modifier = Modifier.size(21.dp)
                        )
                    }
                }
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = AppBorder
            )
        }
    }
}

// ==========================================
// BOTTOM NAVIGATION
// ==========================================

@Composable
private fun HappyBottomNavigation(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 5.dp,
        shape = RoundedCornerShape(
            topStart = 22.dp,
            topEnd = 22.dp
        )
    ) {

        NavigationBar(
            containerColor = Color.White,
            tonalElevation = 0.dp
        ) {

            homeTabs.forEachIndexed { index, tab ->

                val isSelected = selectedTab == index

                NavigationBarItem(
                    selected = isSelected,

                    onClick = {
                        onTabSelected(index)
                    },

                    icon = {

                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.title,
                            modifier = Modifier.size(22.dp)
                        )
                    },

                    label = {

                        Text(
                            text = tab.title,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Medium
                            },
                            maxLines = 1
                        )
                    },

                    alwaysShowLabel = true,

                    colors = NavigationBarItemDefaults.colors(

                        selectedIconColor = AccentBlue,

                        selectedTextColor = AccentBlue,

                        unselectedIconColor = TextMuted,

                        unselectedTextColor = TextSecondary,

                        indicatorColor = AccentBlue.copy(
                            alpha = 0.08f
                        )
                    )
                )
            }
        }
    }
}

// ==========================================
// TEMPORARY HISTORY / SETTINGS CONTENT
// ==========================================

@Composable
private fun PendingScreen(
    icon: ImageVector,
    title: String,
    description: String
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Surface(
                modifier = Modifier.size(70.dp),
                color = AccentBlue.copy(
                    alpha = 0.07f
                ),
                shape = CircleShape
            ) {

                Box(
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(29.dp),
                        tint = AccentBlue
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}