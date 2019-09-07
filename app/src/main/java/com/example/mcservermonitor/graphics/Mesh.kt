package com.example.mcservermonitor.graphics

import android.opengl.GLES31
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.util.jar.Attributes

class Mesh(meshData: MeshData, private val shaderAttributes: IntArray) {

    private val positionBufferHandle: Int

    private val colorBufferHandle: Int

    private val indexBufferHandle: Int

    private val vaoId: Int

    private val vertexCount: Int = meshData.indices.size

    init {
        val vaoIdBuffer = IntBuffer.allocate(1)
        GLES31.glGenVertexArrays(1, vaoIdBuffer)
        vaoId = vaoIdBuffer[0]
        GLES31.glBindVertexArray(vaoId)


        val buffers = IntArray(3)
        GLES31.glGenBuffers(3, buffers, 0)

        // Position VBO
        val vertexBuffer: FloatBuffer = ByteBuffer.allocateDirect(meshData.positions.size * 4).run {
            order(ByteOrder.nativeOrder())

            asFloatBuffer().apply {
                put(meshData.positions)
                flip()
            }
        }

        positionBufferHandle = buffers[0]
        GLES31.glBindBuffer(GLES31.GL_ARRAY_BUFFER, positionBufferHandle)
        GLES31.glBufferData(GLES31.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 4, vertexBuffer, GLES31.GL_STATIC_DRAW)
        GLES31.glVertexAttribPointer(shaderAttributes[0], 3, GLES31.GL_FLOAT, false, 0, 0)



        // Color VBO
        val colorBuffer: FloatBuffer = ByteBuffer.allocateDirect(meshData.colors.size * 4).run {
            order(ByteOrder.nativeOrder())

            asFloatBuffer().apply {
                put(meshData.colors)
                flip()
            }
        }

        colorBufferHandle = buffers[1]
        GLES31.glBindBuffer(GLES31.GL_ARRAY_BUFFER, colorBufferHandle)
        GLES31.glBufferData(GLES31.GL_ARRAY_BUFFER, colorBuffer.capacity() * 4, colorBuffer, GLES31.GL_STATIC_DRAW)
        GLES31.glVertexAttribPointer(shaderAttributes[1], 3, GLES31.GL_FLOAT, false, 0, 0)

        // Index VBO
        val indexBuffer: IntBuffer = ByteBuffer.allocateDirect(meshData.indices.size * 4).run {
            order(ByteOrder.nativeOrder())

            asIntBuffer().apply {
                put(meshData.indices)
                flip()
            }
        }


        indexBufferHandle = buffers[2]
        GLES31.glBindBuffer(GLES31.GL_ELEMENT_ARRAY_BUFFER, indexBufferHandle)
        GLES31.glBufferData(GLES31.GL_ELEMENT_ARRAY_BUFFER, indexBuffer.capacity() * 4, indexBuffer, GLES31.GL_STATIC_DRAW)

        // Unbind buffer and vertex array
        GLES31.glBindBuffer(GLES31.GL_ARRAY_BUFFER, 0)
        GLES31.glBindVertexArray(0)

        vaoIdBuffer.limit(0)
        vertexBuffer.limit(0)
        indexBuffer.limit(0)
    }

    fun draw() {
        GLES31.glBindVertexArray(vaoId)
        GLES31.glEnableVertexAttribArray(shaderAttributes[0])
        GLES31.glEnableVertexAttribArray(shaderAttributes[1])

        GLES31.glBindBuffer(GLES31.GL_ARRAY_BUFFER, 0)
        GLES31.glDrawElements(GLES31.GL_TRIANGLES, vertexCount, GLES31.GL_UNSIGNED_INT, 0)

        GLES31.glDisableVertexAttribArray(shaderAttributes[0])
        GLES31.glDisableVertexAttribArray(shaderAttributes[1])
        GLES31.glBindVertexArray(0)
    }

    fun cleanUp() {
        GLES31.glDisableVertexAttribArray(0)

        //Delete all VBOs
        GLES31.glBindBuffer(GLES31.GL_ARRAY_BUFFER, 0)
        GLES31.glDeleteBuffers(3, intArrayOf(positionBufferHandle, indexBufferHandle), 0)

        //Delete the VAO
        GLES31.glBindVertexArray(0)
        GLES31.glDeleteVertexArrays(1, intArrayOf(vaoId), 0)
    }

    data class MeshData(val positions: FloatArray, val colors: FloatArray, val indices: IntArray) {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as MeshData

            if (!positions.contentEquals(other.positions)) return false
            if (!colors.contentEquals(other.colors)) return false
            if (!indices.contentEquals(other.indices)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = positions.contentHashCode()
            result = 31 * result + colors.contentHashCode()
            result = 31 * result + indices.contentHashCode()
            return result
        }

    }

}