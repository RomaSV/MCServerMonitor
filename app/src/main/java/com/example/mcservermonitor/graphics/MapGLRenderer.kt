package com.example.mcservermonitor.graphics

import android.content.Context
import android.opengl.GLES31
import android.opengl.GLSurfaceView
import com.example.mcservermonitor.R
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MapGLRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private var orthoMode = true

    private var ratio: Float = 0f

    private lateinit var shaderProgram: ShaderProgram

    private var positionHandle: Int = 0
    private var colorHandle: Int = 0
    private var vertexNormalHandle: Int = 0

    private var projectionMatrixHandle: Int = 0
    private var modelViewMatrixHandle: Int = 0
    private var textureSamplerHandle: Int = 0
    private var ambientLightHandle: Int = 0

    private var sceneChanged = false
    private val drawnObjects: MutableList<SceneObject> = mutableListOf()

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
        shaderProgram.link(attributes = arrayOf("aPosition", "aTexture", "aVertexNormal"))
        positionHandle = GLES31.glGetAttribLocation(shaderProgram.programHandle, "aPosition")
        colorHandle = GLES31.glGetAttribLocation(shaderProgram.programHandle, "aTexture")
        vertexNormalHandle =
            GLES31.glGetAttribLocation(shaderProgram.programHandle, "aVertexNormal")
        projectionMatrixHandle =
            GLES31.glGetUniformLocation(shaderProgram.programHandle, "uProjectionMatrix")
        modelViewMatrixHandle =
            GLES31.glGetUniformLocation(shaderProgram.programHandle, "uModelViewMatrix")
        textureSamplerHandle =
            GLES31.glGetUniformLocation(shaderProgram.programHandle, "uTextureSampler")
        ambientLightHandle =
            GLES31.glGetUniformLocation(shaderProgram.programHandle, "uAmbientLight")

        textureHandle = loadTexture(context, R.drawable.textures)
        shaderAttributes = intArrayOf(positionHandle, colorHandle, vertexNormalHandle)

        GLES31.glEnable(GLES31.GL_DEPTH_TEST)
        GLES31.glEnable(GLES31.GL_BLEND)
        GLES31.glBlendFunc(GLES31.GL_SRC_ALPHA, GLES31.GL_ONE_MINUS_SRC_ALPHA)
        GLES31.glDepthMask(true)

        ready = true
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES31.glViewport(0, 0, width, height)

        ratio = width.toFloat() / height.toFloat()
        val near = -10000f
        val far = 10000f
        calcProjectionMatrix(ratio, near, far, ortho = orthoMode)
    }

    override fun onDrawFrame(unused: GL10) {

        if (sceneChanged) {
            val objIter = drawnObjects.iterator()
            while (objIter.hasNext()) {
                objIter.next().mesh.cleanUp()
                objIter.remove()
            }

            for (obj in scene.getSceneObjects) {
                obj.mesh.construct(shaderAttributes)
                drawnObjects.add(obj)
            }

            sceneChanged = false
        }

        GLES31.glClear(GLES31.GL_COLOR_BUFFER_BIT or GLES31.GL_DEPTH_BUFFER_BIT)

        shaderProgram.bind()

        calcViewMatrix(scene.camera)
        calcProjectionMatrix(ratio, -10000f, 10000f, scene.camera.scale, orthoMode)

        GLES31.glUniform1i(textureSamplerHandle, 0)
        GLES31.glUniform3f(ambientLightHandle, 0.8f, 0.8f, 0.8f)
        GLES31.glUniformMatrix4fv(projectionMatrixHandle, 1, false, getProjectionMatrix(), 0)

        for (sceneObject in drawnObjects) {
            calcModelViewMatrix(sceneObject, getViewMatrix())
            GLES31.glUniformMatrix4fv(modelViewMatrixHandle, 1, false, getModelViewMatrix(), 0)
            sceneObject.mesh.draw(shaderAttributes)
        }

        shaderProgram.unbind()
    }

}