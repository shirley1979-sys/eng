package com.letterbloom.app.data

import android.content.Context
import android.content.SharedPreferences

object LearningPrefs {
    private const val PREFS_NAME = "letterbloom_prefs"
    private const val KEY_TODAY_WORDS = "today_words"
    private const val KEY_TOTAL_WORDS = "total_words"
    private const val KEY_STREAK_DAYS = "streak_days"
    private const val KEY_CURRENT_LEVEL = "current_level"
    private const val KEY_LEVEL_NAME = "level_name"
    private const val KEY_LAST_STUDY_DATE = "last_study_date"
    private const val KEY_DAILY_GOAL = "daily_goal"
    private const val KEY_CORRECT_ANSWERS = "correct_answers"
    private const val KEY_TOTAL_ANSWERS = "total_answers"
    private const val KEY_ONBOARDING_DONE = "onboarding_done"

    // 레벨 50개 — 5단어마다 레벨업
    private val LEVEL_NAMES = listOf(
        // 1-5: 여행 준비
        "여행 준비생", "짐 싸는 중", "여권 발급 완료", "항공권 예매 완료", "출국 준비 완료",
        // 6-10: 공항
        "공항 도착", "체크인 완료", "보안 검색 통과", "면세점 쇼핑러", "탑승구 도착",
        // 11-15: 기내
        "이륙 완료", "기내식 주문 완료", "창가 자리 확보", "기내 영화 감상 중", "착륙 준비 완료",
        // 16-20: 호텔
        "호텔 도착", "체크인 성공", "룸서비스 마스터", "컨시어지와 대화 완료", "스위트룸 입성",
        // 21-25: 식당
        "메뉴판 독해 완료", "주문 성공", "알레르기 표현 완료", "웨이터와 소통 완료", "디저트까지 완벽",
        // 26-30: 쇼핑
        "쇼핑몰 입성", "사이즈 표현 완료", "흥정 성공", "환불 요청 완료", "명품 쇼핑 완료",
        // 31-35: 관광
        "박물관 투어 완료", "가이드 투어 참여", "사진 촬영 요청 완료", "입장권 구매 완료", "현지 맛집 발견",
        // 36-40: 교통
        "지하철 탑승 완료", "택시 목적지 전달 완료", "버스 노선 파악 완료", "렌터카 계약 완료", "길 묻기 완료",
        // 41-45: 응급
        "약국 방문 완료", "병원 접수 완료", "증상 설명 완료", "분실물 신고 완료", "긴급 상황 대처 완료",
        // 46-50: 글로벌
        "현지인과 대화 완료", "비즈니스 미팅 완료", "문화 차이 극복 완료", "완전 자유 여행자", "글로벌 여행러"
    )

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getTodayWords(context: Context): Int = prefs(context).getInt(KEY_TODAY_WORDS, 0)
    fun getTotalWords(context: Context): Int = prefs(context).getInt(KEY_TOTAL_WORDS, 0)
    fun getStreakDays(context: Context): Int = prefs(context).getInt(KEY_STREAK_DAYS, 0)
    fun getCurrentLevel(context: Context): Int = prefs(context).getInt(KEY_CURRENT_LEVEL, 1)
    fun getLevelName(context: Context): String =
        prefs(context).getString(KEY_LEVEL_NAME, LEVEL_NAMES[0]) ?: LEVEL_NAMES[0]
    fun getDailyGoal(context: Context): Int = prefs(context).getInt(KEY_DAILY_GOAL, 10)
    fun getCorrectAnswers(context: Context): Int = prefs(context).getInt(KEY_CORRECT_ANSWERS, 0)
    fun getTotalAnswers(context: Context): Int = prefs(context).getInt(KEY_TOTAL_ANSWERS, 0)
    fun isOnboardingDone(context: Context): Boolean =
        prefs(context).getBoolean(KEY_ONBOARDING_DONE, false)

    // 레벨 기준: 5단어마다 레벨업, 최대 50레벨
    fun levelForWords(totalWords: Int): Int =
        (totalWords / 5 + 1).coerceIn(1, 50)

    fun levelNameForLevel(level: Int): String =
        LEVEL_NAMES[(level - 1).coerceIn(0, 49)]

    // 다음 레벨까지 필요 단어 수
    fun wordsForNextLevel(level: Int): Int = level * 5
    fun wordsForCurrentLevel(level: Int): Int = (level - 1) * 5

