package com.example.mcservermonitor.graphics

data class SceneObject(val mesh: Mesh) {

    var position = floatArrayOf(0f, 0f, 0f)

    var rotation = floatArrayOf(0f, 0f, 0f)

    var scale: Float = 1f

}