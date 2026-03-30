# LetterBloom 개발환경 셋업 & 워크플로우 가이드

> 처음부터 앱 빌드 & 갤럭시 설치까지 전체 흐름을 정리한 문서입니다.
> Mac 기준 (macOS)

---

## 전체 흐름 요약

```
1. 필수 도구 설치
   Homebrew → Git → Android Studio → Android SDK

2. 프로젝트 세팅
   GitHub에서 클론 → 로컬 환경변수 설정

3. 빌드
   ./gradlew assembleDebug → APK 생성

4. 갤럭시 설치
   USB 디버깅 ON → ADB 설치 명령어

5. 코드 변경 후 반영
   수정 → 빌드 → 설치 → 확인 → 커밋 & 푸시
```

---

## STEP 1. 필수 도구 설치

### 1-1. Homebrew (Mac 패키지 매니저)
> 터미널에 아래 명령어 붙여넣기

```bash
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```

- 공식 사이트: https://brew.sh

### 1-2. Git
```bash
brew install git
git --version  # 설치 확인
```

### 1-3. Android Studio
- 다운로드: https://developer.android.com/studio
- `.dmg` 파일 다운로드 → 열기 → `Android Studio.app`을 Applications 폴더로 드래그
- 앱 실행 후 **Standard** 설치 선택 → 완료까지 기다리기 (약 10~20분)

### 1-4. Android SDK (Android Studio 내에서 자동 설치됨)
Android Studio 첫 실행 시 자동으로 SDK가 설치되지만, 아래 경로로 확인:
```
Android Studio → Settings → Appearance & Behavior
  → System Settings → Android SDK
  → SDK Platforms 탭: Android 14 (API 35) 체크
  → SDK Tools 탭: Android SDK Build-Tools, Android Emulator 체크
```

SDK 기본 설치 경로:
```
/Users/[사용자명]/Library/Android/sdk
```

### 1-5. ADB (Android Debug Bridge) — SDK에 포함됨
```bash
# 설치 확인
/Users/crystal/Library/Android/sdk/platform-tools/adb version
```

---

## STEP 2. 프로젝트 클론

### 2-1. GitHub에서 프로젝트 받기
```bash
# 원하는 폴더로 이동 (예: ~/dev/test/)
mkdir -p ~/dev/test
cd ~/dev/test

# 클론
git clone https://github.com/shirley1979-sys/eng.git

# 프로젝트 폴더로 이동
cd eng
```

### 2-2. local.properties 설정
클론 후 `local.properties` 파일을 프로젝트 루트에 수동 생성 필요
(`.gitignore`에 포함되어 있어 GitHub에 올라가지 않음)

```bash
# local.properties 파일 생성
echo "sdk.dir=/Users/[사용자명]/Library/Android/sdk" > local.properties
```

> `[사용자명]` 자리에 본인 Mac 계정 이름 입력
> 예: `/Users/crystal/Library/Android/sdk`

---

## STEP 3. 환경변수 설정

빌드 시마다 JAVA_HOME 환경변수가 필요합니다.

```bash
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
```

매번 입력하기 귀찮으면 `.zshrc`에 영구 등록:
```bash
echo 'export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"' >> ~/.zshrc
source ~/.zshrc
```

---

## STEP 4. APK 빌드

```bash
cd ~/dev/test/eng

# 환경변수 설정 (위에서 영구 등록했으면 생략 가능)
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"

# 디버그 APK 빌드
./gradlew assembleDebug
```

빌드 성공 시 출력:
```
BUILD SUCCESSFUL in Xs
```

APK 생성 위치:
```
app/build/outputs/apk/debug/app-debug.apk
```

### 문제 발생 시: 클린 빌드
```bash
./gradlew clean assembleDebug
```

---

## STEP 5. 갤럭시(안드로이드) 설치

### 5-1. 갤럭시 개발자 옵션 ON
1. 갤럭시 **설정** 앱 열기
2. **휴대전화 정보** → **소프트웨어 정보**
3. **빌드번호** 7번 연속 탭
4. "개발자 모드가 켜졌습니다" 메시지 확인
5. 뒤로가기 → **개발자 옵션** 메뉴 생성됨
6. **개발자 옵션** → **USB 디버깅** ON

