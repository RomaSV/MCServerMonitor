package com.example.mcservermonitor.ftp

import android.util.Log
import com.example.mcservermonitor.util.HOST
import com.example.mcservermonitor.util.PASSWORD
import com.example.mcservermonitor.util.USER
import com.example.mcservermonitor.util.WORLD_NAME
import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import kotlin.math.floor


fun getPlayerUIDs(): Array<String>? {
    val ftpClient = FTPClient()
    ftpClient.connect(HOST)

    return if (ftpClient.login(USER, PASSWORD)) {
        ftpClient.enterLocalPassiveMode()
        ftpClient.type(FTP.BINARY_FILE_TYPE)

        val files = ftpClient.listFiles("/$WORLD_NAME/playerdata")
        val result = Array(files.size) {
            files[it].name.slice(0 until files[it].name.length - 4)
        }
        result
    } else {
        null
    }
}

fun getRegionFile(x: Int, z: Int, outFile: File): Boolean {
    val ftpClient = FTPClient()
    ftpClient.connect(HOST)

    return if (ftpClient.login(USER, PASSWORD)) {
        ftpClient.enterLocalPassiveMode()
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE)

        val remoteFile = "/$WORLD_NAME/region/r.$x.$z.mca"
        Log.i("MC-INFO", "Downloading region file: $remoteFile")
        val outputStream = BufferedOutputStream(FileOutputStream(outFile))
        val result = ftpClient.retrieveFile(remoteFile, outputStream)
        outputStream.close()
        result
    } else {
        false
    }
}

fun getRegionFileByChunk(x: Int, z: Int, outFile: File): Boolean {
    val regionX = floor(x / 32.0).toInt()
    val regionZ = floor(z / 32.0).toInt()
    return getRegionFile(regionX, regionZ, outFile)
}