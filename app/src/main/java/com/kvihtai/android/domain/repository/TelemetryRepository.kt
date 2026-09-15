package com.kvihtai.android.domain.repository

import com.kvihtai.android.domain.model.LiveTelemetry
import com.kvihtai.android.domain.model.SetSummary
import kotlinx.coroutines.flow.SharedFlow

interface TelemetryRepository {
    val liveTelemetry: SharedFlow<LiveTelemetry>

    suspend fun connectWebSocket(ip: String, port: Int = 8080)
    suspend fun disconnectWebSocket()
    suspend fun getLatestSetSummary(ip: String, port: Int = 8080): SetSummary
}
