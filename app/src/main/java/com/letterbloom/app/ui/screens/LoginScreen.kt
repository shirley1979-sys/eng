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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.letterbloom.app.ui.theme.*

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(targetValue = if (visible) 1f else 0f,
        animationSpec = tween(900), label = "fade")
    val slideY by animateDpAsState(targetValue = if (visible) 0.dp else 30.dp,
        animationSpec = tween(900, easing = EaseOut), label = "slide")

    LaunchedEffect(Unit) { visible = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HermesCream)
    ) {
        // 상단 헤더 영역
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.42f)
                .background(Brush.verticalGradient(listOf(HermesBrown, Color(0xFF3D1E0C))))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp)
                    .alpha(alpha)
                    .offset(y = slideY),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "✦",
                    fontSize = 48.sp,
                    color = HermesGold
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "LETTERBLOOM",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = HermesGold,
                    letterSpacing = 4.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "레터블룸",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "여행 영어, 지금 시작해요",
                    fontSize = 14.sp,
                    color = HermesIvory.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
            }
        }

        // 하단 카드 영역
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp)
                .padding(bottom = 48.dp)
                .alpha(alpha),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 시작하기 카드
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White)
                    .border(1.dp, BorderWarm, RoundedCornerShape(24.dp))
                    .padding(28.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "시작하기",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = InkBrown
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "해외여행에서 바로 쓰는\n실전 영어를 배워요",
                        fontSize = 14.sp,
                        color = TextMedium,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // 시작 버튼
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(HermesBrown)
                            .clickable { onLoginSuccess() }
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✦  학습 시작하기",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = HermesIvory,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "학습 내용이 자동으로 저장돼요",
                        fontSize = 12.sp,
                        color = TextLight,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                FeaturePoint(emoji = "✈️", text = "여행 영어")
                FeaturePoint(emoji = "🔊", text = "발음 학습")
                FeaturePoint(emoji = "⭐", text = "레벨업")
            }
        }
    }
}

@Composable
private fun FeaturePoint(emoji: String, text: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = emoji, fontSize = 26.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = text, fontSize = 12.sp, color = TextMedium, fontWeight = FontWeight.Medium)
    }
}
