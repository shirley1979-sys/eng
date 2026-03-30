package com.letterbloom.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.letterbloom.app.navigation.LetterBloomNavGraph
import com.letterbloom.app.ui.theme.LetterBloomTheme
import com.letterbloom.app.ui.theme.WarmWhite

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LetterBloomTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = WarmWhite
                ) {
                    val navController = rememberNavController()
                    LetterBloomNavGraph(navController = navController)
                }
            }
        }
    }
}
