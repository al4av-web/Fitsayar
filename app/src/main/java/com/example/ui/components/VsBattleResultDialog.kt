package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.ExerciseType
import com.example.model.LevelProgress
import com.example.model.RankTier
import com.example.model.VsOpponent
import com.example.model.VsWinner
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

@Composable
fun VsBattleResultDialog(
    winner: VsWinner,
    exercise: ExerciseType,
    opponent: VsOpponent,
    playerReps: Int,
    opponentReps: Int,
    earnedXp: Int,
    earnedRp: Int,
    currentRankTier: RankTier,
    currentLevelInfo: LevelProgress,
    onRematch: () -> Unit,
    onDismiss: () -> Unit
) {
    val isWin = winner == VsWinner.PLAYER
    val isDraw = winner == VsWinner.DRAW

    val titleText = when {
        isWin -> "🏆 ZAFER!"
        isDraw -> "🤝 BERABERE!"
        else -> "💀 MAĞLUBİYET"
    }

    val titleColor = when {
        isWin -> BattlePlayerColor
        isDraw -> ImmersivePrimary
        else -> ImmersiveCoral
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
            border = androidx.compose.foundation.BorderStroke(2.dp, titleColor),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .testTag("vs_result_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Large Result Badge
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(titleColor.copy(alpha = 0.2f), shape = CircleShape)
                        .border(2.dp, titleColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isWin) "👑" else if (isDraw) "🤝" else "💔",
                        fontSize = 36.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = titleText,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = titleColor
                )

                Text(
                    text = if (isWin) "Harika bir performans sergiledin!" else "Bir dahaki sefere daha hızlı ol!",
                    fontSize = 13.sp,
                    color = ImmersiveTextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Score comparison banner
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = ImmersiveSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ImmersiveOutline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Player Score
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "SEN",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BattlePlayerColor
                            )
                            Text(
                                text = "$playerReps",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = BattlePlayerColor
                            )
                            Text(
                                text = "Tekrar",
                                fontSize = 10.sp,
                                color = ImmersiveTextMuted
                            )
                        }

                        Text(
                            text = "VS",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = ImmersiveTextMuted
                        )

                        // Opponent Score
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = opponent.name,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BattleOpponentColor
                            )
                            Text(
                                text = "$opponentReps",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = BattleOpponentColor
                            )
                            Text(
                                text = "Tekrar",
                                fontSize = 10.sp,
                                color = ImmersiveTextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Rewards breakdown (XP + RP)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // XP Reward Card
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = ImmersivePrimaryContainer.copy(alpha = 0.4f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ImmersivePrimary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = ImmersivePrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "+$earnedXp XP",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = ImmersivePrimary
                                )
                            }
                            Text(
                                text = "Seviye ${currentLevelInfo.level}",
                                fontSize = 11.sp,
                                color = ImmersiveTextSecondary
                            )
                        }
                    }

                    // RP Reward Card
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (earnedRp >= 0) ImmersiveGreen.copy(alpha = 0.15f) else ImmersiveCoral.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (earnedRp >= 0) ImmersiveGreen else ImmersiveCoral
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (earnedRp >= 0) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                                    contentDescription = null,
                                    tint = if (earnedRp >= 0) ImmersiveGreen else ImmersiveCoral,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (earnedRp >= 0) "+$earnedRp RP" else "$earnedRp RP",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (earnedRp >= 0) ImmersiveGreen else ImmersiveCoral
                                )
                            }
                            Text(
                                text = currentRankTier.title.split(" ").first(),
                                fontSize = 11.sp,
                                color = currentRankTier.rankColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = CircleShape,
                        border = androidx.compose.foundation.BorderStroke(1.dp, ImmersiveOutline),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("vs_result_close_button")
                    ) {
                        Text(text = "Lobiye Dön", color = ImmersiveTextPrimary)
                    }

                    Button(
                        onClick = onRematch,
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ImmersivePrimary,
                            contentColor = ImmersiveOnPrimary
                        ),
                        modifier = Modifier
                            .weight(1.2f)
                            .height(48.dp)
                            .testTag("vs_rematch_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Rövanş Yap", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
