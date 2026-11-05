package com.example.data.model

import java.util.UUID

enum class NewsCategory(val displayName: String, val feedSlug: String) {
    ALL("Top Stories", "all"),
    TECH("Technology & AI", "tech"),
    BUSINESS("Business & Markets", "business"),
    WORLD("World News", "world"),
    SCIENCE("Science & Space", "science"),
    CULTURE("Culture & Arts", "culture"),
    SPORTS("Sports", "sports")
}

data class Article(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val content: String,
    val author: String = "Editorial Desk",
    val source: String,
    val sourceUrl: String = "",
    val imageUrl: String? = null,
    val category: NewsCategory = NewsCategory.ALL,
    val publishedAt: String,
    val readTimeMinutes: Int = 3,
    val isBookmarked: Boolean = false,
    val isRead: Boolean = false,
    val savedAt: Long = 0L
)

data class FeedSource(
    val id: String,
    val name: String,
    val url: String,
    val category: NewsCategory,
    val isEnabled: Boolean = true
)
