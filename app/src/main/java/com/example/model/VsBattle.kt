package com.example.model

enum class VsMatchMode(
    val title: String,
    val durationSeconds: Int,
    val targetReps: Int,
    val isTimed: Boolean,
    val iconEmoji: String,
    val description: String
) {
    TIMED_60S(
        title = "60 Saniye Kapışması",
        durationSeconds = 60,
        targetReps = 0,
        isTimed = true,
        iconEmoji = "⏱️",
        description = "60 saniyede en çok tekrarı yapan kazanır!"
    ),
    TIMED_30S(
        title = "30 Saniye Yıldırım",
        durationSeconds = 30,
        targetReps = 0,
        isTimed = true,
        iconEmoji = "⚡",
        description = "Hızlı 30 saniyelik patlayıcı güç testi!"
    ),
    RACE_25(
        title = "25 Tekrar Yarışı",
        durationSeconds = 90,
        targetReps = 25,
        isTimed = false,
        iconEmoji = "🏁",
        description = "İlk 25 tekrara ulaşan maçı alır!"
    ),
    RACE_50(
        title = "50 Tekrar Maratonu",
        durationSeconds = 180,
        targetReps = 50,
        isTimed = false,
        iconEmoji = "🔥",
        description = "İlk 50 tekrara ulaşan zafere ulaşır!"
    )
}

enum class VsWinner {
    PLAYER,
    OPPONENT,
    DRAW
}

data class VsOpponent(
    val id: String,
    val name: String,
    val avatarEmoji: String,
    val rankTier: RankTier,
    val level: Int,
    val repsPerMinute: Float,      // Base speed
    val fatigueFactor: Float,      // Slowdown rate
    val quote: String,
    val isAiBot: Boolean = true,
    val countryFlag: String = "🇹🇷"
)

object VsOpponentPresets {
    val allOpponents = listOf(
        // Wood & Bronze Bots
        VsOpponent(
            id = "bot_wood_1",
            name = "Çaylak Ali",
            avatarEmoji = "🌱",
            rankTier = RankTier.WOOD,
            level = 1,
            repsPerMinute = 16f,
            fatigueFactor = 0.85f,
            quote = "Daha yeniyim ama elimden geleni yapacağım!",
            isAiBot = true,
            countryFlag = "🇹🇷"
        ),
        VsOpponent(
            id = "bot_bronze_1",
            name = "KevinKO",
            avatarEmoji = "🥊",
            rankTier = RankTier.BRONZE,
            level = 2,
            repsPerMinute = 22f,
            fatigueFactor = 0.90f,
            quote = "Bro thought I'd give up 💀",
            isAiBot = true,
            countryFlag = "🇺🇸"
        ),
        VsOpponent(
            id = "bot_bronze_2",
            name = "Zeynep Fitness",
            avatarEmoji = "🏃‍♀️",
            rankTier = RankTier.BRONZE,
            level = 3,
            repsPerMinute = 25f,
            fatigueFactor = 0.92f,
            quote = "Tempo tutturursan kazanırsın!",
            isAiBot = true,
            countryFlag = "🇹🇷"
        ),
        // Silver & Gold Bots
        VsOpponent(
            id = "bot_silver_1",
            name = "Mert Beast",
            avatarEmoji = "🦁",
            rankTier = RankTier.SILVER,
            level = 4,
            repsPerMinute = 30f,
            fatigueFactor = 0.94f,
            quote = "Asla pes etmem, sonuna kadar devam!",
            isAiBot = true,
            countryFlag = "🇩🇪"
        ),
        VsOpponent(
            id = "bot_gold_1",
            name = "Demir Kol Mike",
            avatarEmoji = "🦾",
            rankTier = RankTier.GOLD,
            level = 6,
            repsPerMinute = 36f,
            fatigueFactor = 0.95f,
            quote = "Kollarımdaki gücü hisset!",
            isAiBot = true,
            countryFlag = "🇬🇧"
        ),
        // Platinum & Diamond Bots
        VsOpponent(
            id = "bot_plat_1",
            name = "Selin Turbo",
            avatarEmoji = "⚡",
            rankTier = RankTier.PLATINUM,
            level = 10,
            repsPerMinute = 42f,
            fatigueFactor = 0.96f,
            quote = "Hızımı takip bile edemezsin!",
            isAiBot = true,
            countryFlag = "🇹🇷"
        ),
        VsOpponent(
            id = "bot_diam_1",
            name = "Cyber Spartan",
            avatarEmoji = "🛡️",
            rankTier = RankTier.DIAMOND,
            level = 16,
            repsPerMinute = 48f,
            fatigueFactor = 0.98f,
            quote = "Yapay zeka hızında insan gücü!",
            isAiBot = true,
            countryFlag = "🇯🇵"
        ),
        // Champion & Titan Bots
        VsOpponent(
            id = "bot_champ_1",
            name = "Şampiyon Alex",
            avatarEmoji = "🏆",
            rankTier = RankTier.CHAMPION,
            level = 25,
            repsPerMinute = 54f,
            fatigueFactor = 0.985f,
            quote = "Şampiyonluk unvanımı kimseye vermem!",
            isAiBot = true,
            countryFlag = "🇧🇷"
        ),
        VsOpponent(
            id = "bot_titan_1",
            name = "Titan Barbaros",
            avatarEmoji = "🦍",
            rankTier = RankTier.TITAN,
            level = 50,
            repsPerMinute = 60f,
            fatigueFactor = 0.99f,
            quote = "Benimle kapışmak için çelik gibi olmalısın!",
            isAiBot = true,
            countryFlag = "🇹🇷"
        ),
        // Olympian
        VsOpponent(
            id = "bot_olympian_1",
            name = "Zeus Olympian",
            avatarEmoji = "👑",
            rankTier = RankTier.OLYMPIAN,
            level = 1000,
            repsPerMinute = 70f,
            fatigueFactor = 1.0f,
            quote = "Olimpiyat dağının zirvesindeyim.",
            isAiBot = true,
            countryFlag = "🇬🇷"
        )
    )

    fun getRandomOpponentForTier(tier: RankTier): VsOpponent {
        val matches = allOpponents.filter { it.rankTier == tier }
        return if (matches.isNotEmpty()) {
            matches.random()
        } else {
            allOpponents.random()
        }
    }

    fun getQuickMatchOpponent(userRp: Int): VsOpponent {
        val userTier = RankTier.getTierForRP(userRp)
        val candidates = allOpponents.filter {
            Math.abs(it.rankTier.ordinal - userTier.ordinal) <= 1
        }
        return if (candidates.isNotEmpty()) candidates.random() else allOpponents[1]
    }
}
