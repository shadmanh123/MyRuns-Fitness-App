package com.example.shadman_hossain_myruns1

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ExerciseRepository(private val exerciseDatabaseDao: ExerciseDatabaseDao) {

    val allComments: Flow<List<ExerciseEntry>> = exerciseDatabaseDao.getAllEntries()

    fun insert(exerciseEntry: ExerciseEntry){
        CoroutineScope(IO).launch {
            exerciseDatabaseDao.insertExercise(exerciseEntry)
        }
    }

    fun delete(id: Long){
        CoroutineScope(IO).launch {
            exerciseDatabaseDao.deleteExercise(id)
        }
    }
}