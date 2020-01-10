package com.example.mcservermonitor.ftp

import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient


const val host = "HOST"
const val user = "USER"
const val passwd = "PASSWORD"
const val worldName = "Wonderful Wrld"

fun getPlayerUIDs(): Array<String>? {
    val ftpClient = FTPClient()
    ftpClient.connect(host)

    return if (ftpClient.login(user, passwd)) {
        ftpClient.enterLocalPassiveMode()
        ftpClient.type(FTP.BINARY_FILE_TYPE)

        val files = ftpClient.listFiles("/$worldName/playerdata")
        val result = Array(files.size) {
            files[it].name.slice(0 until files[it].name.length - 4)
        }
        result
    } else {
        null
    }
}