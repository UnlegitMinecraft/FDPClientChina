/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/UnlegitMinecraft/FDPClientChina/
 */
package net.ccbluex.liquidbounce.features.command.commands

import net.ccbluex.liquidbounce.FDPClientChina
import net.ccbluex.liquidbounce.features.command.Command
import net.ccbluex.liquidbounce.features.command.CommandManager
import net.ccbluex.liquidbounce.features.module.modules.misc.KillInsults
import net.ccbluex.liquidbounce.ui.cape.GuiCapeManager
import net.ccbluex.liquidbounce.ui.font.Fonts

class ReloadCommand : Command("reload", emptyArray()) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        alert("Reloading...")
        alert("§c§lReloading commands...")
        FDPClientChina.commandManager = CommandManager()
        FDPClientChina.commandManager.registerCommands()
        FDPClientChina.isStarting = true
        FDPClientChina.isLoadingConfig = true
        FDPClientChina.scriptManager.disableScripts()
        FDPClientChina.scriptManager.unloadScripts()
        for (module in FDPClientChina.moduleManager.modules)
            FDPClientChina.moduleManager.generateCommand(module)
        alert("§c§lReloading scripts...")
        FDPClientChina.scriptManager.loadScripts()
        FDPClientChina.scriptManager.enableScripts()
        alert("§c§lReloading fonts...")
        Fonts.loadFonts()
        alert("§c§lReloading modules...")
        FDPClientChina.configManager.load(FDPClientChina.configManager.nowConfig, false)
        KillInsults.loadFile()
        GuiCapeManager.load()
        alert("§c§lReloading accounts...")
        FDPClientChina.fileManager.loadConfig(FDPClientChina.fileManager.accountsConfig)
        alert("§c§lReloading friends...")
        FDPClientChina.fileManager.loadConfig(FDPClientChina.fileManager.friendsConfig)
        alert("§c§lReloading xray...")
        FDPClientChina.fileManager.loadConfig(FDPClientChina.fileManager.xrayConfig)
        alert("§c§lReloading HUD...")
        FDPClientChina.fileManager.loadConfig(FDPClientChina.fileManager.hudConfig)
        alert("Reloaded.")
        FDPClientChina.isStarting = false
        FDPClientChina.isLoadingConfig = false
        System.gc()
    }
}
