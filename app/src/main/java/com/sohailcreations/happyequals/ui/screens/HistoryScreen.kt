
package com.sohailcreations.happyequals.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.material3.*

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.composables.icons.lucide.*

import com.sohailcreations.happyequals.data.HistoryEntry
import com.sohailcreations.happyequals.data.HistoryRepository
import com.sohailcreations.happyequals.ui.theme.*

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ==========================================
// HAPPY EQUALS - HISTORY SCREEN
// ==========================================

@Composable
fun HistoryScreen() {

    val context = LocalContext.current

    val history = remember {
        mutableStateListOf<HistoryEntry>()
    }

    var searchQuery by rememberSaveable {
        mutableStateOf("")
    }

    var showClearDialog by remember {
        mutableStateOf(false)
    }

    // Refresh history whenever this screen opens.

    LaunchedEffect(Unit) {

        history.clear()

        history.addAll(
            HistoryRepository.load(context)
        )
    }

    val filteredHistory = history.filter { item ->

        val query = searchQuery.trim()

        query.isEmpty() ||
                item.expression.contains(
                    query,
                    ignoreCase = true
                ) ||
                item.result.contains(
                    query,
                    ignoreCase = true
                ) ||
                item.source.contains(
                    query,
                    ignoreCase = true
                )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(
                horizontal = 18.dp
            )
    ) {

        Spacer(modifier = Modifier.height(18.dp))

        // ==================================
        // PAGE HEADER
        // ==================================

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(14.dp),
                color = AccentBlue.copy(
                    alpha = 0.08f
                )
            ) {

                Box(
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Lucide.History,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                        tint = AccentBlue
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "History",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = "${history.size} saved calculations",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            if (history.isNotEmpty()) {

                TextButton(
                    onClick = {
                        showClearDialog = true
                    }
                ) {

                    Text(
                        text = "Clear all",
                        color = ErrorColor,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ==================================
        // SEARCH FIELD
        // ==================================

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = AppSurface,
            border = BorderStroke(
                width = 1.dp,
                color = AppBorder
            )
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(horizontal = 15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Lucide.Search,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = TextSecondary
                )

                Spacer(modifier = Modifier.width(11.dp))

                BasicTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                    },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = TextPrimary
                    ),
                    cursorBrush = SolidColor(AccentBlue),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    decorationBox = { innerTextField ->

                        Box {

                            if (searchQuery.isEmpty()) {

                                Text(
                                    text = "Search calculations",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextMuted
                                )
                            }

                            innerTextField()
                        }
                    }
                )

                if (searchQuery.isNotEmpty()) {

                    IconButton(
                        onClick = {
                            searchQuery = ""
                        },
                        modifier = Modifier.size(32.dp)
                    ) {

                        Icon(
                            imageVector = Lucide.X,
                            contentDescription = "Clear search",
                            tint = TextSecondary,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // ==================================
        // HISTORY CONTENT
        // ==================================

        if (filteredHistory.isEmpty()) {

            HistoryEmptyState(
                modifier = Modifier.weight(1f),
                isSearching = searchQuery.isNotBlank()
            )

        } else {

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(
                    12.dp
                ),
                contentPadding = PaddingValues(
                    bottom = 20.dp
                )
            ) {

                items(
                    items = filteredHistory,
                    key = { it.id }
                ) { entry ->

                    HistoryCard(
                        entry = entry,
                        onCopy = {

                            val clipboard =
                                context.getSystemService(
                                    Context.CLIPBOARD_SERVICE
                                ) as ClipboardManager

                            clipboard.setPrimaryClip(
                                ClipData.newPlainText(
                                    "Happy Equals",
                                    entry.result
                                )
                            )
                        },
                        onDelete = {

                            HistoryRepository.delete(
                                context = context,
                                id = entry.id
                            )

                            history.remove(entry)
                        }
                    )
                }
            }
        }
    }

    // ======================================
    // CLEAR ALL CONFIRMATION
    // ======================================

    if (showClearDialog) {

        AlertDialog(
            onDismissRequest = {
                showClearDialog = false
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = AppSurface,
            title = {

                Text(
                    text = "Clear history?",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {

                Text(
                    text = "All your saved calculations will be deleted.",
                    color = TextSecondary
                )
            },
            confirmButton = {

                TextButton(
                    onClick = {

                        HistoryRepository.clear(context)

                        history.clear()

                        showClearDialog = false
                    }
                ) {

                    Text(
                        text = "Clear all",
                        color = ErrorColor,
                        fontWeight = FontWeight.Bold
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
// HISTORY CARD
// ==========================================

@Composable
private fun HistoryCard(
    entry: HistoryEntry,
    onCopy: () -> Unit,
    onDelete: () -> Unit
) {

    val shape = RoundedCornerShape(20.dp)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = shape,
        color = AppSurface,
        border = BorderStroke(
            width = 1.dp,
            color = AppBorder
        )
    ) {

        Column(
            modifier = Modifier.padding(17.dp),
            verticalArrangement = Arrangement.spacedBy(
                13.dp
            )
        ) {

            // Source and timestamp

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Surface(
                    shape = CircleShape,
                    color = AccentBlue.copy(
                        alpha = 0.08f
                    )
                ) {

                    Text(
                        text = entry.source,
                        modifier = Modifier.padding(
                            horizontal = 11.dp,
                            vertical = 6.dp
                        ),
                        color = AccentBlue,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = remember(entry.timestamp) {

                        SimpleDateFormat(
                            "dd MMM, h:mm a",
                            Locale.getDefault()
                        ).format(
                            Date(entry.timestamp)
                        )
                    },
                    color = TextMuted,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            // Expression

            Text(
                text = entry.expression,
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            HorizontalDivider(
                color = AppBorder
            )

            // Result and actions

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "= ${entry.result}",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                IconButton(
                    onClick = onCopy
                ) {

                    Icon(
                        imageVector = Lucide.Copy,
                        contentDescription = "Copy result",
                        modifier = Modifier.size(19.dp),
                        tint = AccentBlue
                    )
                }

                IconButton(
                    onClick = onDelete
                ) {

                    Icon(
                        imageVector = Lucide.Trash2,
                        contentDescription = "Delete calculation",
                        modifier = Modifier.size(19.dp),
                        tint = ErrorColor
                    )
                }
            }
        }
    }
}

// ==========================================
// EMPTY STATE
// ==========================================

@Composable
private fun HistoryEmptyState(
    modifier: Modifier = Modifier,
    isSearching: Boolean
) {

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                12.dp
            )
        ) {

            Surface(
                modifier = Modifier.size(76.dp),
                shape = CircleShape,
                color = AccentBlue.copy(
                    alpha = 0.07f
                )
            ) {

                Box(
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = if (isSearching) {
                            Lucide.Search
                        } else {
                            Lucide.History
                        },
                        contentDescription = null,
                        modifier = Modifier.size(30.dp),
                        tint = AccentBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isSearching) {
                    "No matching calculations"
                } else {
                    "No calculations yet"
                },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Text(
                text = if (isSearching) {
                    "Try searching for a different expression or result."
                } else {
                    "Your completed calculations will appear here."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}