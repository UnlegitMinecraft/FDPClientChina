/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.misc

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer

@ModuleInfo(name = "BorderWarn", category = ModuleCategory.MISC)
class BorderWarn : Module() {
    val timer = MSTimer()

    @EventTarget
    fun onUpdate(e: UpdateEvent) {
        if (mc.theWorld == null || mc.thePlayer == null) return
        val world = mc.theWorld!!
        val player = mc.thePlayer!!

        val playerDistance = world.worldBorder.getClosestDistance(player)

        if (playerDistance <= 25) {
            if (timer.hasTimePassed(1000L)) {
                timer.reset()

                ClientUtils.displayChatMessage("§8[§bBorder Warn§8]§r §lThe Border is §c§dCOMING!!! §r§aDistance: $playerDistance")
            }
        }
    }
}
