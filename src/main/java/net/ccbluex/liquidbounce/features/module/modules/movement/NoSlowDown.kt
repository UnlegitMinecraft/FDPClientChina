package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.PacketUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.item.*
import net.minecraft.network.Packet
import net.minecraft.network.play.INetHandlerPlayServer
import net.minecraft.network.play.client.*
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import java.util.*
import kotlin.math.sqrt

@ModuleInfo(name = "NoSlowDwon", category = ModuleCategory.MOVEMENT)
class NoSlowDown : Module() {

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if ((mc.thePlayer.isBlocking || LiquidBounce.moduleManager[KillAura::class.java]!!.blockingStatus) && mc.thePlayer.heldItem.item is ItemSword) {
            if (event.eventState == EventState.PRE) {
                PacketUtils.sendPacketNoEvent(
                        C07PacketPlayerDigging(
                                C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
                                BlockPos.ORIGIN,
                                EnumFacing.DOWN
                        )
                )
            }
            if (event.eventState == EventState.POST) {
                PacketUtils.sendPacketNoEvent(C0FPacketConfirmTransaction());
                mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.heldItem))
            }
        }
    }
    @EventTarget
    fun onSlowDown(event: SlowDownEvent) {
        if(mc.thePlayer == null || mc.theWorld == null)
            return

        if ((mc.thePlayer.isBlocking || LiquidBounce.moduleManager[KillAura::class.java]!!.blockingStatus) && mc.thePlayer.heldItem.item is ItemSword) {
            event.forward = 1.0F
            event.strafe = 1.0F
        }
    }
}