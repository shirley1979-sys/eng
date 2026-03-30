package com.letterbloom.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.letterbloom.app.data.LearningPrefs
import com.letterbloom.app.ui.theme.*

private val chapterEmojis = listOf(
    "🌱","✈️","🛫","🏨","🍽️","🛍️","🗺️","🚇","🏥","🌍"
)

private val chapterNames = listOf(
    "여행 준비","공항","기내","호텔","식당","쇼핑","관광","교통","응급","글로벌"
)

@Composable
fun ProgressScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val totalWords = LearningPrefs.getTotalWords(context)
    val streakDays = LearningPrefs.getStreakDays(context)
    val currentLevel = LearningPrefs.getCurrentLevel(context)
    val correctAnswers = LearningPrefs.getCorrectAnswers(context)
    val totalAnswers = LearningPrefs.getTotalAnswers(context)
    val accuracy = if (totalAnswers > 0) (correctAnswers * 100 / totalAnswers) else 0

    Box(modifier = Modifier.fillMaxSize().background(HermesCream)) {
        Column(modifier = Modifier.fillMaxSize()) {

            // 헤더
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(HermesBrown, Color(0xFF3D1E0C))))
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "뒤로",
                                tint = HermesGold)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text(text = "MY JOURNEY", fontSize = 9.sp,
                                fontWeight = FontWeight.Bold, color = HermesGold,
                                letterSpacing = 2.sp)
                            Text(text = "나의 학습 여정", fontSize = 20.sp,
                                fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // 스탯 3개
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround) {
                        HeaderStat(value = "${totalWords}", label = "총 단어", emoji = "📖")
                        Box(modifier = Modifier.width(1.dp).height(40.dp)
                            .background(Color.White.copy(alpha = 0.15f)))
                        HeaderStat(value = "${streakDays}일", label = "연속 학습", emoji = "🔥")
                        Box(modifier = Modifier.width(1.dp).height(40.dp)
                            .background(Color.White.copy(alpha = 0.15f)))
                        HeaderStat(value = "${accuracy}%", label = "정답률", emoji = "⭐")
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // 전체 진도
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                        .border(1.dp, BorderWarm, RoundedCornerShape(20.dp))
                        .padding(20.dp)
                ) {
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "전체 진도", fontSize = 12.sp, color = TextLight)
                            Text(text = "Lv.$currentLevel / 50",
                                fontSize = 13.sp, fontWeight = FontWeight.Bold, color = HermesOrange)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        val totalProgressAnim by animateFloatAsState(
                            targetValue = currentLevel.toFloat() / 50f,
                            animationSpec = tween(1200, easing = EaseOut), label = "total")
                        LinearProgressIndicator(
                            progress = { totalProgressAnim },
                            modifier = Modifier.fillMaxWidth().height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = HermesOrange, trackColor = HermesIvory
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${totalWords} / 250 단어  (5단어마다 레벨업)",
                            fontSize = 11.sp, color = TextLight
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(text = "LEVEL ROADMAP", fontSize = 9.sp,
                    fontWeight = FontWeight.Bold, color = HermesGold, letterSpacing = 2.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "50단계 여행 레벨", fontSize = 16.sp,
                    fontWeight = FontWeight.Bold, color = InkBrown)

                Spacer(modifier = Modifier.height(16.dp))

                // 챕터별 카드 (5레벨씩 10개 챕터)
                for (chapter in 0 until 10) {
                    val chapterStart = chapter * 5 + 1
                    val chapterEnd = chapter * 5 + 5
                    val isChapterUnlocked = currentLevel >= chapterStart
                    val isChapterComplete = currentLevel > chapterEnd

                    ChapterCard(
                        chapterNum = chapter + 1,
                        emoji = chapterEmojis[chapter],
                        name = chapterNames[chapter],
                        levelStart = chapterStart,
                        levelEnd = chapterEnd,
                        currentLevel = currentLevel,
                        isUnlocked = isChapterUnlocked,
                        isComplete = isChapterComplete
                    )

                    if (chapter < 9) {
                        Box(
                            modifier = Modifier
                                .padding(start = 30.dp)
                                .width(2.dp)
                                .height(16.dp)
                                .background(
                                    if (isChapterComplete) HermesOrange.copy(alpha = 0.4f)
                                    else HermesSand
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun ChapterCard(
    chapterNum: Int,
    emoji: String,
    name: String,
    levelStart: Int,
    levelEnd: Int,
    currentLevel: Int,
    isUnlocked: Boolean,
    isComplete: Boolean
) {
    val alpha = if (isUnlocked) 1f else 0.4f
    val chapterProgress = when {
        isComplete -> 1f
        isUnlocked -> (currentLevel - levelStart + 1).toFloat() / 5f
        else -> 0f
    }
    val progressAnim by animateFloatAsState(
        targetValue = chapterProgress,
        animationSpec = tween(800, easing = EaseOut), label = "cp"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isComplete) HermesWarm else Color.White)
            .border(
                1.dp,
                if (isComplete) HermesOrange.copy(alpha = 0.3f) else BorderWarm,
                RoundedCornerShape(16.dp)
            )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 챕터 아이콘
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (isComplete) HermesOrange
                        else if (isUnlocked) HermesWarm
                        else HermesIvory
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isUnlocked) {
                    Text(text = emoji, fontSize = 22.sp)
                } else {
                    Text(text = "🔒", fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "CH.$chapterNum",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isUnlocked) HermesOrange else TextLight,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isUnlocked) InkBrown else TextLight
                    )
                    if (isComplete) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "✓", fontSize = 13.sp, color = HermesOrange,
                            fontWeight = FontWeight.Bold)
                    }
                }
                Text(
                    text = "Lv.$levelStart — Lv.$levelEnd",
                    fontSize = 11.sp,
                    color = TextLight
                )
                if (isUnlocked && !isComplete) {
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { progressAnim },
                        modifier = Modifier.fillMaxWidth().height(3.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = HermesOrange, trackColor = HermesSand
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = "Lv.$currentLevel 진행 중",
                        fontSize = 10.sp, color = HermesOrange)
                }
            }

            if (!isUnlocked) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(HermesIvory)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = "Lv.$levelStart~", fontSize = 10.sp, color = TextLight)
                }
            }
        }
    }
}

@Composable
private fun HeaderStat(value: String, label: String, emoji: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = emoji, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = HermesGold)
        Text(text = label, fontSize = 10.sp, color = Color.White.copy(alpha = 0.6f))
    }
}
