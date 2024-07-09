package net.ccbluex.liquidbounce.launch.options

import net.ccbluex.liquidbounce.FDPClientChina
import net.ccbluex.liquidbounce.launch.EnumLaunchFilter
import net.ccbluex.liquidbounce.launch.LaunchFilterInfo
import net.ccbluex.liquidbounce.launch.LaunchOption
import net.ccbluex.liquidbounce.launch.data.legacyui.ClickGUIModule
import net.ccbluex.liquidbounce.launch.data.legacyui.ClickGuiConfig
import net.ccbluex.liquidbounce.launch.data.legacyui.clickgui.ClickGui
import net.ccbluex.liquidbounce.ui.client.GuiSelectPerformance
import java.io.File

@LaunchFilterInfo([EnumLaunchFilter.LEGACY_UI])
object LegacyUiLaunchOption : LaunchOption() {

    @JvmStatic
    lateinit var clickGui: ClickGui

    @JvmStatic
    lateinit var clickGuiConfig: ClickGuiConfig

    override fun start() {
        FDPClientChina.mainMenu = GuiSelectPerformance()
        FDPClientChina.moduleManager.registerModule(ClickGUIModule())

        clickGui = ClickGui()
        clickGuiConfig = ClickGuiConfig(File(FDPClientChina.fileManager.dir, "clickgui.json"))
        FDPClientChina.fileManager.loadConfig(clickGuiConfig)
    }

    override fun stop() {
        FDPClientChina.fileManager.saveConfig(clickGuiConfig)
    }
}