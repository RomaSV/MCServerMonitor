package com.example.mcservermonitor

import android.content.Context
import android.opengl.GLSurfaceView
import android.os.AsyncTask
import android.view.MotionEvent
import com.example.mcservermonitor.graphics.*
import com.example.mcservermonitor.model.Block
import com.example.mcservermonitor.model.Chunk
import com.example.mcservermonitor.model.Section

class MapView(context: Context) : GLSurfaceView(context) {

    private var previousX: Float = 0f
    private var previousY: Float = 0f

    private val renderer: MapGLRenderer

    init {

        setEGLContextClientVersion(3)

        renderer = MapGLRenderer(context)
        setRenderer(renderer)

        ConstructMapTask().execute(renderer)

        // Render the view only when there is a change in the drawing data
//        renderMode = RENDERMODE_WHEN_DIRTY
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x: Float = event.x
        val y: Float = event.y

        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                var dx: Float = x - previousX
                var dy: Float = y - previousY

                // reverse direction of rotation above the mid-line
                if (y > height / 2) {
                    dx *= -1
                }
                // reverse direction of rotation to left of the mid-line
                if (x < width / 2) {
                    dy *= -1
                }

                requestRender()
            }
        }

        previousX = x
        previousY = y
        return true
    }

    class ConstructMapTask : AsyncTask<MapGLRenderer, Void, Any>() {

        override fun doInBackground(vararg renderer: MapGLRenderer) {
            while (!renderer[0].ready) {
                Thread.sleep(100)
            }

            // setup scene
            val sectionData = Array(16) { Array(16) { Array(16) { Block.AIR } } }
            sectionData[0][0][0] = Block.GRASS_BLOCK
            sectionData[0][0][1] = Block.DIRT
            sectionData[0][1][1] = Block.STONE
            val chunkSection = Section(sectionData)
            val chunk = Chunk(listOf(chunkSection))

            val chunkMesh = ChunkMesh(chunk, renderer[0].textureHandle)
            val chunkObj = SceneObject(chunkMesh.mesh)
            chunkObj.position = floatArrayOf(0f, 0f, -5f)

            val scene = Scene()
            scene.addSceneObject(chunkObj)

            renderer[0].scene = scene
        }

    }
}