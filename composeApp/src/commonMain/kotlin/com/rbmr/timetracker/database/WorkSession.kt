package com.rbmr.timetracker.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WorkSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startTime: Long, // Stored as Epoch Milliseconds
    val endTime: Long?, // Stored as Epoch Milliseconds
    val note: String
)