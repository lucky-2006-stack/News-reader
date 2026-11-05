package com.example.ui.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

enum class ReaderTheme(
    val displayName: String,
    val backgroundColor: Color,
    val textColor: Color,
    val surfaceColor: Color
) {
    SYSTEM(
        displayName = "Auto / System",
        backgroundColor = Color.Unspecified,
        textColor = Color.Unspecified,
        surfaceColor = Color.Unspecified
    ),
    LIGHT(
        displayName = "Editorial Paper",
        backgroundColor = Color(0xFFFBFBFD),
        textColor = Color(0xFF191C1E),
        surfaceColor = Color(0xFFF1F3F5)
    ),
    SEPIA(
        displayName = "Vintage Sepia",
        backgroundColor = Color(0xFFF7EED6),
        textColor = Color(0xFF433422),
        surfaceColor = Color(0xFFEFE2C5)
    ),
    SLATE(
        displayName = "Night Slate",
        backgroundColor = Color(0xFF141A29),
        textColor = Color(0xFFE2E8F0),
        surfaceColor = Color(0xFF1E2738)
    ),
    OLED(
        displayName = "Pure Black",
        backgroundColor = Color(0xFF000000),
        textColor = Color(0xFFE8E8E8),
        surfaceColor = Color(0xFF121212)
    )
}

enum class ReaderFont(val displayName: String, val fontFamily: FontFamily) {
    SERIF("Classic Serif", FontFamily.Serif),
    SANS_SERIF("Modern Sans", FontFamily.SansSerif),
    MONOSPACE("Monospace", FontFamily.Monospace)
}

data class ReaderSettings(
    val fontSizeSp: Float = 18f,
    val lineSpacingMultiplier: Float = 1.6f,
    val theme: ReaderTheme = ReaderTheme.SYSTEM,
    val font: ReaderFont = ReaderFont.SERIF
) {
    val fontSize: TextUnit get() = fontSizeSp.sp
    val lineHeight: TextUnit get() = (fontSizeSp * lineSpacingMultiplier).sp
}
