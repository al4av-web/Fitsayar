package com.example.detector

import android.content.Context
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.model.ExerciseStage
import com.example.model.ExerciseType
import com.example.model.PoseFrameResult
import com.example.model.PoseLandmarkPoint
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseDetector
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.max
import kotlin.math.min

class PoseDetectorHelper(
    private val onRepCounted: (exerciseType: ExerciseType, newCount: Int, formScore: Int) -> Unit,
    private val onPoseFrame: (PoseFrameResult) -> Unit
) : ImageAnalysis.Analyzer {

    private val options = PoseDetectorOptions.Builder()
        .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
        .setPreferredHardwareConfigs(PoseDetectorOptions.CPU_GPU)
        .build()

    private val detector: PoseDetector = PoseDetection.getClient(options)

    @Volatile
    var currentExercise: ExerciseType = ExerciseType.PUSH_UP

    @Volatile
    var isPaused: Boolean = false

    private var currentRepCount = 0
    private var stage = ExerciseStage.IDLE
    private var lastRepTimestamp = 0L
    private var totalFormScoreSum = 0
    private var repSamplesCount = 0

    fun resetCounter() {
        currentRepCount = 0
        stage = ExerciseStage.IDLE
        lastRepTimestamp = 0L
        totalFormScoreSum = 0
        repSamplesCount = 0
    }

    fun setManualRepCount(count: Int) {
        currentRepCount = max(0, count)
    }

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage == null || isPaused) {
            imageProxy.close()
            return
        }

        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
        val image = InputImage.fromMediaImage(mediaImage, rotationDegrees)

        detector.process(image)
            .addOnSuccessListener { pose ->
                processPose(pose, imageProxy.width, imageProxy.height)
            }
            .addOnFailureListener {
                // Graceful fallback
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

    private fun processPose(pose: Pose, imageWidth: Int, imageHeight: Int) {
        val allLandmarks = pose.allPoseLandmarks
        if (allLandmarks.isEmpty()) {
            onPoseFrame(
                PoseFrameResult(
                    landmarks = emptyMap(),
                    primaryAngle = 0f,
                    secondaryAngle = 0f,
                    stage = stage,
                    feedback = "Kamera karşısına geçin",
                    formScore = 100,
                    repProgress = 0f,
                    isBodyVisible = false
                )
            )
            return
        }

        val landmarkMap = mutableMapOf<Int, PoseLandmarkPoint>()
        for (landmark in allLandmarks) {
            landmarkMap[landmark.landmarkType] = PoseLandmarkPoint(
                x = landmark.position.x / imageWidth.toFloat(),
                y = landmark.position.y / imageHeight.toFloat(),
                likelihood = landmark.inFrameLikelihood
            )
        }

        when (currentExercise) {
            ExerciseType.PUSH_UP -> evaluatePushUp(pose, landmarkMap)
            ExerciseType.SIT_UP -> evaluateSitUp(pose, landmarkMap)
            ExerciseType.SQUAT -> evaluateSquat(pose, landmarkMap)
        }
    }

    private fun evaluatePushUp(pose: Pose, landmarkMap: Map<Int, PoseLandmarkPoint>) {
        val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
        val leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)

        val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
        val rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
        val rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)

        val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
        val rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)

        val leftVisible = leftShoulder != null && leftElbow != null && leftWrist != null &&
                leftShoulder.inFrameLikelihood > 0.4f && leftElbow.inFrameLikelihood > 0.4f && leftWrist.inFrameLikelihood > 0.4f

        val rightVisible = rightShoulder != null && rightElbow != null && rightWrist != null &&
                rightShoulder.inFrameLikelihood > 0.4f && rightElbow.inFrameLikelihood > 0.4f && rightWrist.inFrameLikelihood > 0.4f

        if (!leftVisible && !rightVisible) {
            onPoseFrame(
                PoseFrameResult(
                    landmarks = landmarkMap,
                    feedback = "Kollarınızı ve vücudunuzu gösterin",
                    isBodyVisible = false
                )
            )
            return
        }

        val leftAngle = if (leftVisible) calculateAngle(leftShoulder!!, leftElbow!!, leftWrist!!) else 0.0
        val rightAngle = if (rightVisible) calculateAngle(rightShoulder!!, rightElbow!!, rightWrist!!) else 0.0

        val elbowAngle = when {
            leftVisible && rightVisible -> (leftAngle + rightAngle) / 2.0
            leftVisible -> leftAngle
            else -> rightAngle
        }.toFloat()

        // Rep progress from 160° (0%) down to 90° (100%)
        val progress = min(1f, max(0f, (160f - elbowAngle) / (160f - 85f)))
        val currentTime = System.currentTimeMillis()

        var feedback = "Pozisyon hazır"
        var formScore = 95

        if (elbowAngle > 155f) {
            if (stage == ExerciseStage.DOWN && (currentTime - lastRepTimestamp) > 600) {
                // Count successful rep!
                currentRepCount++
                lastRepTimestamp = currentTime
                stage = ExerciseStage.UP
                formScore = 100
                onRepCounted(ExerciseType.PUSH_UP, currentRepCount, formScore)
                feedback = "Harika tekrar! (${currentRepCount})"
            } else {
                stage = ExerciseStage.UP
                feedback = "Aşağı inin ↓"
            }
        } else if (elbowAngle < 90f) {
            stage = ExerciseStage.DOWN
            feedback = "Mükemmel derinlik! Şimdi yukarı ↑"
        } else {
            feedback = if (stage == ExerciseStage.DOWN) "Yukarı itin ↑" else "Daha derine inin ↓"
        }

        onPoseFrame(
            PoseFrameResult(
                landmarks = landmarkMap,
                primaryAngle = elbowAngle,
                secondaryAngle = progress * 100f,
                stage = stage,
                feedback = feedback,
                formScore = formScore,
                repProgress = progress,
                isBodyVisible = true
            )
        )
    }

    private fun evaluateSitUp(pose: Pose, landmarkMap: Map<Int, PoseLandmarkPoint>) {
        val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
        val leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)

        val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
        val rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
        val rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)

        val leftVisible = leftShoulder != null && leftHip != null && leftKnee != null &&
                leftShoulder.inFrameLikelihood > 0.4f && leftHip.inFrameLikelihood > 0.4f

        val rightVisible = rightShoulder != null && rightHip != null && rightKnee != null &&
                rightShoulder.inFrameLikelihood > 0.4f && rightHip.inFrameLikelihood > 0.4f

        if (!leftVisible && !rightVisible) {
            onPoseFrame(
                PoseFrameResult(
                    landmarks = landmarkMap,
                    feedback = "Gövdenizi ve bacaklarınızı gösterin",
                    isBodyVisible = false
                )
            )
            return
        }

        val leftAngle = if (leftVisible) calculateAngle(leftShoulder!!, leftHip!!, leftKnee!!) else 0.0
        val rightAngle = if (rightVisible) calculateAngle(rightShoulder!!, rightHip!!, rightKnee!!) else 0.0

        val hipAngle = when {
            leftVisible && rightVisible -> (leftAngle + rightAngle) / 2.0
            leftVisible -> leftAngle
            else -> rightAngle
        }.toFloat()

        // Sit-up progress: 135° (lying down 0%) to 75° (sitting up 100%)
        val progress = min(1f, max(0f, (135f - hipAngle) / (135f - 75f)))
        val currentTime = System.currentTimeMillis()

        var feedback = "Pozisyon hazır"
        var formScore = 95

        if (hipAngle > 125f) {
            if (stage == ExerciseStage.UP && (currentTime - lastRepTimestamp) > 600) {
                currentRepCount++
                lastRepTimestamp = currentTime
                stage = ExerciseStage.DOWN
                formScore = 100
                onRepCounted(ExerciseType.SIT_UP, currentRepCount, formScore)
                feedback = "Harika mekik! (${currentRepCount})"
            } else {
                stage = ExerciseStage.DOWN
                feedback = "Gövdenizi kaldırın ↑"
            }
        } else if (hipAngle < 85f) {
            stage = ExerciseStage.UP
            feedback = "Tepe noktası harika! Kontrollü inin ↓"
        } else {
            feedback = if (stage == ExerciseStage.UP) "Yavaşça geriye inin ↓" else "Kalkmaya devam edin ↑"
        }

        onPoseFrame(
            PoseFrameResult(
                landmarks = landmarkMap,
                primaryAngle = hipAngle,
                secondaryAngle = progress * 100f,
                stage = stage,
                feedback = feedback,
                formScore = formScore,
                repProgress = progress,
                isBodyVisible = true
            )
        )
    }

    private fun evaluateSquat(pose: Pose, landmarkMap: Map<Int, PoseLandmarkPoint>) {
        val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
        val leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
        val leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)

        val rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
        val rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)
        val rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)

        val leftVisible = leftHip != null && leftKnee != null && leftAnkle != null &&
                leftHip.inFrameLikelihood > 0.4f && leftKnee.inFrameLikelihood > 0.4f && leftAnkle.inFrameLikelihood > 0.35f

        val rightVisible = rightHip != null && rightKnee != null && rightAnkle != null &&
                rightHip.inFrameLikelihood > 0.4f && rightKnee.inFrameLikelihood > 0.4f && rightAnkle.inFrameLikelihood > 0.35f

        if (!leftVisible && !rightVisible) {
            onPoseFrame(
                PoseFrameResult(
                    landmarks = landmarkMap,
                    feedback = "Dizlerinizi ve ayaklarınızı kadraja alın",
                    isBodyVisible = false
                )
            )
            return
        }

        val leftAngle = if (leftVisible) calculateAngle(leftHip!!, leftKnee!!, leftAnkle!!) else 0.0
        val rightAngle = if (rightVisible) calculateAngle(rightHip!!, rightKnee!!, rightAnkle!!) else 0.0

        val kneeAngle = when {
            leftVisible && rightVisible -> (leftAngle + rightAngle) / 2.0
            leftVisible -> leftAngle
            else -> rightAngle
        }.toFloat()

        // Progress from 165° (standing 0%) to 95° (squat 100%)
        val progress = min(1f, max(0f, (165f - kneeAngle) / (165f - 95f)))
        val currentTime = System.currentTimeMillis()

        var feedback = "Pozisyon hazır"
        var formScore = 95

        if (kneeAngle > 158f) {
            if (stage == ExerciseStage.DOWN && (currentTime - lastRepTimestamp) > 600) {
                currentRepCount++
                lastRepTimestamp = currentTime
                stage = ExerciseStage.UP
                formScore = 100
                onRepCounted(ExerciseType.SQUAT, currentRepCount, formScore)
                feedback = "Mükemmel squat! (${currentRepCount})"
            } else {
                stage = ExerciseStage.UP
                feedback = "Çömelin ↓"
            }
        } else if (kneeAngle < 100f) {
            stage = ExerciseStage.DOWN
            feedback = "Harika derinlik! Şimdi doğrulun ↑"
        } else {
            feedback = if (stage == ExerciseStage.DOWN) "Doğrulun ↑" else "Daha derin squat ↓"
        }

        onPoseFrame(
            PoseFrameResult(
                landmarks = landmarkMap,
                primaryAngle = kneeAngle,
                secondaryAngle = progress * 100f,
                stage = stage,
                feedback = feedback,
                formScore = formScore,
                repProgress = progress,
                isBodyVisible = true
            )
        )
    }

    private fun calculateAngle(
        firstPoint: PoseLandmark,
        midPoint: PoseLandmark,
        lastPoint: PoseLandmark
    ): Double {
        var angle = Math.toDegrees(
            atan2(
                (lastPoint.position.y - midPoint.position.y).toDouble(),
                (lastPoint.position.x - midPoint.position.x).toDouble()
            ) - atan2(
                (firstPoint.position.y - midPoint.position.y).toDouble(),
                (firstPoint.position.x - midPoint.position.x).toDouble()
            )
        )
        angle = abs(angle)
        if (angle > 180) {
            angle = 360.0 - angle
        }
        return angle
    }
}
