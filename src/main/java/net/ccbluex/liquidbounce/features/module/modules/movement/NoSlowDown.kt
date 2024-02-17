package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.features.module.modules.exploit.Disabler
import net.ccbluex.liquidbounce.utils.PacketUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiDownloadTerrain
import net.minecraft.item.ItemSword
import net.minecraft.network.INetHandler
import net.minecraft.network.Packet
import net.minecraft.network.login.server.S01PacketEncryptionRequest
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.network.play.client.C09PacketHeldItemChange
import net.minecraft.network.play.client.C0FPacketConfirmTransaction
import net.minecraft.network.play.server.S00PacketKeepAlive
import net.minecraft.network.play.server.S01PacketJoinGame
import net.minecraft.network.play.server.S02PacketChat
import net.minecraft.network.play.server.S03PacketTimeUpdate
import net.minecraft.network.play.server.S04PacketEntityEquipment
import net.minecraft.network.play.server.S06PacketUpdateHealth
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import net.minecraft.network.play.server.S0FPacketSpawnMob
import net.minecraft.network.play.server.S12PacketEntityVelocity
import net.minecraft.network.play.server.S13PacketDestroyEntities
import net.minecraft.network.play.server.S18PacketEntityTeleport
import net.minecraft.network.play.server.S19PacketEntityHeadLook
import net.minecraft.network.play.server.S19PacketEntityStatus
import net.minecraft.network.play.server.S1CPacketEntityMetadata
import net.minecraft.network.play.server.S20PacketEntityProperties
import net.minecraft.network.play.server.S21PacketChunkData
import net.minecraft.network.play.server.S22PacketMultiBlockChange
import net.minecraft.network.play.server.S23PacketBlockChange
import net.minecraft.network.play.server.S27PacketExplosion
import net.minecraft.network.play.server.S2FPacketSetSlot
import net.minecraft.network.play.server.S32PacketConfirmTransaction
import net.minecraft.network.play.server.S35PacketUpdateTileEntity
import net.minecraft.network.play.server.S38PacketPlayerListItem
import net.minecraft.network.play.server.S3EPacketTeams
import net.minecraft.network.play.server.S3FPacketCustomPayload
import net.minecraft.network.play.server.S40PacketDisconnect
import net.minecraft.network.play.server.S44PacketWorldBorder
import net.minecraft.network.play.server.S47PacketPlayerListHeaderFooter
import net.minecraft.network.status.server.S00PacketServerInfo
import net.minecraft.network.status.server.S01PacketPong
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.CopyOnWriteArrayList

@ModuleInfo(name = "NoSlowDown", category = ModuleCategory.MOVEMENT)
class NoSlowDown : Module() {

    var lastSprinting = false
    var lastSlot = 0
    private var lastResult = false
    var storedPackets: MutableList<Packet<INetHandler>> = CopyOnWriteArrayList()
    var pingPackets: ConcurrentLinkedDeque<Int> = ConcurrentLinkedDeque()

    val post = BoolValue("Post",true)

