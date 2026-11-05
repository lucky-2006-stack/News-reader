package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatColorText
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Article
import com.example.tts.TtsManager
import com.example.ui.components.CategoryBadge
import com.example.ui.model.ReaderFont
import com.example.ui.model.ReaderSettings
import com.example.ui.model.ReaderTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailScreen(
    article: Article,
    readerSettings: ReaderSettings,
    ttsManager: TtsManager,
    onBack: () -> Unit,
    onBookmarkToggle: () -> Unit,
    onFontSizeChange: (Float) -> Unit,
    onFontChange: (ReaderFont) -> Unit,
    onThemeChange: (ReaderTheme) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    BackHandler { onBack() }

    val isSpeaking by ttsManager.isSpeaking.collectAsState()
    var showFormatSheet by remember { mutableStateOf(false) }

    // Resolve active reader theme colors
    val (bgColor, textColor, surfaceColor) = when (readerSettings.theme) {
        ReaderTheme.SYSTEM -> Triple(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.onBackground,
            MaterialTheme.colorScheme.surface
        )
        ReaderTheme.LIGHT -> Triple(Color(0xFFFBFBFD), Color(0xFF191C1E), Color(0xFFF1F3F5))
        ReaderTheme.SEPIA -> Triple(Color(0xFFF7EED6), Color(0xFF382C1E), Color(0xFFEBE0C4))
        ReaderTheme.SLATE -> Triple(Color(0xFF141A29), Color(0xFFE2E8F0), Color(0xFF1E2738))
        ReaderTheme.OLED -> Triple(Color(0xFF000000), Color(0xFFEDEDED), Color(0xFF121212))
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // App Bar
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("article_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = textColor
                        )
                    }
                },
                actions = {
                    // Reader Appearance Settings
                    IconButton(
                        onClick = { showFormatSheet = true },
                        modifier = Modifier.testTag("reader_appearance_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatColorText,
                            contentDescription = "Text style and appearance",
                            tint = textColor
                        )
                    }

                    // TTS Narration Button
                    IconButton(
                        onClick = {
                            val textToSpeak = "${article.title}. By ${article.author}. ${article.content}"
                            ttsManager.speak(textToSpeak)
                        },
                        modifier = Modifier.testTag("tts_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isSpeaking) Icons.Default.Pause else Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = if (isSpeaking) "Pause reading" else "Listen to article",
                            tint = if (isSpeaking) MaterialTheme.colorScheme.secondary else textColor
                        )
                    }

                    // Bookmark
                    IconButton(
                        onClick = onBookmarkToggle,
                        modifier = Modifier.testTag("article_detail_bookmark_button")
                    ) {
                        Icon(
                            imageVector = if (article.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = if (article.isBookmarked) "Remove bookmark" else "Bookmark",
                            tint = if (article.isBookmarked) MaterialTheme.colorScheme.primary else textColor
                        )
                    }

                    // Share
                    IconButton(
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "\"${article.title}\"\n\n${article.description}\n\nRead more via Pulse News: ${article.sourceUrl}"
                                )
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Story"))
                        },
                        modifier = Modifier.testTag("share_article_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share article",
                            tint = textColor
                        )
                    }

                    // Open In Browser
                    if (article.sourceUrl.isNotBlank()) {
                        IconButton(
                            onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.sourceUrl))
                                    context.startActivity(intent)
                                } catch (_: Exception) {}
                            },
                            modifier = Modifier.testTag("open_browser_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.OpenInBrowser,
                                contentDescription = "Open in browser",
                                tint = textColor
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = bgColor
                )
            )

            // Article Body Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                // Category & Read Time
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CategoryBadge(category = article.category)

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Schedule,
                            contentDescription = null,
                            tint = textColor.copy(alpha = 0.65f),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${article.readTimeMinutes} min read",
                            fontSize = 12.sp,
                            color = textColor.copy(alpha = 0.65f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Headline
                Text(
                    text = article.title,
                    fontSize = (readerSettings.fontSizeSp + 8f).sp,
                    lineHeight = (readerSettings.fontSizeSp + 16f).sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = readerSettings.font.fontFamily,
                    color = textColor
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Author & Source info
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = article.author,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                        Text(
                            text = "${article.source} • ${article.publishedAt}",
                            fontSize = 12.sp,
                            color = textColor.copy(alpha = 0.65f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Image Banner
                if (!article.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = article.imageUrl,
                        contentDescription = article.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                            .clip(RoundedCornerShape(16.dp))
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // Lead Description
                if (article.description.isNotBlank() && article.description != article.content) {
                    Text(
                        text = article.description,
                        fontSize = (readerSettings.fontSizeSp + 1f).sp,
                        lineHeight = ((readerSettings.fontSizeSp + 1f) * 1.5f).sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = readerSettings.font.fontFamily,
                        color = textColor.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Full Content Paragraphs
                val paragraphs = article.content.split("\n\n").filter { it.isNotBlank() }
                if (paragraphs.isNotEmpty()) {
                    paragraphs.forEach { para ->
                        Text(
                            text = para.trim(),
                            fontSize = readerSettings.fontSize,
                            lineHeight = readerSettings.lineHeight,
                            fontFamily = readerSettings.font.fontFamily,
                            color = textColor
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                } else {
                    Text(
                        text = article.content,
                        fontSize = readerSettings.fontSize,
                        lineHeight = readerSettings.lineHeight,
                        fontFamily = readerSettings.font.fontFamily,
                        color = textColor
                    )
                }

                Spacer(modifier = Modifier.height(120.dp))
            }
        }

        // Floating Audio Player Banner (Visible when TTS is speaking or active)
        AnimatedVisibility(
            visible = isSpeaking,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tts_player_bar")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Narrating Story",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                            Text(
                                text = article.title,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                maxLines = 1
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { ttsManager.stop() },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = "Stop",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        }

        // Formatting Modal Sheet
        if (showFormatSheet) {
            ModalBottomSheet(
                onDismissRequest = { showFormatSheet = false },
                sheetState = rememberModalBottomSheetState(),
                containerColor = surfaceColor
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                        .navigationBarsPadding()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Reader Preferences",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                        IconButton(onClick = { showFormatSheet = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = textColor)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "THEME",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = textColor.copy(alpha = 0.6f),
                            letterSpacing = 1.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ReaderTheme.entries.forEach { theme ->
                            val isSelected = readerSettings.theme == theme
                            val tBg = when (theme) {
                                ReaderTheme.SYSTEM -> Color(0xFFCCCCCC)
                                ReaderTheme.LIGHT -> Color(0xFFFBFBFD)
                                ReaderTheme.SEPIA -> Color(0xFFF7EED6)
                                ReaderTheme.SLATE -> Color(0xFF141A29)
                                ReaderTheme.OLED -> Color.Black
                            }
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onThemeChange(theme) }
                                    .padding(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(tBg)
                                        .border(
                                            if (isSelected) 3.dp else 1.dp,
                                            if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray.copy(0.4f),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Aa",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = when (theme) {
                                            ReaderTheme.SYSTEM -> Color.Black
                                            ReaderTheme.LIGHT -> Color(0xFF191C1E)
                                            ReaderTheme.SEPIA -> Color(0xFF433422)
                                            ReaderTheme.SLATE -> Color(0xFFE2E8F0)
                                            ReaderTheme.OLED -> Color(0xFFE8E8E8)
                                        }
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = theme.displayName.split(" ").first(),
                                    fontSize = 11.sp,
                                    color = textColor
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "TYPOGRAPHY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = textColor.copy(alpha = 0.6f),
                            letterSpacing = 1.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ReaderFont.entries.forEach { fontOption ->
                            val isSelected = readerSettings.font == fontOption
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                        else surfaceColor
                                    )
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray.copy(0.3f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { onFontChange(fontOption) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = fontOption.displayName,
                                    fontFamily = fontOption.fontFamily,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 12.sp,
                                    color = textColor
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "FONT SIZE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = textColor.copy(alpha = 0.6f),
                                letterSpacing = 1.sp
                            )
                        )
                        Text(
                            text = "${readerSettings.fontSizeSp.toInt()} sp",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 13.sp
                        )
                    }

                    Slider(
                        value = readerSettings.fontSizeSp,
                        onValueChange = onFontSizeChange,
                        valueRange = 14f..26f,
                        steps = 5,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
