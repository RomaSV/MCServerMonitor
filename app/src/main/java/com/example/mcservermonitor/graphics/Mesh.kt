package com.example.mcservermonitor.graphics

import android.opengl.GLES31
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer

class Mesh(private val meshData: MeshData) {

    private var positionBufferHandle = 0

    private var textureCoordsBufferHandle = 0

    private var normalBufferHandle = 0

    private var indexBufferHandle = 0

    private var vaoId = 0

    private val vertexCount: Int = meshData.indices.size

    /**
     * Pass the MeshData to OpenGL
     * Must be called ONLY in the GLSurfaceView.Renderer methods
     */
    fun construct(shaderAttributes: IntArray) {
        val vaoIdBuffer = IntBuffer.allocate(1)
        GLES31.glGenVertexArrays(1, vaoIdBuffer)
        vaoId = vaoIdBuffer[0]
        GLES31.glBindVertexArray(vaoId)


        val buffers = IntArray(4)
        GLES31.glGenBuffers(4, buffers, 0)

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
        GLES31.glBufferData(
            GLES31.GL_ARRAY_BUFFER,
            vertexBuffer.capacity() * 4,
            vertexBuffer,
            GLES31.GL_STATIC_DRAW
        )
        GLES31.glVertexAttribPointer(shaderAttributes[0], 3, GLES31.GL_FLOAT, false, 0, 0)


        // Texture Coords VBO
        val textureCoordsBuffer: FloatBuffer =
            ByteBuffer.allocateDirect(meshData.textureCoords.size * 4).run {
                order(ByteOrder.nativeOrder())

                asFloatBuffer().apply {
                    put(meshData.textureCoords)
                    flip()
                }
            }

        textureCoordsBufferHandle = buffers[1]
        GLES31.glBindBuffer(GLES31.GL_ARRAY_BUFFER, textureCoordsBufferHandle)
        GLES31.glBufferData(
            GLES31.GL_ARRAY_BUFFER,
            textureCoordsBuffer.capacity() * 4,
            textureCoordsBuffer,
            GLES31.GL_STATIC_DRAW
        )
        GLES31.glVertexAttribPointer(shaderAttributes[1], 2, GLES31.GL_FLOAT, false, 0, 0)

        // Normals VBO
        val normalBuffer: FloatBuffer = ByteBuffer.allocateDirect(meshData.normals.size * 4).run {
            order(ByteOrder.nativeOrder())

            asFloatBuffer().apply {
                put(meshData.normals)
                flip()
            }
        }
        normalBufferHandle = buffers[2]
        GLES31.glBindBuffer(GLES31.GL_ARRAY_BUFFER, normalBufferHandle)
        GLES31.glBufferData(
            GLES31.GL_ARRAY_BUFFER,
            normalBuffer.capacity() * 4,
            normalBuffer,
            GLES31.GL_STATIC_DRAW
        )
        GLES31.glVertexAttribPointer(shaderAttributes[2], 3, GLES31.GL_FLOAT, false, 0, 0)

        // Index VBO
        val indexBuffer: IntBuffer = ByteBuffer.allocateDirect(meshData.indices.size * 4).run {
            order(ByteOrder.nativeOrder())

            asIntBuffer().apply {
                put(meshData.indices)
                flip()
            }
        }

        indexBufferHandle = buffers[3]
        GLES31.glBindBuffer(GLES31.GL_ELEMENT_ARRAY_BUFFER, indexBufferHandle)
        GLES31.glBufferData(
            GLES31.GL_ELEMENT_ARRAY_BUFFER,
            indexBuffer.capacity() * 4,
            indexBuffer,
            GLES31.GL_STATIC_DRAW
        )

        // Unbind buffer and vertex array
        GLES31.glBindBuffer(GLES31.GL_ARRAY_BUFFER, 0)
        GLES31.glBindVertexArray(0)

        vaoIdBuffer.limit(0)
        vertexBuffer.limit(0)
        textureCoordsBuffer.limit(0)
        normalBuffer.limit(0)
        indexBuffer.limit(0)
    }

    /**
     * Render the mesh
     * Must be called ONLY in the GLSurfaceView.Renderer methods
     */
    fun draw(shaderAttributes: IntArray) {
        // Activate the first texture bank and bind the texture
        GLES31.glActiveTexture(GLES31.GL_TEXTURE0)
        GLES31.glBindTexture(GLES31.GL_TEXTURE_2D, meshData.textureHandle)

        // Draw the mesh
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
        GLES31.glDeleteBuffers(
            4,
            intArrayOf(
                positionBufferHandle,
                indexBufferHandle,
                textureCoordsBufferHandle,
                normalBufferHandle
            ),
            0
        )

        //Delete the VAO
        GLES31.glBindVertexArray(0)
        GLES31.glDeleteVertexArrays(1, intArrayOf(vaoId), 0)
    }

    data class MeshData(
        val positions: FloatArray,
        val textureCoords: FloatArray,
        val normals: FloatArray,
        val indices: IntArray,
        val textureHandle: Int
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as MeshData

            if (!positions.contentEquals(other.positions)) return false
            if (!textureCoords.contentEquals(other.textureCoords)) return false
            if (!normals.contentEquals(other.normals)) return false
            if (!indices.contentEquals(other.indices)) return false
            if (textureHandle != other.textureHandle) return false

            return true
        }

        override fun hashCode(): Int {
            var result = positions.contentHashCode()
            result = 31 * result + textureCoords.contentHashCode()
            result = 31 * result + normals.contentHashCode()
            result = 31 * result + indices.contentHashCode()
            result = 31 * result + textureHandle
            return result
        }

    }

}