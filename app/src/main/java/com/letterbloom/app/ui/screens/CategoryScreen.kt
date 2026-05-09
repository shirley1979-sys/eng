package com.letterbloom.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.letterbloom.app.data.WordCategory
import com.letterbloom.app.data.wordList
import com.letterbloom.app.ui.theme.*

@Composable
fun CategoryScreen(onCategorySelected: (String) -> Unit, onBack: () -> Unit) {
    val categories = WordCategory.values().toList()

    Box(modifier = Modifier.fillMaxSize().background(HermesCream)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 헤더
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(HermesBrown, Color(0xFF3D1E0C))))
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로", tint = HermesGold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = "SELECT CATEGORY", fontSize = 9.sp,
                            fontWeight = FontWeight.Bold, color = HermesGold, letterSpacing = 2.sp)
                        Text(text = "어떤 상황의 영어를 배울까요?", fontSize = 18.sp,
                            fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Text(text = "8가지 여행 카테고리 중 하나를 선택하세요",
                    fontSize = 12.sp, color = TextMedium)
                Spacer(modifier = Modifier.height(16.dp))

                categories.chunked(2).forEach { row ->
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        row.forEach { category ->
                            val wordCount = wordList.count { it.category == category }
                            CategoryCard(
                                category = category,
                                wordCount = wordCount,
                                modifier = Modifier.weight(1f),
                                onClick = { onCategorySelected(category.name) }
                            )
                        }
                        if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun CategoryCard(
    category: WordCategory,
    wordCount: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val accentColor = when (category) {
        WordCategory.AIRPORT -> HermesOrange
        WordCategory.HOTEL -> HermesGold
        WordCategory.RESTAURANT -> Color(0xFF8B5E3C)
        WordCategory.SHOPPING -> HermesOrangeDeep
        WordCategory.SIGHTSEEING -> Color(0xFF5C7A3E)
        WordCategory.TRANSPORT -> Color(0xFF4A6FA5)
        WordCategory.EMERGENCY -> Color(0xFFB03A2E)
        WordCategory.DAILY -> HermesBrownMid
        WordCategory.BUSINESS -> TiffanyDeep
    }

    Box(
        modifier = modifier
            .aspectRatio(0.85f)
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .border(1.dp, BorderWarm, RoundedCornerShape(18.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 상단 컬러 바
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(accentColor.copy(alpha = 0.4f))
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(text = category.emoji, fontSize = 36.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = category.displayName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = InkBrown,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(accentColor.copy(alpha = 0.1f))
                    .padding(horizontal = 10.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "${wordCount}단어",
                    fontSize = 11.sp,
                    color = accentColor,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
