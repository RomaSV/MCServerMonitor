package com.example.mcservermonitor.model

import java.io.File
import java.nio.ByteBuffer

val blockUtils = BlockUtil()

class MapDecoder {

    fun decodeRegionFile(file: File, chunkX: Int, chunkY: Int): Array<Array<Array<Block>>> {
        val regionFile = RegionFile(file)
        val input = regionFile.getChunkData(chunkX, chunkY) ?: return emptyArray()

        val chunkSections: MutableList<ChunkSection> = mutableListOf()
        val chunkSectionsData = input.compound.getCompound("Level").getCompoundList("Sections")
        for (section in chunkSectionsData) {
            if (!section.containsKey("Palette")) continue
            val palette = ChunkSectionPalette()
            section.getCompoundList("Palette").forEach { entry ->
                palette.addBlock(entry.getString("Name"))
            }
            val blockStates = section.getLongArray("BlockStates")
            val blockStatesBytes = ByteBuffer.allocate(blockStates.size * Long.SIZE_BYTES)
            blockStates.forEach { block -> blockStatesBytes.putLong(block) }
            chunkSections.add(ChunkSection(palette, blockStatesBytes.array()))
        }

        return readChunkSection(chunkSections[3])
    }

    private fun readChunkSection(section: ChunkSection): Array<Array<Array<Block>>> {
        val heightMapIndices = Array(16) { Array(16) { Array(16) { Block.AIR } } }

        for (y in 0..15) {
            for (z in 0..15) {
                for (x in 0..15) {
                    val blockIndex = (x + z * 16 + y * 16 * 16)
                    // each block is described as a 4-bit id
                    val blockPalletId = if (blockIndex % 2 == 0) {
                        section.blockStates[blockIndex / 2].toInt() and 0xF0 ushr 4
                    } else {
                        section.blockStates[blockIndex / 2].toInt() and 0x0F
                    }
                    heightMapIndices[y][z][x] = section.palette.usedBlocks[blockPalletId]
                }
            }
        }

        return heightMapIndices
    }

    data class ChunkSection(val palette: ChunkSectionPalette, val blockStates: ByteArray) {
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