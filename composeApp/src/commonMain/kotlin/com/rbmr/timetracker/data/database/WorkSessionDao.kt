package com.rbmr.timetracker.data.database

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
    suspend fun insert(session: WorkSession): Long

    @Update
    suspend fun update(session: WorkSession)

    @Delete
    suspend fun delete(session: WorkSession)

    @Query("SELECT * FROM WorkSession ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<WorkSession>>

    @Query("SELECT * FROM WorkSession WHERE id = :id")
    suspend fun getSessionById(id: Long): WorkSession?

    @Query("SELECT * FROM WorkSession WHERE id = :id")
    fun getSessionByIdFlow(id: Long): Flow<WorkSession?>

    @Query("SELECT * FROM WorkSession WHERE endTime IS NOT NULL ORDER BY startTime DESC")
    fun getHistoricalSessionsFlow(): Flow<List<WorkSession>>

    @Query("SELECT * FROM WorkSession WHERE endTime IS NULL LIMIT 1")
    suspend fun getOngoingSession(): WorkSession?

    @Query("SELECT * FROM WorkSession WHERE endTime IS NULL LIMIT 1")
    fun getOngoingSessionFlow(): Flow<WorkSession?>
}