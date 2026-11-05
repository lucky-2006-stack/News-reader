package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.Article
import com.example.data.model.NewsCategory

@Entity(tableName = "saved_articles")
data class ArticleEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val content: String,
    val author: String,
    val source: String,
    val sourceUrl: String,
    val imageUrl: String?,
    val categoryName: String,
    val publishedAt: String,
    val readTimeMinutes: Int,
    val isBookmarked: Boolean,
    val isRead: Boolean,
    val savedAt: Long = System.currentTimeMillis()
) {
    fun toArticle(): Article {
        val cat = try {
            NewsCategory.valueOf(categoryName)
        } catch (e: Exception) {
            NewsCategory.ALL
        }
        return Article(
            id = id,
            title = title,
            description = description,
            content = content,
            author = author,
            source = source,
            sourceUrl = sourceUrl,
            imageUrl = imageUrl,
            category = cat,
            publishedAt = publishedAt,
            readTimeMinutes = readTimeMinutes,
            isBookmarked = isBookmarked,
            isRead = isRead,
            savedAt = savedAt
        )
    }

    companion object {
        fun fromArticle(article: Article): ArticleEntity {
            return ArticleEntity(
                id = article.id,
                title = article.title,
                description = article.description,
                content = article.content,
                author = article.author,
                source = article.source,
                sourceUrl = article.sourceUrl,
                imageUrl = article.imageUrl,
                categoryName = article.category.name,
                publishedAt = article.publishedAt,
                readTimeMinutes = article.readTimeMinutes,
                isBookmarked = article.isBookmarked,
                isRead = article.isRead,
                savedAt = if (article.savedAt != 0L) article.savedAt else System.currentTimeMillis()
            )
        }
    }
}
