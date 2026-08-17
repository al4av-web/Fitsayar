package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_records")
data class WorkoutRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val exerciseType: String,
    val reps: Int,
    val targetGoal: Int,
    val durationSeconds: Long,
    val caloriesBurned: Double,
    val averageFormScore: Int,
    val timestamp: Long = System.currentTimeMillis()
)