### 5-2. Mac과 USB 연결
1. USB-C 케이블로 갤럭시 ↔ Mac 연결
2. 갤럭시 화면에 팝업 뜨면 **"항상 허용"** 선택

### 5-3. 연결 확인
```bash
/Users/crystal/Library/Android/sdk/platform-tools/adb devices
```
출력 예시:
```
List of devices attached
R3CN204XXXX    device
```
`device`가 보이면 연결 성공.

### 5-4. APK 설치
```bash
/Users/crystal/Library/Android/sdk/platform-tools/adb install -r \
  ~/dev/test/eng/app/build/outputs/apk/debug/app-debug.apk
```

설치 성공 시:
```
Performing Streamed Install
Success
```

---

## STEP 6. 코드 수정 후 반영 워크플로우

```
코드 수정
  ↓
빌드
  ./gradlew assembleDebug
  ↓
설치
  adb install -r app/build/outputs/apk/debug/app-debug.apk
  ↓
갤럭시에서 확인
  ↓
문제 없으면 커밋 & 푸시
  git add [파일명]
  git commit -m "변경 내용 설명"
  git push
```

---

## STEP 7. GitHub 커밋 & 푸시

```bash
cd ~/dev/test/eng

# 변경된 파일 확인
git status

# 특정 파일 추가
git add app/src/main/java/com/letterbloom/app/ui/screens/HomeScreen.kt

# 또는 수정된 파일 전체 추가
git add -u

# 커밋
git commit -m "어떤 변경인지 간단히 설명"

# 푸시
git push
```

GitHub 저장소: https://github.com/shirley1979-sys/eng

---

## 자주 쓰는 명령어 모음

```bash
# ── 빌드 ──────────────────────────────────────────
./gradlew assembleDebug               # 일반 빌드
./gradlew clean assembleDebug         # 클린 빌드

# ── ADB ───────────────────────────────────────────
adb devices                           # 연결된 기기 확인
adb install -r app-debug.apk          # APK 설치
adb logcat -s "letterbloom"           # 앱 로그 확인

# ── Git ───────────────────────────────────────────
git status                            # 변경 파일 확인
git diff                              # 변경 내용 확인
git log --oneline -10                 # 최근 커밋 10개
git push                              # 원격 저장소에 푸시
```

> ADB를 매번 전체 경로로 입력하기 불편하면 `.zshrc`에 추가:
> ```bash
> export PATH="$PATH:/Users/crystal/Library/Android/sdk/platform-tools"
> ```

---

## 외부 서비스 링크 모음

| 서비스 | 용도 | 링크 |
|--------|------|------|
| GitHub 저장소 | 소스코드 관리 | https://github.com/shirley1979-sys/eng |
| Supabase 프로젝트 | 클라우드 DB | https://supabase.com/dashboard/project/bsleikhmkxnvyrtprzbq |
| Firebase Console | 앱 배포 (App Distribution) | https://console.firebase.google.com |
| Android Studio 다운로드 | IDE | https://developer.android.com/studio |
| Homebrew | Mac 패키지 매니저 | https://brew.sh |

---

## 트러블슈팅

| 증상 | 원인 | 해결 |
|------|------|------|
| `JAVA_HOME not set` | 환경변수 미설정 | `export JAVA_HOME=...` 실행 |
| `SDK location not found` | local.properties 없음 | 프로젝트 루트에 local.properties 생성 |
| `adb: device not found` | USB 디버깅 미허용 | 갤럭시 팝업에서 "항상 허용" 선택 |
| `BUILD FAILED` | 코드 오류 or 캐시 문제 | `./gradlew clean assembleDebug` |
| `Duplicate class` | 라이브러리 버전 충돌 | `libs.versions.toml` 버전 확인 |
| 앱 데이터 리셋 | 온보딩 플래그 없음 | `onboarding_done` 키 확인 (LearningPrefs) |

---

*LetterBloom — 지금이 딱 좋은 때 ✦*
