package com.letterbloom.app.data

data class Word(
    val english: String,
    val korean: String,
    val ipa: String,
    val category: WordCategory,
    val exampleSentence: String,
    val exampleKorean: String,
    val situation: String
)

enum class WordCategory(val displayName: String, val emoji: String) {
    AIRPORT("공항", "✈️"),
    HOTEL("호텔", "🏨"),
    RESTAURANT("식당", "🍽️"),
    SHOPPING("쇼핑", "🛍️"),
    SIGHTSEEING("관광", "🗺️"),
    TRANSPORT("교통", "🚌"),
    EMERGENCY("응급상황", "🆘"),
    DAILY("일상회화", "💬")
}

data class QuizQuestion(
    val sentence: String,
    val blank: String,
    val options: List<String>,
    val correctIndex: Int,
    val situation: String,
    val situationKorean: String
)

val wordList = listOf(
    // 공항
    Word("boarding", "탑승", "/ˈbɔːrdɪŋ/", WordCategory.AIRPORT,
        "Please show your boarding pass.", "탑승권을 보여주세요.", "체크인 카운터에서"),
    Word("passport", "여권", "/ˈpæspɔːrt/", WordCategory.AIRPORT,
        "May I see your passport, please?", "여권을 보여주시겠어요?", "입국 심사대에서"),
    Word("gate", "탑승구", "/ɡeɪt/", WordCategory.AIRPORT,
        "Your gate is B12.", "탑승구는 B12입니다.", "공항 안내에서"),
    Word("customs", "세관", "/ˈkʌstəmz/", WordCategory.AIRPORT,
        "Please fill out the customs form.", "세관 신고서를 작성해주세요.", "입국 심사에서"),
    Word("luggage", "수하물", "/ˈlʌɡɪdʒ/", WordCategory.AIRPORT,
        "Where can I collect my luggage?", "수하물은 어디서 찾나요?", "공항 도착 후"),
    Word("delay", "지연", "/dɪˈleɪ/", WordCategory.AIRPORT,
        "The flight is delayed by two hours.", "비행기가 두 시간 지연됩니다.", "탑승 대기 중"),

    // 호텔
    Word("reservation", "예약", "/ˌrezərˈveɪʃn/", WordCategory.HOTEL,
        "I have a reservation under my name.", "제 이름으로 예약했어요.", "호텔 체크인 시"),
    Word("check-out", "체크아웃", "/ˈtʃek aʊt/", WordCategory.HOTEL,
        "What time is check-out?", "체크아웃은 몇 시인가요?", "호텔 프런트에서"),
    Word("wifi", "와이파이", "/ˈwaɪfaɪ/", WordCategory.HOTEL,
        "Do you have wifi?", "와이파이가 있나요?", "카페/호텔에서"),
    Word("room service", "룸서비스", "/ruːm ˈsɜːrvɪs/", WordCategory.HOTEL,
        "I'd like to order room service.", "룸서비스를 주문하고 싶어요.", "호텔 객실에서"),
    Word("towel", "수건", "/ˈtaʊəl/", WordCategory.HOTEL,
        "Could I get extra towels?", "수건을 더 주실 수 있나요?", "호텔 요청 시"),
    Word("key card", "키 카드", "/kiː kɑːrd/", WordCategory.HOTEL,
        "My key card doesn't work.", "키 카드가 작동하지 않아요.", "호텔 프런트에서"),

    // 식당
    Word("order", "주문하다", "/ˈɔːrdər/", WordCategory.RESTAURANT,
        "Can I order now?", "지금 주문해도 될까요?", "식당에서"),
    Word("menu", "메뉴", "/ˈmenjuː/", WordCategory.RESTAURANT,
        "Can I see the menu, please?", "메뉴판을 볼 수 있을까요?", "식당에서"),
    Word("allergic", "알레르기가 있는", "/əˈlɜːrdʒɪk/", WordCategory.RESTAURANT,
        "I'm allergic to nuts.", "저는 견과류 알레르기가 있어요.", "식당에서 주문 시"),
    Word("spicy", "매운", "/ˈspaɪsi/", WordCategory.RESTAURANT,
        "Is this spicy?", "이거 매운가요?", "음식 주문 시"),
    Word("bill", "계산서", "/bɪl/", WordCategory.RESTAURANT,
        "Can I have the bill, please?", "계산서 주세요.", "식사 후"),
    Word("vegetarian", "채식주의자", "/ˌvedʒəˈteriən/", WordCategory.RESTAURANT,
        "Do you have vegetarian options?", "채식 메뉴가 있나요?", "식당에서"),

    // 쇼핑
    Word("refund", "환불", "/ˈriːfʌnd/", WordCategory.SHOPPING,
        "I'd like a refund, please.", "환불하고 싶어요.", "쇼핑 후"),
    Word("discount", "할인", "/ˈdɪskaʊnt/", WordCategory.SHOPPING,
        "Is there any discount?", "할인이 되나요?", "쇼핑 시"),
    Word("size", "사이즈", "/saɪz/", WordCategory.SHOPPING,
        "Do you have this in a larger size?", "더 큰 사이즈가 있나요?", "옷 가게에서"),
    Word("receipt", "영수증", "/rɪˈsiːt/", WordCategory.SHOPPING,
        "Can I have a receipt?", "영수증 주세요.", "계산 후"),
    Word("exchange", "교환", "/ɪksˈtʃeɪndʒ/", WordCategory.SHOPPING,
        "I'd like to exchange this.", "이걸 교환하고 싶어요.", "쇼핑 후"),
    Word("price", "가격", "/praɪs/", WordCategory.SHOPPING,
        "What's the price of this?", "이거 얼마예요?", "쇼핑 시"),

    // 관광
    Word("recommend", "추천하다", "/ˌrekəˈmend/", WordCategory.SIGHTSEEING,
        "Can you recommend a good restaurant?", "좋은 식당 추천해주실 수 있나요?", "관광지에서"),
    Word("museum", "박물관", "/mjuːˈziːəm/", WordCategory.SIGHTSEEING,
        "Where is the nearest museum?", "가장 가까운 박물관이 어디인가요?", "관광 시"),
    Word("ticket", "티켓", "/ˈtɪkɪt/", WordCategory.SIGHTSEEING,
        "Two tickets, please.", "티켓 두 장 주세요.", "관광지 입장 시"),
    Word("photo", "사진", "/ˈfoʊtoʊ/", WordCategory.SIGHTSEEING,
        "Could you take a photo of me?", "사진 찍어주실 수 있나요?", "관광지에서"),

    // 교통
    Word("taxi", "택시", "/ˈtæksi/", WordCategory.TRANSPORT,
        "Can you call a taxi for me?", "택시를 불러주실 수 있나요?", "이동 시"),
    Word("station", "역", "/ˈsteɪʃn/", WordCategory.TRANSPORT,
        "Where is the subway station?", "지하철역이 어디인가요?", "길 찾기"),
    Word("fare", "요금", "/fer/", WordCategory.TRANSPORT,
        "How much is the fare?", "요금이 얼마예요?", "교통 이용 시"),
    Word("transfer", "환승", "/ˈtrænsfɜːr/", WordCategory.TRANSPORT,
        "Do I need to transfer?", "환승해야 하나요?", "대중교통 이용 시"),

    // 응급상황
    Word("help", "도움", "/help/", WordCategory.EMERGENCY,
        "I need help!", "도움이 필요해요!", "응급 상황에서"),
    Word("lost", "길을 잃은", "/lɒst/", WordCategory.EMERGENCY,
        "I'm lost. Can you help me?", "길을 잃었어요. 도와주실 수 있나요?", "길을 잃었을 때"),
    Word("hospital", "병원", "/ˈhɒspɪtl/", WordCategory.EMERGENCY,
        "Please take me to a hospital.", "병원에 데려다 주세요.", "응급 상황에서"),
    Word("police", "경찰", "/pəˈliːs/", WordCategory.EMERGENCY,
        "Please call the police.", "경찰을 불러주세요.", "응급 상황에서"),
    Word("emergency", "응급", "/ɪˈmɜːrdʒənsi/", WordCategory.EMERGENCY,
        "This is an emergency!", "응급 상황이에요!", "위급 상황에서"),

    // 일상회화
    Word("excuse me", "실례합니다", "/ɪkˈskjuːz miː/", WordCategory.DAILY,
        "Excuse me, where is the restroom?", "실례합니다, 화장실이 어디인가요?", "길 물어볼 때"),
    Word("please", "부탁드려요", "/pliːz/", WordCategory.DAILY,
        "Could you speak slowly, please?", "천천히 말씀해 주시겠어요?", "의사소통 시"),
    Word("thank you", "감사합니다", "/θæŋk juː/", WordCategory.DAILY,
        "Thank you so much for your help!", "도와주셔서 정말 감사합니다!", "감사 표현"),
    Word("sorry", "죄송합니다", "/ˈsɒri/", WordCategory.DAILY,
        "I'm sorry, I don't understand.", "죄송해요, 이해를 못 했어요.", "의사소통 시"),
    Word("understand", "이해하다", "/ˌʌndərˈstænd/", WordCategory.DAILY,
        "I don't understand. Can you repeat?", "이해를 못 했어요. 반복해 주시겠어요?", "대화 중"),
    Word("charge", "충전하다", "/tʃɑːrdʒ/", WordCategory.DAILY,
        "Where can I charge my phone?", "핸드폰을 어디서 충전할 수 있나요?", "카페/공공장소에서")
)

