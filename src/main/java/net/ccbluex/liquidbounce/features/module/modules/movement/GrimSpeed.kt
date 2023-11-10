package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.extensions.getDistanceToEntityBox
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.minecraft.entity.EntityLivingBase

@ModuleInfo(name = "GrimSpeed", category = ModuleCategory.MOVEMENT)
class GrimSpeed : Module() {
    private val onlyAir = BoolValue("OnlyAir",false)
    private val okstrafe = BoolValue("Strafe",false)
    private val noHurt = BoolValue("NoHurt",true)
    private val speedUp = BoolValue("SpeedUp",false)
    private val speed = IntegerValue("Speed", 16, 0, 30)
    private val distance = FloatValue("Range", 0f, 0f, 2f)
    private var speeded = false

    override fun onEnable() {
        speeded = false
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val strafe = LiquidBounce.moduleManager.getModule(Strafe::class.java) as Strafe
        for (entity in mc.theWorld.loadedEntityList) {
            if (entity is EntityLivingBase && entity.entityId != mc.thePlayer.entityId && mc.thePlayer.getDistanceToEntityBox(entity) <= distance.get() && ( !onlyAir.get() || !mc.thePlayer.onGround) && (!noHurt.get() || mc.thePlayer.hurtTime > 1)) {
                        if(speedUp.get()) {
                            mc.thePlayer.motionX *= (1 + (speed.get() * 0.01))
                            mc.thePlayer.motionZ *= (1 + (speed.get() * 0.01))
                        }
                        if(okstrafe.get()){
                            strafe.state = true
                        }
                        return
                    } else {
                strafe.state = false
            }
        }
    }
}