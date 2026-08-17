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

enum class LeagueTier(
    val title: String,
    val minPoints: Int,
    val maxPoints: Int,
    val iconEmoji: String,
    val primaryColorHex: Long,
    val secondaryColorHex: Long,
    val requiredLevel: Int,
    val description: String
) {
    WOOD(
        title = "Wood (Ahşap)",
        minPoints = 0,
        maxPoints = 99,
        iconEmoji = "🪵",
        primaryColorHex = 0xFF8D6E63,
        secondaryColorHex = 0xFF5D4037,
        requiredLevel = 1,
        description = "Yeni başlayanlar için temel lig"
    ),
    BRONZE(
        title = "Bronze (Bronz)",
        minPoints = 100,
        maxPoints = 299,
        iconEmoji = "🥉",
        primaryColorHex = 0xFFCD7F32,
        secondaryColorHex = 0xFF8B5A2B,
        requiredLevel = 1,
        description = "Formunu yükselten azimli sporcular"
    ),
    SILVER(
        title = "Silver (Gümüş)",
        minPoints = 300,
        maxPoints = 699,
        iconEmoji = "🥈",
        primaryColorHex = 0xFFC0C0C0,
        secondaryColorHex = 0xFF708090,
        requiredLevel = 2,
        description = "Kondisyonunu artıran istikrarlı atletler"
    ),
    GOLD(
        title = "Gold (Altın)",
        minPoints = 700,
        maxPoints = 1299,
        iconEmoji = "🥇",
        primaryColorHex = 0xFFFFD700,
        secondaryColorHex = 0xFFDAA520,
        requiredLevel = 3,
        description = "Güçlü ve disiplinli savaşçılar"
    ),
    PLATINUM(
        title = "Platinum (Platin)",
        minPoints = 1300,
        maxPoints = 2199,
        iconEmoji = "💎",
        primaryColorHex = 0xFF00E5FF,
        secondaryColorHex = 0xFF0097A7,
        requiredLevel = 5,
        description = "Elit seviye egzersiz şampiyonları"
    ),
    DIAMOND(
        title = "Diamond (Elmas)",
        minPoints = 2200,
        maxPoints = 3499,
        iconEmoji = "🔮",
        primaryColorHex = 0xFFB388FF,
        secondaryColorHex = 0xFF7C4DFF,
        requiredLevel = 8,
        description = "Kusursuz form ve durdurulamaz güç"
    ),
    CHAMPION(
        title = "Champion (Şampiyon)",
        minPoints = 3500,
        maxPoints = 5199,
        iconEmoji = "🏆",
        primaryColorHex = 0xFFFF5252,
        secondaryColorHex = 0xFFD32F2F,
        requiredLevel = 12,
        description = "Ligleri domine eden efsane şampiyonlar"
    ),
    TITAN(
        title = "Titan",
        minPoints = 5200,
        maxPoints = 7999,
        iconEmoji = "⚡",
        primaryColorHex = 0xFFFF9100,
        secondaryColorHex = 0xFFE65100,
        requiredLevel = 20,
        description = "İnsanüstü dayanıklılık ve devlerin gücü"
    ),
    OLYMPIAN(
        title = "Olympian (Olimpiyat)",
        minPoints = 8000,
        maxPoints = 999999,
        iconEmoji = "👑",
        primaryColorHex = 0xFFFFD54F,
        secondaryColorHex = 0xFFFFB300,
        requiredLevel = 1000,
        description = "Olimpiyat Tanrıları zirvesi — Efsanevi Seviye"
    );

    val primaryColor: Color get() = Color(primaryColorHex)
    val secondaryColor: Color get() = Color(secondaryColorHex)

    companion object {
        fun getTierForPoints(points: Int): LeagueTier {
            return values().findLast { points >= it.minPoints } ?: WOOD
        }
    }
}

data class Competitor(
    val id: String,
    val name: String,
    val avatarEmoji: String,
    val reps: Int,
    val points: Int,
    val isCurrentUser: Boolean = false,
    val rank: Int = 0,
    val streakDays: Int = 1,
    val recentExercise: String = "Şınav",
    val leagueTier: LeagueTier = LeagueTier.WOOD,
    val userLevel: Int = 1
)
