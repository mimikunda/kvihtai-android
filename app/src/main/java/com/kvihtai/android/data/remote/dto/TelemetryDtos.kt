package com.kvihtai.android.data.remote.dto

import com.kvihtai.android.domain.model.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LiveTelemetryDto(
    @SerialName("timestamp_ms") val timestampMs: Long,
    @SerialName("frame_id") val frameId: Long,
    @SerialName("lift_state") val liftState: String,
    @SerialName("bar_center") val barCenter: BarCenterDto,
    @SerialName("kinematics") val kinematics: KinematicsDto,
    @SerialName("phase") val phase: String
) {
    fun toDomain() = LiveTelemetry(
        timestampMs = timestampMs,
        frameId = frameId,
        liftState = liftState,
        barCenter = barCenter.toDomain(),
        kinematics = kinematics.toDomain(),
        phase = phase
    )
}

@Serializable
data class BarCenterDto(
    @SerialName("x_mm") val xMm: Double,
    @SerialName("y_mm") val yMm: Double
) {
    fun toDomain() = BarCenter(xMm = xMm, yMm = yMm)
}

@Serializable
data class KinematicsDto(
    @SerialName("current_velocity_mps") val currentVelocityMps: Double,
    @SerialName("acceleration_mps2") val accelerationMps2: Double
) {
    fun toDomain() = Kinematics(
        currentVelocityMps = currentVelocityMps,
        accelerationMps2 = accelerationMps2
    )
}

@Serializable
data class SetSummaryDto(
    @SerialName("set_id") val setId: String,
    @SerialName("timestamp") val timestamp: String,
    @SerialName("metrics") val metrics: SetMetricsDto,
    @SerialName("trajectory") val trajectory: List<TrajectoryPointDto>
) {
    fun toDomain() = SetSummary(
        setId = setId,
        timestamp = timestamp,
        metrics = metrics.toDomain(),
        trajectory = trajectory.map { it.toDomain() }
    )
}

@Serializable
data class SetMetricsDto(
    @SerialName("mean_concentric_velocity_mps") val meanConcentricVelocityMps: Double,
    @SerialName("peak_velocity_mps") val peakVelocityMps: Double,
    @SerialName("horizontal_loop_deviation_mm") val horizontalLoopDeviationMm: Double,
    @SerialName("vertical_displacement_mm") val verticalDisplacementMm: Double,
    @SerialName("duration_ms") val durationMs: Long
) {
    fun toDomain() = SetMetrics(
        meanConcentricVelocityMps = meanConcentricVelocityMps,
        peakVelocityMps = peakVelocityMps,
        horizontalLoopDeviationMm = horizontalLoopDeviationMm,
        verticalDisplacementMm = verticalDisplacementMm,
        durationMs = durationMs
    )
}

@Serializable
data class TrajectoryPointDto(
    @SerialName("x_mm") val xMm: Double,
    @SerialName("y_mm") val yMm: Double,
    @SerialName("t_ms") val tMs: Long
) {
    fun toDomain() = TrajectoryPoint(xMm = xMm, yMm = yMm, tMs = tMs)
}