val quizQuestions = listOf(
    QuizQuestion(
        sentence = "Can I ___ now?",
        blank = "order",
        options = listOf("order", "menu", "bill", "eat"),
        correctIndex = 0,
        situation = "🍽️ At a restaurant",
        situationKorean = "식당에서 주문할 때"
    ),
    QuizQuestion(
        sentence = "Do you have ___?",
        blank = "wifi",
        options = listOf("wifi", "water", "table", "menu"),
        correctIndex = 0,
        situation = "☕ At a café",
        situationKorean = "카페에서"
    ),
    QuizQuestion(
        sentence = "I have a ___ under my name.",
        blank = "reservation",
        options = listOf("reservation", "key card", "room", "ticket"),
        correctIndex = 0,
        situation = "🏨 At hotel check-in",
        situationKorean = "호텔 체크인 시"
    ),
    QuizQuestion(
        sentence = "I'm ___ to nuts.",
        blank = "allergic",
        options = listOf("allergic", "sensitive", "hungry", "full"),
        correctIndex = 0,
        situation = "🍽️ Ordering food",
        situationKorean = "음식 주문 시"
    ),
    QuizQuestion(
        sentence = "I'd like a ___, please.",
        blank = "refund",
        options = listOf("refund", "discount", "exchange", "receipt"),
        correctIndex = 0,
        situation = "🛍️ At a store",
        situationKorean = "쇼핑 후 환불 시"
    ),
    QuizQuestion(
        sentence = "Please show your ___ pass.",
        blank = "boarding",
        options = listOf("boarding", "passport", "gate", "ticket"),
        correctIndex = 0,
        situation = "✈️ At the airport",
        situationKorean = "공항 탑승 시"
    ),
    QuizQuestion(
        sentence = "What time is ___?",
        blank = "check-out",
        options = listOf("check-out", "check-in", "breakfast", "dinner"),
        correctIndex = 0,
        situation = "🏨 At the hotel",
        situationKorean = "호텔에서"
    ),
    QuizQuestion(
        sentence = "I'm ___. Can you help me?",
        blank = "lost",
        options = listOf("lost", "tired", "hungry", "late"),
        correctIndex = 0,
        situation = "🆘 Emergency",
        situationKorean = "길을 잃었을 때"
    )
)

data class LevelInfo(
    val level: Int,
    val name: String,
    val emoji: String,
    val description: String,
    val color: androidx.compose.ui.graphics.Color,
    val wordsRequired: Int
)
