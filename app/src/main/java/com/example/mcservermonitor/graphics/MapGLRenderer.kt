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

    private val sceneObjects: MutableList<SceneObject> = mutableListOf()

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

        val meshData = Mesh.MeshData(
            floatArrayOf(
                // V0
                -0.5f, 0.5f, 0.5f,
                // V1
                -0.5f, -0.5f, 0.5f,
                // V2
                0.5f, -0.5f, 0.5f,
                // V3
                0.5f, 0.5f, 0.5f,
                // V4
                -0.5f, 0.5f, -0.5f,
                // V5
                0.5f, 0.5f, -0.5f,
                // V6
                -0.5f, -0.5f, -0.5f,
                // V7
                0.5f, -0.5f, -0.5f,

                // For text coords in top face
                // V8: V4 repeated
                -0.5f, 0.5f, -0.5f,
                // V9: V5 repeated
                0.5f, 0.5f, -0.5f,
                // V10: V0 repeated
                -0.5f, 0.5f, 0.5f,
                // V11: V3 repeated
                0.5f, 0.5f, 0.5f,

                // For text coords in right face
                // V12: V3 repeated
                0.5f, 0.5f, 0.5f,
                // V13: V2 repeated
                0.5f, -0.5f, 0.5f,

                // For text coords in left face
                // V14: V0 repeated
                -0.5f, 0.5f, 0.5f,
                // V15: V1 repeated
                -0.5f, -0.5f, 0.5f,

                // For text coords in bottom face
                // V16: V6 repeated
                -0.5f, -0.5f, -0.5f,
                // V17: V7 repeated
                0.5f, -0.5f, -0.5f,
                // V18: V1 repeated
                -0.5f, -0.5f, 0.5f,
                // V19: V2 repeated
                0.5f, -0.5f, 0.5f
            ),
            floatArrayOf(
                0.0f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.5f, 0.0f,

                0.0f, 0.0f,
                0.5f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,

                // For text coords in top face
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.0f, 1.0f,
                0.5f, 1.0f,

                // For text coords in right face
                0.0f, 0.0f,
                0.0f, 0.5f,

                // For text coords in left face
                0.5f, 0.0f,
                0.5f, 0.5f,

                // For text coords in bottom face
                0.5f, 0.0f,
                1.0f, 0.0f,
                0.5f, 0.5f,
                1.0f, 0.5f
            ),
            intArrayOf(
                // Front face
                0, 1, 3, 3, 1, 2,
                // Top Face
                8, 10, 11, 9, 8, 11,
                // Right face
                12, 13, 7, 5, 12, 7,
                // Left face
                14, 15, 6, 4, 14, 6,
                // Bottom face
                16, 18, 19, 17, 16, 19,
                // Back face
                4, 6, 7, 5, 4, 7
            ),
            loadTexture(context, R.drawable.dirt)
        )
        val shaderAttributes = intArrayOf(positionHandle, colorHandle)

        val obj = SceneObject(Mesh(meshData, shaderAttributes))
        obj.position = floatArrayOf(0f, 0f, -5f)
        sceneObjects.add(obj)

        GLES31.glEnable(GLES31.GL_DEPTH_TEST)
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES31.glViewport(0, 0, width, height)

        val ratio = width.toFloat() / height.toFloat()
        val near = 1f
        val far = 1000f
        calcProjectionMatrix(ratio, near, far)
    }

    override fun onDrawFrame(unused: GL10) {
        GLES31.glClear(GLES31.GL_COLOR_BUFFER_BIT or GLES31.GL_DEPTH_BUFFER_BIT)

        shaderProgram.bind()

        GLES31.glUniformMatrix4fv(projectionMatrixHandle, 1, false, getProjectionMatrix(), 0)
        GLES31.glUniform1i(textureSamplerHandle, 0)

        for (sceneObject in sceneObjects) {
            sceneObject.rotation[0]++
            sceneObject.rotation[1]++
            if (sceneObject.rotation[0] > 360f) {
                sceneObject.rotation[0] = 0f
                sceneObject.rotation[1] = 0f
            }
            calcWorldMatrix(sceneObject.position, sceneObject.rotation, sceneObject.scale)
            GLES31.glUniformMatrix4fv(worldMatrixHandle, 1, false, getWorldMatrix(), 0)
            sceneObject.mesh.draw()
        }

        shaderProgram.unbind()
    }

}