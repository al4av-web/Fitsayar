package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ExerciseType
import com.example.model.VsMatchMode
import com.example.model.VsOpponent
import com.example.ui.theme.BattleOpponentColor
import com.example.ui.theme.BattlePlayerColor
import com.example.ui.theme.ImmersiveBackground
import com.example.ui.theme.ImmersiveCoral
import com.example.ui.theme.ImmersiveGreen
import com.example.ui.theme.ImmersiveOutline
import com.example.ui.theme.ImmersivePrimary
import com.example.ui.theme.ImmersiveSurface
import com.example.ui.theme.ImmersiveSurfaceVariant
import com.example.ui.theme.ImmersiveTextMuted
import com.example.ui.theme.ImmersiveTextPrimary
import com.example.ui.theme.ImmersiveTextSecondary
import java.util.Locale

@Composable
fun VsBattleHUD(
    userName: String,
    exercise: ExerciseType,
    matchMode: VsMatchMode,
    opponent: VsOpponent,
    playerReps: Int,
    opponentReps: Int,
    remainingSeconds: Int,
    countdownNumber: Int,
    isCountdown: Boolean,
    onGiveUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Tug of War progress calculation
    val totalRepsCombined = (playerReps + opponentReps).coerceAtLeast(0)
    val playerFraction = if (totalRepsCombined == 0) {
        0.5f
    } else {
        (playerReps.toFloat() / totalRepsCombined.toFloat()).coerceIn(0.08f, 0.92f)
    }

    val animatedFraction by animateFloatAsState(
        targetValue = playerFraction,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "battle_progress"
    )

    val leadDifference = playerReps - opponentReps
    val statusText = when {
        leadDifference > 2 -> "🔥 Öndesin! Farkı Koru!"
        leadDifference > 0 -> "⚡ +$leadDifference Öndesin!"
        leadDifference == 0 -> "⚔️ Başa Baş Gidiyor!"
        leadDifference >= -2 -> "⚠️ -$leadDifference Geridesin! Hızlan!"
        else -> "🚨 Pes Etme! Yakalayabilirsin!"
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("vs_battle_hud")
    ) {
        // Countdown Overlay
        AnimatedVisibility(
            visible = isCountdown,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Surface(
                shape = CircleShape,
                color = ImmersiveSurface.copy(alpha = 0.92f),
                border = androidx.compose.foundation.BorderStroke(2.dp, ImmersivePrimary),
                modifier = Modifier.size(150.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (countdownNumber > 0) "$countdownNumber" else "VS!",
                            fontSize = 64.sp,
                            fontWeight = FontWeight.Black,
                            color = ImmersivePrimary
                        )
                        Text(
                            text = "HAZIRLAN",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            color = ImmersiveTextSecondary
                        )
                    }
                }
            }
        }

        // Top Section: VS Header & Player vs Opponent Battle Bar (Screenshot Style)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Top Match Title
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "VS ${opponent.name.uppercase(Locale.getDefault())}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp,
                    color = ImmersiveTextPrimary
                )
                Text(
                    text = "${exercise.title.uppercase(Locale.getDefault())} BATTLE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = BattlePlayerColor
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Player vs Opponent HUD Card (Exact match to screenshot)
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = ImmersiveSurface.copy(alpha = 0.92f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, ImmersiveOutline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Player Side (Left)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(BattlePlayerColor.copy(alpha = 0.2f), shape = CircleShape)
                                    .border(2.dp, BattlePlayerColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "⭐", fontSize = 22.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = userName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ImmersiveTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "SEN",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = BattlePlayerColor
                                )
                            }
                        }

                        // Player Live Reps
                        Text(
                            text = "$playerReps",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = BattlePlayerColor,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        // Center: Match Time / Target
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            Text(
                                text = if (matchMode.isTimed) "TIME" else "GOAL",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = ImmersiveTextMuted
                            )
                            Text(
                                text = if (matchMode.isTimed) "${remainingSeconds}S" else "${matchMode.targetReps}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = ImmersiveTextPrimary
                            )
                        }

                        // Opponent Live Reps
                        Text(
                            text = "$opponentReps",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = BattleOpponentColor,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        // Opponent Side (Right)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = opponent.name,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ImmersiveTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${opponent.rankTier.iconEmoji} ${opponent.rankTier.title.split(" ").first()}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = opponent.rankTier.rankColor
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(BattleOpponentColor.copy(alpha = 0.2f), shape = CircleShape)
                                    .border(2.dp, BattleOpponentColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = opponent.avatarEmoji, fontSize = 22.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Split Progress Bar (Green vs Magenta Tug-of-war)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp)
                            .clip(RoundedCornerShape(7.dp))
                            .background(ImmersiveSurfaceVariant)
                    ) {
                        Row(modifier = Modifier.fillMaxSize()) {
                            // Player bar
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(animatedFraction)
                                    .background(BattlePlayerColor)
                            )
                            // Divider line
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(2.dp)
                                    .background(Color.White)
                            )
                            // Opponent bar
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight((1f - animatedFraction).coerceAtLeast(0.01f))
                                    .background(BattleOpponentColor)
                            )
                        }
                    }
                }
            }
        }

        // Center Area: Opponent Quote & Big Rep Counter
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Opponent quote banner (like in the screenshot: "Bro thought I'd give up 💀")
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = ImmersiveSurface.copy(alpha = 0.85f),
                border = androidx.compose.foundation.BorderStroke(1.dp, ImmersiveOutline),
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "\"${opponent.quote}\"",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ImmersiveTextPrimary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Big Center Reps display
            Text(
                text = "REPS",
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 3.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
            Text(
                text = "$playerReps",
                fontSize = 88.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )

            // Dynamic Lead status pill
            Surface(
                shape = CircleShape,
                color = if (leadDifference >= 0) BattlePlayerColor.copy(alpha = 0.2f) else BattleOpponentColor.copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (leadDifference >= 0) BattlePlayerColor else BattleOpponentColor
                )
            ) {
                Text(
                    text = statusText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (leadDifference >= 0) BattlePlayerColor else BattleOpponentColor,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp)
                )
            }
        }

        // Bottom Bar: Give Up button & elapsed time
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onGiveUp,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE53935),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("give_up_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Flag,
                        contentDescription = "Give Up",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Give Up / Maçı Bitir",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = null,
                    tint = ImmersiveTextMuted,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                val minutes = remainingSeconds / 60
                val seconds = remainingSeconds % 60
                Text(
                    text = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds),
                    fontSize = 13.sp,
                    color = ImmersiveTextMuted
                )
            }
        }
    }
}
