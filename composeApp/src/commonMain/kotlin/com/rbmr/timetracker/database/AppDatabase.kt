package com.rbmr.timetracker.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [WorkSession::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun workSessionDao(): WorkSessionDao
}