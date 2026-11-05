package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {
    @Query("SELECT * FROM saved_articles WHERE isBookmarked = 1 ORDER BY savedAt DESC")
    fun getBookmarkedArticles(): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM saved_articles WHERE isRead = 1 ORDER BY savedAt DESC")
    fun getReadingHistory(): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM saved_articles WHERE id = :id LIMIT 1")
    suspend fun getArticleById(id: String): ArticleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(article: ArticleEntity)

    @Update
    suspend fun update(article: ArticleEntity)

    @Query("UPDATE saved_articles SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun updateBookmarkStatus(id: String, isBookmarked: Boolean)

    @Query("UPDATE saved_articles SET isRead = :isRead, savedAt = :timestamp WHERE id = :id")
    suspend fun updateReadStatus(id: String, isRead: Boolean, timestamp: Long = System.currentTimeMillis())

    @Delete
    suspend fun delete(article: ArticleEntity)

    @Query("DELETE FROM saved_articles WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM saved_articles WHERE isRead = 1 AND isBookmarked = 0")
    suspend fun clearHistory()

    @Query("DELETE FROM saved_articles WHERE isBookmarked = 1")
    suspend fun clearAllBookmarks()
}
