package com.example.mcservermonitor.graphics

import android.content.Context
import android.opengl.GLES31
import android.util.Log

class ShaderProgram(private val context: Context) {

    val programHandle: Int = GLES31.glCreateProgram()

    private var vertexShaderId: Int = 0
    private var fragmentShaderId: Int = 0

    init {
        if (programHandle == 0) {
            Log.e("AH_SHIT", "Error creating a shader program")
        }
    }

    fun createVertexShader(shaderRes: Int) {
        vertexShaderId = createShader(GLES31.GL_VERTEX_SHADER, shaderRes)
    }

    fun createFragmentShader(shaderRes: Int) {
        fragmentShaderId = createShader(GLES31.GL_FRAGMENT_SHADER, shaderRes)
    }

    fun link(attributes: Array<String>?) {
        if (attributes != null) {
            for (i in 0 until attributes.size) {
                GLES31.glBindAttribLocation(programHandle, i, attributes[i])
            }
        }

        GLES31.glLinkProgram(programHandle)

        var linkStatus = IntArray(1)
        GLES31.glGetProgramiv(programHandle, GLES31.GL_LINK_STATUS, linkStatus, 0)
        if (linkStatus[0] == 0) {
            Log.e("AH_SHIT", "Error compiling shader program")
        }

        if (vertexShaderId != 0) {
            GLES31.glDetachShader(programHandle, vertexShaderId)
        }
        if (fragmentShaderId != 0) {
            GLES31.glDetachShader(programHandle, fragmentShaderId)
        }

        GLES31.glValidateProgram(programHandle)
    }

    fun bind() {
        GLES31.glUseProgram(programHandle)
    }

    fun unbind() {
        GLES31.glUseProgram(0)
    }

    private fun createShader(type: Int, shaderRes: Int): Int {
        val shaderSrc = context.resources.openRawResource(shaderRes)
        val shaderCode = shaderSrc.bufferedReader().readLines().joinToString("\n")

        return createShader(type, shaderCode)
    }

    private fun createShader(type: Int, shaderCode: String): Int {
        return GLES31.glCreateShader(type).also {
            GLES31.glShaderSource(it, shaderCode)
            GLES31.glCompileShader(it)
            GLES31.glAttachShader(programHandle, it)
        }
    }
}