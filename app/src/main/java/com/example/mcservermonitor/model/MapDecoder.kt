package com.example.mcservermonitor.model

import com.example.mcservermonitor.util.BitBuffer
import java.io.File
import kotlin.math.ceil
import kotlin.math.log2
import kotlin.math.max

val blockUtils = BlockUtil()

class MapDecoder(inputFile: File) {

    private val regionFile: RegionFile = RegionFile(inputFile)

    fun decodeRegionFile(chunkX: Int, chunkY: Int): Chunk {
        val input = regionFile.getChunkData(chunkX, chunkY) ?: return Chunk(emptyMap())

        val chunkSections: MutableList<ChunkSection> = mutableListOf()
        val chunkSectionsData = input.compound.getCompound("Level").getCompoundList("Sections")
        var sectionsCount = 0
        for (section in chunkSectionsData) {
            if (!section.containsKey("Palette")) continue
            sectionsCount++
            val palette = ChunkSectionPalette()
            section.getCompoundList("Palette").forEach { entry ->
                palette.addBlock(entry.getString("Name"))
            }
            val blockStates = section.getLongArray("BlockStates")
            chunkSections.add(ChunkSection(palette, blockStates))
        }

        val topSections = mutableMapOf<Int, Section>()
        topSections[chunkSections.lastIndex - 1] = Section(readChunkSection(chunkSections[chunkSections.lastIndex - 1]))
        topSections[chunkSections.lastIndex] = Section(readChunkSection(chunkSections[chunkSections.lastIndex]))
        return Chunk(topSections)
    }

    private fun readChunkSection(section: ChunkSection): Array<Array<Array<Block>>> {
        val bitsPerSection: Int =
            max(ceil(log2(section.palette.usedBlocks.size.toDouble())).toInt(), 4)

        val bitBuffer = BitBuffer(section.blockStates, bitsPerSection)

        val heightMapIndices = Array(16 * 16 * 16) { Block.AIR }

        for (i in 0 until 16 * 16 * 16) {
            val blockPalletId = bitBuffer.read()
            val block = section.palette.usedBlocks[blockPalletId]
            heightMapIndices[i] = block
        }

        // Treat hidden blocks like they are air
        val optimizedIndices = Array(16) { Array(16) { Array(16) { Block.AIR } } }
        var hidden: Boolean

        for (y in 15 downTo 0) {
            hidden = true
            for (z in 0..15) {
                for (x in 0..15) {
                    val block = heightMapIndices[x + 16 * (z + 16 * y)]
                    optimizedIndices[y][z][x] = block
                    if (block == Block.AIR) hidden = false
                }
            }
            if (hidden) break
        }

        return optimizedIndices
    }

    data class ChunkSection(val palette: ChunkSectionPalette, val blockStates: LongArray) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ChunkSection

            if (palette != other.palette) return false
            if (!blockStates.contentEquals(other.blockStates)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = palette.hashCode()
            result = 31 * result + blockStates.contentHashCode()
            return result
        }

    }

    class ChunkSectionPalette {
        val usedBlocks: MutableList<Block> = mutableListOf()

        fun addBlock(mcName: String) {
            usedBlocks.add(blockUtils.blockTypes.getOrDefault(mcName, Block.AIR))
        }
    }
}