    fun addLearnedWords(context: Context, count: Int) {
        val prefs = prefs(context)
        val today = System.currentTimeMillis() / (1000 * 60 * 60 * 24)
        val lastDay = prefs.getLong(KEY_LAST_STUDY_DATE, 0L)

        val todayWords = if (today == lastDay) prefs.getInt(KEY_TODAY_WORDS, 0) + count else count
        val totalWords = prefs.getInt(KEY_TOTAL_WORDS, 0) + count
        val streak = when {
            today == lastDay -> prefs.getInt(KEY_STREAK_DAYS, 1)
            today - lastDay == 1L -> prefs.getInt(KEY_STREAK_DAYS, 0) + 1
            else -> 1
        }

        // 일일 목표 자동 조절
        val correctRate = if (prefs.getInt(KEY_TOTAL_ANSWERS, 0) > 0)
            prefs.getInt(KEY_CORRECT_ANSWERS, 0).toFloat() / prefs.getInt(KEY_TOTAL_ANSWERS, 1)
        else 0f
        val newGoal = when {
            correctRate >= 0.8f && streak >= 5 -> minOf(30, prefs.getInt(KEY_DAILY_GOAL, 10) + 5)
            correctRate >= 0.8f && streak >= 3 -> minOf(20, prefs.getInt(KEY_DAILY_GOAL, 10) + 2)
            else -> prefs.getInt(KEY_DAILY_GOAL, 10)
        }

        // 레벨 자동 업데이트 (50레벨)
        val newLevel = levelForWords(totalWords)
        val newLevelName = levelNameForLevel(newLevel)

        prefs.edit()
            .putInt(KEY_TODAY_WORDS, todayWords)
            .putInt(KEY_TOTAL_WORDS, totalWords)
            .putInt(KEY_STREAK_DAYS, streak)
            .putLong(KEY_LAST_STUDY_DATE, today)
            .putInt(KEY_DAILY_GOAL, newGoal)
            .putInt(KEY_CURRENT_LEVEL, newLevel)
            .putString(KEY_LEVEL_NAME, newLevelName)
            .apply()
    }

    fun addQuizResult(context: Context, correct: Int, total: Int) {
        val prefs = prefs(context)
        prefs.edit()
            .putInt(KEY_CORRECT_ANSWERS, prefs.getInt(KEY_CORRECT_ANSWERS, 0) + correct)
            .putInt(KEY_TOTAL_ANSWERS, prefs.getInt(KEY_TOTAL_ANSWERS, 0) + total)
            .apply()
    }

    fun setLevelFromDiagnosis(context: Context, level: Int, name: String) {
        prefs(context).edit()
            .putInt(KEY_CURRENT_LEVEL, level)
            .putString(KEY_LEVEL_NAME, name)
            .putBoolean(KEY_ONBOARDING_DONE, true)
            .apply()
    }
    // ── 즐겨찾기 ──
    private const val KEY_FAVORITES = "favorites"

    fun getFavorites(context: Context): Set<String> =
        prefs(context).getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()

    fun toggleFavorite(context: Context, english: String): Boolean {
        val favs = getFavorites(context).toMutableSet()
        return if (english in favs) {
            favs.remove(english)
            prefs(context).edit().putStringSet(KEY_FAVORITES, favs).apply()
            false
        } else {
            favs.add(english)
            prefs(context).edit().putStringSet(KEY_FAVORITES, favs).apply()
            true
        }
    }

    // ── 오답 노트 ──
    private const val KEY_WRONG_WORDS = "wrong_words"

    fun getWrongWords(context: Context): Set<String> =
        prefs(context).getStringSet(KEY_WRONG_WORDS, emptySet()) ?: emptySet()

    fun addWrongWord(context: Context, english: String) {
        if (english.isEmpty()) return
        val wrong = getWrongWords(context).toMutableSet()
        wrong.add(english)
        prefs(context).edit().putStringSet(KEY_WRONG_WORDS, wrong).apply()
    }

    fun markWordCorrect(context: Context, english: String) {
        val wrong = getWrongWords(context).toMutableSet()
        wrong.remove(english)
        prefs(context).edit().putStringSet(KEY_WRONG_WORDS, wrong).apply()
    }

    // ── 이어서 학습 ──
    private fun progressKey(category: String) = "progress_$category"

    fun getCategoryProgress(context: Context, category: String): Int =
        prefs(context).getInt(progressKey(category), 0)

    fun saveCategoryProgress(context: Context, category: String, index: Int) {
        prefs(context).edit().putInt(progressKey(category), index).apply()
    }

    fun resetCategoryProgress(context: Context, category: String) {
        prefs(context).edit().remove(progressKey(category)).apply()
    }
}
