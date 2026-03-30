package com.letterbloom.app.data

import android.content.Context
import android.provider.Settings
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

@Serializable
data class UserProgress(
    val device_id: String,
    val today_words: Int,
    val total_words: Int,
    val streak_days: Int,
    val current_level: Int,
    val level_name: String,
    val daily_goal: Int,
    val correct_answers: Int,
    val total_answers: Int,
    val last_study_date: Long
)

object SupabaseSync {
    private const val SUPABASE_URL = "https://bsleikhmkxnvyrtprzbq.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJzbGVpa2hta3hudnlydHByemJxIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzQ3MzcwMDEsImV4cCI6MjA5MDMxMzAwMX0.5BzldR28p6y9F32I-XLv-HaLShPiFa-S9DTnEGmiuCQ"

    private val client = createSupabaseClient(SUPABASE_URL, SUPABASE_KEY) {
        install(Postgrest)
    }

    fun getDeviceId(context: Context): String =
        Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

    suspend fun saveProgress(context: Context) = withContext(Dispatchers.IO) {
        runCatching {
            val prefs = LearningPrefs
            val progress = UserProgress(
                device_id = getDeviceId(context),
                today_words = prefs.getTodayWords(context),
                total_words = prefs.getTotalWords(context),
                streak_days = prefs.getStreakDays(context),
                current_level = prefs.getCurrentLevel(context),
                level_name = prefs.getLevelName(context),
                daily_goal = prefs.getDailyGoal(context),
                correct_answers = prefs.getCorrectAnswers(context),
                total_answers = prefs.getTotalAnswers(context),
                last_study_date = context.getSharedPreferences("letterbloom_prefs", Context.MODE_PRIVATE)
                    .getLong("last_study_date", 0L)
            )
            client.postgrest["user_progress"].upsert(progress) {
                onConflict = "device_id"
            }
        }
    }

    suspend fun loadProgress(context: Context) = withContext(Dispatchers.IO) {
        runCatching {
            val deviceId = getDeviceId(context)
            val result = client.postgrest["user_progress"]
                .select { filter { eq("device_id", deviceId) } }
                .decodeSingleOrNull<UserProgress>()

            result?.let { p ->
                context.getSharedPreferences("letterbloom_prefs", Context.MODE_PRIVATE)
                    .edit()
                    .putInt("today_words", p.today_words)
                    .putInt("total_words", p.total_words)
                    .putInt("streak_days", p.streak_days)
                    .putInt("current_level", p.current_level)
                    .putString("level_name", p.level_name)
                    .putInt("daily_goal", p.daily_goal)
                    .putInt("correct_answers", p.correct_answers)
                    .putInt("total_answers", p.total_answers)
                    .putLong("last_study_date", p.last_study_date)
                    .apply()
            }
            result
        }
    }
}
