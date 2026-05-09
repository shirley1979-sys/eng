package com.letterbloom.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.letterbloom.app.data.LearningPrefs
import com.letterbloom.app.data.SupabaseSync
import com.letterbloom.app.data.quizQuestions
import com.letterbloom.app.ui.theme.*

@Composable
fun QuizScreen(category: String, onComplete: () -> Unit, onBack: () -> Unit) {
    val context = LocalContext.current
    val questions = remember { quizQuestions.shuffled().take(5) }
    var currentIndex by remember { mutableStateOf(0) }
    var selectedOption by remember { mutableStateOf(-1) }
    var showResult by remember { mutableStateOf(false) }
    var score by remember { mutableStateOf(0) }
    var showFinal by remember { mutableStateOf(false) }

    val progressAnim by animateFloatAsState(
        targetValue = (currentIndex + 1).toFloat() / questions.size,
        animationSpec = tween(500), label = "progress"
    )

    if (showFinal) {
        LearningPrefs.addQuizResult(context, score, questions.size)
        LaunchedEffect(Unit) { SupabaseSync.saveProgress(context) }
        QuizResultScreen(score = score, total = questions.size, onComplete = onComplete)
        return
    }

    val question = questions[currentIndex]

    Box(modifier = Modifier.fillMaxSize().background(HermesCream)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 상단 바
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "뒤로", tint = HermesBrown)
                }
                Text(text = "QUIZ  ${currentIndex + 1} / ${questions.size}",
                    fontSize = 11.sp, fontWeight = FontWeight.Bold,
                    color = TextMedium, letterSpacing = 1.sp)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(HermesWarm)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(text = "✦ $score", fontSize = 13.sp, color = HermesGold,
                        fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progressAnim },
                modifier = Modifier.fillMaxWidth().height(3.dp).clip(RoundedCornerShape(2.dp)),
                color = HermesOrange, trackColor = HermesSand
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 상황 태그
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(HermesWarm)
                    .border(1.dp, BorderWarm, RoundedCornerShape(20.dp))
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(text = question.situation, fontSize = 13.sp,
                    color = TextBrown, fontWeight = FontWeight.Medium)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = question.situationKorean, fontSize = 12.sp, color = TextLight)

            Spacer(modifier = Modifier.height(16.dp))

            // 문제 카드
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.verticalGradient(listOf(HermesBrown, Color(0xFF4A2010))))
                    .border(1.dp, HermesGold.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
                    .padding(24.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()) {
                    Text(text = "빈칸에 알맞은 단어는?", fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.5f), letterSpacing = 0.5.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    val parts = question.sentence.split("___")
                    val filledWord = if (showResult) question.blank else "___"
                    val fillColor = when {
                        !showResult -> HermesGold
                        selectedOption == question.correctIndex -> Color(0xFF86EFAC)
                        else -> Color(0xFFFCA5A5)
                    }

                    Text(
                        text = buildAnnotatedString {
                            append(parts.getOrElse(0) { "" })
                            withStyle(SpanStyle(color = fillColor, fontWeight = FontWeight.Bold,
                                fontSize = 22.sp)) { append(filledWord) }
                            append(parts.getOrElse(1) { "" })
                        },
                        fontSize = 19.sp, fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center, lineHeight = 30.sp
                    )

                    if (showResult) {
                        Spacer(modifier = Modifier.height(12.dp))
                        val isCorrect = selectedOption == question.correctIndex
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isCorrect) Color(0x2286EFAC) else Color(0x22FCA5A5)
                                )
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (isCorrect) "✓  정답!" else "✕  오답",
                                fontSize = 14.sp, fontWeight = FontWeight.Bold,
                                color = if (isCorrect) Color(0xFF86EFAC) else Color(0xFFFCA5A5)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 선택지
            question.options.forEachIndexed { index, option ->
                val isCorrect = index == question.correctIndex
                val isSelected = index == selectedOption
                val bgColor = when {
                    !showResult -> if (isSelected) HermesBrown else Color.White
                    isCorrect -> Color(0xFFDCFCE7)
                    isSelected && !isCorrect -> Color(0xFFFEE2E2)
                    else -> Color.White
                }
                val borderColor = when {
                    !showResult -> if (isSelected) HermesOrange else BorderWarm
                    isCorrect -> Color(0xFF86EFAC)
                    isSelected && !isCorrect -> Color(0xFFFCA5A5)
                    else -> BorderWarm
                }
                val textColor = when {
                    !showResult -> if (isSelected) HermesIvory else InkBrown
                    isCorrect -> Color(0xFF166534)
                    isSelected && !isCorrect -> Color(0xFF991B1B)
                    else -> TextBrown
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(bgColor)
                        .border(1.5.dp, borderColor, RoundedCornerShape(14.dp))
                        .clickable(enabled = !showResult) {
                            selectedOption = index
                            showResult = true
                            if (index == question.correctIndex) {
                                score++
                                if (question.wordEnglish.isNotEmpty())
                                    LearningPrefs.markWordCorrect(context, question.wordEnglish)
                            } else {
                                if (question.wordEnglish.isNotEmpty())
                                    LearningPrefs.addWrongWord(context, question.wordEnglish)
                            }
                        }
                        .padding(horizontal = 18.dp, vertical = 14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (isSelected && !showResult) HermesOrange
                                    else HermesWarm
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = listOf("A","B","C","D")[index],
                                fontSize = 11.sp, fontWeight = FontWeight.Bold,
                                color = if (isSelected && !showResult) Color.White else TextMedium
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = option, fontSize = 15.sp, color = textColor,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            if (showResult) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(InkBrown)
                        .clickable {
                            if (currentIndex < questions.size - 1) {
                                currentIndex++; selectedOption = -1; showResult = false
                            } else showFinal = true
                        }
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (currentIndex < questions.size - 1) "다음 문제  →" else "결과 보기  ✦",
                        fontSize = 16.sp, fontWeight = FontWeight.Bold,
                        color = HermesIvory, letterSpacing = 0.5.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun QuizResultScreen(score: Int, total: Int, onComplete: () -> Unit) {
    val percent = (score.toFloat() / total * 100).toInt()
    val (emoji, message) = when {
        percent >= 80 -> "✦" to "완벽해요! 여행에서 바로 쓸 수 있어요"
        percent >= 60 -> "◈" to "잘 하셨어요! 조금만 더 연습해요"
        else -> "◇" to "다시 한번 도전해봐요!"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(HermesBrown, Color(0xFF4A2010)))),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(text = emoji, fontSize = 60.sp, color = HermesGold)
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "QUIZ COMPLETE", fontSize = 10.sp,
                color = Color.White.copy(alpha = 0.5f), letterSpacing = 3.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "$score / $total 정답", fontSize = 40.sp,
                fontWeight = FontWeight.Bold, color = HermesGold)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "${percent}% 정답률", fontSize = 16.sp,
                color = HermesOrange, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, HermesGold.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                .padding(horizontal = 20.dp, vertical = 12.dp)) {
                Text(text = message, fontSize = 15.sp,
                    color = HermesIvory, textAlign = TextAlign.Center)
            }
            Spacer(modifier = Modifier.height(40.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(HermesOrange)
                    .clickable { onComplete() }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "홈으로 돌아가기  →", fontSize = 16.sp,
                    fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = 0.5.sp)
            }
        }
    }
}
