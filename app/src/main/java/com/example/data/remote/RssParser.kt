package com.example.data.remote

import android.util.Xml
import com.example.data.model.Article
import com.example.data.model.NewsCategory
import org.xmlpull.v1.XmlPullParser
import java.io.StringReader
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID

object RssParser {

    fun parse(xmlContent: String, sourceName: String, category: NewsCategory): List<Article> {
        val articles = mutableListOf<Article>()
        try {
            val parser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(StringReader(xmlContent))

            var eventType = parser.eventType
            var inItem = false

            var title = ""
            var description = ""
            var content = ""
            var link = ""
            var pubDate = ""
            var author = ""
            var imageUrl: String? = null

            while (eventType != XmlPullParser.END_DOCUMENT) {
                val tagName = parser.name?.lowercase() ?: ""
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        if (tagName == "item" || tagName == "entry") {
                            inItem = true
                            title = ""
                            description = ""
                            content = ""
                            link = ""
                            pubDate = ""
                            author = ""
                            imageUrl = null
                        } else if (inItem) {
                            when {
                                tagName == "title" -> title = readText(parser)
                                tagName == "description" || tagName == "summary" -> {
                                    val rawDesc = readText(parser)
                                    if (imageUrl == null) {
                                        imageUrl = extractImageFromHtml(rawDesc)
                                    }
                                    description = cleanHtml(rawDesc)
                                }
                                tagName == "content:encoded" || tagName == "content" -> {
                                    val rawContent = readText(parser)
                                    if (imageUrl == null) {
                                        imageUrl = extractImageFromHtml(rawContent)
                                    }
                                    content = cleanHtml(rawContent)
                                }
                                tagName == "link" -> {
                                    val href = parser.getAttributeValue(null, "href")
                                    if (!href.isNullOrBlank()) {
                                        link = href
                                    } else {
                                        link = readText(parser)
                                    }
                                }
                                tagName == "pubdate" || tagName == "published" || tagName == "updated" -> {
                                    pubDate = formatPubDate(readText(parser))
                                }
                                tagName == "author" || tagName == "dc:creator" -> {
                                    val text = readText(parser)
                                    if (text.isNotBlank()) author = text
                                }
                                tagName == "enclosure" || tagName == "media:content" || tagName == "media:thumbnail" -> {
                                    val url = parser.getAttributeValue(null, "url")
                                    val type = parser.getAttributeValue(null, "type") ?: ""
                                    if (!url.isNullOrBlank() && (type.contains("image") || isImageUrl(url))) {
                                        if (imageUrl == null) imageUrl = url
                                    }
                                }
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        if (tagName == "item" || tagName == "entry") {
                            inItem = false
                            if (title.isNotBlank()) {
                                val finalContent = if (content.isNotBlank()) content else description
                                val wordCount = finalContent.split("\\s+".toRegex()).size
                                val readMins = (wordCount / 200).coerceAtLeast(2)

                                articles.add(
                                    Article(
                                        id = UUID.nameUUIDFromBytes((link.ifBlank { title }).toByteArray()).toString(),
                                        title = cleanHtml(title),
                                        description = description.ifBlank { finalContent.take(160) },
                                        content = finalContent.ifBlank { description },
                                        author = author.ifBlank { sourceName },
                                        source = sourceName,
                                        sourceUrl = link,
                                        imageUrl = imageUrl,
                                        category = category,
                                        publishedAt = pubDate.ifBlank { "Recently" },
                                        readTimeMinutes = readMins
                                    )
                                )
                            }
                        }
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return articles
    }

    private fun readText(parser: XmlPullParser): String {
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text ?: ""
            parser.nextTag()
        }
        return result
    }

    private fun cleanHtml(html: String): String {
        return html
            .replace(Regex("<script[^>]*>[\\s\\S]*?</script>", RegexOption.IGNORE_CASE), "")
            .replace(Regex("<style[^>]*>[\\s\\S]*?</style>", RegexOption.IGNORE_CASE), "")
            .replace(Regex("<[^>]+>"), " ")
            .replace("&amp;", "&")
            .replace("&quot;", "\"")
            .replace("&apos;", "'")
            .replace("&#39;", "'")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&nbsp;", " ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    private fun extractImageFromHtml(html: String): String? {
        val pattern = Regex("<img[^>]+src=[\"']([^\"']+)[\"']", RegexOption.IGNORE_CASE)
        val match = pattern.find(html)
        return match?.groups?.get(1)?.value
    }

    private fun isImageUrl(url: String): Boolean {
        val lower = url.lowercase()
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png") ||
                lower.endsWith(".webp") || lower.contains("image")
    }

    private fun formatPubDate(raw: String): String {
        if (raw.isBlank()) return "Recently"
        val inputFormats = listOf(
            SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH),
            SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH),
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH),
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.ENGLISH),
            SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        )
        for (format in inputFormats) {
            try {
                val date = format.parse(raw)
                if (date != null) {
                    val diff = System.currentTimeMillis() - date.time
                    val minutes = diff / (1000 * 60)
                    val hours = minutes / 60
                    val days = hours / 24

                    return when {
                        minutes < 2 -> "Just now"
                        minutes < 60 -> "${minutes}m ago"
                        hours < 24 -> "${hours}h ago"
                        days == 1L -> "Yesterday"
                        days < 7 -> "${days}d ago"
                        else -> SimpleDateFormat("MMM d", Locale.ENGLISH).format(date)
                    }
                }
            } catch (_: Exception) {}
        }
        return raw.take(16)
    }
}