    fun hasSword(): Boolean {
        return Minecraft.getMinecraft().thePlayer.inventory.currentItem != null && (Minecraft.getMinecraft().thePlayer.heldItem.item is ItemSword)
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if ((mc.thePlayer.isBlocking || LiquidBounce.moduleManager[KillAura::class.java]!!.blockingStatus) && mc.thePlayer.heldItem.item is ItemSword) {
            if (event.eventState == EventState.PRE) {
                mc.netHandler.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem % 8 + 1));
                mc.netHandler.addToSendQueue(C0FPacketConfirmTransaction(Int.MAX_VALUE, 32767.toShort(), true))
                mc.netHandler.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            }
            if (event.eventState == EventState.POST) {
                mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()))
            }
        }
    }

    @EventTarget
    fun onSlowDown(event: SlowDownEvent) {
        if (hasSword()) {
            if(mc.thePlayer == null || mc.theWorld == null)
                return

            if ((mc.thePlayer.isBlocking || LiquidBounce.moduleManager[KillAura::class.java]!!.blockingStatus) && mc.thePlayer.heldItem.item is ItemSword) {
                event.forward = 1.0F
                event.strafe = 1.0F
            }
        }

        @EventTarget
        fun onWorld(event: WorldEvent) {
            this.lastSlot = -1
            this.lastSprinting = false
        }
    }

    fun getGrimPost(): Boolean {
        val disabler = LiquidBounce.moduleManager.getModule(Disabler::class.java) as Disabler
        val result = disabler.state && post.get()
                && mc.thePlayer != null
                && mc.thePlayer!!.isEntityAlive
                && mc.thePlayer!!.ticksExisted >= 10
                && mc.currentScreen !is GuiDownloadTerrain

        if (lastResult && !result) {
            lastResult = false
            mc.addScheduledTask { processPackets() }
        }
        return result.also { lastResult = it }
    }
    fun processPackets() {
        if (storedPackets.isNotEmpty()) {
            for (packet in storedPackets) {
                val event = PacketEvent(packet, PacketEvent.Type.RECEIVE)
                LiquidBounce.eventManager.callEvent(event)
                if (event.isCancelled) {
                    continue
                }
                packet.processPacket(mc.netHandler)
            }
            storedPackets.clear()
        }
    }

    fun grimPostDelay(packet: Packet<*>): Boolean {
        if (mc.thePlayer == null) {
            return false
        }
        if (mc.currentScreen is GuiDownloadTerrain) {
            return false
        }
        if (packet is S00PacketServerInfo) {
            return false
        }
        if (packet is S01PacketEncryptionRequest) {
            return false
        }
        if (packet is S38PacketPlayerListItem) {
            return false
        }
        if (packet is S40PacketDisconnect) {
            return false
        }
        if (packet is S21PacketChunkData) {
            return false
        }
        if (packet is S01PacketPong) {
            return false
        }
        if (packet is S44PacketWorldBorder) {
            return false
        }
        if (packet is S01PacketJoinGame) {
            return false
        }
        if (packet is S19PacketEntityHeadLook) {
            return false
        }
        if (packet is S3EPacketTeams) {
            return false
        }
        if (packet is S02PacketChat) {
            return false
        }
        if (packet is S2FPacketSetSlot) {
            return false
        }
        if (packet is S1CPacketEntityMetadata) {
            return false
        }
        if (packet is S20PacketEntityProperties) {
            return false
        }
        if (packet is S35PacketUpdateTileEntity) {
            return false
        }
        if (packet is S03PacketTimeUpdate) {
            return false
        }
        if (packet is S47PacketPlayerListHeaderFooter) {
            return false
        }
        if (packet is S12PacketEntityVelocity) {
            val sPacketEntityVelocity: S12PacketEntityVelocity = packet
            return sPacketEntityVelocity.entityID == mc.thePlayer!!.entityId
        }
        return packet is S27PacketExplosion
                || packet is S32PacketConfirmTransaction
                || packet is S08PacketPlayerPosLook
                || packet is S18PacketEntityTeleport
                || packet is S19PacketEntityStatus
                || packet is S04PacketEntityEquipment
                || packet is S23PacketBlockChange
                || packet is S22PacketMultiBlockChange
                || packet is S13PacketDestroyEntities
                || packet is S00PacketKeepAlive
                || packet is S06PacketUpdateHealth
                || packet is S18PacketEntityTeleport
                || packet is S0FPacketSpawnMob
                || packet is S3FPacketCustomPayload
    }
    fun fixC0F(packet: C0FPacketConfirmTransaction) {
        val id: Int = packet.uid.toInt()
        if (id >= 0 || pingPackets.isEmpty()) {
            PacketUtils.sendPacketNoEvent(packet)
        } else {
            do {
                val current: Int = pingPackets.first
                PacketUtils.sendPacketNoEvent(C0FPacketConfirmTransaction(packet.windowId, current.toShort(), true))
                pingPackets.pollFirst()
                if (current == id) {
                    break
                }
            } while (!pingPackets.isEmpty())
        }
    }
}
