package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.RankBronze
import com.example.ui.theme.RankChampion
import com.example.ui.theme.RankDiamond
import com.example.ui.theme.RankGold
import com.example.ui.theme.RankOlympian
import com.example.ui.theme.RankPlatinum
import com.example.ui.theme.RankSilver
import com.example.ui.theme.RankTitan
import com.example.ui.theme.RankWood

enum class RankTier(
    val title: String,
    val minRP: Int,
    val maxRP: Int,
    val iconEmoji: String,
    val rankColor: Color,
    val requiredLevelToDisplay: Int,
    val description: String
) {
    WOOD(
        title = "Wood (Ahşap)",
        minRP = 0,
        maxRP = 199,
        iconEmoji = "🪵",
        rankColor = RankWood,
        requiredLevelToDisplay = 1,
        description = "Maceraya yeni başlayan azimli çaylak"
    ),
    BRONZE(
        title = "Bronze (Bronz)",
        minRP = 200,
        maxRP = 499,
        iconEmoji = "🥉",
        rankColor = RankBronze,
        requiredLevelToDisplay = 1,
        description = "Temel gücünü inşa eden sporcu"
    ),
    SILVER(
        title = "Silver (Gümüş)",
        minRP = 500,
        maxRP = 899,
        iconEmoji = "🥈",
        rankColor = RankSilver,
        requiredLevelToDisplay = 2,
        description = "Formunu ve kondisyonunu katlayan atlet"
    ),
    GOLD(
        title = "Gold (Altın)",
        minRP = 900,
        maxRP = 1399,
        iconEmoji = "🥇",
        rankColor = RankGold,
        requiredLevelToDisplay = 3,
        description = "Güçlü ve yarışmaya hazır savaşçı"
    ),
    PLATINUM(
        title = "Platinum (Platin)",
        minRP = 1400,
        maxRP = 1999,
        iconEmoji = "💎",
        rankColor = RankPlatinum,
        requiredLevelToDisplay = 5,
        description = "Elit seviye egzersiz ustası"
    ),
    DIAMOND(
        title = "Diamond (Elmas)",
        minRP = 2000,
        maxRP = 2699,
        iconEmoji = "🔮",
        rankColor = RankDiamond,
        requiredLevelToDisplay = 8,
        description = "Kusursuz form ve durdurulamaz güç"
    ),
    CHAMPION(
        title = "Champion (Şampiyon)",
        minRP = 2700,
        maxRP = 3499,
        iconEmoji = "🏆",
        rankColor = RankChampion,
        requiredLevelToDisplay = 12,
        description = "Ligleri domine eden efsane şampiyon"
    ),
    TITAN(
        title = "Titan",
        minRP = 3500,
        maxRP = 4999,
        iconEmoji = "⚡",
        rankColor = RankTitan,
        requiredLevelToDisplay = 20,
        description = "İnsanüstü dayanıklılık ve devlerin gücü"
    ),
    OLYMPIAN(
        title = "Olympian (Olimpiyat)",
        minRP = 5000,
        maxRP = 99999,
        iconEmoji = "👑",
        rankColor = RankOlympian,
        requiredLevelToDisplay = 1000,
        description = "Olimpiyat Tanrıları zirvesi — Efsanelerin Efsanesi"
    );

    companion object {
        const val RANKED_UNLOCK_LEVEL = 3

        fun getTierForRP(rp: Int): RankTier {
            return values().findLast { rp >= it.minRP } ?: WOOD
        }
    }
}

data class LevelProgress(
    val level: Int,
    val currentLevelXp: Int,
    val xpRequiredForNextLevel: Int,
    val totalLifetimeXp: Int,
    val progressFraction: Float
)

object LevelCalculator {
    /**
     * Seviye 1 için 100 XP gerekir.
     * Seviye L için (L'den L+1'e geçmek için) gereken XP: 100 + (L - 1) * 25 XP
     * Her bir şınav/squat/mekik = 1 XP
     */
    fun getXpNeededForLevel(level: Int): Int {
        if (level < 1) return 100
        return 100 + (level - 1) * 25
    }

    fun calculateLevel(totalXp: Int): LevelProgress {
        var remainingXp = totalXp.coerceAtLeast(0)
        var level = 1
        var needed = getXpNeededForLevel(level)

        while (remainingXp >= needed) {
            remainingXp -= needed
            level++
            needed = getXpNeededForLevel(level)
        }

        val fraction = if (needed > 0) (remainingXp.toFloat() / needed.toFloat()).coerceIn(0f, 1f) else 1f

        return LevelProgress(
            level = level,
            currentLevelXp = remainingXp,
            xpRequiredForNextLevel = needed,
            totalLifetimeXp = totalXp,
            progressFraction = fraction
        )
    }
}
