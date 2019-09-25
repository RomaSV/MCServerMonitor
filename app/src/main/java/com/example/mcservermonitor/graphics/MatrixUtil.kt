package com.example.mcservermonitor.graphics

import android.opengl.Matrix

private val projectionMatrix = FloatArray(16)
private val modelViewMatrix = FloatArray(16)
private val viewMatrix = FloatArray(16)
private val modelMatrix = FloatArray(16)

fun calcProjectionMatrix(ratio: Float, near: Float, far: Float, scale: Float = 1f, ortho: Boolean = false) {
    if (ortho) {
        Matrix.orthoM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, near, far)
    } else {
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, near, far)
    }
    Matrix.scaleM(projectionMatrix, 0, scale, scale, scale)
}

fun calcModelViewMatrix(obj: SceneObject, viewMatrix: FloatArray) {
    val offset = obj.position
    val rotation = obj.rotation
    val scale = obj.scale
    Matrix.setIdentityM(modelMatrix, 0)
    Matrix.translateM(modelMatrix, 0, offset[0], offset[1], offset[2])
    Matrix.rotateM(modelMatrix, 0, -rotation[0], 1f, 0f, 0f)
    Matrix.rotateM(modelMatrix, 0, -rotation[1], 0f, 1f, 0f)
    Matrix.rotateM(modelMatrix, 0, -rotation[2], 0f, 0f, 1f)
    Matrix.scaleM(modelMatrix, 0, scale, scale, scale)
    Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0)
}

fun calcViewMatrix(camera: Camera) {
    Matrix.setIdentityM(viewMatrix, 0)
    Matrix.rotateM(viewMatrix, 0, camera.rotation[0], 1f, 0f, 0f)
    Matrix.rotateM(viewMatrix, 0, camera.rotation[1], 0f, 1f, 0f)
    Matrix.translateM(viewMatrix, 0, -camera.position[0], -camera.position[1], -camera.position[2])
}

fun getProjectionMatrix(): FloatArray = projectionMatrix

fun getModelViewMatrix(): FloatArray = modelViewMatrix

fun getViewMatrix(): FloatArray = viewMatrix