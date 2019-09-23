package com.example.mcservermonitor.graphics

import android.content.Context
import android.opengl.GLES31
import android.opengl.GLSurfaceView
import com.example.mcservermonitor.R
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MapGLRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private lateinit var shaderProgram: ShaderProgram

    private var positionHandle: Int = 0

    private var colorHandle: Int = 0

    private var projectionMatrixHandle: Int = 0
    private var worldMatrixHandle: Int = 0
    private var textureSamplerHandle: Int = 0

    private var sceneChanged = false

    private lateinit var shaderAttributes: IntArray

    /**
     * Pointer to the generated texture in OpenGL.
     */
    var textureHandle = 0
        private set

    /**
     * Scene to be rendered.
     * If you need to switch the scene, simply change the variable.
     */
    var scene = Scene()
        set(value) {
            if (ready) {
                sceneChanged = true
                field = value
            }
        }

    /**
     * Indicates status of the drawing surface initialisation.
     */
    var ready: Boolean = false
        private set

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {

        shaderProgram = ShaderProgram(context)
        shaderProgram.createVertexShader(R.raw.vertex)
        shaderProgram.createFragmentShader(R.raw.fragment)
        shaderProgram.link(attributes = arrayOf("aPosition", "aTexture"))
        positionHandle = GLES31.glGetAttribLocation(shaderProgram.programHandle, "aPosition")
        colorHandle = GLES31.glGetAttribLocation(shaderProgram.programHandle, "aTexture")
        projectionMatrixHandle =
            GLES31.glGetUniformLocation(shaderProgram.programHandle, "uProjectionMatrix")
        worldMatrixHandle = GLES31.glGetUniformLocation(shaderProgram.programHandle, "uWorldMatrix")
        textureSamplerHandle =
            GLES31.glGetUniformLocation(shaderProgram.programHandle, "uTextureSampler")

        textureHandle = loadTexture(context, R.drawable.textures)
        shaderAttributes = intArrayOf(positionHandle, colorHandle)

        GLES31.glEnable(GLES31.GL_DEPTH_TEST)

        ready = true
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES31.glViewport(0, 0, width, height)

        val ratio = width.toFloat() / height.toFloat()
        val near = 1f
        val far = 1000f
        calcProjectionMatrix(ratio, near, far)
    }

    override fun onDrawFrame(unused: GL10) {

        if (sceneChanged) {
            for (obj in scene.getSceneObjects) {
                obj.mesh.construct(shaderAttributes)
            }
            sceneChanged = false
        }

        GLES31.glClear(GLES31.GL_COLOR_BUFFER_BIT or GLES31.GL_DEPTH_BUFFER_BIT)

        shaderProgram.bind()

        GLES31.glUniformMatrix4fv(projectionMatrixHandle, 1, false, getProjectionMatrix(), 0)
        GLES31.glUniform1i(textureSamplerHandle, 0)

        for (sceneObject in scene.getSceneObjects) {
            sceneObject.rotation[0]++
            sceneObject.rotation[1]++
            if (sceneObject.rotation[0] > 360f) {
                sceneObject.rotation[0] = 0f
                sceneObject.rotation[1] = 0f
            }
            calcWorldMatrix(sceneObject.position, sceneObject.rotation, sceneObject.scale)
            GLES31.glUniformMatrix4fv(worldMatrixHandle, 1, false, getWorldMatrix(), 0)
            sceneObject.mesh.draw(shaderAttributes)
        }

        shaderProgram.unbind()
    }

}