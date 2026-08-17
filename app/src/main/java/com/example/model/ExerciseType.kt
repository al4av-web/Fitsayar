package com.example.model

enum class ExerciseType(
    val title: String,
    val description: String,
    val caloriesPerRep: Double,
    val defaultGoal: Int,
    val instruction: String,
    val keyPointsDesc: String
) {
    PUSH_UP(
        title = "Şınav",
        description = "Göğüs, omuz ve triceps odaklı temel vücut ağırlığı hareketi",
        caloriesPerRep = 0.45,
        defaultGoal = 20,
        instruction = "Telefonu yere veya yanınıza koyun. Vücudunuzu düz tutarak kollarınızı 90 derecenin altına bükün ve tamamen yukarı kalkın.",
        keyPointsDesc = "Dirsek açısı (<90° in, >150° kalk)"
    ),
    SIT_UP(
        title = "Mekik",
        description = "Karın ve merkez (core) kaslarını güçlendirici zemin hareketi",
        caloriesPerRep = 0.35,
        defaultGoal = 25,
        instruction = "Sırt üstü uzanın, dizlerinizi bükün. Gövdenizi dizlerinize doğru kaldırın ve kontrollü bir şekilde geri yatın.",
        keyPointsDesc = "Gövde açısı (>130° yat, <85° kalk)"
    ),
    SQUAT(
        title = "Squat",
        description = "Bacak, basen ve kalça kaslarını çalıştıran güçlü alt vücut hareketi",
        caloriesPerRep = 0.55,
        defaultGoal = 30,
        instruction = "Ayaklar omuz genişliğinde açık durun. Kalçanızı geriye vererek dizlerinizi 90 dereceye kadar bükün ve tamamen doğrulun.",
        keyPointsDesc = "Diz açısı (<100° in, >160° kalk)"
    )
}

enum class ExerciseStage {
    IDLE,
    DOWN,
    UP,
    HALF_WAY
}

data class PoseLandmarkPoint(
    val x: Float,
    val y: Float,
    val likelihood: Float
)

data class PoseFrameResult(
    val landmarks: Map<Int, PoseLandmarkPoint> = emptyMap(),
    val primaryAngle: Float = 0f,
    val secondaryAngle: Float = 0f,
    val stage: ExerciseStage = ExerciseStage.IDLE,
    val feedback: String = "Kamera karşısına geçin",
    val formScore: Int = 100,
    val repProgress: Float = 0f,
    val isBodyVisible: Boolean = false
)
