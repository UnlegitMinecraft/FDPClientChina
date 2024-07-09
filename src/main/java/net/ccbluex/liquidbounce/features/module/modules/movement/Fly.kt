package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.FDPClientChina
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.EnumAutoDisableType
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.movement.flys.FlyMode
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.ClassUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.network.play.server.S19PacketEntityStatus
import net.minecraft.util.MathHelper
import org.lwjgl.input.Keyboard
import java.awt.Color
import java.math.BigDecimal

@ModuleInfo(name = "Fly", category = ModuleCategory.MOVEMENT, autoDisable = EnumAutoDisableType.FLAG, keyBind = Keyboard.KEY_F)
class Fly : Module() {
    private val modes = ClassUtils.resolvePackage("${this.javaClass.`package`.name}.flys", FlyMode::class.java)
        .map { it.newInstance() as FlyMode }
        .sortedBy { it.modeName }

    private val mode: FlyMode
        get() = modes.find { modeValue.equals(it.modeName) } ?: throw NullPointerException() // this should not happen

    private val modeValue: ListValue = object : ListValue("Mode", modes.map { it.modeName }.toTypedArray(), "Vanilla") {
        override fun onChange(oldValue: String, newValue: String) {
            if (state) onDisable()
        }

        override fun onChanged(oldValue: String, newValue: String) {
            if (state) onEnable()
        }
    }

    private val motionResetValue = BoolValue("MotionReset", false)

    // Visuals
    private val markValue = ListValue("Mark", arrayOf("Up", "Down", "Off"), "Up")
    private val fakeDamageValue = BoolValue("FakeDamage", false)
    private val viewBobbingValue = BoolValue("ViewBobbing", false)
    private val viewBobbingYawValue = FloatValue("ViewBobbingYaw", 0.1f, 0f, 0.5f).displayable { viewBobbingValue.get() }
    private val speeddisplay = BoolValue("SpeedDisplay", true)

    var launchX = 0.0
    var launchY = 0.0
    var launchZ = 0.0
    var launchYaw = 0f
    var launchPitch = 0f

    var antiDesync = false

    var needReset = true

    override fun onEnable() {
        antiDesync = false
        needReset = true
        if (mc.thePlayer.onGround && fakeDamageValue.get()) {
            val event = PacketEvent(S19PacketEntityStatus(mc.thePlayer, 2.toByte()), PacketEvent.Type.RECEIVE)
            FDPClientChina.eventManager.callEvent(event)
            if (!event.isCancelled) {
                mc.thePlayer.handleStatusUpdate(2.toByte())
            }
        }

        launchX = mc.thePlayer.posX
        launchY = mc.thePlayer.posY
        launchZ = mc.thePlayer.posZ
        launchYaw = mc.thePlayer.rotationYaw
        launchPitch = mc.thePlayer.rotationPitch

        mode.onEnable()
    }

    override fun onDisable() {
        antiDesync = false
        mc.thePlayer.capabilities.isFlying = false
        mc.thePlayer.capabilities.flySpeed = 0.05f
        mc.thePlayer.noClip = false

        mc.timer.timerSpeed = 1F
        mc.thePlayer.speedInAir = 0.02F

        if (motionResetValue.get() && needReset) {
            mc.thePlayer.motionX = 0.0
            mc.thePlayer.motionY = 0.0
            mc.thePlayer.motionZ = 0.0
        }

        mode.onDisable()
    }

    @EventTarget
    fun onRender2D(event: Render2DEvent?) {
        if (speeddisplay.get()) {
            val sr = ScaledResolution(mc)
            val xDiff = (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * 2
            val zDiff = (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * 2
            val bg = BigDecimal(MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff) * 10.0)
            val speed = (bg.toInt() * mc.timer.timerSpeed).toInt()
            val str = speed.toString() + "block/s"
            RenderUtils.drawRect(sr.scaledWidth / 2F - 50F, 60F, sr.scaledWidth / 2F + 50F, 72F, Color(0, 0, 0, 140).rgb)
            Fonts.font35.drawString(str,(sr.scaledWidth - Fonts.font35.getStringWidth(str)) / 2F, 62F, Color(220, 220, 50).rgb)
        }
    }

    @EventTarget
    fun onRender3d(event: Render3DEvent) {
        if (markValue.equals("Off")) {
            return
        }

        RenderUtils.drawPlatform(
            if (markValue.equals("Up")) launchY + 2.0 else launchY,
            if (mc.thePlayer.entityBoundingBox.maxY < launchY + 2.0) Color(0, 255, 0, 90) else Color(255, 0, 0, 90),
            1.0)
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        mode.onUpdate(event)
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if(viewBobbingValue.get()) {
            mc.thePlayer.cameraYaw = viewBobbingYawValue.get()
            mc.thePlayer.prevCameraYaw = viewBobbingYawValue.get()
        }
        mode.onMotion(event)
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        mode.onPacket(event)
    }

    @EventTarget
    fun onMove(event: MovementEvent) {
        mode.onMove(event)
    }

    @EventTarget
    fun onBlockBB(event: BlockBBEvent) {
        mode.onBlockBB(event)
    }

    @EventTarget
    fun onJump(event: JumpEvent) {
        mode.onJump(event)
    }

    @EventTarget
    fun onStep(event: StepEvent) {
        mode.onStep(event)
    }

    override val tag: String
        get() = modeValue.get()

    /**
     * 读取mode中的value并和本体中的value合并
     * 所有的value必须在这个之前初始化
     */
    override val values = super.values.toMutableList().also { modes.map { mode -> mode.values.forEach { value -> it.add(value.displayable { modeValue.equals(mode.modeName) }) } } }
}