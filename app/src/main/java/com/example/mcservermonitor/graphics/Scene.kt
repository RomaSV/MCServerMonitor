package com.example.mcservermonitor.graphics

class Scene(val camera: Camera = Camera()) {

    private val _sceneObjects: MutableList<SceneObject> = mutableListOf()

    val getSceneObjects: List<SceneObject>
        get() = _sceneObjects.toList()

    fun addSceneObject(obj: SceneObject) {
        _sceneObjects.add(obj)
    }

    fun removeSceneObject(obj: SceneObject) {
        _sceneObjects.remove(obj)
    }

    fun clear() {
        _sceneObjects.clear()
    }

}