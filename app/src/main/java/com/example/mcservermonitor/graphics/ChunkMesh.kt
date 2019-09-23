package com.example.mcservermonitor.graphics

import android.util.Log
import com.example.mcservermonitor.model.Block
import com.example.mcservermonitor.model.BlockTexturePositions
import com.example.mcservermonitor.model.Chunk

class ChunkMesh(val chunk: Chunk, texturesHolder: Int) {

    private val blockTextureSize = 1f / 16f

    val mesh: Mesh

    init {
        var cubesCount = 0
        val vertexPositions = mutableListOf<Float>()
        val texturePositions = mutableListOf<Float>()
        val indices = mutableListOf<Int>()
        for (section in 0 until chunk.sections.size) {
            for (y in 0..15) {
                for (z in 0..15) {
                    for (x in 0..15) {
                        val block = chunk.sections[section].blocks[y][z][x]
                        if (block != Block.AIR && block.textureAtlasPos.sideX != -1 && isVisible(section, x, y, z)) {
                            Log.i("MESH", "Constructing mesh at $x, $y, $z")
                            cubesCount++
                            vertexPositions.addAll(getCubeVertexPositions(x, y, z).toList())
                            texturePositions.addAll(getCubeTexturePositions(block.textureAtlasPos).toList())
                            indices.addAll(getCubeIndices(cubesCount))
                        }
                    }
                }
            }
        }

        val meshData = Mesh.MeshData(
            vertexPositions.toFloatArray(),
            texturePositions.toFloatArray(),
            indices.toIntArray(),
            texturesHolder
        )

        mesh = Mesh(meshData)
    }

    private fun getCubeVertexPositions(x: Int, y: Int, z: Int): FloatArray = floatArrayOf(
        // V0
        x + -0.5f, y + 0.5f, z + 0.5f,
        // V1
        x + -0.5f, y + -0.5f, z + 0.5f,
        // V2
        x + 0.5f, y + -0.5f, z + 0.5f,
        // V3
        x + 0.5f, y + 0.5f, z + 0.5f,
        // V4
        x + -0.5f, y + 0.5f, z + -0.5f,
        // V5
        x + 0.5f, y + 0.5f, z + -0.5f,
        // V6
        x + -0.5f, y + -0.5f, z + -0.5f,
        // V7
        x + 0.5f, y + -0.5f, z + -0.5f,

        // For text coords in top face
        // V8: V4 repeated
        x + -0.5f, y + 0.5f, z + -0.5f,
        // V9: V5 repeated
        x + 0.5f, y + 0.5f, z + -0.5f,
        // V10: V0 repeated
        x + -0.5f, y + 0.5f, z + 0.5f,
        // V11: V3 repeated
        x + 0.5f, y + 0.5f, z + 0.5f,

        // For text coords in right face
        // V12: V3 repeated
        x + 0.5f, y + 0.5f, z + 0.5f,
        // V13: V2 repeated
        x + 0.5f, y + -0.5f, z + 0.5f,

        // For text coords in left face
        // V14: V0 repeated
        x + -0.5f, y + 0.5f, z + 0.5f,
        // V15: V1 repeated
        x + -0.5f, y + -0.5f, z + 0.5f,

        // For text coords in bottom face
        // V16: V6 repeated
        x + -0.5f, y + -0.5f, z + -0.5f,
        // V17: V7 repeated
        x + 0.5f, y + -0.5f, z + -0.5f,
        // V18: V1 repeated
        x + -0.5f, y + -0.5f, z + 0.5f,
        // V19: V2 repeated
        x + 0.5f, y + -0.5f, z + 0.5f
    )

    private fun getCubeTexturePositions(texture: BlockTexturePositions): FloatArray = floatArrayOf(
        // Front
        texture.sideX * blockTextureSize, texture.sideY * blockTextureSize,
        texture.sideX * blockTextureSize, (texture.sideY + 1) * blockTextureSize,
        (texture.sideX + 1) * blockTextureSize, (texture.sideY + 1) * blockTextureSize,
        (texture.sideX + 1) * blockTextureSize, texture.sideY * blockTextureSize,

        // Back
        texture.sideX * blockTextureSize, texture.sideY * blockTextureSize,
        (texture.sideX + 1) * blockTextureSize, texture.sideY * blockTextureSize,
        texture.sideX * blockTextureSize, (texture.sideY + 1) * blockTextureSize,
        (texture.sideX + 1) * blockTextureSize, (texture.sideY + 1) * blockTextureSize,

        // For text coords in top face
        texture.topX * blockTextureSize, texture.topY * blockTextureSize,
        (texture.topX + 1) * blockTextureSize, texture.topY * blockTextureSize,
        texture.topX * blockTextureSize, (texture.topY + 1) * blockTextureSize,
        (texture.topX + 1) * blockTextureSize, (texture.topY + 1) * blockTextureSize,

        // For text coords in right face
        texture.sideX * blockTextureSize, texture.sideY * blockTextureSize,
        texture.sideX * blockTextureSize, (texture.sideY + 1) * blockTextureSize,

        // For text coords in left face
        (texture.sideX + 1) * blockTextureSize, texture.sideY * blockTextureSize,
        (texture.sideX + 1) * blockTextureSize, (texture.sideY + 1) * blockTextureSize,

        // For text coords in bottom face
        texture.bottomX * blockTextureSize, texture.bottomY * blockTextureSize,
        (texture.bottomX + 1) * blockTextureSize, texture.bottomY * blockTextureSize,
        texture.bottomX * blockTextureSize, (texture.bottomY + 1) * blockTextureSize,
        (texture.bottomX + 1) * blockTextureSize, (texture.bottomY + 1) * blockTextureSize
    )

    private fun getCubeIndices(cubesCount: Int): List<Int> {
        return listOf(
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
        ).map { element -> element + 20 * (cubesCount - 1) }
    }

    private fun isVisible(section: Int, x: Int, y: Int, z: Int): Boolean =
        x > 0 && chunk.sections[section].blocks[y][z][x - 1] == Block.AIR ||
        x < 15 && chunk.sections[section].blocks[y][z][x + 1] == Block.AIR ||
        y > 0 && chunk.sections[section].blocks[y - 1][z][x] == Block.AIR ||
        y < 15 && chunk.sections[section].blocks[y + 1][z][x] == Block.AIR ||
        z > 0 && chunk.sections[section].blocks[y][z - 1][x] == Block.AIR ||
        z < 15 && chunk.sections[section].blocks[y][z + 1][x] == Block.AIR ||
        x == 0 || x == 15 || y == 0 || y == 15 || z == 0 || z == 15
}