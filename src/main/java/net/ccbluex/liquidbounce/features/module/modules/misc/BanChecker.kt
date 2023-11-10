package net.ccbluex.liquidbounce.features.module.modules.misc

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.NotifyType
import net.ccbluex.liquidbounce.value.TextValue
import net.minecraft.network.play.server.S02PacketChat
import java.util.regex.Pattern

@ModuleInfo(name = "BanChecker",  category = ModuleCategory.MISC)
class BanChecker : Module() {

    private val textValue = TextValue("Text", "Very good cheaters enjoy banned!")
    private var ban = 0

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (packet is S02PacketChat) {
            val text = packet.chatComponent.unformattedText
            val matcher = Pattern.compile("玩家(.*?)在本局游戏中行为异常, 已被踢出游戏并封禁处罚")
                    .matcher(packet.chatComponent.unformattedText)
            if (matcher.find() && text.startsWith("➤")) {
                val banname = matcher.group(1)
                if (banname != mc.thePlayer!!.name) {
                    ban++
                    LiquidBounce.hud.addNotification(
                            Notification(
                                    "BanChecker",
                                    "$banname was banned. (banned:$ban)",
                                    NotifyType.INFO,
                                    animeTime = 1000
                            )
                    )
                    mc.thePlayer!!.sendChatMessage(textValue.get())
                }
            }
        }
    }
}