package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.injection.backend.unwrap
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.network.Packet
import net.minecraft.network.play.INetHandlerPlayClient
import net.minecraft.network.play.client.*
import net.minecraft.network.play.server.SPacketConfirmTransaction
import net.minecraft.network.play.server.SPacketEntityVelocity
import net.minecraft.network.play.server.SPacketPlayerPosLook
import java.util.*
import java.util.concurrent.LinkedBlockingQueue



@ModuleInfo(name = "GrimFull", description = "GrimFull", category = ModuleCategory.COMBAT)
class GrimFull : Module() {
    private val modeValue = ListValue("Mode", arrayOf("FDP","New"),"New")
    private val disable = BoolValue("S08Disable",true)
    private val reEnable = BoolValue("ReEnable",true)
    private var cancelPacket = 6
    private var resetPersecFDP = 8
    private var grimTCancel = 0
    private var updatesFDP = 0
    private var cancelPackets = 0
    private var resetPersec = 8
    private var updates = 0
    private val packets = LinkedBlockingQueue<Packet<*>>()
    private var disableLogger = false
    private val inBus = LinkedList<Packet<INetHandlerPlayClient>>()


    override fun onEnable() {
        grimTCancel = 0
        cancelPackets = 0
    }
    override fun onDisable(){
        if (modeValue.get().equals("New")) {
            closeblink()
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val thePlayer = mc.thePlayer ?: return

        val packet = event.packet
        val packet1 = event.packet.unwrap()
        fun startBlink(){
            val packet2 = event.packet.unwrap()
            if (mc.thePlayer == null || disableLogger) return
            if (packet2 is CPacketPlayer)
                event.cancelEvent()
            if (packet2 is CPacketPlayer.Position || packet2 is CPacketPlayer.PositionRotation ||
                    packet2 is CPacketPlayerTryUseItemOnBlock ||
                    packet2 is CPacketAnimation ||
                    packet2 is CPacketEntityAction || packet2 is CPacketUseEntity || (packet2::class.java.simpleName.startsWith("C", true))
            ) {
                event.cancelEvent()
                packets.add(packet2)
            }
            if(packet2::class.java.getSimpleName().startsWith("S", true)) {
                if(packet2 is SPacketEntityVelocity && (mc.theWorld?.getEntityByID(packet2.entityID) ?: return) == mc.thePlayer){return}
                event.cancelEvent()
                inBus.add(packet2 as Packet<INetHandlerPlayClient>)
            }
        }
        when(modeValue.get()){
            "FDP" -> {
                if (MovementUtils.isMoving) {
                    if (classProvider.isSPacketEntityVelocity(packet)) {
                        val packetEntityVelocity = packet.asSPacketEntityVelocity()

                        if ((mc.theWorld?.getEntityByID(packetEntityVelocity.entityID) ?: return) != thePlayer)
                            return

                        event.cancelEvent()
                        grimTCancel = cancelPacket
                    }
                    if (packet1 is SPacketConfirmTransaction && grimTCancel > 0) {
                        event.cancelEvent()
                        grimTCancel--
                    }
                }
            }
            "New" -> {
                if (packet1 is SPacketEntityVelocity) {
                    val packetEntityVelocity = packet.asSPacketEntityVelocity()
                    if ((mc.theWorld?.getEntityByID(packetEntityVelocity.entityID) ?: return) != thePlayer)
                        return

                    event.cancelEvent()
                    cancelPackets = 3
                }
                if(cancelPackets > 0){
                    startBlink()
                }
            }
        }

        if (packet1 is SPacketPlayerPosLook){
            if (disable.get()){
                val velocity = LiquidBounce.moduleManager.getModule(GrimFull::class.java) as GrimFull
                velocity.state = false
                if (reEnable.get()){
                    Thread {
                        try {
                            Thread.sleep(1000)
                            velocity.state = true
                        } catch (ex: InterruptedException) {
                            ex.printStackTrace()
                        }
                    }.start()
                }
            }
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        updatesFDP++

        if (resetPersecFDP > 0) {
            if (updatesFDP >= 0 || updatesFDP >= resetPersecFDP) {
                updatesFDP = 0
                if (grimTCancel > 0) {
                    grimTCancel--
                }
            }
        }
        updates++
        if (resetPersec > 0) {
            if (updates >= 0 || updates >= resetPersec) {
                updates = 0
                if (cancelPackets > 0){
                    cancelPackets--
                }
            }
        }
        if(cancelPackets == 0){
            if (modeValue.get().equals("New")){
                closeblink()
            }
        }
    }
    private fun closeblink() {
        try {
            disableLogger = true
            while (!packets.isEmpty()) {
                mc2.connection!!.networkManager.sendPacket(packets.take())
            }
            while (!inBus.isEmpty()) {
                inBus.poll()?.processPacket(mc2!!.connection)
            }
            disableLogger = false
        } catch (e: Exception) {
            e.printStackTrace()
            disableLogger = false
        }
    }
}
