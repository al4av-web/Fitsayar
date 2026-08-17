package com.example.ui.screens

import android.Manifest
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.repository.UserProfile
import com.example.model.ExerciseType
import com.example.model.LeagueTier
import com.example.model.RankTier
import com.example.ui.camera.CameraPreviewWithPose
import com.example.ui.components.ExerciseHelpDialog
import com.example.ui.components.LeaderboardSheet
import com.example.ui.components.RankedInfoDialog
import com.example.ui.components.VsBattleHUD
import com.example.ui.components.VsBattleResultDialog
import com.example.ui.components.VsLobbyDialog
import com.example.ui.components.WorkoutHUD
import com.example.ui.components.WorkoutHistorySheet
import com.example.ui.components.WorkoutSummaryDialog
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
import com.example.ui.theme.PushUpAccent
import com.example.ui.theme.RankGold
import com.example.ui.theme.SitUpAccent
import com.example.ui.theme.SquatAccent
import com.example.ui.viewmodel.WorkoutSessionState
import com.example.ui.viewmodel.WorkoutViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.util.Locale

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: WorkoutViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val allWorkouts by viewModel.allWorkouts.collectAsStateWithLifecycle()
    val totalReps by viewModel.totalReps.collectAsStateWithLifecycle()
    val totalCalories by viewModel.totalCalories.collectAsStateWithLifecycle()
    val totalDurationSeconds by viewModel.totalDurationSeconds.collectAsStateWithLifecycle()

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    val isWorkoutActive = uiState.sessionState == WorkoutSessionState.ACTIVE ||
            uiState.sessionState == WorkoutSessionState.PAUSED ||
            uiState.sessionState == WorkoutSessionState.COUNTDOWN

    val userPoints = viewModel.getUserPoints()
    val userTier = LeagueTier.getTierForPoints(userPoints)
    val userRank = userProfile.rankTier
    val levelProgress = userProfile.levelProgress

    Box(modifier = modifier.fillMaxSize().background(ImmersiveBackground)) {
        if (isWorkoutActive && cameraPermissionState.status.isGranted) {
            // Live Camera & Pose Detection Screen
            Box(modifier = Modifier.fillMaxSize()) {
                CameraPreviewWithPose(
                    poseHelper = viewModel.poseHelper,
                    poseResult = uiState.poseResult,
                    lensFacing = uiState.lensFacing,
                    onSwitchCamera = { viewModel.toggleCamera() },
                    modifier = Modifier.fillMaxSize()
                )

                if (uiState.isVsMode) {
                    // VS BATTLE HUD (Screenshot Style)
                    VsBattleHUD(
                        userName = userProfile.name,
                        exercise = uiState.selectedExercise,
                        matchMode = uiState.vsMatchMode,
                        opponent = uiState.vsOpponent,
                        playerReps = uiState.vsPlayerReps,
                        opponentReps = uiState.vsOpponentReps,
                        remainingSeconds = uiState.vsRemainingSeconds,
                        countdownNumber = uiState.countdownNumber,
                        isCountdown = uiState.sessionState == WorkoutSessionState.COUNTDOWN,
                        onGiveUp = { viewModel.giveUpVsBattle() },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Solo Training HUD
                    WorkoutHUD(
                        uiState = uiState,
                        onPauseResume = {
                            if (uiState.sessionState == WorkoutSessionState.PAUSED) {
                                viewModel.resumeWorkout()
                            } else {
                                viewModel.pauseWorkout()
                            }
                        },
                        onFinishWorkout = { viewModel.finishWorkout() },
                        onManualAdd = { viewModel.manualAddRep() },
                        onManualMinus = { viewModel.manualMinusRep() },
                        onToggleSound = { viewModel.toggleSound() },
                        onToggleCamera = { viewModel.toggleCamera() },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        } else {
            // Dashboard / Home Screen with Immersive UI styling
            Scaffold(
                containerColor = ImmersiveBackground,
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = ImmersiveBackground),
                        title = {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${userRank.iconEmoji} ${userRank.title.split(" ").first()}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.5.sp,
                                        color = userRank.rankColor
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
                                    text = "FitSayar AI",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ImmersiveTextPrimary
                                )
                            }
                        },
                        actions = {
                            // Ranked Info Button
                            IconButton(
                                onClick = { viewModel.setShowRankedInfo(true) },
                                modifier = Modifier.testTag("ranked_info_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Rank Sistemi",
                                    tint = userRank.rankColor
                                )
                            }
                            // Leaderboard / Leagues Trophy Button
                            IconButton(
                                onClick = { viewModel.setShowLeaderboardSheet(true) },
                                modifier = Modifier.testTag("leaderboard_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = "Skor Tablosu & Ligler",
                                    tint = RankGold
                                )
                            }
                            IconButton(
                                onClick = { viewModel.setShowHelpDialog(true) },
                                modifier = Modifier.testTag("help_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.HelpOutline,
                                    contentDescription = "Nasıl Kullanılır?",
                                    tint = ImmersiveTextSecondary
                                )
                            }
                            IconButton(
                                onClick = { viewModel.setShowHistorySheet(true) },
                                modifier = Modifier.testTag("history_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = "Geçmiş",
                                    tint = ImmersivePrimary
                                )
                            }
                        }
                    )
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // Level & XP Progress Card
                    UserLevelCard(
                        userProfile = userProfile,
                        onOpenRankedInfo = { viewModel.setShowRankedInfo(true) }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 1v1 VS BATTLE Entry Card (High Impact Banner)
                    VsBattleEntryCard(
                        userProfile = userProfile,
                        onOpenVsLobby = {
                            if (cameraPermissionState.status.isGranted) {
                                viewModel.setShowVsLobby(true)
                            } else {
                                cameraPermissionState.launchPermissionRequest()
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Immersive Hero Camera Banner
                    ImmersiveHeroBanner(
                        isPermissionGranted = cameraPermissionState.status.isGranted,
                        onRequestPermission = { cameraPermissionState.launchPermissionRequest() }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 3-Column Immersive Quick Stats
                    ImmersiveQuickStats(
                        totalReps = totalReps ?: 0,
                        totalCalories = totalCalories ?: 0.0,
                        totalDuration = totalDurationSeconds ?: 0L
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Competitive League & Leaderboard Preview Card
                    LeaguePreviewCard(
                        userName = userProfile.name,
                        userTier = userTier,
                        userPoints = userPoints,
                        onOpenLeaderboard = { viewModel.setShowLeaderboardSheet(true) }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Section Title: Solo Training
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Tek Başına Antrenman",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = ImmersiveTextPrimary
                        )

                        Surface(
                            shape = CircleShape,
                            color = ImmersiveSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, ImmersiveOutline)
                        ) {
                            Text(
                                text = "1 TEKRAR = 1 XP",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = ImmersivePrimary,
                                letterSpacing = 1.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 3 Main Exercise Cards (Şınav, Mekik, Squat)
                    ExerciseType.values().forEach { exercise ->
                        ImmersiveExerciseCard(
                            exercise = exercise,
                            isSelected = uiState.selectedExercise == exercise,
                            targetGoal = if (uiState.selectedExercise == exercise) uiState.targetGoal else exercise.defaultGoal,
                            onSelect = { viewModel.selectExercise(exercise) },
                            onGoalChange = { goal ->
                                viewModel.selectExercise(exercise)
                                viewModel.setTargetGoal(goal)
                            },
                            onStart = {
                                if (cameraPermissionState.status.isGranted) {
                                    viewModel.selectExercise(exercise)
                                    viewModel.startWorkoutWithCountdown()
                                } else {
                                    cameraPermissionState.launchPermissionRequest()
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        // VS Lobby Dialog
        if (uiState.showVsLobbySheet) {
            VsLobbyDialog(
                userProfile = userProfile,
                onStartBattle = { exercise, mode, opponent, isRanked ->
                    viewModel.startVsBattle(exercise, mode, opponent, isRanked)
                },
                onOpenRankedInfo = {
                    viewModel.setShowVsLobby(false)
                    viewModel.setShowRankedInfo(true)
                },
                onDismiss = { viewModel.setShowVsLobby(false) }
            )
        }

        // VS Battle Result Dialog
        if (uiState.vsShowResultDialog && uiState.vsWinner != null) {
            VsBattleResultDialog(
                winner = uiState.vsWinner!!,
                exercise = uiState.selectedExercise,
                opponent = uiState.vsOpponent,
                playerReps = uiState.vsPlayerReps,
                opponentReps = uiState.vsOpponentReps,
                earnedXp = uiState.vsEarnedXp,
                earnedRp = uiState.vsEarnedRp,
                currentRankTier = userRank,
                currentLevelInfo = levelProgress,
                onRematch = { viewModel.rematchVsBattle() },
                onDismiss = { viewModel.dismissVsResultDialog() }
            )
        }

        // Ranked Info Dialog
        if (uiState.showRankedInfoDialog) {
            RankedInfoDialog(
                userRp = userProfile.rankedRp,
                userLevel = levelProgress.level,
                onDismiss = { viewModel.setShowRankedInfo(false) }
            )
        }

        // Leaderboard & League Bottom Sheet
        if (uiState.showLeaderboardSheet) {
            val competitors = viewModel.getCompetitorsForTier(uiState.selectedLeagueTier)
            LeaderboardSheet(
                userName = userProfile.name,
                userReps = totalReps ?: 0,
                userPoints = userPoints,
                selectedTier = uiState.selectedLeagueTier,
                competitors = competitors,
                onSelectTier = { viewModel.selectLeagueTier(it) },
                onUpdateUserName = { viewModel.updateUserName(it) },
                onDismiss = { viewModel.setShowLeaderboardSheet(false) }
            )
        }

        // Summary Dialog on Workout Complete
        if (uiState.showSummaryDialog && uiState.lastCompletedRecord != null) {
            WorkoutSummaryDialog(
                record = uiState.lastCompletedRecord!!,
                onDismiss = { viewModel.dismissSummaryDialog() }
            )
        }

        // History Bottom Sheet
        if (uiState.showHistorySheet) {
            WorkoutHistorySheet(
                workouts = allWorkouts,
                onDismiss = { viewModel.setShowHistorySheet(false) },
                onDeleteRecord = { viewModel.deleteWorkoutRecord(it) },
                onClearAll = { viewModel.clearAllHistory() }
            )
        }

        // Help Dialog
        if (uiState.showHelpDialog) {
            ExerciseHelpDialog(
                selectedExercise = uiState.selectedExercise,
                onDismiss = { viewModel.setShowHelpDialog(false) }
            )
        }
    }
}

@Composable
fun UserLevelCard(
    userProfile: UserProfile,
    onOpenRankedInfo: () -> Unit
) {
    val levelProgress = userProfile.levelProgress
    val rankTier = userProfile.rankTier

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, rankTier.rankColor.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenRankedInfo() }
            .testTag("user_level_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(rankTier.rankColor.copy(alpha = 0.15f), shape = CircleShape)
                            .border(1.5.dp, rankTier.rankColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = rankTier.iconEmoji, fontSize = 22.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Seviye ${levelProgress.level}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = ImmersiveTextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = CircleShape,
                                color = rankTier.rankColor.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = rankTier.title.split(" ").first().uppercase(Locale.getDefault()),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = rankTier.rankColor,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "${userProfile.rankedRp} RP • Toplam ${userProfile.totalXp} XP",
                            fontSize = 12.sp,
                            color = ImmersiveTextSecondary
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${(levelProgress.progressFraction * 100).toInt()}%",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = ImmersivePrimary
                        )
                        Text(
                            text = "${levelProgress.currentLevelXp}/${levelProgress.xpRequiredForNextLevel} XP",
                            fontSize = 10.sp,
                            color = ImmersiveTextMuted
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = ImmersiveTextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { levelProgress.progressFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = ImmersivePrimary,
                trackColor = ImmersiveSurfaceVariant,
                strokeCap = StrokeCap.Round
            )
        }
    }
}

@Composable
fun VsBattleEntryCard(
    userProfile: UserProfile,
    onOpenVsLobby: () -> Unit
) {
    val rankTier = userProfile.rankTier

    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = ImmersiveSurfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, BattlePlayerColor.copy(alpha = 0.6f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenVsLobby() }
            .testTag("vs_battle_entry_card")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(BattlePlayerColor.copy(alpha = 0.2f), shape = CircleShape)
                            .border(1.5.dp, BattlePlayerColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SportsKabaddi,
                            contentDescription = null,
                            tint = BattlePlayerColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "1v1 VS BATTLE",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp,
                                color = ImmersiveTextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = CircleShape,
                                color = BattlePlayerColor
                            ) {
                                Text(
                                    text = "CANLI",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.Black,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Seçtiğin botlarla veya ranked'da kapış!",
                            fontSize = 12.sp,
                            color = ImmersiveTextSecondary
                        )
                    }
                }

                Text(
                    text = "${userProfile.vsWins}G / ${userProfile.vsLosses}M",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BattlePlayerColor
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick launch button
            Button(
                onClick = onOpenVsLobby,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BattlePlayerColor,
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "DÜELLO BAŞLAT",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun LeaguePreviewCard(
    userName: String,
    userTier: LeagueTier,
    userPoints: Int,
    onOpenLeaderboard: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, userTier.primaryColor.copy(alpha = 0.6f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenLeaderboard() }
            .testTag("league_preview_card")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(userTier.primaryColor.copy(alpha = 0.15f), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = userTier.iconEmoji, fontSize = 24.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = userTier.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ImmersiveTextPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = CircleShape,
                            color = userTier.primaryColor.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "LİGİNİZ",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = userTier.primaryColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = "$userName • $userPoints XP • Sıralamada Yarışın",
                        fontSize = 12.sp,
                        color = ImmersiveTextSecondary
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = userTier.primaryColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Skor Tablosu",
                    tint = ImmersiveTextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun ImmersiveHeroBanner(
    isPermissionGranted: Boolean,
    onRequestPermission: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black),
        border = androidx.compose.foundation.BorderStroke(1.dp, ImmersiveOutline),
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .testTag("immersive_hero_banner")
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.workout_hero_banner),
                contentDescription = "Workout Hero",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.4f),
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = ImmersivePrimaryContainer.copy(alpha = 0.8f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ImmersivePrimary.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "AI POSE DETECTION",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ImmersivePrimary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            letterSpacing = 1.2.sp
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(if (isPermissionGranted) ImmersiveGreen else ImmersiveCoral, shape = CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isPermissionGranted) "KAMERA HAZIR" else "İZİN BEKLENİYOR",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isPermissionGranted) ImmersiveGreen else ImmersiveCoral,
                            letterSpacing = 0.8.sp
                        )
                    }
                }

                Column {
                    Text(
                        text = "Otomatik Tekrar & Form Analizi",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = ImmersiveTextPrimary
                    )
                    Text(
                        text = "Kamera karşısına geçin, yapay zeka şınav, mekik ve squatlarınızı saysın.",
                        fontSize = 12.sp,
                        color = ImmersiveTextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ImmersiveQuickStats(
    totalReps: Int,
    totalCalories: Double,
    totalDuration: Long
) {
    val minutes = totalDuration / 60

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ImmersiveStatBox(
            icon = Icons.Default.FitnessCenter,
            value = "$totalReps",
            label = "Tekrar",
            modifier = Modifier.weight(1f)
        )
        ImmersiveStatBox(
            icon = Icons.Default.LocalFireDepartment,
            value = String.format(Locale.getDefault(), "%.0f", totalCalories),
            label = "Kalori",
            modifier = Modifier.weight(1f)
        )
        ImmersiveStatBox(
            icon = Icons.Default.Timer,
            value = "${minutes}m",
            label = "Süre",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ImmersiveStatBox(
    icon: ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = ImmersiveSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, ImmersiveOutline),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = ImmersivePrimary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = ImmersiveTextPrimary
            )
            Text(
                text = label.uppercase(Locale.getDefault()),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp,
                color = ImmersiveTextSecondary
            )
        }
    }
}

@Composable
fun ImmersiveExerciseCard(
    exercise: ExerciseType,
    isSelected: Boolean,
    targetGoal: Int,
    onSelect: () -> Unit,
    onGoalChange: (Int) -> Unit,
    onStart: () -> Unit
) {
    val accentColor = when (exercise) {
        ExerciseType.PUSH_UP -> PushUpAccent
        ExerciseType.SQUAT -> SquatAccent
        ExerciseType.SIT_UP -> SitUpAccent
    }

    val iconVector = when (exercise) {
        ExerciseType.PUSH_UP -> Icons.Default.FitnessCenter
        ExerciseType.SQUAT -> Icons.Default.AccessibilityNew
        ExerciseType.SIT_UP -> Icons.Default.SelfImprovement
    }

    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) ImmersiveSurfaceVariant else ImmersiveSurface
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) accentColor else ImmersiveOutline
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("exercise_card_${exercise.name}")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(ImmersivePrimaryContainer.copy(alpha = 0.6f), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = iconVector,
                            contentDescription = exercise.title,
                            tint = accentColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = exercise.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = ImmersiveTextPrimary
                        )
                        Text(
                            text = String.format(Locale.getDefault(), "%.2f kcal/tekrar", exercise.caloriesPerRep),
                            fontSize = 12.sp,
                            color = accentColor
                        )
                    }
                }

                if (isSelected) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = ImmersivePrimaryContainer,
                        border = androidx.compose.foundation.BorderStroke(1.dp, ImmersivePrimary.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = "SEÇİLDİ",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = ImmersivePrimary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = exercise.description,
                fontSize = 13.sp,
                color = ImmersiveTextSecondary,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Goal Pills
            Text(
                text = "HEDEF TEKRAR",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = ImmersiveTextMuted
            )

            Spacer(modifier = Modifier.height(6.dp))

            val goals = listOf(10, 15, 20, 30, 50)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                goals.forEach { goalOption ->
                    val isGoalActive = targetGoal == goalOption
                    Surface(
                        shape = CircleShape,
                        color = if (isGoalActive) ImmersivePrimary else ImmersiveBackground,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isGoalActive) ImmersivePrimary else ImmersiveOutline
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onGoalChange(goalOption) }
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$goalOption",
                                fontSize = 12.sp,
                                fontWeight = if (isGoalActive) FontWeight.Bold else FontWeight.Normal,
                                color = if (isGoalActive) ImmersiveOnPrimary else ImmersiveTextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Start Workout Button in Pill Shape with Immersive styling
            Button(
                onClick = onStart,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ImmersivePrimary,
                    contentColor = ImmersiveOnPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("start_workout_button_${exercise.name}")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Başlat",
                        tint = ImmersiveOnPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${exercise.title} Başlat",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = ImmersiveOnPrimary
                    )
                }
            }
        }
    }
}
