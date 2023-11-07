package com.example.shadman_hossain_myruns1

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData

class ExerciseViewModel(private val repository: ExerciseRepository): ViewModel() {
    val allExerciseLiveData: LiveData<List<ExerciseEntry>> = repository.allEntries.asLiveData()

    fun insert(exerciseEntry: ExerciseEntry){
        repository.insert(exerciseEntry)
    }

    fun delete(id: Long){
        repository.delete(id)
    }

    fun getEntryByID(entryID: Long): ExerciseEntry?{
        return repository.getEntryByID(entryID)
    }
}

class ExerciseViewModelFactory(private val repository: ExerciseRepository): ViewModelProvider.Factory{
    override fun<T:ViewModel> create(modelClass: Class<T>) : T{
        if (modelClass.isAssignableFrom(ExerciseViewModel::class.java)){
            return ExerciseViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}