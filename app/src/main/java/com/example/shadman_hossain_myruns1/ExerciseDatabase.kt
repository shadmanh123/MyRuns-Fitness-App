package com.example.shadman_hossain_myruns1

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [ExerciseEntry::class], version = 1)
@TypeConverters(com.example.shadman_hossain_myruns1.TypeConverter::class)
abstract class ExerciseDatabase: RoomDatabase() {

    abstract val exerciseDatabaseDao: ExerciseDatabaseDao
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