package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.FDPClientChina
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.ui.RenderLeave
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.NotifyType

@ModuleInfo(name = "BetterFont", category = ModuleCategory.RENDER)
class BetterFont : Module(){
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if(FDPClientChina.RENDERLEAVE==RenderLeave.LOW){
            this.state=false;
            FDPClientChina.hud.addNotification(Notification("Performance", "For performance, this module will disabled!", NotifyType.WARNING))
        }
    }
}