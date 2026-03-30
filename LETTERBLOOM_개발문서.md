# LetterBloom 개발 문서

> **버전:** 1.2.0
> **최종 업데이트:** 2026-03-30
> **타겟:** 40대 여성을 위한 해외여행 실용 영어 학습 Android 앱

---

## 1. 프로젝트 개요

### 앱 컨셉
- 중학교 수준 영어 단어를 여행 상황 실전 문장으로 학습
- 발음기호 + TTS 음성 + 플래시카드 3초 공개 방식
- **레벨 50단계** (여행 준비생 → 글로벌 여행러)
- 5단어마다 레벨업, 하루 10단어 → 실력에 따라 자동 증가
- Supabase 클라우드 저장 (학습 기록 자동 동기화)

### 타겟 사용자
- 40대 여성
- 영어 초보 (중학교 수준)
- 해외여행 시 바로 사용 가능한 영어 원하는 분

### 앱 이름 선정 이유
**LetterBloom (레터블룸)**
- Letter = 알파벳, 글자 → 영어 학습
- Bloom = 꽃피다 → 40대에 다시 피어나는 느낌
- 구글 플레이 스토어 중복 없음 확인 완료

---

## 2. 기술 스펙

### 개발 환경
| 항목 | 내용 |
|------|------|
| IDE | Android Studio (Homebrew 설치) |
| 언어 | Kotlin 2.0.0 |
| UI 프레임워크 | Jetpack Compose (BOM 2024.08.00) |
| 최소 Android | 8.0 (API 26) |
| 타겟 Android | 14 (API 35) |
| 빌드 도구 | Gradle 8.7 |
| Java | Android Studio 내장 JBR |
| 클라우드 DB | Supabase (PostgreSQL) |

### 주요 라이브러리
| 라이브러리 | 버전 | 용도 |
|-----------|------|------|
| Material 3 | Compose BOM | 디자인 시스템 |
| Navigation Compose | 2.7.7 | 화면 전환 |
| Google Play Services Auth | 21.2.0 | 구글 로그인 |
| Accompanist SystemUI | 0.34.0 | 상태바 커스텀 |
| Android TTS | 내장 | 영어 발음 재생 |
| Supabase Kotlin SDK | 3.0.2 | 클라우드 저장/동기화 |
| Ktor Client OkHttp | 3.0.3 | HTTP 클라이언트 |

### 환경 변수 / 경로
```
JAVA_HOME = /Applications/Android Studio.app/Contents/jbr/Contents/Home
Android SDK = /Users/crystal/Library/Android/sdk
프로젝트 경로 = /Users/crystal/dev/test/eng
APK 경로 = /Users/crystal/dev/test/eng/app/build/outputs/apk/debug/app-debug.apk
```

### 빌드 명령어
```bash
cd /Users/crystal/dev/test/eng
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
./gradlew assembleDebug       # 디버그 APK 빌드
./gradlew clean assembleDebug # 클린 빌드
```

### 갤럭시 설치 (USB ADB)
```bash
# 1. 갤럭시: 설정 → 휴대전화 정보 → 빌드번호 7번 탭 → 개발자 옵션 ON → USB 디버깅 ON
# 2. USB 연결 후 팝업에서 "허용"
/Users/crystal/Library/Android/sdk/platform-tools/adb install -r \
  /Users/crystal/dev/test/eng/app/build/outputs/apk/debug/app-debug.apk
```

---

## 3. 프로젝트 구조

```
eng/
├── app/
│   └── src/main/
│       ├── java/com/letterbloom/app/
│       │   ├── MainActivity.kt
│       │   ├── data/
│       │   │   ├── WordData.kt          # 단어 45개 + 퀴즈 데이터
│       │   │   ├── LearningPrefs.kt     # SharedPreferences (로컬 저장)
│       │   │   └── SupabaseSync.kt      # Supabase 클라우드 동기화
│       │   ├── navigation/
│       │   │   └── NavGraph.kt          # 화면 라우팅 (온보딩 스킵 로직)
│       │   └── ui/
│       │       ├── screens/
│       │       │   ├── SplashScreen.kt
│       │       │   ├── LoginScreen.kt
│       │       │   ├── LevelDiagnosisScreen.kt
│       │       │   ├── HomeScreen.kt
│       │       │   ├── CategoryScreen.kt
│       │       │   ├── FlashcardScreen.kt
│       │       │   ├── QuizScreen.kt
│       │       │   └── ProgressScreen.kt
│       │       └── theme/
│       │           ├── Color.kt         # 에르메스 컬러 팔레트
│       │           ├── Theme.kt
│       │           └── Type.kt
│       └── AndroidManifest.xml
├── gradle/
│   └── libs.versions.toml
└── LETTERBLOOM_개발문서.md
```

