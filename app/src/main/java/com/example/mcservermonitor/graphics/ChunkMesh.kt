package com.example.mcservermonitor.graphics

import com.example.mcservermonitor.model.Block
import com.example.mcservermonitor.model.BlockTexturePositions
import com.example.mcservermonitor.model.Chunk

class ChunkMesh(private val chunk: Chunk, texturesHolder: Int) {

    private val blockTextureSizeX = 1f / 32f
    private val blockTextureSizeY = 1f / 16f

    val mesh: Mesh

    init {
        var cubesCount = 0
        val vertexPositions = mutableListOf<Float>()
        val texturePositions = mutableListOf<Float>()
        val normals = mutableListOf<Float>()
        val indices = mutableListOf<Int>()
        for (section in chunk.sections.keys) {
            for (y in 0..15) {
                for (z in 0..15) {
                    for (x in 0..15) {
                        val block =
                            chunk.sections[section]!!.blocks[y][z][x] // section shouldn't be null
                        if (block != Block.AIR && block.textureAtlasPos.sideX != -1
                            && isVisible(section, x, y, z)
                        ) {
                            cubesCount++
                            vertexPositions.addAll(
                                getCubeVertexPositions(
                                    x,
                                    y + (section * 16),
                                    z
                                ).toList()
                            )
                            normals.addAll(getCubeNormals().toList())
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
            normals.toFloatArray(),
            indices.toIntArray(),
            texturesHolder
        )

        mesh = Mesh(meshData)
    }

    private fun getCubeVertexPositions(x: Int, y: Int, z: Int): FloatArray = floatArrayOf(
        // RIGHT
        // V0
        x - 0.5f, y + 0.5f, z + 0.5f,
        // V1
        x - 0.5f, y - 0.5f, z + 0.5f,
        // V2
        x + 0.5f, y - 0.5f, z + 0.5f,
        // V3
        x + 0.5f, y + 0.5f, z + 0.5f,

        // TOP
        // V4
        x - 0.5f, y + 0.5f, z - 0.5f,
        // V5
        x - 0.5f, y + 0.5f, z + 0.5f,
        // V6
        x + 0.5f, y + 0.5f, z + 0.5f,
        // V7
        x + 0.5f, y + 0.5f, z - 0.5f,

        // FRONT
        // V8
        x + 0.5f, y + 0.5f, z + 0.5f,
        // V9
        x + 0.5f, y - 0.5f, z + 0.5f,
        // V10
        x + 0.5f, y - 0.5f, z - 0.5f,
        // V11
        x + 0.5f, y + 0.5f, z - 0.5f,

        // BACK
        // V12
        x - 0.5f, y - 0.5f, z - 0.5f,
        // V13
        x - 0.5f, y - 0.5f, z + 0.5f,
        // V14
        x - 0.5f, y + 0.5f, z + 0.5f,
        // V15
        x - 0.5f, y + 0.5f, z - 0.5f,

        // BOTTOM
        // V16
        x + 0.5f, y - 0.5f, z + 0.5f,
        // V17
        x - 0.5f, y - 0.5f, z + 0.5f,
        // V18
        x - 0.5f, y - 0.5f, z - 0.5f,
        // V19
        x + 0.5f, y - 0.5f, z - 0.5f,

        // LEFT
        // V20
        x + 0.5f, y - 0.5f, z - 0.5f,
        // V21
        x - 0.5f, y - 0.5f, z - 0.5f,
        // V22
        x - 0.5f, y + 0.5f, z - 0.5f,
        // V23
        x + 0.5f, y + 0.5f, z - 0.5f
    )

    private fun getCubeTexturePositions(texture: BlockTexturePositions): FloatArray = floatArrayOf(
        // Right
        (texture.sideX + 1) * blockTextureSizeX, texture.sideY * blockTextureSizeY,
        (texture.sideX + 1) * blockTextureSizeX, (texture.sideY + 1) * blockTextureSizeY,
        texture.sideX * blockTextureSizeX, (texture.sideY + 1) * blockTextureSizeY,
        texture.sideX * blockTextureSizeX, texture.sideY * blockTextureSizeY,

        // Top
        texture.topX * blockTextureSizeX, texture.topY * blockTextureSizeY,
        (texture.topX + 1) * blockTextureSizeX, texture.topY * blockTextureSizeY,
        (texture.topX + 1) * blockTextureSizeX, (texture.topY + 1) * blockTextureSizeY,
        texture.topX * blockTextureSizeX, (texture.topY + 1) * blockTextureSizeY,

        // Front
        (texture.sideX + 1) * blockTextureSizeX, texture.sideY * blockTextureSizeY,
        (texture.sideX + 1) * blockTextureSizeX, (texture.sideY + 1) * blockTextureSizeY,
        texture.sideX * blockTextureSizeX, (texture.sideY + 1) * blockTextureSizeY,
        texture.sideX * blockTextureSizeX, texture.sideY * blockTextureSizeY,

        // Back (front)
        texture.sideX * blockTextureSizeX, (texture.sideY + 1) * blockTextureSizeY,
        (texture.sideX + 1) * blockTextureSizeX, (texture.sideY + 1) * blockTextureSizeY,
        (texture.sideX + 1) * blockTextureSizeX, texture.sideY * blockTextureSizeY,
        texture.sideX * blockTextureSizeX, texture.sideY * blockTextureSizeY,

        // Bottom

        (texture.bottomX + 1) * blockTextureSizeX, (texture.bottomY + 1) * blockTextureSizeY,
        texture.bottomX * blockTextureSizeX, (texture.bottomY + 1) * blockTextureSizeY,
        texture.bottomX * blockTextureSizeX, texture.bottomY * blockTextureSizeY,
        (texture.bottomX + 1) * blockTextureSizeX, texture.bottomY * blockTextureSizeY,

        // Left
        (texture.sideX + 1) * blockTextureSizeX, (texture.sideY + 1) * blockTextureSizeY,
        texture.sideX * blockTextureSizeX, (texture.sideY + 1) * blockTextureSizeY,
        texture.sideX * blockTextureSizeX, texture.sideY * blockTextureSizeY,
        (texture.sideX + 1) * blockTextureSizeX, texture.sideY * blockTextureSizeY
    )

    private fun getCubeNormals(): FloatArray = floatArrayOf(
        0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f,
        0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f,
        1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f,
        -1f, 0f, 0f, -1f, 0f, 0f, -1f, 0f, 0f, -1f, 0f, 0f,
        0f, -1f, 0f, 0f, -1f, 0f, 0f, -1f, 0f, 0f, -1f, 0f,
        0f, 0f, -1f, 0f, 0f, -1f, 0f, 0f, -1f, 0f, 0f, -1f
    )

    private fun getCubeIndices(cubesCount: Int): List<Int> {
        return listOf(
            // Right face
            0, 1, 3, 3, 1, 2,
            // Top face
            4, 5, 6, 7, 4, 6,
            // Front face
            8, 9, 10, 11, 8, 10,
            // Back face
            12, 13, 14, 12, 14, 15,
            // Bottom face
            16, 17, 18, 16, 18, 19,
            // Left face
            20, 21, 22, 20, 22, 23
        ).map { element -> element + 24 * (cubesCount - 1) }
    }

    private fun isVisible(section: Int, x: Int, y: Int, z: Int): Boolean =
        x > 0 && chunk.sections[section]!!.blocks[y][z][x - 1] == Block.AIR ||
                x < 15 && chunk.sections[section]!!.blocks[y][z][x + 1] == Block.AIR ||
                y > 0 && chunk.sections[section]!!.blocks[y - 1][z][x] == Block.AIR ||
                y < 15 && chunk.sections[section]!!.blocks[y + 1][z][x] == Block.AIR ||
                z > 0 && chunk.sections[section]!!.blocks[y][z - 1][x] == Block.AIR ||
                z < 15 && chunk.sections[section]!!.blocks[y][z + 1][x] == Block.AIR ||
                x == 0 || x == 15 || y == 0 || y == 15 || z == 0 || z == 15
}