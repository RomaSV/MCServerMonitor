package com.example.mcservermonitor.model

import br.com.gamemods.nbtmanipulator.NbtFile
import br.com.gamemods.nbtmanipulator.NbtIO
import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.io.File
import java.io.RandomAccessFile
import java.util.zip.InflaterInputStream

private const val SECTOR_SIZE = 4096
private const val HEADER_SIZE = 4096
private const val CHUNK_DESCRIPTOR_SIZE = 4

class RegionFile(path: File) {

    private val offsets = IntArray(HEADER_SIZE / CHUNK_DESCRIPTOR_SIZE)

    private val fileReader = RandomAccessFile(path, "r")

    init {
        for (i in 0 until (HEADER_SIZE / CHUNK_DESCRIPTOR_SIZE)) {
            val offset = fileReader.readInt()
            offsets[i] = offset
        }
    }

    fun hasChunk(x: Int, z: Int): Boolean {
        return getOffset(x, z) != 0
    }

    /**
     * Get chunk data in the NBT format
     * Returns null if chunk is missing
     */
    fun getChunkData(x: Int, z: Int): NbtFile? {
        if (outOfBounds(x, z) || !hasChunk(x, z)) return null

        val offset = getOffset(x, z)
        val sectorNumber = offset shr 8
        val sectorsCount = offset and 0xFF

        fileReader.seek((sectorNumber * SECTOR_SIZE).toLong())
        val chunkLength = fileReader.readInt()
        fileReader.readByte() // unused - packing method
        val chunkData = ByteArray(chunkLength - 1)
        fileReader.read(chunkData)
        val chunkDataStream = BufferedInputStream(InflaterInputStream(ByteArrayInputStream(chunkData)))
        return NbtIO.readNbtFile(chunkDataStream, false)
    }

    private fun getOffset(x: Int, z: Int): Int {
        return offsets[x + z * 32]
    }

    private fun outOfBounds(x: Int, z: Int): Boolean {
        return x < 0 || x > 31 || z < 0 || z > 31
    }

}