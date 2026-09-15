package com.kvihtai.android.data.repository

import com.kvihtai.android.data.remote.dto.LiveTelemetryDto
import com.kvihtai.android.data.remote.dto.SetSummaryDto
import com.kvihtai.android.domain.model.LiveTelemetry
import com.kvihtai.android.domain.model.SetSummary
import com.kvihtai.android.domain.repository.TelemetryRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.util.concurrent.atomic.AtomicBoolean

class TelemetryRepositoryImpl : TelemetryRepository {

    private val client = HttpClient(CIO) {
        install(WebSockets)
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    private val _liveTelemetry = MutableSharedFlow<LiveTelemetry>(replay = 1)
    override val liveTelemetry: SharedFlow<LiveTelemetry> = _liveTelemetry.asSharedFlow()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var webSocketJob: Job? = null
    private val isConnected = AtomicBoolean(false)

    override suspend fun connectWebSocket(ip: String, port: Int) {
        if (isConnected.get()) return
        isConnected.set(true)

        webSocketJob?.cancel()
        webSocketJob = scope.launch {
            var retryDelayMs = 1000L
            val maxRetryDelayMs = 16000L

            while (isActive && isConnected.get()) {
                try {
                    client.webSocket(host = ip, port = port, path = "/ws/live") {
                        // Reset retry delay upon successful connection
                        retryDelayMs = 1000L

                        for (frame in incoming) {
                            if (frame is Frame.Text) {
                                val text = frame.readText()
                                try {
                                    val dto = Json.decodeFromString<LiveTelemetryDto>(text)
                                    _liveTelemetry.emit(dto.toDomain())
                                } catch (e: Exception) {
                                    // Log or handle parsing error for individual frames
                                }
                            }
                        }
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    // Connection dropped or failed, wait before retrying
                    if (!isConnected.get()) break
                    delay(retryDelayMs)
                    retryDelayMs = (retryDelayMs * 2).coerceAtMost(maxRetryDelayMs)
                }
            }
        }
    }

    override suspend fun disconnectWebSocket() {
        isConnected.set(false)
        webSocketJob?.cancel()
        webSocketJob = null
    }

    override suspend fun getLatestSetSummary(ip: String, port: Int): SetSummary {
        val url = "http://$ip:$port/api/v1/sets/latest"
        val dto = client.get(url).body<SetSummaryDto>()
        return dto.toDomain()
    }
}
