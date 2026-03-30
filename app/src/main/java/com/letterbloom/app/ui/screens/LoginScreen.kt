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
    val slideY by animateDpAsState(targetValue = if (visible) 0.dp else 40.dp,
        animationSpec = tween(900, easing = EaseOut), label = "slide")

    LaunchedEffect(Unit) { visible = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(SlateDark, NavyDeep, NavyMid)))
    ) {
        // 배경 글로우 효과
        Box(
            modifier = Modifier
                .size(400.dp)
                .offset(x = (-50).dp, y = (-100).dp)
                .alpha(0.08f)
                .background(Brush.radialGradient(listOf(TealElectric, NavyDeep)))
        )
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 50.dp, y = 100.dp)
                .alpha(0.1f)
                .background(Brush.radialGradient(listOf(AmberGold, NavyDeep)))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "✦", fontSize = 48.sp, color = AmberGold,
                modifier = Modifier.alpha(alpha).offset(y = slideY))

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "LetterBloom", fontSize = 32.sp, fontWeight = FontWeight.Bold,
                color = OffWhite, modifier = Modifier.alpha(alpha).offset(y = slideY))

            Spacer(modifier = Modifier.height(6.dp))

            Text(text = "여행 영어, 지금 시작해요", fontSize = 15.sp, color = White60,
                modifier = Modifier.alpha(alpha).offset(y = slideY))

            Spacer(modifier = Modifier.height(52.dp))

            // 로그인 카드
            Card(
                modifier = Modifier.fillMaxWidth().alpha(alpha),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CardDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "시작하기", fontSize = 20.sp, fontWeight = FontWeight.Bold,
                        color = OffWhite)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "해외여행에서 바로 쓰는\n실전 영어를 배워요",
                        fontSize = 14.sp, color = White60, textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // 구글 로그인 버튼
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.horizontalGradient(listOf(AmberGold, AmberLight))
                            )
                            .clickable { onLoginSuccess() }
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "G", fontSize = 18.sp, fontWeight = FontWeight.Bold,
                                color = NavyDeep)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = "Google로 시작하기", fontSize = 16.sp,
                                fontWeight = FontWeight.Bold, color = NavyDeep)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "한 번 로그인하면 학습 내용이 자동 저장돼요",
                        fontSize = 12.sp, color = White30, textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(44.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth().alpha(alpha)
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
        Text(text = text, fontSize = 12.sp, color = White60)
    }
}
