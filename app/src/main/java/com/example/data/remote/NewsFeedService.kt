package com.example.data.remote

import com.example.data.model.Article
import com.example.data.model.FeedSource
import com.example.data.model.NewsCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class NewsFeedService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(6, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .followRedirects(true)
        .build()

    val defaultSources: List<FeedSource> = listOf(
        FeedSource("bbc_world", "BBC World", "https://feeds.bbci.co.uk/news/world/rss.xml", NewsCategory.WORLD),
        FeedSource("bbc_tech", "BBC Tech", "https://feeds.bbci.co.uk/news/technology/rss.xml", NewsCategory.TECH),
        FeedSource("bbc_biz", "BBC Business", "https://feeds.bbci.co.uk/news/business/rss.xml", NewsCategory.BUSINESS),
        FeedSource("bbc_science", "BBC Science", "https://feeds.bbci.co.uk/news/science_and_environment/rss.xml", NewsCategory.SCIENCE),
        FeedSource("npr_top", "NPR News", "https://feeds.npr.org/1001/rss.xml", NewsCategory.ALL),
        FeedSource("hackernews", "Hacker News", "https://news.ycombinator.com/rss", NewsCategory.TECH)
    )

    suspend fun fetchFeed(source: FeedSource): List<Article> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url(source.url)
                .header("User-Agent", "PulseNewsReader/1.0 (Android; en-US)")
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    if (!body.isNullOrBlank()) {
                        return@withContext RssParser.parse(body, source.name, source.category)
                    }
                }
            }
        } catch (_: Exception) {
            // Silently fall back to cached / curated articles
        }
        emptyList()
    }

    suspend fun fetchAllFeeds(sources: List<FeedSource> = defaultSources): List<Article> = withContext(Dispatchers.IO) {
        val deferredList = sources.filter { it.isEnabled }.map { source ->
            async { fetchFeed(source) }
        }
        val results = deferredList.awaitAll().flatten()
        results
    }
}
