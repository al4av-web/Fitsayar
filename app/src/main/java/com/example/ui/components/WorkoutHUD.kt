package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ExerciseStage
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
import com.example.ui.viewmodel.WorkoutSessionState
import com.example.ui.viewmodel.WorkoutUiState
import java.util.Locale

@Composable
fun WorkoutHUD(
    uiState: WorkoutUiState,
    onPauseResume: () -> Unit,
    onFinishWorkout: () -> Unit,
    onManualAdd: () -> Unit,
    onManualMinus: () -> Unit,
    onToggleSound: () -> Unit,
    onToggleCamera: () -> Unit,
    modifier: Modifier = Modifier
) {
    val minutes = uiState.elapsedSeconds / 60
    val seconds = uiState.elapsedSeconds % 60
    val timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

    val progress = (uiState.currentReps.toFloat() / uiState.targetGoal.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "progress"
    )

    val scaleAnim = remember { Animatable(1f) }
    LaunchedEffect(uiState.currentReps) {
        if (uiState.currentReps > 0) {
            scaleAnim.animateTo(1.25f, animationSpec = tween(100))
            scaleAnim.animateTo(1f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("workout_hud")
    ) {
        // Countdown Overlay
        if (uiState.sessionState == WorkoutSessionState.COUNTDOWN) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.82f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "HAZIRLANIN",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ImmersivePrimary,
                        letterSpacing = 4.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    AnimatedContent(
                        targetState = uiState.countdownNumber,
                        transitionSpec = {
                            (scaleIn(initialScale = 0.5f) + fadeIn()) togetherWith (scaleOut(targetScale = 1.5f) + fadeOut())
                        },
                        label = "countdown"
                    ) { count ->
                        Text(
                            text = "$count",
                            fontSize = 110.sp,
                            fontWeight = FontWeight.Black,
                            color = ImmersivePrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = uiState.selectedExercise.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ImmersiveTextPrimary
                    )
                }
            }
            return@Box
        }

        // Top Header Info Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.8f), Color.Transparent)
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Exercise Title & Goal Badge
            Column {
                Text(
                    text = uiState.selectedExercise.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ImmersiveTextPrimary
                )
                Text(
                    text = "Hedef: ${uiState.targetGoal} Tekrar",
                    fontSize = 13.sp,
                    color = ImmersivePrimary
                )
            }

            // Duration Timer
            Surface(
                shape = CircleShape,
                color = ImmersiveSurface.copy(alpha = 0.85f),
                border = androidx.compose.foundation.BorderStroke(1.dp, ImmersiveOutline)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = "Süre",
                        tint = ImmersivePrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = timeFormatted,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = ImmersiveTextPrimary
                    )
                }
            }

            // Audio Mute Button
            IconButton(
                onClick = onToggleSound,
                modifier = Modifier
                    .size(40.dp)
                    .background(ImmersiveSurface.copy(alpha = 0.8f), shape = CircleShape)
            ) {
                Icon(
                    imageVector = if (uiState.isSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                    contentDescription = "Ses",
                    tint = if (uiState.isSoundEnabled) ImmersivePrimary else ImmersiveTextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Center HUD: Rep Counter Circle & Feedback
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Circular Rep Counter
            Box(
                modifier = Modifier.size(160.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background Track
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.fillMaxSize(),
                    color = ImmersiveSurfaceVariant.copy(alpha = 0.5f),
                    strokeWidth = 10.dp,
                    strokeCap = StrokeCap.Round,
                )

                // Active Progress
                CircularProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.fillMaxSize(),
                    color = if (progress >= 1f) ImmersiveGreen else ImmersivePrimary,
                    strokeWidth = 10.dp,
                    strokeCap = StrokeCap.Round,
                )

                // Rep Count Number
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.scale(scaleAnim.value)
                ) {
                    Text(
                        text = "${uiState.currentReps}",
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Black,
                        color = if (progress >= 1f) ImmersiveGreen else ImmersiveTextPrimary
                    )
                    Text(
                        text = "TEKRAR",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ImmersivePrimary,
                        letterSpacing = 2.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Form Feedback Pill
            val feedbackBg = when (uiState.poseResult.stage) {
                ExerciseStage.DOWN -> ImmersivePrimaryContainer.copy(alpha = 0.9f)
                ExerciseStage.UP -> ImmersiveSurfaceVariant.copy(alpha = 0.9f)
                else -> ImmersiveSurface.copy(alpha = 0.85f)
            }

            Surface(
                shape = CircleShape,
                color = feedbackBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, ImmersiveOutline)
            ) {
                Text(
                    text = uiState.poseResult.feedback,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ImmersiveTextPrimary,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    textAlign = TextAlign.Center
                )
            }

            if (uiState.poseResult.isBodyVisible && uiState.poseResult.primaryAngle > 0f) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = CircleShape,
                    color = ImmersiveBackground.copy(alpha = 0.75f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ImmersiveOutline.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = String.format(Locale.getDefault(), "Açı: %.0f°", uiState.poseResult.primaryAngle),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = ImmersivePrimary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Bottom Action Controls
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            // Manual Rep Adjuster (+ / -)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = ImmersiveSurface.copy(alpha = 0.85f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ImmersiveOutline)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onManualMinus,
                            modifier = Modifier.size(36.dp).testTag("manual_minus_button")
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Azalt", tint = ImmersiveTextSecondary)
                        }

                        Text(
                            text = "Manuel Düzelt",
                            fontSize = 12.sp,
                            color = ImmersiveTextSecondary,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        IconButton(
                            onClick = onManualAdd,
                            modifier = Modifier.size(36.dp).testTag("manual_add_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Arttır", tint = ImmersivePrimary)
                        }
                    }
                }
            }

            // Main Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Pause / Resume Button
                Surface(
                    shape = CircleShape,
                    color = if (uiState.sessionState == WorkoutSessionState.PAUSED) ImmersiveGreen else ImmersivePrimaryContainer,
                    border = androidx.compose.foundation.BorderStroke(1.dp, ImmersivePrimary.copy(alpha = 0.6f)),
                    modifier = Modifier.size(56.dp)
                ) {
                    IconButton(
                        onClick = onPauseResume,
                        modifier = Modifier.fillMaxSize().testTag("pause_resume_button")
                    ) {
                        Icon(
                            imageVector = if (uiState.sessionState == WorkoutSessionState.PAUSED) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = if (uiState.sessionState == WorkoutSessionState.PAUSED) "Devam Et" else "Duraklat",
                            tint = if (uiState.sessionState == WorkoutSessionState.PAUSED) Color.Black else ImmersivePrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                // Finish Workout Button
                Surface(
                    shape = CircleShape,
                    color = ImmersiveCoral,
                    modifier = Modifier.size(64.dp)
                ) {
                    IconButton(
                        onClick = onFinishWorkout,
                        modifier = Modifier.fillMaxSize().testTag("finish_workout_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = "Bitir",
                            tint = Color(0xFF381E72),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }
    }
}
