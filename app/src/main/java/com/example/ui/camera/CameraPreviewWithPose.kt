package com.example.ui.camera

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.detector.PoseDetectorHelper
import com.example.model.ExerciseStage
import com.example.model.PoseFrameResult
import com.example.ui.theme.ImmersiveCoral
import com.example.ui.theme.ImmersiveGreen
import com.example.ui.theme.ImmersiveOutline
import com.example.ui.theme.ImmersivePrimary
import com.example.ui.theme.ImmersiveSurface
import com.google.mlkit.vision.pose.PoseLandmark
import java.util.concurrent.Executors

@Composable
fun CameraPreviewWithPose(
    poseHelper: PoseDetectorHelper,
    poseResult: PoseFrameResult,
    lensFacing: Int,
    onSwitchCamera: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    var previewView by remember { mutableStateOf<PreviewView?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            try {
                val cameraProvider = ProcessCameraProvider.getInstance(context).get()
                cameraProvider.unbindAll()
            } catch (e: Exception) {
                // Ignore cleanup error
            }
            cameraExecutor.shutdown()
        }
    }

    LaunchedEffect(lensFacing, previewView) {
        val currentPreviewView = previewView ?: return@LaunchedEffect
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(currentPreviewView.surfaceProvider)
                    }

                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor, poseHelper)
                    }

                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(lensFacing)
                    .build()

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )
            } catch (e: Exception) {
                // Handle camera bind error safely
            }
        }, ContextCompat.getMainExecutor(context))
    }

    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    previewView = this
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Real-time Skeleton Overlay
        PoseSkeletonOverlay(
            poseResult = poseResult,
            isFrontCamera = lensFacing == CameraSelector.LENS_FACING_FRONT,
            modifier = Modifier.fillMaxSize()
        )

        // Switch Camera Button (Top-Right Floating)
        Surface(
            shape = CircleShape,
            color = ImmersiveSurface.copy(alpha = 0.8f),
            border = androidx.compose.foundation.BorderStroke(1.dp, ImmersiveOutline),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .size(48.dp)
        ) {
            IconButton(
                onClick = onSwitchCamera,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = Icons.Default.Cameraswitch,
                    contentDescription = "Kamerayı Değiştir",
                    tint = ImmersivePrimary
                )
            }
        }
    }
}

@Composable
fun PoseSkeletonOverlay(
    poseResult: PoseFrameResult,
    isFrontCamera: Boolean,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        if (!poseResult.isBodyVisible || poseResult.landmarks.isEmpty()) return@Canvas

        val w = size.width
        val h = size.height

        fun mapPoint(landmarkType: Int): Offset? {
            val pt = poseResult.landmarks[landmarkType] ?: return null
            if (pt.likelihood < 0.35f) return null
            val x = if (isFrontCamera) (1f - pt.x) * w else pt.x * w
            val y = pt.y * h
            return Offset(x, y)
        }

        val boneColor = when (poseResult.stage) {
            ExerciseStage.DOWN -> ImmersiveGreen
            ExerciseStage.UP -> ImmersiveCoral
            else -> ImmersivePrimary
        }

        val jointColor = Color.White
        val strokeWidthPx = 7.dp.toPx()
        val jointRadiusPx = 6.dp.toPx()

        val boneConnections = listOf(
            // Left Arm
            Pair(PoseLandmark.LEFT_SHOULDER, PoseLandmark.LEFT_ELBOW),
            Pair(PoseLandmark.LEFT_ELBOW, PoseLandmark.LEFT_WRIST),
            // Right Arm
            Pair(PoseLandmark.RIGHT_SHOULDER, PoseLandmark.RIGHT_ELBOW),
            Pair(PoseLandmark.RIGHT_ELBOW, PoseLandmark.RIGHT_WRIST),
            // Torso
            Pair(PoseLandmark.LEFT_SHOULDER, PoseLandmark.RIGHT_SHOULDER),
            Pair(PoseLandmark.LEFT_SHOULDER, PoseLandmark.LEFT_HIP),
            Pair(PoseLandmark.RIGHT_SHOULDER, PoseLandmark.RIGHT_HIP),
            Pair(PoseLandmark.LEFT_HIP, PoseLandmark.RIGHT_HIP),
            // Left Leg
            Pair(PoseLandmark.LEFT_HIP, PoseLandmark.LEFT_KNEE),
            Pair(PoseLandmark.LEFT_KNEE, PoseLandmark.LEFT_ANKLE),
            // Right Leg
            Pair(PoseLandmark.RIGHT_HIP, PoseLandmark.RIGHT_KNEE),
            Pair(PoseLandmark.RIGHT_KNEE, PoseLandmark.RIGHT_ANKLE)
        )

        // Draw bone lines
        for ((startType, endType) in boneConnections) {
            val start = mapPoint(startType)
            val end = mapPoint(endType)
            if (start != null && end != null) {
                drawLine(
                    color = boneColor.copy(alpha = 0.9f),
                    start = start,
                    end = end,
                    strokeWidth = strokeWidthPx,
                    cap = StrokeCap.Round
                )
            }
        }

        // Draw joint points
        for ((type, pt) in poseResult.landmarks) {
            if (pt.likelihood >= 0.35f) {
                val mapped = mapPoint(type)
                if (mapped != null) {
                    drawCircle(
                        color = jointColor,
                        radius = jointRadiusPx,
                        center = mapped
                    )
                    drawCircle(
                        color = boneColor,
                        radius = jointRadiusPx * 0.6f,
                        center = mapped
                    )
                }
            }
        }
    }
}
