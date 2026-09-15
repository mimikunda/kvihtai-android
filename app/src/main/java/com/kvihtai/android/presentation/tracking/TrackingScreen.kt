package com.kvihtai.android.presentation.tracking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kvihtai.android.domain.model.BarCenter

@Composable
fun TrackingScreen(
    viewModel: TrackingViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TrackingScreenContent(
        uiState = uiState,
        modifier = modifier
    )
}

@Composable
fun TrackingScreenContent(
    uiState: TelemetryUiState,
    modifier: Modifier = Modifier
) {
    // High contrast dark gym theme background
    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(0xFF121212)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Trajectory Canvas
            TrajectoryCanvas(
                trajectoryPath = uiState.trajectoryPath,
                modifier = Modifier.fillMaxSize()
            )

            // Foreground UI Overlay (Status Header & Velocity Display)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Status Header Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1E1E1E).copy(alpha = 0.85f)
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Connection Status Dot
                        val statusColor = when (uiState.connectionStatus) {
                            is ConnectionStatus.Connected -> Color(0xFF00E676)
                            is ConnectionStatus.Connecting -> Color(0xFFFFEA00)
                            is ConnectionStatus.Disconnected -> Color(0xFFFF5252)
                            is ConnectionStatus.Error -> Color(0xFFFF5252)
                        }
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .background(statusColor, shape = androidx.compose.foundation.shape.CircleShape)
                        )
                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            val statusText = when (uiState.connectionStatus) {
                                is ConnectionStatus.Connected -> "Connected to Pi"
                                is ConnectionStatus.Connecting -> "Connecting..."
                                is ConnectionStatus.Disconnected -> "Disconnected"
                                is ConnectionStatus.Error -> "Connection Error"
                            }
                            Text(
                                text = statusText,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Phase: ${uiState.currentPhase.uppercase()}",
                                color = Color(0xFFB0BEC5),
                                fontSize = 12.sp
                            )
                        }

                        // Lift State Badge
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (uiState.liftState.equals("active", true)) Color(0xFF00E676).copy(alpha = 0.2f) else Color.DarkGray
                            )
                        ) {
                            Text(
                                text = uiState.liftState.uppercase(),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                color = if (uiState.liftState.equals("active", true)) Color(0xFF00E676) else Color.LightGray,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Large Numeric Overlay for Velocity
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1E1E1E).copy(alpha = 0.9f)
                    ),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "CURRENT VELOCITY",
                            color = Color(0xFF90A4AE),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = String.format("%.2f", uiState.currentVelocityMps),
                                color = Color(0xFF00E676),
                                fontSize = 64.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "m/s",
                                color = Color(0xFFB0BEC5),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Accel: ${String.format("%.1f", uiState.accelerationMps2)} m/s²",
                            color = Color(0xFF90A4AE),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TrackingScreenPreview() {
    val sampleState = TelemetryUiState(
        connectionStatus = ConnectionStatus.Connected,
        currentPhase = "second_pull",
        currentVelocityMps = 1.42,
        accelerationMps2 = 9.8,
        liftState = "active",
        trajectoryPath = listOf(
            BarCenter(0.0, 225.0),
            BarCenter(2.1, 340.2),
            BarCenter(10.5, 600.0),
            BarCenter(-5.2, 850.0),
            BarCenter(-15.4, 1100.5)
        )
    )
    TrackingScreenContent(uiState = sampleState)
}
