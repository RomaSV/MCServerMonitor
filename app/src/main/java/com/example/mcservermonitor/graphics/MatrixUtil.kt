package com.example.mcservermonitor.graphics

import android.opengl.Matrix

private val projectionMatrix = FloatArray(16)
private val worldMatrix = FloatArray(16)

fun calcProjectionMatrix(ratio: Float, near: Float, far: Float) {
    Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, near, far)
}

fun calcWorldMatrix(offset: FloatArray, rotation: FloatArray, scale: Float) {
    Matrix.setIdentityM(worldMatrix, 0)
    Matrix.translateM(worldMatrix, 0, offset[0], offset[1], offset[2])
    Matrix.rotateM(worldMatrix, 0, rotation[0], 1f, 0f, 0f)
    Matrix.rotateM(worldMatrix, 0, rotation[1], 0f, 1f, 0f)
    Matrix.rotateM(worldMatrix, 0, rotation[2], 0f, 0f, 1f)
    Matrix.scaleM(worldMatrix, 0, scale, scale, scale)
}

fun getProjectionMatrix(): FloatArray = projectionMatrix

fun getWorldMatrix(): FloatArray = worldMatrix