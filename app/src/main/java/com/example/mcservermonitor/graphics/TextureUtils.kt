package com.example.mcservermonitor.graphics

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES31
import android.opengl.GLUtils

fun loadTexture(context: Context, resourceId: Int): Int {

    val textureHandle = IntArray(1)
    GLES31.glGenTextures(1, textureHandle, 0)

    val bitmapOptions = BitmapFactory.Options()
    bitmapOptions.inScaled = false

    val bitmap = BitmapFactory.decodeResource(context.resources, resourceId, bitmapOptions)

    // Bind to the texture in OpenGL
    GLES31.glBindTexture(GLES31.GL_TEXTURE_2D, textureHandle[0])

    // Set filtering
    GLES31.glTexParameteri(GLES31.GL_TEXTURE_2D, GLES31.GL_TEXTURE_MIN_FILTER, GLES31.GL_NEAREST)
    GLES31.glTexParameteri(GLES31.GL_TEXTURE_2D, GLES31.GL_TEXTURE_MAG_FILTER, GLES31.GL_NEAREST)

    // Load the bitmap into the bound texture.
    GLUtils.texImage2D(GLES31.GL_TEXTURE_2D, 0, bitmap, 0)

    GLES31.glGenerateMipmap(GLES31.GL_TEXTURE_2D)

    bitmap.recycle()

    return textureHandle[0]
}