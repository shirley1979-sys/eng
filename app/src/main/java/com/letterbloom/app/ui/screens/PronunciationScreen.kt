package com.letterbloom.app.ui.screens

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.letterbloom.app.data.LearningPrefs
import com.letterbloom.app.data.wordList
import com.letterbloom.app.ui.theme.*
import java.util.Locale

private enum class RecordState { IDLE, LISTENING, DONE }

@Composable
fun PronunciationScreen(onBack: () -> Unit) {
    val context = LocalContext.current

    val words = remember {
        val level = LearningPrefs.getCurrentLevel(context)
        val all = wordList.let { list ->
            list.filterIndexed { i, _ ->
                LearningPrefs.requiredLevelForWordIndex(i, list.size) <= level
            }
        }
        (if (all.size >= 8) all else wordList).shuffled().take(10)
    }

    var currentIndex by remember { mutableStateOf(0) }
    var recordState by remember { mutableStateOf(RecordState.IDLE) }
    var recognizedText by remember { mutableStateOf("") }
    var isCorrect by remember { mutableStateOf<Boolean?>(null) }
    var score by remember { mutableStateOf(0) }
    var showComplete by remember { mutableStateOf(false) }
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var hasMicPermission by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasMicPermission = granted }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) tts?.language = Locale.US
        }
    }
    DisposableEffect(Unit) { onDispose { tts?.shutdown() } }

    val word = words.getOrNull(currentIndex)

    if (showComplete || word == null) {
        PronunciationResultScreen(score = score, total = words.size, onBack = onBack)
        return
    }

    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }
    DisposableEffect(Unit) { onDispose { speechRecognizer.destroy() } }

    fun startListening() {
        recordState = RecordState.LISTENING
        recognizedText = ""
        isCorrect = null
        val target = word.english

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) { recordState = RecordState.IDLE }
            override fun onPartialResults(partial: Bundle?) {}
            override fun onEvent(type: Int, params: Bundle?) {}
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val recognized = matches?.firstOrNull()?.lowercase()?.trim() ?: ""
                recognizedText = recognized
                val t = target.lowercase().trim()
                val correct = recognized.contains(t) ||
                              t.contains(recognized) ||
                              levenshteinSimilarity(recognized, t) >= 0.7f
                isCorrect = correct
                if (correct) {
                    score++
                    LearningPrefs.addXP(context, 15)
                }
                recordState = RecordState.DONE
            }
        })

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
        }
        speechRecognizer.startListening(intent)
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.18f,
        animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse),
        label = "scale"
    )

    val wordFontSize = when {
        word.english.length <= 6  -> 40.sp
        word.english.length <= 10 -> 32.sp
        word.english.length <= 14 -> 26.sp
        else                      -> 22.sp
    }

    Box(modifier = Modifier.fillMaxSize().background(HermesCream)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 상단
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, "뒤로", tint = HermesBrown)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("발음 레슨", fontSize = 11.sp, color = HermesGold,
                        fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Text("${currentIndex + 1} / ${words.size}", fontSize = 11.sp, color = TextLight)
                }
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(HermesWarm)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("✦ $score", fontSize = 13.sp, color = HermesGold,
                        fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { (currentIndex + 1).toFloat() / words.size },
                modifier = Modifier.fillMaxWidth().height(3.dp).clip(RoundedCornerShape(2.dp)),
                color = HermesOrange, trackColor = HermesSand
            )
            Spacer(modifier = Modifier.height(24.dp))

            // 단어 카드
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Brush.verticalGradient(listOf(HermesBrown, Color(0xFF4A2010))))
                    .border(1.dp, HermesGold.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                    .padding(28.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = word.english,
                        fontSize = wordFontSize,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(word.ipa, fontSize = 15.sp, color = HermesGold,
                        textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(word.korean, fontSize = 18.sp, color = HermesIvory,
                        fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(20.dp))

                    // 참고 발음 듣기
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.12f))
                            .border(1.dp, HermesGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .clickable {
                                tts?.speak(word.english, TextToSpeech.QUEUE_FLUSH, null, null)
                            }
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VolumeUp, "발음 듣기",
                                tint = HermesGold, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("발음 듣기", fontSize = 13.sp, color = HermesGold,
                                fontWeight = FontWeight.Medium)
                        }
                    }

                    // 예문
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.12f), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "\"${word.exampleSentence}\"",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // 결과 or 마이크 버튼
            when {
                recordState == RecordState.DONE && isCorrect != null -> {
                    val correct = isCorrect!!
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (correct) Color(0xFFDCFCE7) else Color(0xFFFEE2E2))
                            .border(
                                1.dp,
                                if (correct) Color(0xFF86EFAC) else Color(0xFFFCA5A5),
                                RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (correct) "완벽해요!" else "다시 해봐요",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (correct) Color(0xFF166534) else Color(0xFF991B1B)
                            )
                            if (recognizedText.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "인식: \"$recognizedText\"",
                                    fontSize = 13.sp,
                                    color = if (correct) Color(0xFF166534) else Color(0xFF991B1B)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 다시 말하기 (오답일 때)
                    if (!correct) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(HermesBrown.copy(alpha = 0.1f))
                                .border(1.dp, HermesBrown.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                                .clickable { startListening() }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Mic, null, tint = HermesBrown,
                                    modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("다시 말하기", fontSize = 14.sp, color = HermesBrown,
                                    fontWeight = FontWeight.Medium)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(HermesOrange)
                            .clickable {
                                if (currentIndex < words.size - 1) {
                                    currentIndex++
                                    recordState = RecordState.IDLE
                                    recognizedText = ""
                                    isCorrect = null
                                } else {
                                    showComplete = true
                                }
                            }
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            if (currentIndex < words.size - 1) "다음 단어  →" else "결과 보기  ✦",
                            fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                else -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = when (recordState) {
                                RecordState.IDLE -> if (hasMicPermission) "마이크를 눌러 발음해보세요"
                                                   else "마이크 권한을 허용해주세요"
                                RecordState.LISTENING -> "듣고 있어요... 발음해주세요"
                                RecordState.DONE -> ""
                            },
                            fontSize = 14.sp, color = TextMedium,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        // 마이크 버튼
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .scale(if (recordState == RecordState.LISTENING) pulseScale else 1f)
                                .clip(CircleShape)
                                .background(
                                    when (recordState) {
                                        RecordState.LISTENING -> HermesOrange
                                        else -> HermesBrown
                                    }
                                )
                                .clickable(enabled = hasMicPermission && recordState == RecordState.IDLE) {
                                    startListening()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Mic, "말하기",
                                tint = Color.White, modifier = Modifier.size(40.dp))
                        }

                        if (recordState == RecordState.LISTENING) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("말하기를 마치면 자동으로 인식돼요",
                                fontSize = 11.sp, color = TextLight)
                        }
                    }
                }
            }
        }
    }
}

