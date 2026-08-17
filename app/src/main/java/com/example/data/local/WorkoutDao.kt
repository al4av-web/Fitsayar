package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workout_records ORDER BY timestamp DESC")
    fun getAllWorkouts(): Flow<List<WorkoutRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(record: WorkoutRecord): Long

    @Query("DELETE FROM workout_records WHERE id = :id")
    suspend fun deleteWorkoutById(id: Long)

    @Query("DELETE FROM workout_records")
    suspend fun deleteAllWorkouts()

    @Query("SELECT SUM(reps) FROM workout_records WHERE exerciseType = :type")
    fun getTotalRepsByExercise(type: String): Flow<Int?>

    @Query("SELECT SUM(reps) FROM workout_records")
    fun getTotalReps(): Flow<Int?>

    @Query("SELECT SUM(caloriesBurned) FROM workout_records")
    fun getTotalCalories(): Flow<Double?>

    @Query("SELECT SUM(durationSeconds) FROM workout_records")
    fun getTotalDurationSeconds(): Flow<Long?>
}
