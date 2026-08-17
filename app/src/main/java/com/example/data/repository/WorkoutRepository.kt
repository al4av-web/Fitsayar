package com.example.data.repository

import com.example.data.local.WorkoutDao
import com.example.data.local.WorkoutRecord
import kotlinx.coroutines.flow.Flow

class WorkoutRepository(private val workoutDao: WorkoutDao) {
    val allWorkouts: Flow<List<WorkoutRecord>> = workoutDao.getAllWorkouts()
    val totalReps: Flow<Int?> = workoutDao.getTotalReps()
    val totalCalories: Flow<Double?> = workoutDao.getTotalCalories()
    val totalDurationSeconds: Flow<Long?> = workoutDao.getTotalDurationSeconds()

    fun getTotalRepsByExercise(exerciseType: String): Flow<Int?> {
        return workoutDao.getTotalRepsByExercise(exerciseType)
    }

    suspend fun insertWorkout(record: WorkoutRecord): Long {
        return workoutDao.insertWorkout(record)
    }

    suspend fun deleteWorkoutById(id: Long) {
        workoutDao.deleteWorkoutById(id)
    }

    suspend fun clearAll() {
        workoutDao.deleteAllWorkouts()
    }
}
