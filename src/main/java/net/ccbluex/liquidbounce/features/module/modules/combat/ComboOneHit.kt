/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/UnlegitMinecraft/FDPClientChina/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.AttackEvent
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.minecraft.network.play.client.C02PacketUseEntity
import net.minecraft.network.play.client.C0APacketAnimation

@ModuleInfo(name = "ComboOneHit", category = ModuleCategory.COMBAT)
@SuppressWarnings("ALL")
class ComboOneHit : Module() {

    private val amountValue = IntegerValue("Packets", 200, 0, 500)
    private val onlyAuraValue = BoolValue("OnlyAura", false)
    private val gameBreaking = BoolValue("GameBreaking", false)

    @EventTarget
    fun onAttack(event: AttackEvent) {
        fun sendPacket() {
            mc.netHandler.addToSendQueue(C02PacketUseEntity(event.targetEntity, C02PacketUseEntity.Action.ATTACK))
        }
        fun swingPacket() {
            mc.netHandler.addToSendQueue(C0APacketAnimation())
        }

        repeat (amountValue.get()) {
            swingPacket()
            sendPacket()
        }
        if (gameBreaking.get()) {
            repeat (amountValue.get()) {
                swingPacket()
                repeat(3) {
                    sendPacket()
                }
            }
        }
    }
}