package com.example.mcservermonitor.util

/* Copyright (c) 2019 Jesper Öqvist <jesper@llbit.se>
 *
 * This file is part of Chunky.
 *
 * Chunky is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Chunky is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with Chunky.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Converted to Kotlin with standard IDEA tool
 */


/**
 * Reads fixed-width bit fields from a long array.
 */
class BitBuffer
/**
 * @param data the data (immutable).
 * @param stride the number of bits to read at a time (1-32).
 */
    (private val data: LongArray, private val stride: Int) {

    private val mask: Int = (1 shl stride) - 1

    // Current 8-byte position:
    private var offset: Int = 0

    // Next bit position:
    private var shift = 0

    fun read(): Int {
        var res: Int
        if (shift + stride < 64) {
            res = data[offset].ushr(shift).toInt() and mask
            shift += stride
        } else {
            if (shift + stride == 64) {
                res = data[offset].ushr(shift).toInt() and mask
                offset += 1
                shift = 0
            } else {
                // High bits:
                val bits = 64 - shift
                res = data[offset].ushr(shift).toInt()
                offset += 1
                val rem = stride - bits
                // Low bits:
                res = res or (data[offset].toInt() and (1 shl rem) - 1 shl bits)
                shift = rem
            }
        }
        return res
    }
}