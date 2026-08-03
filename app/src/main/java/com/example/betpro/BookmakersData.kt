package com.example.betpro

object BookmakersData {
    val bookmakers = listOf(
        Bookmaker(
            id = "pari",
            shortName = "PR",
            fullName = "Pari",
            description = "Букмекерская компания с биржевым модулем и отложенными ставками.",
            advantages = "• Биржевой модуль для обмена ставками\n• Отложенные ставки\n• Ранний выкуп позиции",
            bonusText = "Фрибет 3 000 ₽ за регистрацию",
            refLink = "https://pari.ru"
        ),
        Bookmaker(
            id = "fonbet",
            shortName = "FB",
            fullName = "Фонбет",
            description = "Первый букмекер России, лидер по количеству спортивных событий.",
            advantages = "• Фрибет до 15 000 ₽\n• Бесплатные трансляции\n• Центр статистики",
            bonusText = "Бесплатные трансляции матчей",
            refLink = "https://fonbet.ru"
        ),
        Bookmaker(
            id = "betcity",
            shortName = "BC",
            fullName = "Betcity",
            description = "Один из крупнейших букмекеров, основанный в 2003 году.",
            advantages = "• Минимальная маржа 2.3-3.5%\n• Кэшбэк до 25%\n• Три фрибета по 500 ₽",
            bonusText = "Кэшбэк до 25% за месяц",
            refLink = "https://betcity.ru"
        )
    )
}