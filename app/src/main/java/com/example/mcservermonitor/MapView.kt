package com.example.mcservermonitor

import android.content.Context
import android.opengl.GLSurfaceView
import android.os.AsyncTask
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import com.example.mcservermonitor.graphics.*
import com.example.mcservermonitor.model.*
import java.io.File
import java.io.FileOutputStream

class MapView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    GLSurfaceView(context, attrs) {

    private var previousX: Float = 0f
    private var previousY: Float = 0f
    private val moveSpeed: Float = 0.002f

    private val scaleGestureDetector: ScaleGestureDetector
    private var rescaled: Boolean = false

    private val renderer: MapGLRenderer

    // Temp file to test map construction
    private val regionFile: File

    var currentChunkX: Int = 1
        private set
    var currentChunkZ: Int = 2
        private set

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
        scaleGestureDetector = ScaleGestureDetector(context, ScaleListener())
        regionFile = createTempFile("region", "mca", context.cacheDir)
        val inputStream = context.assets.open("test-region.mca")
        val outputStream = FileOutputStream(regionFile)
        val buffer = ByteArray(1024 * 512)
        var count = inputStream.read(buffer)
        while (count != -1) {
            outputStream.write(buffer, 0, count)
            count = inputStream.read(buffer)
        }
        outputStream.close()
        inputStream.close()
        ConstructMapTask().execute(renderer)
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
                    val dx: Float = x - previousX
                    val dy: Float = y - previousY

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

    fun updateCoords(x: Int, z: Int) {
        if (x !in 0..15 || z !in 0..15) return
        currentChunkX = x
        currentChunkZ = z
        ConstructMapTask().execute(renderer)
    }

    inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
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

            scene.clear()

            val mapDecoder = MapDecoder()

            val sectionData = mapDecoder.decodeRegionFile(regionFile, currentChunkX, currentChunkZ)
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