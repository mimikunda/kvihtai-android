package com.kvihtai.android.presentation.tracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kvihtai.android.data.repository.TelemetryRepositoryImpl
import com.kvihtai.android.domain.repository.TelemetryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TrackingViewModel(
    private val telemetryRepository: TelemetryRepository = TelemetryRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(TelemetryUiState())
    val uiState: StateFlow<TelemetryUiState> = _uiState.asStateFlow()

    init {
        connectToPi("192.168.4.1")
        observeTelemetry()
    }

    fun connectToPi(ip: String, port: Int = 8080) {
        viewModelScope.launch {
            _uiState.update { it.copy(connectionStatus = ConnectionStatus.Connecting) }
            try {
                telemetryRepository.connectWebSocket(ip, port)
                _uiState.update { it.copy(connectionStatus = ConnectionStatus.Connected) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(connectionStatus = ConnectionStatus.Error(e.localizedMessage ?: "Connection failed")) 
                }
            }
        }
    }

    private fun observeTelemetry() {
        viewModelScope.launch {
            telemetryRepository.liveTelemetry.collect { telemetry ->
                _uiState.update { currentState ->
                    val newLiftState = telemetry.liftState
                    val updatedPath = if (newLiftState.equals("active", ignoreCase = true)) {
                        currentState.trajectoryPath + telemetry.barCenter
                    } else if (newLiftState.equals("idle", ignoreCase = true) || newLiftState.equals("completed", ignoreCase = true)) {
                        emptyList()
                    } else {
                        currentState.trajectoryPath
                    }

                    currentState.copy(
                        currentPhase = telemetry.phase,
                        currentVelocityMps = telemetry.kinematics.currentVelocityMps,
                        accelerationMps2 = telemetry.kinematics.accelerationMps2,
                        liftState = newLiftState,
                        trajectoryPath = updatedPath
                    )
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            telemetryRepository.disconnectWebSocket()
        }
    }
}
