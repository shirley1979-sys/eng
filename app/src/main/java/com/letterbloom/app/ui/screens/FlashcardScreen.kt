package com.letterbloom.app.ui.screens

import android.speech.tts.TextToSpeech
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.letterbloom.app.data.LearningPrefs
import com.letterbloom.app.data.SupabaseSync
import com.letterbloom.app.data.WordCategory
import com.letterbloom.app.data.wordList
import com.letterbloom.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun FlashcardScreen(category: String, onComplete: () -> Unit, onBack: () -> Unit) {
    val context = LocalContext.current
    val isSpecial = category == "FAVORITES" || category == "WRONG"
    val wordCategory = if (!isSpecial) {
        try { WordCategory.valueOf(category) } catch (e: Exception) { WordCategory.AIRPORT }
    } else WordCategory.AIRPORT

    val words = remember {
        when (category) {
            "FAVORITES" -> {
                val favs = LearningPrefs.getFavorites(context)
                wordList.filter { it.english in favs }
            }
            "WRONG" -> {
                val wrong = LearningPrefs.getWrongWords(context)
                wordList.filter { it.english in wrong }
            }
            else -> wordList.filter { it.category == wordCategory }
        }
    }
    val startIndex = if (isSpecial) 0 else LearningPrefs.getCategoryProgress(context, category)
    var currentIndex by remember { mutableStateOf(startIndex.coerceAtMost((words.size - 1).coerceAtLeast(0))) }
    var isFavorite by remember(currentIndex) {
        mutableStateOf(words.getOrNull(currentIndex)?.english?.let { LearningPrefs.getFavorites(context).contains(it) } ?: false)
    }
    var showKorean by remember { mutableStateOf(false) }
    var countdown by remember { mutableStateOf(2) }
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }

    LaunchedEffect(Unit) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) tts?.language = Locale.US
        }
    }
    DisposableEffect(Unit) { onDispose { tts?.shutdown() } }

    LaunchedEffect(currentIndex) {
        showKorean = false
        if (!isSpecial) LearningPrefs.saveCategoryProgress(context, category, currentIndex)
        countdown = 2
        repeat(2) { delay(1000); countdown-- }
        showKorean = true
    }

    val koreanAlpha by animateFloatAsState(
        targetValue = if (showKorean) 1f else 0f,
        animationSpec = tween(600), label = "korean"
    )

    if (words.isEmpty()) {
        Box(Modifier.fillMaxSize().background(HermesCream)) {
            Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text("아직 단어가 없어요", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (category == "FAVORITES") "플래시카드에서 ❤️를 눌러 추가해보세요"
                           else "퀴즈를 풀면 오답이 여기 저장돼요",
                    fontSize = 14.sp, color = TextLight, textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(HermesOrange)
                        .clickable { onBack() }.padding(horizontal = 24.dp, vertical = 12.dp)
                ) { Text("← 돌아가기", color = Color.White, fontWeight = FontWeight.Bold) }
            }
        }
        return
    }

    val word = words[currentIndex]

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HermesCream)
    ) {
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
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "${wordCategory.emoji}  ${wordCategory.displayName}",
                        fontSize = 12.sp, color = TextMedium, fontWeight = FontWeight.Medium)
                    Text(text = "${currentIndex + 1} / ${words.size}",
                        fontSize = 11.sp, color = TextLight)
                }
                IconButton(onClick = {
                    val eng = words.getOrNull(currentIndex)?.english ?: return@IconButton
                    isFavorite = LearningPrefs.toggleFavorite(context, eng)
                }) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite
                                      else Icons.Default.FavoriteBorder,
                        contentDescription = "즐겨찾기",
                        tint = if (isFavorite) Color(0xFFE91E63) else TextLight,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { (currentIndex + 1).toFloat() / words.size },
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
                Text(text = word.situation, fontSize = 13.sp, color = TextBrown,
                    fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 플래시카드 메인
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(HermesBrown, Color(0xFF4A2010))
                        )
                    )
                    .border(1.dp, HermesGold.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                    .padding(28.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()) {

                    Text(
                        text = word.english,
                        fontSize = 46.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = word.ipa, fontSize = 17.sp, color = HermesGold)

                    Spacer(modifier = Modifier.height(16.dp))

                    // TTS 버튼
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.12f))
                            .border(1.dp, HermesGold.copy(alpha = 0.5f), CircleShape)
                            .clickable {
                                tts?.speak(word.english, TextToSpeech.QUEUE_FLUSH, null, null)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = "발음",
                            tint = HermesGold, modifier = Modifier.size(22.dp))
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.15f), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(20.dp))

                    if (!showKorean) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "$countdown", fontSize = 44.sp,
                                fontWeight = FontWeight.Bold, color = HermesOrange)
                            Text(text = "초 후 뜻 공개", fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.4f))
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.alpha(koreanAlpha)
                        ) {
                            Text(text = word.korean, fontSize = 30.sp,
                                fontWeight = FontWeight.Bold, color = HermesIvory,
                                textAlign = TextAlign.Center)

                            Spacer(modifier = Modifier.height(20.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.08f))
                                    .padding(16.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "\"${word.exampleSentence}\"",
                                        fontSize = 14.sp, color = Color.White.copy(alpha = 0.9f),
                                        fontWeight = FontWeight.Medium, lineHeight = 22.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = word.exampleKorean, fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.55f), lineHeight = 20.sp)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            if (showKorean) {
                val coroutineScope = rememberCoroutineScope()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(HermesOrange)
                        .clickable {
                            LearningPrefs.addLearnedWords(context, 1)
                            coroutineScope.launch { SupabaseSync.saveProgress(context) }
                            if (currentIndex < words.size - 1) currentIndex++
                            else {
                                if (!isSpecial) LearningPrefs.resetCategoryProgress(context, category)
                                onComplete()
                            }
                        }
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (currentIndex < words.size - 1) "다음 단어  →" else "퀴즈 시작  ✦",
                        fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
