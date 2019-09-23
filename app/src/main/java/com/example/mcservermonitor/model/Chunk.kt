package com.example.mcservermonitor.model

class Chunk(val sections: List<Section>)

class Section(val blocks: Array<Array<Array<Block>>>)