package net.ccbluex.liquidbounce.features.module.modules.client

import net.ccbluex.liquidbounce.FDPClientChina
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.ui.client.GuiSelectPerformance

@ModuleInfo(name = "PerformanceLevel", category = ModuleCategory.CLIENT)
class PerformanceLevel : Module() {
    override fun onEnable() {
        FDPClientChina.RENDERLEAVESELECTED=3;
        mc.displayGuiScreen(GuiSelectPerformance())
        state=false
    }
}