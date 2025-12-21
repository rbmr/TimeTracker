package com.rbmr.timetracker.data.repository

import com.rbmr.timetracker.data.database.WorkSession
import com.rbmr.timetracker.data.database.WorkSessionDao
import kotlinx.coroutines.flow.Flow

class WorkSessionRepository(private val dao: WorkSessionDao) {

    // Sources of Truth
    val ongoingSession: Flow<WorkSession?> = dao.getOngoingSessionFlow()
    val historicalSessions: Flow<List<WorkSession>> = dao.getHistoricalSessionsFlow()

    val allSessions: Flow<List<WorkSession>> = dao.getAllSessions()

    fun getSessionById(id: Long): Flow<WorkSession?> = dao.getSessionByIdFlow(id)

    // Actions
    suspend fun insert(session: WorkSession) = dao.insert(session)
    suspend fun insertAll(sessions: List<WorkSession>) = dao.insertAll(sessions)
    suspend fun update(session: WorkSession) = dao.update(session)
    suspend fun delete(session: WorkSession) = dao.delete(session)

}