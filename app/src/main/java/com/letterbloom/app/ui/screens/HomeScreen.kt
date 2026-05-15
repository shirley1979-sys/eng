package com.letterbloom.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.letterbloom.app.data.LearningPrefs
import com.letterbloom.app.data.WordCategory
import com.letterbloom.app.ui.theme.*

@Composable
fun HomeScreen(onCategoryClick: () -> Unit, onProgressClick: () -> Unit) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var todayWords by remember { mutableStateOf(0) }
    var dailyGoal by remember { mutableStateOf(10) }
    var streakDays by remember { mutableStateOf(0) }
    var currentLevel by remember { mutableStateOf("여행 준비생") }
    var totalWords by remember { mutableStateOf(0) }
    var currentLevelNum by remember { mutableStateOf(1) }
    var hearts by remember { mutableStateOf(LearningPrefs.MAX_HEARTS) }
    var xp by remember { mutableStateOf(0) }

    LaunchedEffect(true) {
        todayWords = LearningPrefs.getTodayWords(context)
        dailyGoal = LearningPrefs.getDailyGoal(context)
        streakDays = LearningPrefs.getStreakDays(context)
        currentLevel = LearningPrefs.getLevelName(context)
        totalWords = LearningPrefs.getTotalWords(context)
        currentLevelNum = LearningPrefs.getCurrentLevel(context)
        hearts = LearningPrefs.getHearts(context)
        xp = LearningPrefs.getXP(context)
    }

    val progressAnim by animateFloatAsState(
        targetValue = if (dailyGoal > 0) (todayWords.toFloat() / dailyGoal).coerceIn(0f, 1f) else 0f,
        animationSpec = tween(1000, easing = EaseOut), label = "progress"
    )

    val nextLevelWords = LearningPrefs.wordsForNextLevel(currentLevelNum)
    val prevLevelWords = LearningPrefs.wordsForCurrentLevel(currentLevelNum)
    val levelProgress = if (nextLevelWords > prevLevelWords)
        ((totalWords - prevLevelWords).toFloat() / (nextLevelWords - prevLevelWords)).coerceIn(0f, 1f)
    else 1f
    val levelProgressAnim by animateFloatAsState(
        targetValue = levelProgress,
        animationSpec = tween(1200, easing = EaseOut), label = "level"
    )

    Box(modifier = Modifier.fillMaxSize().background(HermesCream)) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {

            // ── 헤더 ──────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(listOf(HermesBrown, Color(0xFF3D1E0C)))
                    )
                    .padding(horizontal = 24.dp, vertical = 28.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "L E T T E R B L O O M",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = HermesGold,
                            letterSpacing = 3.sp
                        )
                        // 하트 + XP
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Row {
                                repeat(LearningPrefs.MAX_HEARTS) { i ->
                                    Text(if (i < hearts) "❤️" else "🖤", fontSize = 14.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(HermesGold.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text("✦ $xp XP", fontSize = 11.sp,
                                    color = HermesGold, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = "지금이",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Light,
                                color = HermesIvory
                            )
                            Text(
                                text = "딱 좋은 때",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, HermesGold.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                .clickable { onProgressClick() }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "Lv.$currentLevelNum", fontSize = 10.sp,
                                    color = HermesGold, fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp)
                                Text(text = currentLevel, fontSize = 11.sp,
                                    color = HermesIvory, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }

            // ── 오늘 진도 ──────────────────────────────────────
            Column(modifier = Modifier.padding(20.dp)) {

                // 오늘의 학습 카드
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                        .border(1.dp, BorderWarm, RoundedCornerShape(20.dp))
                        .padding(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // 원형 진도
                        Box(modifier = Modifier.size(88.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                progress = { progressAnim },
                                modifier = Modifier.size(88.dp),
                                color = HermesOrange,
                                trackColor = HermesIvory,
                                strokeWidth = 7.dp,
                                strokeCap = StrokeCap.Round
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "$todayWords", fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold, color = HermesBrown)
                                Text(text = "/$dailyGoal", fontSize = 11.sp, color = TextLight)
                            }
                        }
                        Spacer(modifier = Modifier.width(20.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "오늘의 학습", fontSize = 11.sp,
                                color = TextLight, letterSpacing = 0.5.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            if (todayWords >= dailyGoal) {
                                Text(text = "목표 달성! 🎉", fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold, color = HermesOrange)
                            } else {
                                Text(text = "${dailyGoal - todayWords}단어\n더 해봐요",
                                    fontSize = 18.sp, fontWeight = FontWeight.Bold,
                                    color = InkBrown, lineHeight = 26.sp)
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(HermesWarm)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)) {
                                    Text(text = "🔥 ${streakDays}일 연속", fontSize = 11.sp,
                                        color = HermesBrownMid, fontWeight = FontWeight.Medium)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(HermesWarm)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)) {
                                    Text(text = "📖 ${totalWords}단어", fontSize = 11.sp,
                                        color = HermesBrownMid, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 레벨업 진도 카드
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(HermesOrange, HermesOrangeDeep)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "레벨업 진도", fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.8f), letterSpacing = 0.5.sp)
                            if (currentLevelNum < 50) {
                                Text(text = "→  Lv.${currentLevelNum + 1}  ${LearningPrefs.levelNameForLevel(currentLevelNum + 1)}",
                                    fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(text = "Lv.$currentLevelNum  $currentLevel", fontSize = 18.sp,
                                fontWeight = FontWeight.Bold, color = Color.White)
                            if (currentLevelNum < 50) {
                                Text(
                                    text = "  +${(nextLevelWords - totalWords).coerceAtLeast(0)}단어",
                                    fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.padding(bottom = 3.dp)
                                )
                            } else {
                                Text(text = "  MAX!", fontSize = 12.sp,
                                    color = HermesGold, modifier = Modifier.padding(bottom = 3.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress = { levelProgressAnim },
                            modifier = Modifier.fillMaxWidth().height(5.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = Color.White,
                            trackColor = Color.White.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "$totalWords / $nextLevelWords 단어",
                            fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 학습 시작 버튼
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(InkBrown)
                        .clickable { onCategoryClick() }
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✦  오늘 학습 시작하기",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = HermesIvory,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // 카테고리 섹션
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "CATEGORIES", fontSize = 9.sp,
                            fontWeight = FontWeight.Bold, color = HermesGold,
                            letterSpacing = 2.sp)
                        Text(text = "카테고리별 학습", fontSize = 16.sp,
                            fontWeight = FontWeight.Bold, color = InkBrown)
                    }
                    TextButton(onClick = onProgressClick) {
                        Text(text = "여정 보기 →", fontSize = 12.sp, color = HermesOrange,
                            fontWeight = FontWeight.Medium)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                val categories = WordCategory.values().toList()
                categories.chunked(2).forEach { row ->
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        row.forEach { cat ->
                            CategoryMiniCard(category = cat,
                                modifier = Modifier.weight(1f), onClick = onCategoryClick)
                        }
                        if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun CategoryMiniCard(category: WordCategory, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, BorderWarm, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(vertical = 18.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = category.emoji, fontSize = 28.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = category.displayName, fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold, color = TextBrown,
                textAlign = TextAlign.Center)
        }
    }
}
