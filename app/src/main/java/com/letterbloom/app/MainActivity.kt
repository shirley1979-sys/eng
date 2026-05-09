package com.letterbloom.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.letterbloom.app.navigation.LetterBloomNavGraph
import com.letterbloom.app.notification.AlarmScheduler
import com.letterbloom.app.notification.NotificationHelper
import com.letterbloom.app.ui.theme.LetterBloomTheme
import com.letterbloom.app.ui.theme.WarmWhite

class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) AlarmScheduler.scheduleDailyAlarms(this)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        NotificationHelper.createNotificationChannel(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED
            ) {
                AlarmScheduler.scheduleDailyAlarms(this)
            } else {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            AlarmScheduler.scheduleDailyAlarms(this)
        }

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
