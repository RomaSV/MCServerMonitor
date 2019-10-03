package com.example.mcservermonitor.model

import com.example.mcservermonitor.util.BitBuffer
import java.io.File
import kotlin.math.ceil
import kotlin.math.log2
import kotlin.math.max

val blockUtils = BlockUtil()

class MapDecoder {

    fun decodeRegionFile(file: File, chunkX: Int, chunkY: Int): Chunk {
        val regionFile = RegionFile(file)
        val input = regionFile.getChunkData(chunkX, chunkY) ?: return Chunk(emptyMap())

        val chunkSections: MutableList<ChunkSection> = mutableListOf()
        val chunkSectionsData = input.compound.getCompound("Level").getCompoundList("Sections")
        for (section in chunkSectionsData) {
            if (!section.containsKey("Palette")) continue
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
        val heightMapIndices = Array(16) { Array(16) { Array(16) { Block.AIR } } }
        val bitsPerSection: Int =
            max(ceil(log2(section.palette.usedBlocks.size.toDouble())).toInt(), 4)

        val bitBuffer = BitBuffer(section.blockStates, bitsPerSection)

        for (y in 0..15) {
            for (z in 0..15) {
                for (x in 0..15) {
                    val blockPalletId = bitBuffer.read()
                    heightMapIndices[y][z][x] = section.palette.usedBlocks[blockPalletId]
                }
            }
        }

        return heightMapIndices
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