---

## 4. 화면 설계

### 화면 흐름
```
최초 실행: 스플래시 → 로그인 → 레벨진단 → 홈
재실행:    스플래시 → 홈 (온보딩 완료 플래그로 스킵)

홈 → 카테고리 선택 → 플래시카드 학습 → 복습 퀴즈 → 홈
홈 → 진도 화면 (50레벨 로드맵)
```

### 각 화면 상세

#### 스플래시 (SplashScreen)
- 로고 애니메이션 + "지금이 딱 좋은 때"
- 온보딩 완료 여부 확인 후 홈 or 로그인으로 분기

#### 로그인 (LoginScreen)
- 구글 로그인 버튼 (Phase 2에서 실제 Firebase 연동)
- 현재는 탭 시 바로 레벨진단으로 이동

#### 레벨 진단 (LevelDiagnosisScreen)
- 5문제 객관식 퀴즈 (최초 1회만 실행)
- 점수에 따라 1~5레벨 배정 + LearningPrefs에 저장
- 완료 시 `onboarding_done = true` 플래그 저장

#### 홈 (HomeScreen) — 에르메스 디자인
- 다크 브라운 헤더 + 골드 타이포
- 오늘 학습 원형 진도 (오렌지)
- 레벨업 진도바 (현재 레벨 → 다음 레벨, 필요 단어 표시)
- 연속 학습일 / 총 단어 / 현재 레벨 스탯 칩
- 8개 카테고리 카드 그리드

#### 카테고리 (CategoryScreen) — 에르메스 디자인
- 다크 브라운 헤더
- 8개 카테고리 2열 그리드
- 카테고리별 컬러 상단 바 + 단어 수 뱃지

#### 플래시카드 (FlashcardScreen) — 에르메스 디자인
- 다크 브라운 메인 카드 + 골드 테두리
- 영단어 대형 + IPA + TTS 버튼 (골드)
- 3초 카운트다운 → 한국어 뜻 + 예문 공개
- 단어 학습 시 LearningPrefs + Supabase 저장

#### 퀴즈 (QuizScreen) — 에르메스 디자인
- 크림 배경 + 다크 브라운 문제 카드
- 4지선다, 즉각 피드백
- 완료 시 LearningPrefs + Supabase 저장

#### 진도 (ProgressScreen) — 에르메스 디자인
- 다크 브라운 헤더 + 골드 통계
- 전체 진도바 (Lv.X / 50)
- 10개 챕터 × 5레벨 로드맵
- 챕터별 잠금/진행/완료 상태 표시

---

## 5. 레벨 시스템

### 50레벨 구조 (5단어마다 레벨업)
| 챕터 | 레벨 | 테마 | 레벨명 예시 |
|------|------|------|------------|
| 1 | 1~5 | 여행 준비 | 여행 준비생 → 출국 준비 완료 |
| 2 | 6~10 | 공항 | 공항 도착 → 탑승구 도착 |
| 3 | 11~15 | 기내 | 이륙 완료 → 착륙 준비 완료 |
| 4 | 16~20 | 호텔 | 호텔 도착 → 스위트룸 입성 |
| 5 | 21~25 | 식당 | 메뉴판 독해 완료 → 디저트까지 완벽 |
| 6 | 26~30 | 쇼핑 | 쇼핑몰 입성 → 명품 쇼핑 완료 |
| 7 | 31~35 | 관광 | 박물관 투어 완료 → 현지 맛집 발견 |
| 8 | 36~40 | 교통 | 지하철 탑승 완료 → 길 묻기 완료 |
| 9 | 41~45 | 응급 | 약국 방문 완료 → 긴급 상황 대처 |
| 10 | 46~50 | 글로벌 | 현지인 대화 → 글로벌 여행러 |

### 레벨업 기준
- **5단어 학습 = 1레벨 업**
- Lv.50 달성 = 250단어 총 학습
- 일일 목표: 10단어 (정답률 80%+ 연속 시 자동 증가, 최대 30단어)

---

## 6. 데이터 저장

### 로컬 저장 (SharedPreferences)
| 키 | 내용 |
|----|------|
| today_words | 오늘 학습 단어 수 |
| total_words | 누적 학습 단어 수 |
| streak_days | 연속 학습일 |
| current_level | 현재 레벨 (1~50) |
| level_name | 현재 레벨명 |
| daily_goal | 일일 목표 단어 수 |
| correct_answers | 누적 정답 수 |
| total_answers | 누적 퀴즈 문제 수 |
| last_study_date | 마지막 학습일 (일 단위 타임스탬프) |
| onboarding_done | 레벨진단 완료 여부 |

