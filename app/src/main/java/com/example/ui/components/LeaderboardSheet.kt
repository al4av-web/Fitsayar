package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Competitor
import com.example.model.LeagueTier
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardSheet(
    userName: String,
    userReps: Int,
    userPoints: Int,
    selectedTier: LeagueTier,
    competitors: List<Competitor>,
    onSelectTier: (LeagueTier) -> Unit,
    onUpdateUserName: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val userTier = LeagueTier.getTierForPoints(userPoints)
    var showEditNameDialog by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(userName) }

    val userCompetitor = competitors.find { it.isCurrentUser }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = ImmersiveBackground,
        modifier = Modifier.testTag("leaderboard_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            // Header: Title & Weekly Season Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(ImmersivePrimaryContainer, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Ligler",
                            tint = ImmersivePrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "LİGLER & SIRALAMA",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            color = ImmersivePrimary
                        )
                        Text(
                            text = "Haftalık Şampiyona",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = ImmersiveTextPrimary
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ImmersiveSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ImmersiveOutline)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = ImmersiveCoral,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "3G 14S",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ImmersiveTextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // User Current League Status Card
            UserLeagueStatusCard(
                userName = userName,
                userPoints = userPoints,
                userReps = userReps,
                userTier = userTier,
                userRank = userCompetitor?.rank ?: 1,
                onEditNameClick = { showEditNameDialog = true }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // League Tier Selector Tabs (Horizontal Scrollable)
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(LeagueTier.values()) { tier ->
                    val isSelected = selectedTier == tier
                    val isMyTier = userTier == tier

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) tier.primaryColor.copy(alpha = 0.25f) else ImmersiveSurface,
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) tier.primaryColor else ImmersiveOutline
                        ),
                        modifier = Modifier
                            .clickable { onSelectTier(tier) }
                            .testTag("tier_tab_${tier.name}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = tier.iconEmoji, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = tier.title,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) tier.primaryColor else ImmersiveTextPrimary
                                    )
                                    if (isMyTier) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .background(ImmersiveGreen, shape = CircleShape)
                                        )
                                    }
                                }
                                Text(
                                    text = "${tier.minPoints}+ XP",
                                    fontSize = 10.sp,
                                    color = ImmersiveTextMuted
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Podium for Top 3
            if (competitors.size >= 3) {
                Top3PodiumView(
                    top3 = competitors.take(3),
                    tierColor = selectedTier.primaryColor
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Competitor List
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = null,
                                tint = ImmersiveGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "İlk 3 Üst Lige Yükselir",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ImmersiveGreen
                            )
                        }

                        Text(
                            text = "${competitors.size} Yarışmacı",
                            fontSize = 11.sp,
                            color = ImmersiveTextMuted
                        )
                    }
                }

                items(competitors, key = { it.id }) { competitor ->
                    CompetitorRow(
                        competitor = competitor,
                        tier = selectedTier
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    // Edit Name Dialog
    if (showEditNameDialog) {
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            containerColor = ImmersiveSurface,
            title = {
                Text(
                    text = "Sporcu Adınızı Belirleyin",
                    color = ImmersiveTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Liglerde ve skor tablosunda görünecek adınız:",
                        color = ImmersiveTextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = editedName,
                        onValueChange = { if (it.length <= 20) editedName = it },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ImmersivePrimary,
                            unfocusedBorderColor = ImmersiveOutline,
                            focusedTextColor = ImmersiveTextPrimary,
                            unfocusedTextColor = ImmersiveTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("user_name_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editedName.isNotBlank()) {
                            onUpdateUserName(editedName.trim())
                        }
                        showEditNameDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ImmersivePrimary,
                        contentColor = ImmersiveOnPrimary
                    ),
                    shape = CircleShape,
                    modifier = Modifier.testTag("save_user_name_button")
                ) {
                    Text(text = "Kaydet", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditNameDialog = false }) {
                    Text(text = "İptal", color = ImmersiveTextMuted)
                }
            }
        )
    }
}

@Composable
fun UserLeagueStatusCard(
    userName: String,
    userPoints: Int,
    userReps: Int,
    userTier: LeagueTier,
    userRank: Int,
    onEditNameClick: () -> Unit
) {
    val nextTier = when (userTier) {
        LeagueTier.WOOD -> LeagueTier.BRONZE
        LeagueTier.BRONZE -> LeagueTier.SILVER
        LeagueTier.SILVER -> LeagueTier.GOLD
        LeagueTier.GOLD -> LeagueTier.PLATINUM
        LeagueTier.PLATINUM -> LeagueTier.DIAMOND
        LeagueTier.DIAMOND -> LeagueTier.CHAMPION
        LeagueTier.CHAMPION -> LeagueTier.TITAN
        LeagueTier.TITAN -> LeagueTier.OLYMPIAN
        LeagueTier.OLYMPIAN -> null
    }

    val progress = if (nextTier != null) {
        val tierSpan = nextTier.minPoints - userTier.minPoints
        val currentProgress = (userPoints - userTier.minPoints).coerceAtLeast(0)
        (currentProgress.toFloat() / tierSpan.toFloat()).coerceIn(0f, 1f)
    } else 1f

    val pointsNeeded = if (nextTier != null) {
        (nextTier.minPoints - userPoints).coerceAtLeast(0)
    } else 0

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, userTier.primaryColor.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = userTier.iconEmoji,
                        fontSize = 32.sp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = userName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = ImmersiveTextPrimary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            IconButton(
                                onClick = onEditNameClick,
                                modifier = Modifier.size(24.dp).testTag("edit_name_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Adı Değiştir",
                                    tint = ImmersivePrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        Text(
                            text = "${userTier.title} • $userPoints XP ($userReps Tekrar)",
                            fontSize = 12.sp,
                            color = userTier.primaryColor,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ImmersiveSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ImmersiveOutline)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "#$userRank",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = ImmersivePrimary
                        )
                        Text(
                            text = "SIRALAMA",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = ImmersiveTextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Bar to next tier
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (nextTier != null) "${nextTier.title}'e $pointsNeeded XP kaldı" else "Maksimum Ligtasınız!",
                    fontSize = 11.sp,
                    color = ImmersiveTextSecondary
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = userTier.primaryColor
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = userTier.primaryColor,
                trackColor = ImmersiveSurfaceVariant,
                strokeCap = StrokeCap.Round
            )
        }
    }
}

