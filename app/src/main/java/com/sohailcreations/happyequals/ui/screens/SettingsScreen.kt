
package com.sohailcreations.happyequals.ui.screens

import android.content.Context
import android.widget.Toast

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.*

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.composables.icons.lucide.*
import com.sohailcreations.happyequals.R
import com.sohailcreations.happyequals.data.HistoryRepository
import com.sohailcreations.happyequals.ui.theme.*

// ==========================================
// SHARED APP PREFERENCES
// ==========================================

internal object HappyPreferences {

    const val PREFS = "happy_equals_settings"

    const val HAPTICS = "haptic_feedback"

    const val AUTO_SAVE = "auto_save_history"
}

// ==========================================
// SETTINGS SCREEN
// ==========================================

@Composable
fun SettingsScreen() {

    val context = LocalContext.current

    val preferences = remember(context) {

        context.getSharedPreferences(
            HappyPreferences.PREFS,
            Context.MODE_PRIVATE
        )
    }

    var hapticsEnabled by remember {

        mutableStateOf(
            preferences.getBoolean(
                HappyPreferences.HAPTICS,
                true
            )
        )
    }

    var autoSaveEnabled by remember {

        mutableStateOf(
            preferences.getBoolean(
                HappyPreferences.AUTO_SAVE,
                true
            )
        )
    }

    var historyCount by remember {
        mutableIntStateOf(
            HistoryRepository.load(context).size
        )
    }

    var showClearDialog by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {

        historyCount =
            HistoryRepository.load(context).size
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(horizontal = 18.dp)
    ) {

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        // ==================================
        // SCREEN HEADER
        // ==================================

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Surface(
                modifier = Modifier.size(46.dp),
                shape = CircleShape,
                color = AccentBlue.copy(alpha = 0.08f)
            ) {

                Box(
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Lucide.Settings,
                        contentDescription = null,
                        tint = AccentBlue,
                        modifier = Modifier.size(23.dp)
                    )
                }
            }

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Column {

                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = "Your preferences",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        Spacer(
            modifier = Modifier.height(27.dp)
        )

        // ==================================
        // PREFERENCES SECTION
        // ==================================

        SettingsSectionTitle(
            title = "PREFERENCES"
        )

        Spacer(
            modifier = Modifier.height(11.dp)
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(21.dp),
            color = AppSurface,
            border = BorderStroke(
                1.dp,
                AppBorder
            )
        ) {

            Column {

                SettingsSwitchRow(
                    icon = Lucide.SlidersHorizontal,
                    title = "Haptic feedback",
                    description = "Gentle feedback when pressing calculator keys.",
                    checked = hapticsEnabled,
                    onCheckedChange = { enabled ->

                        hapticsEnabled = enabled

                        preferences.edit()
                            .putBoolean(
                                HappyPreferences.HAPTICS,
                                enabled
                            )
                            .apply()
                    }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(
                        horizontal = 16.dp
                    ),
                    color = AppBorder
                )

                SettingsSwitchRow(
                    icon = Lucide.History,
                    title = "Auto-save calculations",
                    description = "Save completed calculations to your history.",
                    checked = autoSaveEnabled,
                    onCheckedChange = { enabled ->

                        autoSaveEnabled = enabled

                        preferences.edit()
                            .putBoolean(
                                HappyPreferences.AUTO_SAVE,
                                enabled
                            )
                            .apply()
                    }
                )
            }
        }

        Spacer(
            modifier = Modifier.height(27.dp)
        )

        // ==================================
        // HISTORY SECTION
        // ==================================

        SettingsSectionTitle(
            title = "DATA & STORAGE"
        )

        Spacer(
            modifier = Modifier.height(11.dp)
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(21.dp),
            color = AppSurface,
            border = BorderStroke(
                1.dp,
                AppBorder
            )
        ) {

            Column(
                modifier = Modifier.padding(17.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    SettingsIcon(
                        icon = Lucide.History
                    )

                    Spacer(
                        modifier = Modifier.width(13.dp)
                    )

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = "Calculation history",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        Text(
                            text = "$historyCount saved calculations",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                HorizontalDivider(
                    color = AppBorder
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                OutlinedButton(
                    onClick = {
                        showClearDialog = true
                    },
                    enabled = historyCount > 0,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(
                        1.dp,
                        if (historyCount > 0) {
                            ErrorColor.copy(alpha = 0.35f)
                        } else {
                            AppBorder
                        }
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = ErrorColor
                    )
                ) {

                    Icon(
                        imageVector = Lucide.Trash2,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(
                        modifier = Modifier.width(9.dp)
                    )

                    Text(
                        text = "Clear History",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(27.dp)
        )

        // ==================================
        // ABOUT SECTION
        // ==================================

        SettingsSectionTitle(
            title = "ABOUT"
        )

        Spacer(
            modifier = Modifier.height(11.dp)
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(21.dp),
            color = AppSurface,
            border = BorderStroke(
                1.dp,
                AppBorder
            )
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(17.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Image(
                    painter = painterResource(
                        id = R.drawable.logo
                    ),
                    contentDescription = "Happy Equals Logo",
                    modifier = Modifier.size(49.dp)
                )

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Happy Equals",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(
                        modifier = Modifier.height(5.dp)
                    )

                    Spacer(
                        modifier = Modifier.height(1.dp)
                    )

                    Text(
                        text = "Version " + remember(context) {

                            runCatching {

                                context.packageManager
                                    .getPackageInfo(
                                        context.packageName,
                                        0
                                    )
                                    .versionName ?: "1.0"

                            }.getOrDefault("1.0")
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )
    }

    // ======================================
    // CLEAR HISTORY CONFIRMATION
    // ======================================

    if (showClearDialog) {

        AlertDialog(
            onDismissRequest = {
                showClearDialog = false
            },

            shape = RoundedCornerShape(23.dp),

            containerColor = AppSurface,

            title = {

                Text(
                    text = "Clear history?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },

            text = {

                Text(
                    text = "All saved calculations will be permanently deleted.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        HistoryRepository.clear(context)

                        historyCount = 0

                        showClearDialog = false

                        Toast.makeText(
                            context,
                            "History cleared",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                ) {

                    Text(
                        text = "Clear",
                        fontWeight = FontWeight.Bold,
                        color = ErrorColor
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        showClearDialog = false
                    }
                ) {

                    Text(
                        text = "Cancel",
                        color = TextSecondary
                    )
                }
            }
        )
    }
}

// ==========================================
// SECTION TITLE
// ==========================================

@Composable
private fun SettingsSectionTitle(
    title: String
) {

    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.3.sp,
        color = TextSecondary,
        modifier = Modifier.padding(start = 3.dp)
    )
}

// ==========================================
// SETTINGS ICON
// ==========================================

@Composable
private fun SettingsIcon(
    icon: ImageVector
) {

    Surface(
        modifier = Modifier.size(42.dp),
        shape = RoundedCornerShape(13.dp),
        color = AccentBlue.copy(alpha = 0.07f)
    ) {

        Box(
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AccentBlue,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// ==========================================
// SETTINGS SWITCH ROW
// ==========================================

@Composable
private fun SettingsSwitchRow(
    icon: ImageVector,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .padding(
                horizontal = 16.dp,
                vertical = 16.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        SettingsIcon(
            icon = icon
        )

        Spacer(
            modifier = Modifier.width(12.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 18.sp
            )
        }

        Spacer(
            modifier = Modifier.width(9.dp)
        )

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = AccentBlue,
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = AppBorder,
                uncheckedBorderColor = AppBorder
            )
        )
    }
}