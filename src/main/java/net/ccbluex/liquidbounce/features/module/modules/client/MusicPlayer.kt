package net.ccbluex.liquidbounce.features.module.modules.client

import cn.hanabi.gui.cloudmusic.ui.MusicPlayerUI
import net.ccbluex.liquidbounce.FDPClientChina
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.NotifyType
import net.ccbluex.liquidbounce.value.BoolValue

@ModuleInfo(name = "MusicPlayer", category = ModuleCategory.CLIENT, canEnable = false)
class MusicPlayer : Module() {
    var JavaFx = BoolValue("I know JavaFX", false)
    override fun onEnable() {
        if (JavaFx.get())
            mc.displayGuiScreen(MusicPlayerUI())
        else{
            FDPClientChina.hud.addNotification(Notification("警告","初始化JavaFX会修改窗口缩放影响体验，请右键打开设置，允许JavaFX设置", NotifyType.WARNING,4000,500))
        }
    }
}