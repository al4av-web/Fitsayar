package com.example.data

import com.example.model.Competitor
import com.example.model.LeagueTier

object LeagueManager {

    private val baseCompetitors = listOf(
        // Wood League
        Competitor("w1", "Çaylak Ali", "🌱", 45, 50, false, 0, 1, "Şınav", LeagueTier.WOOD, 1),
        Competitor("w2", "Selin Başlar", "🏃‍♀️", 38, 42, false, 0, 1, "Squat", LeagueTier.WOOD, 1),
        Competitor("w3", "Kerem Aday", "💪", 25, 28, false, 0, 1, "Mekik", LeagueTier.WOOD, 1),
        Competitor("w4", "Deniz Yeni", "✨", 15, 18, false, 0, 1, "Şınav", LeagueTier.WOOD, 1),

        // Bronze League
        Competitor("b1", "Kaan Demir", "⚡", 180, 210, false, 0, 3, "Şınav", LeagueTier.BRONZE, 2),
        Competitor("b2", "Zeynep Kaya", "🔥", 150, 175, false, 0, 2, "Squat", LeagueTier.BRONZE, 2),
        Competitor("b3", "Emre Yılmaz", "💪", 120, 140, false, 0, 1, "Mekik", LeagueTier.BRONZE, 1),
        Competitor("b4", "Elif Şahin", "🏃‍♀️", 95, 110, false, 0, 4, "Şınav", LeagueTier.BRONZE, 1),

        // Silver League
        Competitor("s1", "Mert Koç", "🦁", 520, 580, false, 0, 6, "Squat", LeagueTier.SILVER, 3),
        Competitor("s2", "Ayşe Polat", "🐯", 460, 510, false, 0, 5, "Şınav", LeagueTier.SILVER, 3),
        Competitor("s3", "Caner Öz", "🥊", 390, 430, false, 0, 4, "Mekik", LeagueTier.SILVER, 2),
        Competitor("s4", "Deniz Arslan", "🚀", 310, 340, false, 0, 7, "Squat", LeagueTier.SILVER, 2),

        // Gold League
        Competitor("g1", "Volkan Aksoy", "🦍", 1120, 1220, false, 0, 12, "Şınav", LeagueTier.GOLD, 5),
        Competitor("g2", "Büşra Yıldız", "⚡", 980, 1070, false, 0, 9, "Squat", LeagueTier.GOLD, 4),
        Competitor("g3", "Serdar Taş", "💥", 860, 930, false, 0, 11, "Mekik", LeagueTier.GOLD, 4),
        Competitor("g4", "Melis Doğan", "🦅", 740, 810, false, 0, 8, "Squat", LeagueTier.GOLD, 3),

        // Platinum League
        Competitor("p1", "Efe Karaca", "🐉", 1950, 2100, false, 0, 21, "Squat", LeagueTier.PLATINUM, 8),
        Competitor("p2", "Selin Güler", "⚡", 1750, 1880, false, 0, 18, "Şınav", LeagueTier.PLATINUM, 7),
        Competitor("p3", "Kerem Vural", "🏆", 1540, 1660, false, 0, 15, "Mekik", LeagueTier.PLATINUM, 6),
        Competitor("p4", "Derya Tunç", "⚔️", 1350, 1440, false, 0, 14, "Squat", LeagueTier.PLATINUM, 5),

        // Diamond League
        Competitor("d1", "Barbaros Titan", "🔮", 3150, 3380, false, 0, 35, "Şınav", LeagueTier.DIAMOND, 14),
        Competitor("d2", "Asena Alp", "🐺", 2850, 3050, false, 0, 28, "Squat", LeagueTier.DIAMOND, 12),
        Competitor("d3", "Cenk Demirci", "🦾", 2550, 2720, false, 0, 25, "Mekik", LeagueTier.DIAMOND, 10),
        Competitor("d4", "İrem Yıldırım", "⚡", 2280, 2430, false, 0, 22, "Squat", LeagueTier.DIAMOND, 9),

        // Champion League
        Competitor("c1", "Hakan Kral", "🏆", 4800, 5050, false, 0, 48, "Şınav", LeagueTier.CHAMPION, 22),
        Competitor("c2", "Leyla Sancak", "👑", 4300, 4520, false, 0, 40, "Squat", LeagueTier.CHAMPION, 19),
        Competitor("c3", "Bora Çelik", "⚔️", 3850, 4050, false, 0, 36, "Mekik", LeagueTier.CHAMPION, 16),

        // Titan League
        Competitor("t1", "Titan Kronos", "⚡", 7400, 7800, false, 0, 75, "Squat", LeagueTier.TITAN, 45),
        Competitor("t2", "Gökhan Atlas", "🌍", 6600, 6950, false, 0, 62, "Şınav", LeagueTier.TITAN, 38),
        Competitor("t3", "Alp Er Tunga", "🐺", 5800, 6100, false, 0, 54, "Mekik", LeagueTier.TITAN, 30),

        // Olympian League (Target: Level 1000 Legends)
        Competitor("o1", "Zeus Olympian", "👑", 15000, 16500, false, 0, 120, "Şınav", LeagueTier.OLYMPIAN, 1000),
        Competitor("o2", "Herkül Prime", "🔥", 12500, 13800, false, 0, 99, "Squat", LeagueTier.OLYMPIAN, 850),
        Competitor("o3", "Ares God of War", "⚔️", 10200, 11400, false, 0, 88, "Mekik", LeagueTier.OLYMPIAN, 700)
    )

    fun getCompetitorsForTier(
        tier: LeagueTier,
        userName: String,
        userReps: Int,
        userPoints: Int,
        userLevel: Int = 1
    ): List<Competitor> {
        val userTier = LeagueTier.getTierForPoints(userPoints)
        val list = baseCompetitors.filter { it.leagueTier == tier }.toMutableList()

        if (userTier == tier) {
            val userCompetitor = Competitor(
                id = "current_user",
                name = userName,
                avatarEmoji = "⭐",
                reps = userReps,
                points = userPoints,
                isCurrentUser = true,
                rank = 0,
                streakDays = if (userReps > 0) 1 else 0,
                recentExercise = "FitSayar",
                leagueTier = userTier,
                userLevel = userLevel
            )
            list.add(userCompetitor)
        }

        val sorted = list.sortedByDescending { it.points }
        return sorted.mapIndexed { index, competitor ->
            competitor.copy(rank = index + 1)
        }
    }
}
