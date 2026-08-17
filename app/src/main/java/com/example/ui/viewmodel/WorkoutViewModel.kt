package com.example.ui.viewmodel

import android.app.Application
import androidx.camera.core.CameraSelector
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.FitnessAudioCoach
import com.example.data.LeagueManager
import com.example.data.local.AppDatabase
import com.example.data.local.WorkoutRecord
import com.example.data.repository.UserProfile
import com.example.data.repository.UserProfileRepository
import com.example.data.repository.WorkoutRepository
import com.example.detector.PoseDetectorHelper
import com.example.model.Competitor
import com.example.model.ExerciseType
import com.example.model.LeagueTier
import com.example.model.LevelProgress
import com.example.model.PoseFrameResult
import com.example.model.RankTier
import com.example.model.VsMatchMode
import com.example.model.VsOpponent
import com.example.model.VsOpponentPresets
import com.example.model.VsWinner
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class WorkoutSessionState {
    IDLE,
    COUNTDOWN,
    ACTIVE,
    PAUSED,
    FINISHED
}

data class WorkoutUiState(
    val selectedExercise: ExerciseType = ExerciseType.PUSH_UP,
    val sessionState: WorkoutSessionState = WorkoutSessionState.IDLE,
    val currentReps: Int = 0,
    val targetGoal: Int = 20,
    val elapsedSeconds: Long = 0L,
    val caloriesBurned: Double = 0.0,
    val poseResult: PoseFrameResult = PoseFrameResult(),
    val countdownNumber: Int = 3,
    val lensFacing: Int = CameraSelector.LENS_FACING_FRONT,
    val isSoundEnabled: Boolean = true,
    val isVoiceCoachEnabled: Boolean = true,
    val lastCompletedRecord: WorkoutRecord? = null,
    val showSummaryDialog: Boolean = false,
    val showHistorySheet: Boolean = false,
    val showHelpDialog: Boolean = false,
    val showLeaderboardSheet: Boolean = false,
    val showVsLobbySheet: Boolean = false,
    val showRankedInfoDialog: Boolean = false,
    val selectedLeagueTier: LeagueTier = LeagueTier.WOOD,
    // VS Battle State
    val isVsMode: Boolean = false,
    val isRankedVs: Boolean = false,
    val vsMatchMode: VsMatchMode = VsMatchMode.TIMED_60S,
    val vsOpponent: VsOpponent = VsOpponentPresets.allOpponents[1],
    val vsPlayerReps: Int = 0,
    val vsOpponentReps: Int = 0,
    val vsRemainingSeconds: Int = 60,
    val vsShowResultDialog: Boolean = false,
    val vsWinner: VsWinner? = null,
    val vsEarnedXp: Int = 0,
    val vsEarnedRp: Int = 0
)

class WorkoutViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WorkoutRepository
    private val userProfileRepo: UserProfileRepository
    val audioCoach: FitnessAudioCoach = FitnessAudioCoach(application)

    private val _uiState = MutableStateFlow(WorkoutUiState())
    val uiState: StateFlow<WorkoutUiState> = _uiState.asStateFlow()

    val userProfile: StateFlow<UserProfile>

    private var timerJob: Job? = null
    private var countdownJob: Job? = null
    private var botSimulationJob: Job? = null

    val allWorkouts: StateFlow<List<WorkoutRecord>>
    val totalReps: StateFlow<Int?>
    val totalCalories: StateFlow<Double?>
    val totalDurationSeconds: StateFlow<Long?>

    val poseHelper: PoseDetectorHelper

    init {
        val db = AppDatabase.getDatabase(application)
        repository = WorkoutRepository(db.workoutDao())
        userProfileRepo = UserProfileRepository(application)
        userProfile = userProfileRepo.userProfile

        allWorkouts = repository.allWorkouts.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        totalReps = repository.totalReps.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

        totalCalories = repository.totalCalories.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

        totalDurationSeconds = repository.totalDurationSeconds.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0L
        )

        poseHelper = PoseDetectorHelper(
            onRepCounted = { exercise, newCount, formScore ->
                handleRepCounted(exercise, newCount, formScore)
            },
            onPoseFrame = { frameResult ->
                _uiState.update { it.copy(poseResult = frameResult) }
            }
        )
    }

    fun getUserPoints(): Int {
        val xp = userProfile.value.totalXp
        val reps = totalReps.value ?: 0
        return (reps * 10) + xp
    }

    fun getCompetitorsForTier(tier: LeagueTier): List<Competitor> {
        val userPoints = getUserPoints()
        val reps = totalReps.value ?: 0
        return LeagueManager.getCompetitorsForTier(
            tier = tier,
            userName = userProfile.value.name,
            userReps = reps,
            userPoints = userPoints,
            userLevel = userProfile.value.levelProgress.level
        )
    }

    fun selectLeagueTier(tier: LeagueTier) {
        _uiState.update { it.copy(selectedLeagueTier = tier) }
    }

    fun updateUserName(name: String) {
        val trimmed = name.trim()
        if (trimmed.isNotBlank()) {
            userProfileRepo.updateName(trimmed)
        }
    }

    fun setShowLeaderboardSheet(show: Boolean) {
        if (show) {
            val userTier = LeagueTier.getTierForPoints(getUserPoints())
            _uiState.update { it.copy(showLeaderboardSheet = true, selectedLeagueTier = userTier) }
        } else {
            _uiState.update { it.copy(showLeaderboardSheet = false) }
        }
    }

    fun setShowVsLobby(show: Boolean) {
        _uiState.update { it.copy(showVsLobbySheet = show) }
    }

    fun setShowRankedInfo(show: Boolean) {
        _uiState.update { it.copy(showRankedInfoDialog = show) }
    }

    fun selectExercise(exercise: ExerciseType) {
        if (_uiState.value.sessionState == WorkoutSessionState.ACTIVE) return
        poseHelper.currentExercise = exercise
        poseHelper.resetCounter()
        _uiState.update {
            it.copy(
                selectedExercise = exercise,
                currentReps = 0,
                targetGoal = exercise.defaultGoal,
                elapsedSeconds = 0L,
                caloriesBurned = 0.0,
                sessionState = WorkoutSessionState.IDLE
            )
        }
    }

    fun setTargetGoal(goal: Int) {
        _uiState.update { it.copy(targetGoal = goal.coerceIn(5, 500)) }
    }

    fun toggleCamera() {
        val newFacing = if (_uiState.value.lensFacing == CameraSelector.LENS_FACING_FRONT) {
            CameraSelector.LENS_FACING_BACK
        } else {
            CameraSelector.LENS_FACING_FRONT
        }
        _uiState.update { it.copy(lensFacing = newFacing) }
    }

    fun toggleSound() {
        val newState = !_uiState.value.isSoundEnabled
        audioCoach.isSoundEnabled = newState
        _uiState.update { it.copy(isSoundEnabled = newState) }
    }

    fun toggleVoiceCoach() {
        val newState = !_uiState.value.isVoiceCoachEnabled
        audioCoach.isVoiceCoachEnabled = newState
        _uiState.update { it.copy(isVoiceCoachEnabled = newState) }
    }

    // ================= SOLO WORKOUT =================

    fun startWorkoutWithCountdown() {
        if (_uiState.value.sessionState == WorkoutSessionState.ACTIVE) return

        poseHelper.resetCounter()
        poseHelper.isPaused = false
        _uiState.update {
            it.copy(
                isVsMode = false,
                sessionState = WorkoutSessionState.COUNTDOWN,
                countdownNumber = 3,
                currentReps = 0,
                elapsedSeconds = 0L,
                caloriesBurned = 0.0
            )
        }

        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            audioCoach.speakMotivation("3")
            delay(1000)
            _uiState.update { it.copy(countdownNumber = 2) }
            audioCoach.speakMotivation("2")
            delay(1000)
            _uiState.update { it.copy(countdownNumber = 1) }
            audioCoach.speakMotivation("1")
            delay(1000)
            _uiState.update { it.copy(sessionState = WorkoutSessionState.ACTIVE) }
            audioCoach.speakMotivation("Başla!")
            startSoloTimer()
        }
    }

    private fun startSoloTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                if (_uiState.value.sessionState == WorkoutSessionState.ACTIVE) {
                    _uiState.update {
                        val newSeconds = it.elapsedSeconds + 1
                        val calories = it.currentReps * it.selectedExercise.caloriesPerRep
                        it.copy(elapsedSeconds = newSeconds, caloriesBurned = calories)
                    }
                }
            }
        }
    }

    // ================= VS BATTLE MODE =================

    fun startVsBattle(
        exercise: ExerciseType,
        mode: VsMatchMode,
        opponent: VsOpponent,
        isRanked: Boolean
    ) {
        poseHelper.currentExercise = exercise
        poseHelper.resetCounter()
        poseHelper.isPaused = false

        _uiState.update {
            it.copy(
                isVsMode = true,
                isRankedVs = isRanked,
                selectedExercise = exercise,
                vsMatchMode = mode,
                vsOpponent = opponent,
                vsPlayerReps = 0,
                vsOpponentReps = 0,
                currentReps = 0,
                vsRemainingSeconds = mode.durationSeconds,
                sessionState = WorkoutSessionState.COUNTDOWN,
                countdownNumber = 3,
                showVsLobbySheet = false,
                vsShowResultDialog = false
            )
        }

        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            audioCoach.speakMotivation("3")
            delay(1000)
            _uiState.update { it.copy(countdownNumber = 2) }
            audioCoach.speakMotivation("2")
            delay(1000)
            _uiState.update { it.copy(countdownNumber = 1) }
            audioCoach.speakMotivation("1")
            delay(1000)
            _uiState.update { it.copy(sessionState = WorkoutSessionState.ACTIVE) }
            audioCoach.speakMotivation("Dövüş Başlasın!")

            startVsTimerAndBot(mode, opponent)
        }
    }

    private fun startVsTimerAndBot(mode: VsMatchMode, opponent: VsOpponent) {
        timerJob?.cancel()
        botSimulationJob?.cancel()

        // Timer Loop
        timerJob = viewModelScope.launch {
            while (_uiState.value.sessionState == WorkoutSessionState.ACTIVE) {
                delay(1000)
                if (_uiState.value.sessionState == WorkoutSessionState.ACTIVE) {
                    val currentRemaining = _uiState.value.vsRemainingSeconds
                    if (mode.isTimed) {
                        val nextSec = currentRemaining - 1
                        _uiState.update { it.copy(vsRemainingSeconds = nextSec) }
                        if (nextSec <= 0) {
                            finishVsBattle()
                            break
                        }
                    } else {
                        // Race mode: count down total time limit
                        val nextSec = currentRemaining - 1
                        _uiState.update { it.copy(vsRemainingSeconds = nextSec) }
                        if (nextSec <= 0) {
                            finishVsBattle()
                            break
                        }
                    }
                }
            }
        }

        // Realistic Bot Rep Cadence Simulation
        botSimulationJob = viewModelScope.launch {
            var botElapsed = 0f
            // Base interval in milliseconds between bot reps
            val baseIntervalMs = (60f / opponent.repsPerMinute) * 1000f

            while (_uiState.value.sessionState == WorkoutSessionState.ACTIVE) {
                // Calculate realistic fatigue delay
                val fatigueMultiplier = 1f + (botElapsed / 60f) * (1f - opponent.fatigueFactor)
                val jitter = Random.nextFloat() * 400f - 200f // +/- 200ms natural human jitter
                val nextDelay = ((baseIntervalMs * fatigueMultiplier) + jitter).toLong().coerceAtLeast(800L)

                delay(nextDelay)
                botElapsed += nextDelay / 1000f

                if (_uiState.value.sessionState == WorkoutSessionState.ACTIVE) {
                    val nextBotReps = _uiState.value.vsOpponentReps + 1
                    _uiState.update { it.copy(vsOpponentReps = nextBotReps) }

                    // In Race mode, if bot reaches target goal first, finish match!
                    if (!mode.isTimed && nextBotReps >= mode.targetReps) {
                        finishVsBattle()
                        break
                    }
                }
            }
        }
    }

    fun giveUpVsBattle() {
        finishVsBattle(isForcedGiveUp = true)
    }

    private fun finishVsBattle(isForcedGiveUp: Boolean = false) {
        timerJob?.cancel()
        countdownJob?.cancel()
        botSimulationJob?.cancel()
        poseHelper.isPaused = true

        val state = _uiState.value
        val playerReps = state.vsPlayerReps
        val opponentReps = state.vsOpponentReps

        val winner = if (isForcedGiveUp) {
            VsWinner.OPPONENT
        } else when {
            playerReps > opponentReps -> VsWinner.PLAYER
            playerReps == opponentReps -> VsWinner.DRAW
            else -> VsWinner.OPPONENT
        }

        val isWin = winner == VsWinner.PLAYER
        val isDraw = winner == VsWinner.DRAW

        // XP Calculation: 1 rep = 1 XP, Win Bonus = +25 XP, Draw = +10 XP, Loss = +5 XP
        val repXp = playerReps * 1
        val bonusXp = if (isWin) 25 else if (isDraw) 10 else 5
        val totalXpEarned = repXp + bonusXp

        // RP Calculation (for Ranked)
        val rpDelta = if (state.isRankedVs) {
            if (isWin) 30 else if (isDraw) 10 else -15
        } else {
            0
        }

        userProfileRepo.recordVsBattleResult(
            isWin = isWin,
            isDraw = isDraw,
            xpEarned = totalXpEarned,
            rpDelta = rpDelta
        )

        // Save workout record as well
        val record = WorkoutRecord(
            exerciseType = state.selectedExercise.name,
            reps = playerReps,
            targetGoal = state.vsMatchMode.targetReps.takeIf { it > 0 } ?: playerReps,
            durationSeconds = (state.vsMatchMode.durationSeconds - state.vsRemainingSeconds).toLong().coerceAtLeast(1L),
            caloriesBurned = playerReps * state.selectedExercise.caloriesPerRep,
            averageFormScore = state.poseResult.formScore
        )
        viewModelScope.launch {
            repository.insertWorkout(record)
        }

        _uiState.update {
            it.copy(
                sessionState = WorkoutSessionState.FINISHED,
                vsWinner = winner,
                vsEarnedXp = totalXpEarned,
                vsEarnedRp = rpDelta,
                vsShowResultDialog = true
            )
        }

        if (isWin) {
            audioCoach.speakMotivation("Tebrikler, maçı kazandın! Harika bir zafer!")
        } else if (isDraw) {
            audioCoach.speakMotivation("Berabere! Başa baş bir mücadeleydi!")
        } else {
            audioCoach.speakMotivation("Maç bitti. Pes etmek yok, antrenmana devam!")
        }
    }

    fun dismissVsResultDialog() {
        _uiState.update {
            it.copy(
                vsShowResultDialog = false,
                isVsMode = false,
                sessionState = WorkoutSessionState.IDLE,
                currentReps = 0,
                vsPlayerReps = 0,
                vsOpponentReps = 0
            )
        }
        poseHelper.resetCounter()
    }

    fun rematchVsBattle() {
        val state = _uiState.value
        startVsBattle(
            exercise = state.selectedExercise,
            mode = state.vsMatchMode,
            opponent = state.vsOpponent,
            isRanked = state.isRankedVs
        )
    }

    // ================= MANUAL & DETECTION REPS =================

    fun pauseWorkout() {
        poseHelper.isPaused = true
        _uiState.update { it.copy(sessionState = WorkoutSessionState.PAUSED) }
    }

    fun resumeWorkout() {
        poseHelper.isPaused = false
        _uiState.update { it.copy(sessionState = WorkoutSessionState.ACTIVE) }
    }

    fun manualAddRep() {
        val nextReps = _uiState.value.currentReps + 1
        poseHelper.setManualRepCount(nextReps)
        handleRepCounted(_uiState.value.selectedExercise, nextReps, 95)
    }

    fun manualMinusRep() {
        val nextReps = (_uiState.value.currentReps - 1).coerceAtLeast(0)
        poseHelper.setManualRepCount(nextReps)
        _uiState.update {
            val calories = nextReps * it.selectedExercise.caloriesPerRep
            it.copy(
                currentReps = nextReps,
                vsPlayerReps = if (it.isVsMode) nextReps else it.vsPlayerReps,
                caloriesBurned = calories
            )
        }
    }

    private fun handleRepCounted(exercise: ExerciseType, count: Int, formScore: Int) {
        val calories = count * exercise.caloriesPerRep

        // Every rep grants 1 XP
        userProfileRepo.addXp(1)

        _uiState.update {
            it.copy(
                currentReps = count,
                vsPlayerReps = if (it.isVsMode) count else it.vsPlayerReps,
                caloriesBurned = calories
            )
        }

        // In VS Mode Race, check if player reached target first!
        if (_uiState.value.isVsMode && !_uiState.value.vsMatchMode.isTimed) {
            if (count >= _uiState.value.vsMatchMode.targetReps) {
                finishVsBattle()
                return
            }
        }

        if (!_uiState.value.isVsMode) {
            audioCoach.onRepCounted(count, _uiState.value.targetGoal)
        } else {
            audioCoach.speakMotivation("$count")
        }
    }

    fun finishWorkout() {
        if (_uiState.value.isVsMode) {
            finishVsBattle()
            return
        }

        timerJob?.cancel()
        countdownJob?.cancel()
        poseHelper.isPaused = true

        val currentState = _uiState.value
        if (currentState.currentReps > 0) {
            val record = WorkoutRecord(
                exerciseType = currentState.selectedExercise.name,
                reps = currentState.currentReps,
                targetGoal = currentState.targetGoal,
                durationSeconds = currentState.elapsedSeconds,
                caloriesBurned = currentState.caloriesBurned,
                averageFormScore = currentState.poseResult.formScore
            )

            viewModelScope.launch {
                repository.insertWorkout(record)
                _uiState.update {
                    it.copy(
                        sessionState = WorkoutSessionState.FINISHED,
                        lastCompletedRecord = record,
                        showSummaryDialog = true
                    )
                }
                audioCoach.speakMotivation("Egzersiz tamamlandı! Harika bir antrenman!")
            }
        } else {
            _uiState.update {
                it.copy(
                    sessionState = WorkoutSessionState.IDLE,
                    currentReps = 0,
                    elapsedSeconds = 0L,
                    caloriesBurned = 0.0
                )
            }
        }
    }

    fun dismissSummaryDialog() {
        _uiState.update {
            it.copy(
                showSummaryDialog = false,
                sessionState = WorkoutSessionState.IDLE,
                currentReps = 0,
                elapsedSeconds = 0L,
                caloriesBurned = 0.0
            )
        }
        poseHelper.resetCounter()
    }

    fun setShowHistorySheet(show: Boolean) {
        _uiState.update { it.copy(showHistorySheet = show) }
    }

    fun setShowHelpDialog(show: Boolean) {
        _uiState.update { it.copy(showHelpDialog = show) }
    }

    fun deleteWorkoutRecord(recordId: Long) {
        viewModelScope.launch {
            repository.deleteWorkoutById(recordId)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        countdownJob?.cancel()
        botSimulationJob?.cancel()
        audioCoach.release()
    }
}
