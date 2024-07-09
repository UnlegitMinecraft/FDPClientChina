package net.ccbluex.liquidbounce.features.macro

import net.ccbluex.liquidbounce.FDPClientChina

class Macro(val key: Int, val command: String) {
    fun exec() {
        FDPClientChina.commandManager.executeCommands(command)
    }
}