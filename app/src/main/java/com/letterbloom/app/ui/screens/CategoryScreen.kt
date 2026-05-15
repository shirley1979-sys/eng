package com.letterbloom.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.ui.platform.LocalContext
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
import com.letterbloom.app.data.LearningPrefs
import com.letterbloom.app.data.wordList
import com.letterbloom.app.ui.theme.*

@Composable
fun CategoryScreen(onCategorySelected: (String) -> Unit, onPronunciation: () -> Unit, onBack: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val favoriteCount = LearningPrefs.getFavorites(context).size
    val wrongCount = LearningPrefs.getWrongWords(context).size
    val categories = WordCategory.values().toList()
    val currentLevel = LearningPrefs.getCurrentLevel(context)

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
                Text(text = "9가지 카테고리 + 나만의 학습 모드", fontSize = 12.sp, color = TextMedium)
                Spacer(modifier = Modifier.height(12.dp))

                // 발음 레슨 배너
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            androidx.compose.ui.graphics.Brush.horizontalGradient(
                                listOf(HermesBrown, Color(0xFF4A2010))
                            )
                        )
                        .border(1.dp, HermesGold.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        .clickable { onPronunciation() }
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("🎤", fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("발음 레슨", fontSize = 15.sp,
                                fontWeight = FontWeight.Bold, color = HermesIvory)
                            Text("마이크로 발음 연습하기",
                                fontSize = 12.sp, color = Color.White.copy(alpha = 0.6f))
                        }
                        Text("→", fontSize = 18.sp, color = HermesGold,
                            fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // 특별 학습 카드
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SpecialCard(
                        icon = { Icon(Icons.Default.Favorite, null, tint = Color(0xFFE91E63), modifier = Modifier.size(28.dp)) },
                        title = "즐겨찾기",
                        count = favoriteCount,
                        color = Color(0xFFE91E63),
                        modifier = Modifier.weight(1f),
                        onClick = { onCategorySelected("FAVORITES") }
                    )
                    SpecialCard(
                        icon = { Icon(Icons.Default.Bookmark, null, tint = Color(0xFFFF9800), modifier = Modifier.size(28.dp)) },
                        title = "오답노트",
                        count = wrongCount,
                        color = Color(0xFFFF9800),
                        modifier = Modifier.weight(1f),
                        onClick = { onCategorySelected("WRONG") }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                Spacer(modifier = Modifier.height(16.dp))

                categories.chunked(2).forEach { row ->
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        row.forEach { category ->
                            val allWords = wordList.filter { it.category == category }
                            val total = allWords.size
                            val unlocked = LearningPrefs.unlockedWordCount(context, total)
                            val nextLock = LearningPrefs.nextUnlockLevel(context, total)
                            CategoryCard(
                                category = category,
                                unlockedCount = unlocked,
                                totalCount = total,
                                nextUnlockLevel = nextLock,
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
    unlockedCount: Int,
    totalCount: Int,
    nextUnlockLevel: Int?,
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
    val hasLocked = unlockedCount < totalCount

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
            // 해금 진도 바
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(accentColor.copy(alpha = 0.15f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(unlockedCount.toFloat() / totalCount)
                        .fillMaxHeight()
                        .background(accentColor.copy(alpha = 0.7f))
                )
            }
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
            // 해금된 단어 수 표시
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(accentColor.copy(alpha = 0.1f))
                    .padding(horizontal = 10.dp, vertical = 3.dp)
            ) {
                Text(
                    text = if (hasLocked) "$unlockedCount / $totalCount 단어"
                           else "$totalCount 단어",
                    fontSize = 11.sp,
                    color = accentColor,
                    fontWeight = FontWeight.SemiBold
                )
            }
            // 다음 해금 레벨 안내
            if (hasLocked && nextUnlockLevel != null) {
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "🔒 Lv.$nextUnlockLevel 해금",
                    fontSize = 10.sp,
                    color = TextLight,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun SpecialCard(
    icon: @Composable () -> Unit,
    title: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .aspectRatio(0.85f)
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .border(1.5.dp, color.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(color.copy(alpha = 0.5f))
            )
            Spacer(modifier = Modifier.height(14.dp))
            icon()
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold,
                color = InkBrown, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(color.copy(alpha = 0.1f))
                    .padding(horizontal = 10.dp, vertical = 3.dp)
            ) {
                Text(text = "${count}단어", fontSize = 11.sp,
                    color = color, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
