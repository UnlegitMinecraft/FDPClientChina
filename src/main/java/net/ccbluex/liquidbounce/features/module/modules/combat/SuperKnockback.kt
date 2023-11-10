/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/UnlegitMinecraft/FDPClientChina/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.AttackEvent
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.Rotation
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.entity.EntityLivingBase
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C0BPacketEntityAction

@ModuleInfo(name = "SuperKnockback", category = ModuleCategory.COMBAT)
class SuperKnockback : Module() {
    private val hurtTimeValue = IntegerValue("HurtTime", 10, 0, 10)
    private val modeValue = ListValue("Mode", arrayOf("LegitSprint", "SilentPacket" ,"ExtraPacket", "LiquidBounce", "GrimPacket" ,"WTap" ,"Packet", "SneakPacket"), "SilentPacket")
    private val onlyMoveValue = BoolValue("OnlyMove", true)
    private val onlyMoveForwardValue = BoolValue("OnlyMoveForward", true).displayable { onlyMoveValue.get() }
    private val onlyGroundValue = BoolValue("OnlyGround", false)
    private val delayValue = IntegerValue("Delay", 0, 0, 500)
    private var lastSprinting = false
    private var thanksMojang = false
    private var wasTeleport = false
    private var lastSneaking = false

    private var ticks = 0

    val timer = MSTimer()

    @EventTarget
    fun onAttack(event: AttackEvent) {
        if (event.targetEntity is EntityLivingBase) {
            if (event.targetEntity.hurtTime > hurtTimeValue.get() || !timer.hasTimePassed(delayValue.get().toLong()) ||
                    (!MovementUtils.isMoving() && onlyMoveValue.get()) || (!mc.thePlayer.onGround && onlyGroundValue.get())) {
                return
            }

            if (onlyMoveForwardValue.get() && RotationUtils.getRotationDifference(Rotation(MovementUtils.movingYaw, mc.thePlayer.rotationPitch), Rotation(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch)) > 35) {
                return
            }

            when (modeValue.get().lowercase()) {
                "legitsprint" -> {
                    ticks = 2
                }

                "silentpacket" -> {
                    ticks = 1
                }

                "vanilla" ->{
                    if (mc.thePlayer.isSprinting)
                        mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING))

                    mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING))
                    mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING))
                    mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING))
                    mc.thePlayer.isSprinting = true
                    mc.thePlayer.serverSprintState = true
                }

                "extrapacket" -> {
                    if (mc.thePlayer.isSprinting)
                        mc.thePlayer.isSprinting = true
                    mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING))

                    mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING))
                    mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING))
                    mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING))
                    mc.thePlayer.serverSprintState = true
                }

                "grimpacket" -> {
                    if (mc.thePlayer.isSprinting) {
                        mc.thePlayer.isSprinting = true
                    }
                    mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING))
                    mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING))
                    mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING))
                    mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING))
                    mc.thePlayer.serverSprintState = true
                }

                "wtap" -> {
                    if (mc.thePlayer.isSprinting)
                        mc.thePlayer.isSprinting = false
                    mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING))
                    mc.thePlayer.serverSprintState = true
                }

                "packet" -> {
                    if (mc.thePlayer.isSprinting) {
                        mc.thePlayer.isSprinting = true
                    }
                    mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING))
                    mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING))
                    mc.thePlayer.serverSprintState = true
                }

                "sneakpacket" -> {
                    if (mc.thePlayer.isSprinting) {
                        mc.thePlayer.isSprinting = true
                    }
                    mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING))
                    mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING))
                    mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING))
                    mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING))
                    mc.thePlayer.serverSprintState = true
                }
            }
            timer.reset()
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (modeValue.equals("LegitSprint")) {
            if (ticks == 2) {
                mc.thePlayer.isSprinting = false
                ticks = 1
            } else if (ticks == 1) {
                mc.thePlayer.isSprinting = true
                ticks = 0
            }
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if ((packet is C03PacketPlayer.C04PacketPlayerPosition || packet is C03PacketPlayer.C06PacketPlayerPosLook) && modeValue.get().equals("SilentPacket",true)) {
            if (ticks == 1) {
                mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING))
                ticks = 2
            } else if (ticks == 2) {
                mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING))
                ticks = 0
            }
        }
        if(modeValue.get().equals("GrimPacket",true)){
            if (packet is C0BPacketEntityAction) {
                if (packet.action == C0BPacketEntityAction.Action.START_SPRINTING) {
                    if (lastSprinting) {
                        if (!thanksMojang) {
                            thanksMojang = true
                            return
                        }
                        event.cancelEvent()
                    }
                    lastSprinting = true
                } else if (packet.action == C0BPacketEntityAction.Action.STOP_SPRINTING) {
                    if (!lastSprinting) {
                        if (!thanksMojang) {
                            thanksMojang = true
                            return
                        }
                        event.cancelEvent()
                    }
                    lastSprinting = false
                }
            }
            if (packet is C0BPacketEntityAction) {
                if (packet.action == C0BPacketEntityAction.Action.START_SNEAKING) {
                    if (lastSneaking && !wasTeleport) {
                        event.cancelEvent()
                    } else {
                        lastSneaking = true
                    }
                } else if (packet.action == C0BPacketEntityAction.Action.STOP_SNEAKING) {
                    if (!lastSneaking && !wasTeleport) {
                        event.cancelEvent()
                    } else {
                        lastSneaking = false
                    }
                }
            }
        }
    }
    override val tag: String
        get() = modeValue.get()
}