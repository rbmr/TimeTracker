package com.rbmr.timetracker.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [WorkSession::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun workSessionDao(): WorkSessionDao
}