package com.example.mcservermonitor

import android.content.Context
import android.opengl.GLSurfaceView
import android.os.AsyncTask
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.ProgressBar
import com.example.mcservermonitor.ftp.getRegionFileByChunk
import com.example.mcservermonitor.graphics.*
import com.example.mcservermonitor.model.MapDecoder
import java.io.File
import kotlin.math.floor

class MapView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    GLSurfaceView(context, attrs) {

    private var previousX: Float = 0f
    private var previousY: Float = 0f
    private val moveSpeed: Float = 0.002f

    private val scaleGestureDetector: ScaleGestureDetector
    private var rescaled: Boolean = false

    private val renderer: MapGLRenderer

    // Temp file to test map construction
    private lateinit var regionFile: File

    lateinit var progressBar: ProgressBar
    var desiredX: Int = 0
    var desiredZ: Int = 0

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
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val rX = floor(desiredX / (16 * 32.0)).toInt()
        val rZ = floor(desiredZ / (16 * 32.0)).toInt()
        val fileName = "r.$rX.$rZ.mca"
        Log.i("MC-INFO", fileName)
        Log.i("MC-INFO", "Desired coords: $desiredX, $desiredZ")
        regionFile = File(context.filesDir, fileName)
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

    inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scene.camera.scale *= detector.scaleFactor
            return true
        }
    }

    inner class ConstructMapTask : AsyncTask<MapGLRenderer, Int, Any>() {

        override fun doInBackground(vararg renderer: MapGLRenderer) {

            if (!regionFile.exists()) {
                val success = getRegionFileByChunk(desiredX / 16, desiredZ / 16, regionFile)
                Log.i("MC-INFO", "Region downloaded: $success")
            }

            scene.clear()
            val mapDecoder = MapDecoder(regionFile)

            var startX = (desiredX / 16) % 32
            var startZ = (desiredZ / 16) % 32
            if (startX < 0) startX += 32
            if (startZ < 0) startZ += 32
            Log.i("MC-INFO", "StartX: $startX, StartZ: $startZ")


            for (z in 0..6) {
                for (x in 0..6) {
                    Log.i("MC-INFO", "Constructing a chunk at ($x,$z)")
                    val chunk = mapDecoder.decodeRegionFile(x + startX, z + startZ)

                    val chunkMesh = ChunkMesh(chunk, renderer[0].textureHandle)
                    val chunkObj = SceneObject(chunkMesh.mesh)
                    chunkObj.position = floatArrayOf((x - 3f) * 16, -4f * 16, (z - 3f) * 16)

                    scene.addSceneObject(chunkObj)
                    publishProgress(x + z * 16)
                }
            }

            renderer[0].scene = scene
        }

        override fun onProgressUpdate(vararg values: Int?) {
            if (values[0] != null) progressBar.progress = values[0]!!
        }

    }
}