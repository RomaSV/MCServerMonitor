package com.example.mcservermonitor.graphics

import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

class Camera(
    var position: FloatArray = FloatArray(3) { 0f },
    var rotation: FloatArray = FloatArray(3) { 0f },
    var scale: Float = 1f
) {
    fun movePosition(offsetX: Float, offsetY: Float, offsetZ: Float) {
        if (abs(offsetZ) > 0.00001f) {
            position[0] += sin(Math.toRadians(rotation[1].toDouble())).toFloat() * -1.0f * offsetZ
            position[2] += cos(Math.toRadians(rotation[1].toDouble())).toFloat() * offsetZ
        }
        if (abs(offsetX) > 0.00001f) {
            position[0] += sin(Math.toRadians(rotation[1].toDouble() - 90)).toFloat() * -1.0f * offsetX
            position[2] += cos(Math.toRadians(rotation[1].toDouble() - 90)).toFloat() * offsetX
        }
        position[1] += offsetY
    }

    fun rotate(offsetX: Float, offsetY: Float, offsetZ: Float) {
        rotation[0] += offsetX
        rotation[1] += offsetY
        rotation[2] += offsetZ
    }
}