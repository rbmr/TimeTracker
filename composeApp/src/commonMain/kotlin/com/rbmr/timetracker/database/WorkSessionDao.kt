package com.rbmr.timetracker.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: WorkSession)

    @Update
    suspend fun update(session: WorkSession)

    @Delete
    suspend fun delete(session: WorkSession)

    @Query("SELECT * FROM WorkSession ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<WorkSession>>

    @Query("SELECT * FROM WorkSession WHERE id = :id")
    suspend fun getSessionById(id: Long): WorkSession?
}