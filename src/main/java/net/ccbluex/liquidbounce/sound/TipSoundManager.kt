package net.ccbluex.liquidbounce.sound

import net.ccbluex.liquidbounce.FDPClientChina
import net.ccbluex.liquidbounce.utils.FileUtils
import java.io.File

class TipSoundManager {
    var enableSound: TipSoundPlayer
    var disableSound: TipSoundPlayer
    var startup: TipSoundPlayer

    init {
        val enableSoundFile = File(FDPClientChina.fileManager.soundsDir, "enable.wav")
        val disableSoundFile = File(FDPClientChina.fileManager.soundsDir, "disable.wav")
        val startupFile = File(FDPClientChina.fileManager.soundsDir, "startup.wav")

        if (!enableSoundFile.exists()) {
            FileUtils.unpackFile(enableSoundFile, "assets/minecraft/fdpclient/sound/enable.wav")
        }
        if (!disableSoundFile.exists()) {
            FileUtils.unpackFile(disableSoundFile, "assets/minecraft/fdpclient/sound/disable.wav")
        }
        if (!startupFile.exists()) {
            FileUtils.unpackFile(startupFile, "assets/minecraft/fdpclient/sound/startup.wav")
        }

        enableSound = TipSoundPlayer(enableSoundFile)
        disableSound = TipSoundPlayer(disableSoundFile)
        startup = TipSoundPlayer(startupFile)
    }
}