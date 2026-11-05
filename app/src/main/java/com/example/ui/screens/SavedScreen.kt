package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Article
import com.example.ui.NewsUiState
import com.example.ui.SavedSubTab
import com.example.ui.components.CompactArticleCard

@Composable
fun SavedScreen(
    state: NewsUiState,
    onTabSelect: (SavedSubTab) -> Unit,
    onArticleClick: (Article) -> Unit,
    onBookmarkToggle: (Article) -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showConfirmDialog by remember { mutableStateOf(false) }

    val currentArticles = when (state.savedSubTab) {
        SavedSubTab.BOOKMARKS -> state.bookmarkedArticles
        SavedSubTab.HISTORY -> state.readingHistory
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Masthead
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Library",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Saved bookmarks and reading records",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            if (currentArticles.isNotEmpty()) {
                IconButton(
                    onClick = { showConfirmDialog = true },
                    modifier = Modifier.testTag("clear_library_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Clear all",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        // Subtabs
        TabRow(
            selectedTabIndex = state.savedSubTab.ordinal,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[state.savedSubTab.ordinal]),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        ) {
            SavedSubTab.entries.forEach { tab ->
                val count = when (tab) {
                    SavedSubTab.BOOKMARKS -> state.bookmarkedArticles.size
                    SavedSubTab.HISTORY -> state.readingHistory.size
                }
                Tab(
                    selected = state.savedSubTab == tab,
                    onClick = { onTabSelect(tab) },
                    text = {
                        Text(
                            text = "${tab.title} ($count)",
                            fontWeight = if (state.savedSubTab == tab) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    modifier = Modifier.testTag("tab_${tab.name.lowercase()}")
                )
            }
        }

        if (currentArticles.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Icon(
                        imageVector = if (state.savedSubTab == SavedSubTab.BOOKMARKS) Icons.Default.Bookmark else Icons.Default.History,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (state.savedSubTab == SavedSubTab.BOOKMARKS) "No bookmarks saved yet" else "No reading history",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (state.savedSubTab == SavedSubTab.BOOKMARKS)
                            "Tap the bookmark icon on any story to save it for offline reading."
                        else "Articles you read will appear here automatically.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("saved_articles_list")
            ) {
                items(currentArticles, key = { it.id }) { article ->
                    CompactArticleCard(
                        article = article,
                        onClick = { onArticleClick(article) },
                        onBookmarkToggle = { onBookmarkToggle(article) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(88.dp))
                }
            }
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = {
                Text(
                    text = if (state.savedSubTab == SavedSubTab.BOOKMARKS) "Clear all bookmarks?" else "Clear reading history?"
                )
            },
            text = {
                Text(
                    text = if (state.savedSubTab == SavedSubTab.BOOKMARKS)
                        "This will remove all saved articles from your local library."
                    else "This will clear all entries from your reading log."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = false
                        onClearAll()
                    },
                    modifier = Modifier.testTag("confirm_clear_button")
                ) {
                    Text("Clear", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showConfirmDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
