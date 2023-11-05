package com.example.shadman_hossain_myruns1

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ExerciseEntry::class], version = 1)
abstract class ExerciseDatabase: RoomDatabase() {
    abstract val exerciseDatabase: ExerciseDatabase

    companion object{
        @Volatile
        private var INSTANCE: ExerciseDatabase? = null

        fun getInstance(context: Context): ExerciseDatabase{
            synchronized(this){
                var instance = INSTANCE
                if(instance == null){
                    instance = Room.databaseBuilder(context.applicationContext,
                        ExerciseDatabase::class.java,"exercise_table").build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}