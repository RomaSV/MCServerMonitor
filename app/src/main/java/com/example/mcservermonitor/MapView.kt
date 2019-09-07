package com.example.mcservermonitor

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import com.example.mcservermonitor.graphics.MapGLRenderer

private const val TOUCH_SCALE_FACTOR: Float = 180.0f / 320f

class MapView(context: Context) : GLSurfaceView(context) {

    private var previousX: Float = 0f
    private var previousY: Float = 0f

    private val renderer: MapGLRenderer

    init {

        setEGLContextClientVersion(3)

        renderer = MapGLRenderer(context)

        setRenderer(renderer)

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

//                renderer.angle += (dx + dy) * TOUCH_SCALE_FACTOR
                requestRender()
            }
        }

        previousX = x
        previousY = y
        return true
    }
}