package com.example.screentimemanager.RoomDatabase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "AppUsage")
class AppUsageSchema {
    @PrimaryKey(autoGenerate = true)
    var applicationID: Long = 0L
//
//    @ColumnInfo(name="inputType")
//    var startTime: String = ""
//
//    @ColumnInfo(name="unit")
//    var endTime: String = ""

}