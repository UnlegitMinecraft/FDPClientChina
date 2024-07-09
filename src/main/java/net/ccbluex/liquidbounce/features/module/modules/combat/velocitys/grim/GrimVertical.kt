package net.ccbluex.liquidbounce.features.module.modules.combat.velocitys.grim

import net.ccbluex.liquidbounce.FDPClientChina
import net.ccbluex.liquidbounce.event.AttackEvent
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.features.module.modules.combat.velocitys.VelocityMode
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.extensions.getDistanceToEntityBox
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.minecraft.entity.EntityLivingBase
import net.minecraft.network.play.client.C02PacketUseEntity
import net.minecraft.network.play.client.C0APacketAnimation
import net.minecraft.network.play.client.C0BPacketEntityAction
import net.minecraft.network.play.client.C0FPacketConfirmTransaction
import net.minecraft.network.play.server.S12PacketEntityVelocity
import net.minecraft.util.Vec3
import org.apache.commons.lang3.RandomUtils

class GrimVertical : VelocityMode("GrimVertical") {
    private var attack = false;
    private val smartVelo = BoolValue("${valuePrefix}SmartVelo",true)
    private val sendc0fValue = BoolValue("${valuePrefix}C0F",false)
    private val C0fpacketamount = IntegerValue("${valuePrefix}C0FPacketAmount",0,1,20).displayable{sendc0fValue.get()}
    private val C02packetamount = IntegerValue("${valuePrefix}C02PacketAmount",1,1,20)
    private val sprintSpoof = BoolValue("${valuePrefix}SpoofSprint",true)
    private val playerJump = BoolValue("${valuePrefix}PlayerJump",true)
    private val spoofJump = BoolValue("${valuePrefix}SpoofJump",false)
    private val callEvent = BoolValue("${valuePrefix}CallEvent",true)

    private var motionXZ = 0.0
    var lastSprinting = false
    override fun onEnable() {
        motionXZ = 0.01
    }
    override fun onUpdate(event: UpdateEvent) {
        if (attack) {
            repeat(C02packetamount.get()) {
                FDPClientChina.moduleManager[KillAura::class.java]!!.currentTarget?.let { it1 -> attackEntity(it1) }
            }

            if(smartVelo.get()){
                mc.thePlayer!!.motionX *= motionXZ
                mc.thePlayer!!.motionZ *= motionXZ
            }else{
                mc.thePlayer!!.motionX *= 0.077760000
                mc.thePlayer!!.motionZ *= 0.077760000
            }
            velocity.velocityInput = false
            attack = false
        }else if (mc.thePlayer.hurtTime == 6 && mc.thePlayer.onGround && !mc.gameSettings.keyBindJump.isKeyDown) {
            if(spoofJump.get()){
                mc.thePlayer.movementInput.jump = true;
            }
        }
    }
    private fun runSwing() {
        mc.netHandler.addToSendQueue(C0APacketAnimation())
    }
    private fun attackEntity(entity: EntityLivingBase){
        val event = AttackEvent(entity)
        if(callEvent.get()){
            FDPClientChina.eventManager.callEvent(event)
            if (event.isCancelled) return
        }
        runSwing()
        mc.netHandler.addToSendQueue(C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK))
    }
    private fun getMotionNoXZ(packetEntityVelocity: S12PacketEntityVelocity): Double {
        val strength: Double = Vec3(
                packetEntityVelocity.getMotionX().toDouble(),
                packetEntityVelocity.getMotionY().toDouble(),
                packetEntityVelocity.getMotionZ().toDouble()
        ).lengthVector()
        val motionNoXZ: Double = if (strength >= 20000.0) {
            if (mc.thePlayer.onGround) {
                0.06425
            } else {
                0.075
            }
        } else if (strength >= 5000.0) {
            if (mc.thePlayer.onGround) {
                0.02625
            } else {
                0.0552
            }
        } else {
            0.0175
        }
        return motionNoXZ
    }
    override fun onVelocityPacket(event: PacketEvent) {
        val packet = event.packet
        if(packet is S12PacketEntityVelocity) {
            if (packet.getMotionX() == 0 && packet.getMotionZ() == 0 || mc.thePlayer == null || (mc.theWorld?.getEntityByID(packet.entityID) ?: return) != mc.thePlayer) { // ignore horizonal velocity
                return
            }
            if (mc.thePlayer!!.onGround && mc.thePlayer.hurtTime > 0) {
                if(playerJump.get()){
                    mc.thePlayer.jump()
                }
            }
            velocity.velocityInput = true
            motionXZ = getMotionNoXZ(packet)

            if (FDPClientChina.moduleManager[KillAura::class.java]!!.state && FDPClientChina.moduleManager[KillAura::class.java]!!.currentTarget != null && mc.thePlayer!!.getDistanceToEntityBox(FDPClientChina.moduleManager[KillAura::class.java]!!.currentTarget!!) <= 3.00) {
                if (mc.thePlayer!!.isSprinting && mc.thePlayer!!.serverSprintState && MovementUtils.isMoving()) {
                    repeat(C0fpacketamount.get()) {
                        if(sendc0fValue.get()){
                            mc.netHandler.addToSendQueue(C0FPacketConfirmTransaction(RandomUtils.nextInt(102, 1000024123), RandomUtils.nextInt(102, 1000024123).toShort(), true))
                        }
                    }
                    attack = true
                }else{
                    if(sprintSpoof.get()){
                        repeat(C0fpacketamount.get()) {
                            if(sendc0fValue.get()){
                                mc.netHandler.addToSendQueue(C0FPacketConfirmTransaction(RandomUtils.nextInt(102, 1000024123), RandomUtils.nextInt(102, 1000024123).toShort(), true))
                            }
                        }
                        mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING))
                        mc.thePlayer.setSprinting(false)
                        attack = true
                        mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer , C0BPacketEntityAction.Action.STOP_SPRINTING))
                    }
                }
            }
        }
        if(packet is C0BPacketEntityAction && velocity.velocityInput && sprintSpoof.get()){
            if (packet.action == C0BPacketEntityAction.Action.START_SPRINTING) {
                if (this.lastSprinting) {
                    FDPClientChina.eventManager.callEvent(event)
                }
                this.lastSprinting = true
            } else if (packet.action == C0BPacketEntityAction.Action.STOP_SPRINTING) {
                if (!this.lastSprinting) {
                    FDPClientChina.eventManager.callEvent(event)
                }
                this.lastSprinting = false
            }
        }
    }
}