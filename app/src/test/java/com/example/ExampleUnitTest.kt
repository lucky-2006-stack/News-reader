package com.example

import com.example.data.local.ArticleEntity
import com.example.data.model.Article
import com.example.data.model.NewsCategory
import com.example.data.remote.CuratedNewsData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun curatedArticles_arePopulated() {
        val articles = CuratedNewsData.allArticles
        assertTrue(articles.isNotEmpty())
        assertTrue(CuratedNewsData.breakingNews.isNotEmpty())
        assertTrue(articles.any { it.category == NewsCategory.TECH })
        assertTrue(articles.any { it.category == NewsCategory.BUSINESS })
        assertTrue(articles.any { it.category == NewsCategory.SCIENCE })
    }

    @Test
    fun articleEntity_conversion_isAccurate() {
        val article = Article(
            id = "test_123",
            title = "Quantum Breakthrough",
            description = "Short summary",
            content = "Full content details.",
            author = "Alice Smith",
            source = "Tech Daily",
            category = NewsCategory.TECH,
            publishedAt = "10m ago",
            readTimeMinutes = 4,
            isBookmarked = true,
            isRead = false
        )

        val entity = ArticleEntity.fromArticle(article)
        assertEquals("test_123", entity.id)
        assertEquals("Quantum Breakthrough", entity.title)
        assertTrue(entity.isBookmarked)
        assertFalse(entity.isRead)

        val restored = entity.toArticle()
        assertEquals(article.id, restored.id)
        assertEquals(article.title, restored.title)
        assertEquals(article.category, restored.category)
        assertEquals(article.isBookmarked, restored.isBookmarked)
    }
}
