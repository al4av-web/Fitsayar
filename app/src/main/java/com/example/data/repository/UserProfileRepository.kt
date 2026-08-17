package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.model.LevelCalculator
import com.example.model.LevelProgress
import com.example.model.RankTier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserProfile(
    val name: String = "Sporcu",
    val totalXp: Int = 0,
    val rankedRp: Int = 0,
    val vsWins: Int = 0,
    val vsLosses: Int = 0,
    val vsDraws: Int = 0,
    val streakDays: Int = 1
) {
    val levelProgress: LevelProgress get() = LevelCalculator.calculateLevel(totalXp)
    val rankTier: RankTier get() = RankTier.getTierForRP(rankedRp)
    val isRankedUnlocked: Boolean get() = levelProgress.level >= RankTier.RANKED_UNLOCK_LEVEL
}

class UserProfileRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("fit_user_profile", Context.MODE_PRIVATE)

    private val _userProfile = MutableStateFlow(loadProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private fun loadProfile(): UserProfile {
        return UserProfile(
            name = prefs.getString("user_name", "Sporcu") ?: "Sporcu",
            totalXp = prefs.getInt("total_xp", 0),
            rankedRp = prefs.getInt("ranked_rp", 0),
            vsWins = prefs.getInt("vs_wins", 0),
            vsLosses = prefs.getInt("vs_losses", 0),
            vsDraws = prefs.getInt("vs_draws", 0),
            streakDays = prefs.getInt("streak_days", 1)
        )
    }

    fun updateName(newName: String) {
        prefs.edit().putString("user_name", newName).apply()
        _userProfile.value = _userProfile.value.copy(name = newName)
    }

    /**
     * Her 1 tekrar = 1 XP ekler
     */
    fun addXp(amount: Int): LevelProgress {
        val newXp = (_userProfile.value.totalXp + amount).coerceAtLeast(0)
        prefs.edit().putInt("total_xp", newXp).apply()
        val updated = _userProfile.value.copy(totalXp = newXp)
        _userProfile.value = updated
        return updated.levelProgress
    }

    fun recordVsBattleResult(isWin: Boolean, isDraw: Boolean, xpEarned: Int, rpDelta: Int) {
        val current = _userProfile.value
        val newXp = (current.totalXp + xpEarned).coerceAtLeast(0)
        val newRp = (current.rankedRp + rpDelta).coerceAtLeast(0)
        val newWins = if (isWin) current.vsWins + 1 else current.vsWins
        val newLosses = if (!isWin && !isDraw) current.vsLosses + 1 else current.vsLosses
        val newDraws = if (isDraw) current.vsDraws + 1 else current.vsDraws

        prefs.edit()
            .putInt("total_xp", newXp)
            .putInt("ranked_rp", newRp)
            .putInt("vs_wins", newWins)
            .putInt("vs_losses", newLosses)
            .putInt("vs_draws", newDraws)
            .apply()

        _userProfile.value = current.copy(
            totalXp = newXp,
            rankedRp = newRp,
            vsWins = newWins,
            vsLosses = newLosses,
            vsDraws = newDraws
        )
    }
}
