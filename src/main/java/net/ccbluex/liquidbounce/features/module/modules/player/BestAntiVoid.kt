package net.ccbluex.liquidbounce.features.module.modules.player

import net.ccbluex.liquidbounce.FDPClientChina
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly
import net.ccbluex.liquidbounce.features.module.modules.world.Scaffold
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.MoveUtils
import net.ccbluex.liquidbounce.utils.PacketUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
import net.minecraft.network.play.server.S08PacketPlayerPosLook

@ModuleInfo(name = "BestAntiVoid", category = ModuleCategory.PLAYER)
class BestAntiVoid : Module() {
    private val pullbackTime = IntegerValue("PullbackTime", 850, 800, 1800)
    private val debug = BoolValue("Debug", false)
    var timer: MSTimer = MSTimer()
    var lastGroundPos = DoubleArray(3)
    var packets = ArrayList<C03PacketPlayer>()

    @EventTarget
    open fun isInVoid(): Boolean {
        for (i in 0..128) {
            if (MoveUtils.isOnGround(i.toDouble())) {
                return false
            }
        }
        return true
    }
    @EventTarget
    fun onPacket(e: PacketEvent) {
        if (!FDPClientChina.moduleManager.get(Fly::class.java)!!.state && !FDPClientChina.moduleManager.get(Scaffold::class.java)!!.state) {
            if (!packets.isEmpty() && mc.thePlayer.ticksExisted < 100) packets.clear()
            if (e.packet is C03PacketPlayer) {
                if (isInVoid()) {
                    e.cancelEvent()
                    packets.add(e.packet)
                    if (timer.hasTimePassed(pullbackTime.get().toLong())) {
                        PacketUtils.sendPacketNoEvent(
                            C04PacketPlayerPosition(
                                lastGroundPos[0], lastGroundPos[1] - 1.0,
                                lastGroundPos[2], true
                            )
                        )
                    }
                } else {
                    lastGroundPos[0] = mc.thePlayer.posX
                    lastGroundPos[1] = mc.thePlayer.posY
                    lastGroundPos[2] = mc.thePlayer.posZ
                    if (!packets.isEmpty()) {
                        val var3: Iterator<*> = packets.iterator()
                        debug("[AntiVoid] Release Packets - " + packets.size)
                        while (var3.hasNext()) {
                            val p = var3.next() as C03PacketPlayer
                            PacketUtils.sendPacketNoEvent(p)
                        }
                        packets.clear()
                    }
                    timer.reset()
                }
            }
        }
    }

    @EventTarget
    fun onRevPacket(e: PacketEvent) {
        if (e.packet is S08PacketPlayerPosLook && packets.size > 1) {
            debug("[AntiVoid] Pullbacks Detected, clear packets list!")
            packets.clear()
        }
    }

    private fun debug(str: String) {
        if (debug.get()) {
            ClientUtils.displayChatMessage(str)
        }
    }

    override val tag: String
        get() = pullbackTime.get().toString()
}