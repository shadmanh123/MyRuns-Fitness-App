package com.example.shadman_hossain_myruns1

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercise_table")
data class ExerciseEntry (
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name = "inputType_column")
    var inputType: Int? = null,

    @ColumnInfo(name = "activityType_column")
    var activityType: Int? = null,

    @ColumnInfo(name = "dateTime_column")
    var dateTime: Long? = null,

    @ColumnInfo(name = "duration_column")
    var duration: Double? = null,

    @ColumnInfo(name = "distance_column")
    var distance: Double? = null,

    @ColumnInfo(name = "avgPace_column")
    var avgPace: Double? = null,

    @ColumnInfo(name = "avgSpeed_column")
    var avgSpeed: Double? = null,

    @ColumnInfo(name = "calorie_column")
    var calorie: Double? = null,

    @ColumnInfo(name = "climb_column")
    var climb: Double? = null,

    @ColumnInfo(name = "heartRate_column")
    var heartRate: Double? = null,

    @ColumnInfo(name = "comment_column")
    var comment: String? = null,

//    @ColumnInfo(name = "locationList_column")
//    var locationList: ArrayList<LatLng>? = null,
)