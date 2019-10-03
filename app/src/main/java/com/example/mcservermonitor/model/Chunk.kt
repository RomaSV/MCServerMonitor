package com.example.mcservermonitor.model

class Chunk(val sections: Map<Int, Section>)

class Section(val blocks: Array<Array<Array<Block>>>)