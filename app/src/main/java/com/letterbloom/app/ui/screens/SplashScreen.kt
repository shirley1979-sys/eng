package com.letterbloom.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.letterbloom.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNavigate: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }

    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(1200, easing = EaseInOut), label = "alpha"
    )
    val scaleAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.82f,
        animationSpec = tween(1200, easing = EaseOutBack), label = "scale"
    )
    val taglineAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(1000, delayMillis = 900, easing = EaseInOut), label = "tagline"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2800)
        onNavigate()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HermesCream),
        contentAlignment = Alignment.Center
    ) {
        // 상단 갈색 장식 바
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .align(Alignment.TopCenter)
                .background(Brush.horizontalGradient(listOf(HermesBrown, HermesOrange, HermesBrown)))
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // 로고 박스
            Box(
                modifier = Modifier
                    .scale(scaleAnim)
                    .alpha(alphaAnim)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "✦",
                        fontSize = 56.sp,
                        color = HermesOrange
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "LETTERBLOOM",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = HermesBrown,
                        letterSpacing = 5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "레터블룸",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = HermesBrown
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // 구분선
            Box(
                modifier = Modifier
                    .alpha(taglineAlpha)
                    .width(40.dp)
                    .height(1.dp)
                    .background(HermesGold)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "지금이 딱 좋은 때",
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                color = HermesBrownMid,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(taglineAlpha)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Travel English, Start Today ✈️",
                fontSize = 13.sp,
                color = TextMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(taglineAlpha)
            )
        }

        // 하단 버전
        Text(
            text = "v1.2",
            fontSize = 11.sp,
            color = TextLight,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .alpha(taglineAlpha)
        )

        // 하단 장식 바
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .align(Alignment.BottomCenter)
                .background(Brush.horizontalGradient(listOf(HermesBrown, HermesOrange, HermesBrown)))
        )
    }
}
