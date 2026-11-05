package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NewsCategory
import com.example.ui.theme.CategoryBusiness
import com.example.ui.theme.CategoryCulture
import com.example.ui.theme.CategoryScience
import com.example.ui.theme.CategorySports
import com.example.ui.theme.CategoryTech
import com.example.ui.theme.CategoryWorld

@Composable
fun CategoryBadge(
    category: NewsCategory,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (category) {
        NewsCategory.TECH -> CategoryTech.copy(alpha = 0.15f) to CategoryTech
        NewsCategory.BUSINESS -> CategoryBusiness.copy(alpha = 0.15f) to CategoryBusiness
        NewsCategory.WORLD -> CategoryWorld.copy(alpha = 0.15f) to CategoryWorld
        NewsCategory.SCIENCE -> CategoryScience.copy(alpha = 0.15f) to CategoryScience
        NewsCategory.CULTURE -> CategoryCulture.copy(alpha = 0.15f) to CategoryCulture
        NewsCategory.SPORTS -> CategorySports.copy(alpha = 0.15f) to CategorySports
        NewsCategory.ALL -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = category.displayName.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            letterSpacing = 0.6.sp
        )
    }
}
