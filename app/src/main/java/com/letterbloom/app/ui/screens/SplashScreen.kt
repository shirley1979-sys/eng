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
        targetValue = if (startAnimation) 1f else 0.75f,
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
            .background(
                Brush.verticalGradient(listOf(SlateDark, NavyDeep, NavyMid))
            ),
        contentAlignment = Alignment.Center
    ) {
        // 배경 글로우
        Box(
            modifier = Modifier
                .size(300.dp)
                .alpha(0.15f)
                .background(
                    Brush.radialGradient(listOf(AmberGold, NavyDeep))
                )
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // 아이콘
            Text(
                text = "✦",
                fontSize = 64.sp,
                color = AmberGold,
                modifier = Modifier.scale(scaleAnim).alpha(alphaAnim)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "LetterBloom",
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                color = OffWhite,
                modifier = Modifier.scale(scaleAnim).alpha(alphaAnim)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "레터블룸",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = AmberGold,
                modifier = Modifier.alpha(alphaAnim)
            )

            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = "지금이 딱 좋은 때",
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                color = White80,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(taglineAlpha)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Travel English, Start Today ✈️",
                fontSize = 13.sp,
                color = TealElectric,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(taglineAlpha)
            )
        }

        Text(
            text = "v1.0",
            fontSize = 12.sp,
            color = White30,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .alpha(taglineAlpha)
        )
    }
}
