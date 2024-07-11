package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.other

import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode
import net.ccbluex.liquidbounce.utils.MoveUtils.isMoving
import net.ccbluex.liquidbounce.utils.PlayerUtil
import net.ccbluex.liquidbounce.value.FloatValue
import net.minecraft.entity.Entity
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class HypixelBhop : SpeedMode("HypixelBhop") {

    private val bspeed = FloatValue("${valuePrefix}Speed", 1F, 0.5F, 8F)
    var hopping: Boolean = false
    override fun onUpdate() {
        if ((mc.thePlayer.isInWater || mc.thePlayer.isInLava || mc.thePlayer.isSneaking)) {
            return;
        }
        if (!PlayerUtil.jumpDown() && isMoving() && mc.currentScreen == null) {
            if (!mc.thePlayer.onGround) {
                return
            }
            mc.thePlayer.jump()
            mc.thePlayer.isSprinting = true
            var horizontalSpeed: Double = getHorizontalSpeed()
            val additionalSpeed: Double = 0.4847 * ((bspeed.get() - 1.0) / 3.0 + 1.0)
            if (horizontalSpeed < additionalSpeed) {
                horizontalSpeed = additionalSpeed
            }
            setSpeed(horizontalSpeed)
            hopping = true
        }
    }

    override fun onDisable() {
        hopping = false
    }

    fun getHorizontalSpeed(): Double {
        return getHorizontalSpeed(mc.thePlayer)
    }

    fun getHorizontalSpeed(entity: Entity): Double {
        return sqrt(entity.motionX * entity.motionX + entity.motionZ * entity.motionZ)
    }

    fun ae(n: Float, n2: Float, n3: Float): Float {
        var n = n
        var n4 = 1.0f
        if (n2 < 0.0f) {
            n += 180.0f
            n4 = -0.5f
        } else if (n2 > 0.0f) {
            n4 = 0.5f
        }
        if (n3 > 0.0f) {
            n -= 90.0f * n4
        } else if (n3 < 0.0f) {
            n += 90.0f * n4
        }
        return n * 0.017453292f
    }

    fun n(): Float {
        return ae(
            mc.thePlayer.rotationYaw,
            mc.thePlayer.movementInput.moveForward,
            mc.thePlayer.movementInput.moveStrafe
        )
    }

    fun setSpeed(n: Double) {
        if (n == 0.0) {
            mc.thePlayer.motionZ = 0.0
            mc.thePlayer.motionX = 0.0
            return
        }
        val n3: Float = n()
        mc.thePlayer.motionX = -sin(n3.toDouble()) * n
        mc.thePlayer.motionZ = cos(n3.toDouble()) * n
    }

}