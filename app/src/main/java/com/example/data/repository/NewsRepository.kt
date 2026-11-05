package com.example.data.repository

import com.example.data.local.ArticleDao
import com.example.data.local.ArticleEntity
import com.example.data.model.Article
import com.example.data.model.NewsCategory
import com.example.data.remote.CuratedNewsData
import com.example.data.remote.NewsFeedService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NewsRepository(
    private val feedService: NewsFeedService,
    private val articleDao: ArticleDao
) {
    private var cachedArticles: List<Article> = CuratedNewsData.allArticles

    val bookmarkedArticles: Flow<List<Article>> = articleDao.getBookmarkedArticles()
        .map { list -> list.map { it.toArticle() } }

    val readingHistory: Flow<List<Article>> = articleDao.getReadingHistory()
        .map { list -> list.map { it.toArticle() } }

    suspend fun loadArticles(forceRefresh: Boolean = false): List<Article> {
        if (!forceRefresh && cachedArticles.isNotEmpty() && cachedArticles.size > CuratedNewsData.allArticles.size) {
            return syncWithLocalData(cachedArticles)
        }

        val remoteList = feedService.fetchAllFeeds()
        val combined = if (remoteList.isNotEmpty()) {
            val combinedList = (remoteList + CuratedNewsData.allArticles)
                .distinctBy { it.title.trim().lowercase() }
            combinedList
        } else {
            CuratedNewsData.allArticles
        }

        cachedArticles = combined
        return syncWithLocalData(combined)
    }

    private suspend fun syncWithLocalData(articles: List<Article>): List<Article> {
        return articles.map { article ->
            val local = articleDao.getArticleById(article.id)
            if (local != null) {
                article.copy(
                    isBookmarked = local.isBookmarked,
                    isRead = local.isRead
                )
            } else {
                article
            }
        }
    }

    suspend fun toggleBookmark(article: Article): Boolean {
        val newStatus = !article.isBookmarked
        val updated = article.copy(isBookmarked = newStatus, savedAt = System.currentTimeMillis())

        val existing = articleDao.getArticleById(article.id)
        if (existing != null) {
            articleDao.updateBookmarkStatus(article.id, newStatus)
        } else {
            articleDao.insertOrUpdate(ArticleEntity.fromArticle(updated))
        }

        // Update in-memory cache
        cachedArticles = cachedArticles.map {
            if (it.id == article.id) it.copy(isBookmarked = newStatus) else it
        }

        return newStatus
    }

    suspend fun markAsRead(article: Article) {
        val updated = article.copy(isRead = true, savedAt = System.currentTimeMillis())
        val existing = articleDao.getArticleById(article.id)
        if (existing != null) {
            articleDao.updateReadStatus(article.id, true)
        } else {
            articleDao.insertOrUpdate(ArticleEntity.fromArticle(updated))
        }

        cachedArticles = cachedArticles.map {
            if (it.id == article.id) it.copy(isRead = true) else it
        }
    }

    suspend fun removeBookmark(articleId: String) {
        articleDao.updateBookmarkStatus(articleId, false)
        cachedArticles = cachedArticles.map {
            if (it.id == articleId) it.copy(isBookmarked = false) else it
        }
    }

    suspend fun clearHistory() {
        articleDao.clearHistory()
        cachedArticles = cachedArticles.map { it.copy(isRead = false) }
    }

    suspend fun clearAllBookmarks() {
        articleDao.clearAllBookmarks()
        cachedArticles = cachedArticles.map { it.copy(isBookmarked = false) }
    }
}
