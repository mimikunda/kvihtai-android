package com.kvihtai.android.domain.model

data class LiveTelemetry(
    val timestampMs: Long,
    val frameId: Long,
    val liftState: String,
    val barCenter: BarCenter,
    val kinematics: Kinematics,
    val phase: String
)

data class BarCenter(
    val xMm: Double,
    val yMm: Double
)

data class Kinematics(
    val currentVelocityMps: Double,
    val accelerationMps2: Double
)

data class SetSummary(
    val setId: String,
    val timestamp: String,
    val metrics: SetMetrics,
    val trajectory: List<TrajectoryPoint>
)

data class SetMetrics(
    val meanConcentricVelocityMps: Double,
    val peakVelocityMps: Double,
    val horizontalLoopDeviationMm: Double,
    val verticalDisplacementMm: Double,
    val durationMs: Long
)

data class TrajectoryPoint(
    val xMm: Double,
    val yMm: Double,
    val tMs: Long
)
