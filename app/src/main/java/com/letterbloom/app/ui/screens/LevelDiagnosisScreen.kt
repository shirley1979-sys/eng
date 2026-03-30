package com.letterbloom.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext
import com.letterbloom.app.data.LearningPrefs
import com.letterbloom.app.ui.theme.*

data class DiagnosisQuestion(
    val question: String,
    val situation: String,
    val options: List<String>,
    val correctIndex: Int
)

val diagnosisQuestions = listOf(
    DiagnosisQuestion("식당에서 주문하려고 해요. 뭐라고 말할까요?", "🍽️ 식당",
        listOf("Can I order now?", "I want food.", "Give me menu.", "Food please."), 0),
    DiagnosisQuestion("호텔에서 체크아웃 시간을 물어볼 때는?", "🏨 호텔",
        listOf("When leave?", "What time is check-out?", "I go out time?", "Check-out when?"), 1),
    DiagnosisQuestion("길을 잃었을 때 도움을 요청하려면?", "🆘 응급상황",
        listOf("I lost!", "Help me lost.", "I'm lost. Can you help me?", "Where am I go?"), 2),
    DiagnosisQuestion("견과류 알레르기가 있다고 말할 때는?", "🍽️ 식당",
        listOf("No nuts for me.", "I'm allergic to nuts.", "I hate nuts.", "Nuts are bad."), 1),
    DiagnosisQuestion("좋은 식당을 추천해달라고 할 때는?", "🗺️ 관광",
        listOf("Good restaurant where?", "Tell me restaurant.", "Can you recommend a good restaurant?", "Where food?"), 2)
)

@Composable
fun LevelDiagnosisScreen(onComplete: () -> Unit) {
    var currentQuestion by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    var selectedOption by remember { mutableStateOf(-1) }
    var showResult by remember { mutableStateOf(false) }
    var showFinalResult by remember { mutableStateOf(false) }

    val progress by animateFloatAsState(
        targetValue = (currentQuestion + 1).toFloat() / diagnosisQuestions.size,
        animationSpec = tween(500), label = "progress")

    if (showFinalResult) {
        LevelResultScreen(score = score, total = diagnosisQuestions.size, onComplete = onComplete)
        return
    }

    val question = diagnosisQuestions[currentQuestion]

    Box(modifier = Modifier.fillMaxSize()
        .background(Brush.verticalGradient(listOf(SlateDark, NavyDeep)))) {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "레벨 진단", fontSize = 13.sp, color = AmberGold,
                fontWeight = FontWeight.Medium)

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "${currentQuestion + 1} / ${diagnosisQuestions.size}",
                fontSize = 22.sp, fontWeight = FontWeight.Bold, color = OffWhite)

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                color = AmberGold, trackColor = NavySurface
            )

            Spacer(modifier = Modifier.height(28.dp))

            Box(modifier = Modifier.clip(RoundedCornerShape(20.dp))
                .background(TealPale)
                .padding(horizontal = 16.dp, vertical = 6.dp)) {
                Text(text = question.situation, fontSize = 14.sp, color = TealLight,
                    fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)) {
                Text(text = question.question, fontSize = 18.sp,
                    fontWeight = FontWeight.Medium, color = OffWhite,
                    textAlign = TextAlign.Center, lineHeight = 26.sp,
                    modifier = Modifier.padding(24.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            question.options.forEachIndexed { index, option ->
                val bgColor = when {
                    !showResult -> if (selectedOption == index) NavySurface else CardDark
                    index == question.correctIndex -> Color(0xFF14532D)
                    index == selectedOption && selectedOption != question.correctIndex -> Color(0xFF450A0A)
                    else -> CardDark
                }
                val borderColor = when {
                    !showResult -> if (selectedOption == index) TealElectric else BorderColor
                    index == question.correctIndex -> Color(0xFF4ADE80)
                    index == selectedOption && selectedOption != question.correctIndex -> CoralAccent
                    else -> BorderColor
                }

                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(bgColor)
                    .border(1.5.dp, borderColor, RoundedCornerShape(14.dp))
                    .clickable(enabled = !showResult) {
                        selectedOption = index; showResult = true
                        if (index == question.correctIndex) score++
                    }
                    .padding(16.dp)) {
                    Text(text = option, fontSize = 15.sp, color = OffWhite,
                        fontWeight = if (selectedOption == index) FontWeight.Medium else FontWeight.Normal)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            if (showResult) {
                Button(
                    onClick = {
                        if (currentQuestion < diagnosisQuestions.size - 1) {
                            currentQuestion++; selectedOption = -1; showResult = false
                        } else showFinalResult = true
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AmberGold)
                ) {
                    Text(
                        text = if (currentQuestion < diagnosisQuestions.size - 1) "다음 문제 →" else "결과 보기",
                        fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NavyDeep
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun LevelResultScreen(score: Int, total: Int, onComplete: () -> Unit) {
    val context = LocalContext.current
    val (levelNum, levelName) = when {
        score <= 1 -> 1 to "여행 준비생"
        score == 2 -> 2 to "공항 통과"
        score == 3 -> 3 to "호텔 체크인"
        score == 4 -> 4 to "자유 여행자"
        else -> 5 to "글로벌 여행러"
    }
    val level = when (levelNum) {
        1 -> Triple("여행 준비생", "🌱", "기초부터 차근차근 시작해요!")
        2 -> Triple("공항 통과", "🛫", "공항에서는 이제 문제없어요!")
        3 -> Triple("호텔 체크인", "🏨", "호텔도 거뜬하게 대화해요!")
        4 -> Triple("자유 여행자", "🗺️", "혼자서도 자유롭게 여행해요!")
        else -> Triple("글로벌 여행러", "🌍", "어디서든 영어로 통해요!")
    }
    LaunchedEffect(Unit) {
        LearningPrefs.setLevelFromDiagnosis(context, levelNum, levelName)
    }

    Box(modifier = Modifier.fillMaxSize()
        .background(Brush.verticalGradient(listOf(SlateDark, NavyDeep))),
        contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)) {
            Text(text = level.second, fontSize = 80.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "현재 레벨", fontSize = 14.sp, color = White60)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = level.first, fontSize = 28.sp,
                fontWeight = FontWeight.Bold, color = AmberGold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "$score / $total 정답", fontSize = 16.sp,
                color = TealElectric, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = level.third, fontSize = 16.sp, color = White80,
                textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(40.dp))
            Button(onClick = onComplete,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AmberGold)) {
                Text(text = "학습 시작하기 ✦", fontSize = 16.sp,
                    fontWeight = FontWeight.Bold, color = NavyDeep)
            }
        }
    }
}
