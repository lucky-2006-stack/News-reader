package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.NewsDatabase
import com.example.data.model.Article
import com.example.data.model.NewsCategory
import com.example.data.remote.CuratedNewsData
import com.example.data.remote.NewsFeedService
import com.example.data.repository.NewsRepository
import com.example.tts.TtsManager
import com.example.ui.model.ReaderFont
import com.example.ui.model.ReaderSettings
import com.example.ui.model.ReaderTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class BottomNavTab(val title: String) {
    HEADLINES("Headlines"),
    DISCOVER("Discover"),
    SAVED("Saved"),
    SETTINGS("Settings")
}

enum class SavedSubTab(val title: String) {
    BOOKMARKS("Bookmarks"),
    HISTORY("History")
}

data class NewsUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val allArticles: List<Article> = emptyList(),
    val breakingNews: List<Article> = CuratedNewsData.breakingNews,
    val selectedCategory: NewsCategory = NewsCategory.ALL,
    val searchQuery: String = "",
    val bookmarkedArticles: List<Article> = emptyList(),
    val readingHistory: List<Article> = emptyList(),
    val selectedArticle: Article? = null,
    val currentTab: BottomNavTab = BottomNavTab.HEADLINES,
    val savedSubTab: SavedSubTab = SavedSubTab.BOOKMARKS,
    val readerSettings: ReaderSettings = ReaderSettings(),
    val error: String? = null
) {
    val displayedArticles: List<Article>
        get() {
            var list = allArticles
            if (selectedCategory != NewsCategory.ALL) {
                list = list.filter { it.category == selectedCategory }
            }
            if (searchQuery.isNotBlank()) {
                val q = searchQuery.trim().lowercase()
                list = list.filter {
                    it.title.lowercase().contains(q) ||
                            it.description.lowercase().contains(q) ||
                            it.source.lowercase().contains(q) ||
                            it.author.lowercase().contains(q)
                }
            }
            return list
        }
}

class NewsViewModel(application: Application) : AndroidViewModel(application) {

    private val database = NewsDatabase.getDatabase(application)
    private val feedService = NewsFeedService()
    private val repository = NewsRepository(feedService, database.articleDao())
    val ttsManager = TtsManager(application)

    private val _uiState = MutableStateFlow(NewsUiState(isLoading = true))
    val uiState: StateFlow<NewsUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
        observeDatabase()
    }

    private fun observeDatabase() {
        viewModelScope.launch {
            repository.bookmarkedArticles.collectLatest { bookmarks ->
                _uiState.update { state ->
                    val bookmarkIds = bookmarks.map { it.id }.toSet()
                    state.copy(
                        bookmarkedArticles = bookmarks,
                        allArticles = state.allArticles.map {
                            it.copy(isBookmarked = bookmarkIds.contains(it.id))
                        },
                        selectedArticle = state.selectedArticle?.let { current ->
                            current.copy(isBookmarked = bookmarkIds.contains(current.id))
                        }
                    )
                }
            }
        }

        viewModelScope.launch {
            repository.readingHistory.collectLatest { history ->
                _uiState.update { state ->
                    val readIds = history.map { it.id }.toSet()
                    state.copy(
                        readingHistory = history,
                        allArticles = state.allArticles.map {
                            it.copy(isRead = readIds.contains(it.id))
                        }
                    )
                }
            }
        }
    }

    fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val articles = repository.loadArticles(forceRefresh = false)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        allArticles = articles,
                        breakingNews = CuratedNewsData.breakingNews
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        allArticles = CuratedNewsData.allArticles,
                        error = "Loaded offline curated news."
                    )
                }
            }
        }
    }

    fun refreshNews() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, error = null) }
            try {
                val articles = repository.loadArticles(forceRefresh = true)
                _uiState.update {
                    it.copy(
                        isRefreshing = false,
                        allArticles = articles
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isRefreshing = false,
                        error = "Could not refresh feeds: showing cached articles"
                    )
                }
            }
        }
    }

    fun selectCategory(category: NewsCategory) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setBottomTab(tab: BottomNavTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun setSavedSubTab(subTab: SavedSubTab) {
        _uiState.update { it.copy(savedSubTab = subTab) }
    }

    fun openArticle(article: Article) {
        viewModelScope.launch {
            repository.markAsRead(article)
            _uiState.update {
                it.copy(
                    selectedArticle = article.copy(isRead = true)
                )
            }
        }
    }

    fun closeArticle() {
        ttsManager.stop()
        _uiState.update { it.copy(selectedArticle = null) }
    }

    fun toggleBookmark(article: Article) {
        viewModelScope.launch {
            val newStatus = repository.toggleBookmark(article)
            _uiState.update { state ->
                state.copy(
                    selectedArticle = if (state.selectedArticle?.id == article.id) {
                        state.selectedArticle.copy(isBookmarked = newStatus)
                    } else state.selectedArticle
                )
            }
        }
    }

    fun toggleTtsForCurrentArticle() {
        val article = _uiState.value.selectedArticle ?: return
        val textToSpeak = "${article.title}. By ${article.author}. ${article.content}"
        ttsManager.speak(textToSpeak)
    }

    fun stopTts() {
        ttsManager.stop()
    }

    fun updateReaderFontSize(sizeSp: Float) {
        _uiState.update {
            it.copy(readerSettings = it.readerSettings.copy(fontSizeSp = sizeSp))
        }
    }

    fun updateReaderFont(font: ReaderFont) {
        _uiState.update {
            it.copy(readerSettings = it.readerSettings.copy(font = font))
        }
    }

    fun updateReaderTheme(theme: ReaderTheme) {
        _uiState.update {
            it.copy(readerSettings = it.readerSettings.copy(theme = theme))
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    fun clearAllBookmarks() {
        viewModelScope.launch {
            repository.clearAllBookmarks()
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown()
    }
}
