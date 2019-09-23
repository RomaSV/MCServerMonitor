package com.example.mcservermonitor.model

class BlockUtil {
    val blockTypes = mutableMapOf<String, Block>()

    init {
        for (blockType in Block.values()) {
            blockTypes[blockType.mcName] = blockType
        }
    }
}

enum class Block(val mcName: String, val textureAtlasPos: BlockTexturePositions) {
    AIR("minecraft:air", BlockTexturePositions(-1, -1)),
    CAVE_AIR("minecraft:cave_air", BlockTexturePositions(-1, -1)),
    BEDROCK("minecraft:bedrock", BlockTexturePositions(5, 2)),
    STONE("minecraft:stone", BlockTexturePositions(1, 0)),
    GRANITE("minecraft:granite", BlockTexturePositions(8, 11)),
    ANDESITE("minecraft:andesite", BlockTexturePositions(8, 11)),
    DIORITE("minecraft:diorite", BlockTexturePositions(8, 11)),
    DIRT("minecraft:dirt", BlockTexturePositions(2, 0)),
    COAL_ORE("minecraft:coal_ore", BlockTexturePositions(2, 2)),
    IRON_ORE("minecraft:iron_ore", BlockTexturePositions(1, 2)),
    REDSTONE_ORE("minecraft:redstone_ore", BlockTexturePositions(3, 3)),
    GOLD_ORE("minecraft:gold_ore", BlockTexturePositions(0, 2)),
    LAPIS_ORE("minecraft:lapis_ore", BlockTexturePositions(0, 10)),
    GRAVEL("minecraft:gravel", BlockTexturePositions(3, 1)),
    SAND("minecraft:sand", BlockTexturePositions(2, 1)),
    CLAY("minecraft:clay", BlockTexturePositions(8, 2)),
    WATER("minecraft:water", BlockTexturePositions(14, 12)),
    GRASS_BLOCK("minecraft:grass_block", BlockTexturePositions(3, 0, 0, 0, 2, 0)),
    GRASS("minecraft:grass", BlockTexturePositions(-1, -1)),
    BIRCH_LOG("minecraft:birch_log", BlockTexturePositions(5, 7)),
    BIRCH_LEAVES("minecraft:birch_leaves", BlockTexturePositions(5, 3)),
    OAK_LOG("minecraft:oak_log", BlockTexturePositions(4, 1, 5, 1)),
    OAK_LEAVES("minecraft:oak_leaves", BlockTexturePositions(5, 3)),
    POPPY("minecraft:poppy", BlockTexturePositions(-1, -1))
}

data class BlockTexturePositions(
    val sideX: Int, val sideY: Int,
    val topX: Int = sideX, val topY: Int = sideY,
    val bottomX: Int = topX, val bottomY: Int = topY
)