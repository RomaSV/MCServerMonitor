package com.example.mcservermonitor

import android.content.Context
import android.opengl.GLSurfaceView
import android.os.AsyncTask
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import com.example.mcservermonitor.graphics.*
import com.example.mcservermonitor.model.Block
import com.example.mcservermonitor.model.Chunk
import com.example.mcservermonitor.model.Section

class MapView(context: Context) : GLSurfaceView(context) {

    private var previousX: Float = 0f
    private var previousY: Float = 0f
    private val moveSpeed: Float = 0.002f

    private val scaleGestureDetector: ScaleGestureDetector
    private var rescaled: Boolean = false

    private val renderer: MapGLRenderer

    val scene = Scene(
        Camera(
            position = floatArrayOf(-2f, 3f, 0f),
            rotation = floatArrayOf(30f, 30f, 0f),
            scale = 0.3f
        )
    )

    init {

        setEGLContextClientVersion(3)

        renderer = MapGLRenderer(context)
        setRenderer(renderer)

        ConstructMapTask().execute(renderer)

        scaleGestureDetector = ScaleGestureDetector(context, ScaleListener())
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {

        scaleGestureDetector.onTouchEvent(event)

        val x: Float = event.x
        val y: Float = event.y

        if (scaleGestureDetector.isInProgress) {
            rescaled = true
            return true
        }

        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                if (!rescaled) {
                    var dx: Float = x - previousX
                    var dy: Float = y - previousY

                    scene.camera.movePosition(
                        -dx * moveSpeed / scene.camera.scale,
                        dy * moveSpeed / scene.camera.scale,
                        0f
                    )
                }
            }
            MotionEvent.ACTION_UP -> {
                rescaled = false
            }
        }

        previousX = x
        previousY = y
        return true
    }

    inner class ScaleListener: ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scene.camera.scale *= detector.scaleFactor
            return true
        }
    }

    inner class ConstructMapTask : AsyncTask<MapGLRenderer, Void, Any>() {

        override fun doInBackground(vararg renderer: MapGLRenderer) {
            while (!renderer[0].ready) {
                Thread.sleep(100)
            }

            // setup scene
            val sectionData = Array(16) { Array(16) { Array(16) { Block.AIR } } }
            sectionData[0][0][0] = Block.GRASS_BLOCK
            sectionData[0][0][1] = Block.GRASS_BLOCK
            sectionData[1][1][1] = Block.GRASS_BLOCK
            sectionData[0][1][1] = Block.DIRT
            sectionData[0][2][1] = Block.SAND
            sectionData[0][1][2] = Block.SAND
            sectionData[0][1][0] = Block.SAND
            sectionData[0][2][0] = Block.WATER
            val chunkSection = Section(sectionData)
            val chunk = Chunk(listOf(chunkSection))

            val chunkMesh = ChunkMesh(chunk, renderer[0].textureHandle)
            val chunkObj = SceneObject(chunkMesh.mesh)
            chunkObj.position = floatArrayOf(0f, 0f, -5f)

            scene.addSceneObject(chunkObj)

            renderer[0].scene = scene
        }

    }
}