### 클라우드 저장 (Supabase)
- **프로젝트:** laterbloom (bsleikhmkxnvyrtprzbq)
- **URL:** https://bsleikhmkxnvyrtprzbq.supabase.co
- **테이블:** user_progress
  - device_id (기기 고유 ID, PRIMARY KEY)
  - today_words, total_words, streak_days
  - current_level, level_name, daily_goal
  - correct_answers, total_answers, last_study_date
- **저장 시점:** 단어 학습 완료 시, 퀴즈 완료 시 자동 upsert

---

## 7. 디자인 시스템 (에르메스 팔레트)

### 컬러
| 이름 | HEX | 용도 |
|------|-----|------|
| HermesOrange | #E8601C | 주 강조색, 진도바, 버튼 |
| HermesBrown | #2C1810 | 헤더 배경, 플래시카드 |
| HermesCream | #FAF4E8 | 앱 배경 |
| HermesIvory | #F5ECD7 | 보조 배경, 태그 |
| HermesGold | #C9A84C | 골드 강조, 타이포 |
| HermesSand | #EAD8BA | 트랙 배경 |
| InkBrown | #1A0E08 | 기본 텍스트 |
| TextMedium | #7A5540 | 보조 텍스트 |

### 타이포그래피
| 용도 | 크기 | 굵기 |
|------|------|------|
| 영단어 | 46sp | Bold |
| 헤더 제목 | 20~32sp | Bold |
| 본문 | 15~16sp | Normal/Medium |
| 레이블 | 9~11sp | Bold + letterSpacing |
| 최소 | 10sp | - |

---

## 8. 개발 Phase 현황

### Phase 1 — 완료 ✅
- [x] 프로젝트 구조 및 빌드 환경 구성
- [x] 8개 화면 Jetpack Compose 구현
- [x] 단어 데이터 45개 (여행 테마 실전 문장)
- [x] Android TTS 발음 재생
- [x] 플래시카드 3초 카운트다운 애니메이션
- [x] 레벨 진단 5문제 퀴즈 (최초 1회)
- [x] 여행 문장 빈칸 채우기 퀴즈
- [x] SharedPreferences 학습 기록 저장
- [x] Supabase 클라우드 동기화 (user_progress 테이블)
- [x] 50레벨 시스템 (5단어마다 레벨업)
- [x] 에르메스 팔레트 디자인 전면 적용
- [x] 온보딩 완료 후 홈 직접 진입 (데이터 리셋 방지)
- [x] Galaxy S23 ADB 설치 완료

### Phase 2 — 예정 📋
- [ ] Firebase Authentication (구글 로그인 실제 연동)
- [ ] 단어 데이터 확장 (250개 이상 — 50레벨 완주 목표)
- [ ] 레벨업 축하 애니메이션
- [ ] 알람 설정 (WorkManager)
- [ ] Google Calendar API 연동
- [ ] 학습 통계 상세 화면

### Phase 3 — 예정 📋
- [ ] 릴리즈 APK 서명
- [ ] 구글 플레이 스토어 출시
- [ ] 푸시 알림
- [ ] Firebase App Distribution (팀 테스트용)

---

## 9. 알려진 이슈 및 해결 내역

| 이슈 | 원인 | 해결 방법 |
|------|------|-----------|
| 데이터 매번 리셋 | 앱 실행 시마다 레벨진단 화면 통과 | onboarding_done 플래그로 최초 1회만 실행 |
| 레벨진단 결과 미저장 | setLevelFromDiagnosis 미호출 | LevelResultScreen에서 LaunchedEffect로 저장 |
| 홈 화면 데이터 미갱신 | remember로 초기화, LaunchedEffect 미사용 | var + LaunchedEffect(Unit)으로 수정 |
| SDK 라이선스 오류 | 라이선스 미동의 | sdkmanager --licenses 자동 동의 |
| Java 없음 오류 | Java 미설치 | JAVA_HOME = Android Studio 내장 JBR |
| ADB device not found | USB 디버깅 팝업 미허용 | 갤럭시 화면에서 "항상 허용" 선택 |

---

## 10. 다음 세션 시작 방법

```bash
cd /Users/crystal/dev/test/eng
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
./gradlew assembleDebug
# 갤럭시 USB 연결 후:
/Users/crystal/Library/Android/sdk/platform-tools/adb install -r \
  app/build/outputs/apk/debug/app-debug.apk
```

**다음 우선순위:**
1. 단어 데이터 확장 (현재 45개 → 250개 목표)
2. 레벨업 축하 애니메이션 추가
3. Firebase 구글 로그인 실제 연동

---

*LetterBloom — 지금이 딱 좋은 때 ✦*
