/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/UnlegitMinecraft/FDPClientChina/
 */
package net.ccbluex.liquidbounce.file.configs

import net.ccbluex.liquidbounce.FDPClientChina
import net.ccbluex.liquidbounce.file.FileConfig
import net.ccbluex.liquidbounce.ui.client.hud.Config
import java.io.File

class HudConfig(file: File) : FileConfig(file) {
    override fun loadConfig(config: String) {
        FDPClientChina.hud.clearElements()
        FDPClientChina.hud = Config(config).toHUD()
    }

    override fun saveConfig(): String {
        return Config(FDPClientChina.hud).toJson()
    }
}