// 레벤슈타인 유사도 (0~1)
private fun levenshteinSimilarity(a: String, b: String): Float {
    if (a.isEmpty() || b.isEmpty()) return 0f
    val maxLen = maxOf(a.length, b.length)
    val dp = Array(a.length + 1) { IntArray(b.length + 1) }
    for (i in 0..a.length) dp[i][0] = i
    for (j in 0..b.length) dp[0][j] = j
    for (i in 1..a.length) for (j in 1..b.length) {
        dp[i][j] = if (a[i - 1] == b[j - 1]) dp[i - 1][j - 1]
                   else 1 + minOf(dp[i - 1][j], dp[i][j - 1], dp[i - 1][j - 1])
    }
    return 1f - dp[a.length][b.length].toFloat() / maxLen
}

@Composable
private fun PronunciationResultScreen(score: Int, total: Int, onBack: () -> Unit) {
    val percent = if (total > 0) score * 100 / total else 0
    val (symbol, message) = when {
        percent >= 80 -> "✦" to "발음이 정확해요! 원어민 수준이에요"
        percent >= 60 -> "◈" to "잘 하셨어요! 조금만 더 연습해요"
        else          -> "◇" to "계속 연습하면 금방 늘어요!"
    }

    Box(
        modifier = Modifier.fillMaxSize()
            .background(Brush.verticalGradient(listOf(HermesBrown, Color(0xFF4A2010)))),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(symbol, fontSize = 60.sp, color = HermesGold)
            Spacer(modifier = Modifier.height(20.dp))
            Text("PRONUNCIATION COMPLETE", fontSize = 9.sp,
                color = Color.White.copy(alpha = 0.5f), letterSpacing = 2.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("$score / $total 정확", fontSize = 40.sp,
                fontWeight = FontWeight.Bold, color = HermesGold)
            Spacer(modifier = Modifier.height(6.dp))
            Text("${percent}% 정확도", fontSize = 16.sp,
                color = HermesOrange, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, HermesGold.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(message, fontSize = 15.sp,
                    color = HermesIvory, textAlign = TextAlign.Center)
            }
            Spacer(modifier = Modifier.height(40.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(HermesOrange)
                    .clickable { onBack() }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("카테고리로 돌아가기  →", fontSize = 16.sp,
                    fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = 0.5.sp)
            }
        }
    }
}