@Composable
fun Top3PodiumView(
    top3: List<Competitor>,
    tierColor: Color
) {
    val first = top3.getOrNull(0) ?: return
    val second = top3.getOrNull(1)
    val third = top3.getOrNull(2)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        // 2nd Place (Left)
        if (second != null) {
            PodiumStep(
                competitor = second,
                rank = 2,
                podiumHeight = 70.dp,
                crownColor = Color(0xFFC0C0C0),
                modifier = Modifier.weight(1f)
            )
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }

        // 1st Place (Center - Highest)
        PodiumStep(
            competitor = first,
            rank = 1,
            podiumHeight = 94.dp,
            crownColor = Color(0xFFFFD700),
            modifier = Modifier.weight(1.15f)
        )

        // 3rd Place (Right)
        if (third != null) {
            PodiumStep(
                competitor = third,
                rank = 3,
                podiumHeight = 56.dp,
                crownColor = Color(0xFFCD7F32),
                modifier = Modifier.weight(1f)
            )
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun PodiumStep(
    competitor: Competitor,
    rank: Int,
    podiumHeight: androidx.compose.ui.unit.Dp,
    crownColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar + Crown
        Box(contentAlignment = Alignment.TopCenter) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(
                        if (competitor.isCurrentUser) ImmersivePrimaryContainer else ImmersiveSurfaceVariant,
                        shape = CircleShape
                    )
                    .border(
                        width = 2.dp,
                        color = if (competitor.isCurrentUser) ImmersivePrimary else crownColor,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = competitor.avatarEmoji, fontSize = 22.sp)
            }

            // Rank Badge Pill
            Surface(
                shape = CircleShape,
                color = crownColor,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .size(18.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "$rank",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = competitor.name,
            fontSize = 12.sp,
            fontWeight = if (competitor.isCurrentUser) FontWeight.Black else FontWeight.Bold,
            color = if (competitor.isCurrentUser) ImmersivePrimary else ImmersiveTextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )

        Text(
            text = "${competitor.points} XP",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = crownColor
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Podium Block
        Surface(
            shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp),
            color = ImmersiveSurfaceVariant.copy(alpha = 0.8f),
            border = androidx.compose.foundation.BorderStroke(1.dp, crownColor.copy(alpha = 0.4f)),
            modifier = Modifier
                .fillMaxWidth()
                .height(podiumHeight)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (rank) {
                        1 -> "🥇 1."
                        2 -> "🥈 2."
                        else -> "🥉 3."
                    },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = crownColor
                )
            }
        }
    }
}

@Composable
fun CompetitorRow(
    competitor: Competitor,
    tier: LeagueTier
) {
    val isPromoted = competitor.rank <= 3
    val isDemoted = competitor.rank >= 6

    val rowBg = if (competitor.isCurrentUser) {
        ImmersivePrimaryContainer.copy(alpha = 0.4f)
    } else {
        ImmersiveSurface
    }

    val rowBorder = if (competitor.isCurrentUser) {
        androidx.compose.foundation.BorderStroke(1.5.dp, ImmersivePrimary)
    } else if (isPromoted) {
        androidx.compose.foundation.BorderStroke(1.dp, ImmersiveGreen.copy(alpha = 0.4f))
    } else {
        androidx.compose.foundation.BorderStroke(1.dp, ImmersiveOutline)
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = rowBg),
        border = rowBorder,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("competitor_row_${competitor.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Rank number
                Box(
                    modifier = Modifier.width(28.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "#${competitor.rank}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (competitor.rank) {
                            1 -> Color(0xFFFFD700)
                            2 -> Color(0xFFC0C0C0)
                            3 -> Color(0xFFCD7F32)
                            else -> if (competitor.isCurrentUser) ImmersivePrimary else ImmersiveTextMuted
                        }
                    )
                }

                // Avatar
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(ImmersiveSurfaceVariant, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = competitor.avatarEmoji, fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (competitor.isCurrentUser) "${competitor.name} (Sen)" else competitor.name,
                            fontSize = 14.sp,
                            fontWeight = if (competitor.isCurrentUser) FontWeight.Black else FontWeight.SemiBold,
                            color = if (competitor.isCurrentUser) ImmersivePrimary else ImmersiveTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (competitor.isCurrentUser) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Surface(
                                shape = CircleShape,
                                color = ImmersivePrimary
                            ) {
                                Text(
                                    text = "SEN",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = ImmersiveOnPrimary,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "${competitor.reps} Tekrar",
                            fontSize = 11.sp,
                            color = ImmersiveTextMuted
                        )
                        Text(
                            text = "•",
                            fontSize = 11.sp,
                            color = ImmersiveTextMuted
                        )
                        Text(
                            text = "${competitor.streakDays} Gün Seri",
                            fontSize = 11.sp,
                            color = ImmersiveCoral
                        )
                    }
                }
            }

            // Points Badge
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = ImmersiveSurfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, ImmersiveOutline)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = if (competitor.isCurrentUser) ImmersivePrimary else Color(0xFFFFD700),
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${competitor.points} XP",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = ImmersiveTextPrimary
                    )
                }
            }
        }
    }
}
