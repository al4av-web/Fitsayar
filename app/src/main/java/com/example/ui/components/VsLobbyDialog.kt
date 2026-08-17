package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.repository.UserProfile
import com.example.model.ExerciseType
import com.example.model.RankTier
import com.example.model.VsMatchMode
import com.example.model.VsOpponent
import com.example.model.VsOpponentPresets
import com.example.ui.theme.BattleOpponentColor
import com.example.ui.theme.BattlePlayerColor
import com.example.ui.theme.ImmersiveBackground
import com.example.ui.theme.ImmersiveCoral
import com.example.ui.theme.ImmersiveGreen
import com.example.ui.theme.ImmersiveOnPrimary
import com.example.ui.theme.ImmersiveOutline
import com.example.ui.theme.ImmersivePrimary
import com.example.ui.theme.ImmersivePrimaryContainer
import com.example.ui.theme.ImmersiveSurface
import com.example.ui.theme.ImmersiveSurfaceVariant
import com.example.ui.theme.ImmersiveTextMuted
import com.example.ui.theme.ImmersiveTextPrimary
import com.example.ui.theme.ImmersiveTextSecondary
import com.example.ui.theme.RankGold

@Composable
fun VsLobbyDialog(
    userProfile: UserProfile,
    onStartBattle: (exercise: ExerciseType, mode: VsMatchMode, opponent: VsOpponent, isRanked: Boolean) -> Unit,
    onOpenRankedInfo: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedExercise by remember { mutableStateOf(ExerciseType.PUSH_UP) }
    var selectedMode by remember { mutableStateOf(VsMatchMode.TIMED_60S) }
    var isRankedSelected by remember { mutableStateOf(userProfile.isRankedUnlocked) }
    var selectedOpponent by remember {
        mutableStateOf(
            VsOpponentPresets.getQuickMatchOpponent(userProfile.rankedRp)
        )
    }

    val levelProgress = userProfile.levelProgress
    val currentRank = userProfile.rankTier

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = ImmersiveBackground),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, ImmersiveOutline),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 16.dp)
                .testTag("vs_lobby_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(ImmersivePrimaryContainer, shape = CircleShape)
                                .border(1.dp, ImmersivePrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SportsKabaddi,
                                contentDescription = null,
                                tint = ImmersivePrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "1v1 VS BATTLE",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                color = ImmersiveTextPrimary
                            )
                            Text(
                                text = "Gerçek Zamanlı AI Kapışması",
                                fontSize = 11.sp,
                                color = ImmersiveTextSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Kapat",
                            tint = ImmersiveTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // User Rank & Level Banner
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = ImmersiveSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, currentRank.rankColor.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenRankedInfo() }
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = currentRank.iconEmoji, fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = currentRank.title,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = currentRank.rankColor
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = CircleShape,
                                            color = ImmersivePrimary.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = "SEVİYE ${levelProgress.level}",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                color = ImmersivePrimary,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${userProfile.rankedRp} RP • 1 Tekrar = 1 XP",
                                        fontSize = 11.sp,
                                        color = ImmersiveTextSecondary
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Rank Bilgisi",
                                    tint = ImmersiveTextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // XP Progress Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Seviye ${levelProgress.level + 1} İlerlemesi",
                                fontSize = 10.sp,
                                color = ImmersiveTextMuted
                            )
                            Text(
                                text = "${levelProgress.currentLevelXp} / ${levelProgress.xpRequiredForNextLevel} XP",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = ImmersivePrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { levelProgress.progressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = ImmersivePrimary,
                            trackColor = ImmersiveSurfaceVariant,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Battle Type: Bot vs Ranked Matchmaking
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Bot Antrenmanı
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (!isRankedSelected) ImmersiveSurfaceVariant else ImmersiveSurface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            if (!isRankedSelected) ImmersivePrimary else ImmersiveOutline
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { isRankedSelected = false }
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.SmartToy,
                                contentDescription = null,
                                tint = if (!isRankedSelected) ImmersivePrimary else ImmersiveTextMuted,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Bot Antrenmanı",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (!isRankedSelected) ImmersiveTextPrimary else ImmersiveTextSecondary
                            )
                            Text(
                                text = "Seçtiğin Botla Kapış",
                                fontSize = 10.sp,
                                color = ImmersiveTextMuted
                            )
                        }
                    }

                    // Dereceli (Ranked) Matchmaking
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isRankedSelected) ImmersiveSurfaceVariant else ImmersiveSurface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            if (isRankedSelected) RankGold else ImmersiveOutline
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if (userProfile.isRankedUnlocked) {
                                    isRankedSelected = true
                                    selectedOpponent = VsOpponentPresets.getQuickMatchOpponent(userProfile.rankedRp)
                                }
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (userProfile.isRankedUnlocked) Icons.Default.EmojiEvents else Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = if (userProfile.isRankedUnlocked) RankGold else ImmersiveCoral,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Dereceli (Ranked)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (userProfile.isRankedUnlocked) ImmersiveTextPrimary else ImmersiveTextMuted
                            )
                            Text(
                                text = if (userProfile.isRankedUnlocked) "RP & Sıralama Kazan" else "🔒 Seviye ${RankTier.RANKED_UNLOCK_LEVEL}'te Açılır",
                                fontSize = 10.sp,
                                color = if (userProfile.isRankedUnlocked) RankGold else ImmersiveCoral
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Exercise Selection
                Text(
                    text = "EGZERSİZ TÜRÜ",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = ImmersiveTextMuted
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ExerciseType.values().forEach { exercise ->
                        val isExSelected = selectedExercise == exercise
                        Surface(
                            shape = CircleShape,
                            color = if (isExSelected) ImmersivePrimary else ImmersiveSurface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isExSelected) ImmersivePrimary else ImmersiveOutline
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedExercise = exercise }
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = exercise.title,
                                    fontSize = 12.sp,
                                    fontWeight = if (isExSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isExSelected) ImmersiveOnPrimary else ImmersiveTextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Match Mode Selector
                Text(
                    text = "KAPIŞMA FORMATI",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = ImmersiveTextMuted
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    VsMatchMode.values().forEach { mode ->
                        val isModeSelected = selectedMode == mode
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isModeSelected) ImmersiveSurfaceVariant else ImmersiveSurface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isModeSelected) ImmersivePrimary else ImmersiveOutline
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedMode = mode }
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = mode.iconEmoji, fontSize = 16.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (mode.isTimed) "${mode.durationSeconds}s" else "${mode.targetReps} Tekrar",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isModeSelected) ImmersivePrimary else ImmersiveTextPrimary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Opponent List / Selection (If not ranked or choosing bot)
                Text(
                    text = if (isRankedSelected) "DÜELLO RAKİBİ (EŞLEŞTİRİLDİ)" else "RAKİP BOTU SEÇİN",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = ImmersiveTextMuted
                )
                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val listToShow = if (isRankedSelected) {
                        listOf(selectedOpponent)
                    } else {
                        VsOpponentPresets.allOpponents
                    }

                    items(listToShow) { opp ->
                        val isOppSelected = selectedOpponent.id == opp.id
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = if (isOppSelected) ImmersiveSurfaceVariant else ImmersiveSurface,
                            border = androidx.compose.foundation.BorderStroke(
                                if (isOppSelected) 2.dp else 1.dp,
                                if (isOppSelected) opp.rankTier.rankColor else ImmersiveOutline
                            ),
                            modifier = Modifier
                                .width(140.dp)
                                .clickable { selectedOpponent = opp }
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(opp.rankTier.rankColor.copy(alpha = 0.2f), shape = CircleShape)
                                        .border(1.5.dp, opp.rankTier.rankColor, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = opp.avatarEmoji, fontSize = 22.sp)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = opp.name,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ImmersiveTextPrimary,
                                    maxLines = 1
                                )
                                Text(
                                    text = "${opp.rankTier.iconEmoji} ${opp.rankTier.title.split(" ").first()}",
                                    fontSize = 10.sp,
                                    color = opp.rankTier.rankColor,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Surface(
                                    shape = CircleShape,
                                    color = ImmersiveBackground
                                ) {
                                    Text(
                                        text = "${opp.repsPerMinute.toInt()} tekrar/dk",
                                        fontSize = 9.sp,
                                        color = ImmersiveTextMuted,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Big Start VS Battle Button
                Button(
                    onClick = {
                        onStartBattle(
                            selectedExercise,
                            selectedMode,
                            selectedOpponent,
                            isRankedSelected
                        )
                    },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BattlePlayerColor,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("start_vs_battle_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "⚔️ VS BATTLE BAŞLAT",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}
