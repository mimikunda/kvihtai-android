package com.kvihtai.android.presentation.tracking

import com.kvihtai.android.domain.model.BarCenter

sealed interface ConnectionStatus {
    data object Disconnected : ConnectionStatus
    data object Connecting : ConnectionStatus
    data object Connected : ConnectionStatus
    data class Error(val message: String) : ConnectionStatus
}

data class TelemetryUiState(
    val connectionStatus: ConnectionStatus = ConnectionStatus.Disconnected,
    val currentPhase: String = "idle",
    val currentVelocityMps: Double = 0.0,
    val accelerationMps2: Double = 0.0,
    val trajectoryPath: List<BarCenter> = emptyList(),
    val liftState: String = "idle"
)
