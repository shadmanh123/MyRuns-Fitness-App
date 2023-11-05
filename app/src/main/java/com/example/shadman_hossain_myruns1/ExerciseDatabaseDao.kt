package com.example.shadman_hossain_myruns1

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDatabaseDao {

    @Insert
    suspend fun insertExercise(exerciseEntry: ExerciseEntry)

    @Query("SELECT * FROM exercise_table")
    fun getAllEntries(): Flow<List<ExerciseEntry>>

    @Query("DELETE FROM exercise_table WHERE id = :key")
    suspend fun deleteExercise(key: Long)
}