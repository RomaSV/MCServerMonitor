package com.example.mcservermonitor.graphics

class Scene() {

    private val _sceneObjects: MutableList<SceneObject> = mutableListOf()

    val getSceneObjects: List<SceneObject>
        get() = _sceneObjects.toList()

    fun addSceneObject(obj: SceneObject) {
        _sceneObjects.add(obj)
    }

    fun removeSceneObject(obj: SceneObject) {
        _sceneObjects.remove(obj)
    }

}