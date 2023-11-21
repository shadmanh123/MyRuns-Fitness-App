package com.example.shadman_hossain_myruns1

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng

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

    @ColumnInfo(name = "distanceUnit_column")
    var distanceUnit: String? = null,

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

    @ColumnInfo(name = "locationList_column")
    var locationList: ArrayList<LatLng>? = null,
) : Parcelable{
    constructor(parcel: Parcel) : this(
        id = parcel.readLong(),
        inputType = parcel.readValue(Int::class.java.classLoader) as? Int,
        activityType = parcel.readValue(Int::class.java.classLoader) as? Int,
        dateTime = parcel.readValue(Long::class.java.classLoader) as? Long,
        duration = parcel.readValue(Double::class.java.classLoader) as? Double,
        distance = parcel.readValue(Double::class.java.classLoader) as? Double,
        distanceUnit = parcel.readString(),
        avgPace = parcel.readValue(Double::class.java.classLoader) as? Double,
        avgSpeed = parcel.readValue(Double::class.java.classLoader) as? Double,
        calorie = parcel.readValue(Double::class.java.classLoader) as? Double,
        climb = parcel.readValue(Double::class.java.classLoader) as? Double,
        heartRate = parcel.readValue(Double::class.java.classLoader) as? Double,
        comment = parcel.readString(),
        locationList = parcel.createTypedArrayList(LatLng.CREATOR)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeValue(inputType)
        parcel.writeValue(activityType)
        parcel.writeValue(dateTime)
        parcel.writeValue(duration)
        parcel.writeValue(distance)
        parcel.writeString(distanceUnit)
        parcel.writeValue(avgPace)
        parcel.writeValue(avgSpeed)
        parcel.writeValue(calorie)
        parcel.writeValue(climb)
        parcel.writeValue(heartRate)
        parcel.writeString(comment)
        parcel.writeTypedList(locationList)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ExerciseEntry> {
        override fun createFromParcel(parcel: Parcel): ExerciseEntry {
            return ExerciseEntry(parcel)
        }

        override fun newArray(size: Int): Array<ExerciseEntry?> {
            return arrayOfNulls(size)
        }
    }
}