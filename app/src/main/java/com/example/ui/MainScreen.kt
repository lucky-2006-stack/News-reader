package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.screens.ArticleDetailScreen
import com.example.ui.screens.DiscoverScreen
import com.example.ui.screens.HeadlinesScreen
import com.example.ui.screens.SavedScreen
import com.example.ui.screens.SettingsScreen

@Composable
fun MainScreen(
    viewModel: NewsViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        state.error?.let { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    if (state.selectedArticle != null) {
        ArticleDetailScreen(
            article = state.selectedArticle!!,
            readerSettings = state.readerSettings,
            ttsManager = viewModel.ttsManager,
            onBack = { viewModel.closeArticle() },
            onBookmarkToggle = { viewModel.toggleBookmark(state.selectedArticle!!) },
            onFontSizeChange = { viewModel.updateReaderFontSize(it) },
            onFontChange = { viewModel.updateReaderFont(it) },
            onThemeChange = { viewModel.updateReaderTheme(it) }
        )
    } else {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    tonalElevation = 6.dp,
                    modifier = Modifier
                        .navigationBarsPadding()
                        .testTag("bottom_nav_bar")
                ) {
                    BottomNavTab.entries.forEach { tab ->
                        val isSelected = state.currentTab == tab
                        val (filledIcon, outlinedIcon) = when (tab) {
                            BottomNavTab.HEADLINES -> Icons.Filled.Newspaper to Icons.Outlined.Newspaper
                            BottomNavTab.DISCOVER -> Icons.Filled.Explore to Icons.Outlined.Explore
                            BottomNavTab.SAVED -> Icons.Filled.Bookmark to Icons.Outlined.BookmarkBorder
                            BottomNavTab.SETTINGS -> Icons.Filled.Tune to Icons.Outlined.Tune
                        }

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.setBottomTab(tab) },
                            icon = {
                                if (tab == BottomNavTab.SAVED && state.bookmarkedArticles.isNotEmpty()) {
                                    BadgedBox(
                                        badge = {
                                            Badge {
                                                Text(text = "${state.bookmarkedArticles.size}")
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (isSelected) filledIcon else outlinedIcon,
                                            contentDescription = tab.title
                                        )
                                    }
                                } else {
                                    Icon(
                                        imageVector = if (isSelected) filledIcon else outlinedIcon,
                                        contentDescription = tab.title
                                    )
                                }
                            },
                            label = {
                                Text(
                                    text = tab.title,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.testTag("nav_item_${tab.name.lowercase()}")
                        )
                    }
                }
            },
            modifier = modifier.fillMaxSize()
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                AnimatedContent(
                    targetState = state.currentTab,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "tab_switch_anim"
                ) { targetTab ->
                    when (targetTab) {
                        BottomNavTab.HEADLINES -> HeadlinesScreen(
                            state = state,
                            onCategorySelected = { viewModel.selectCategory(it) },
                            onArticleClick = { viewModel.openArticle(it) },
                            onBookmarkToggle = { viewModel.toggleBookmark(it) },
                            onRefresh = { viewModel.refreshNews() }
                        )
                        BottomNavTab.DISCOVER -> DiscoverScreen(
                            state = state,
                            onSearchQueryChange = { viewModel.setSearchQuery(it) },
                            onArticleClick = { viewModel.openArticle(it) },
                            onBookmarkToggle = { viewModel.toggleBookmark(it) }
                        )
                        BottomNavTab.SAVED -> SavedScreen(
                            state = state,
                            onTabSelect = { viewModel.setSavedSubTab(it) },
                            onArticleClick = { viewModel.openArticle(it) },
                            onBookmarkToggle = { viewModel.toggleBookmark(it) },
                            onClearAll = {
                                if (state.savedSubTab == SavedSubTab.BOOKMARKS) {
                                    viewModel.clearAllBookmarks()
                                } else {
                                    viewModel.clearHistory()
                                }
                            }
                        )
                        BottomNavTab.SETTINGS -> SettingsScreen(
                            state = state,
                            onFontSizeChange = { viewModel.updateReaderFontSize(it) },
                            onFontFamilyChange = { viewModel.updateReaderFont(it) },
                            onReaderThemeChange = { viewModel.updateReaderTheme(it) },
                            onClearHistory = { viewModel.clearHistory() }
                        )
                    }
                }
            }
        }
    }
}
