package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.other;

import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode;
import net.ccbluex.liquidbounce.utils.ClientUtils;
import net.ccbluex.liquidbounce.utils.MinecraftInstance;
import net.ccbluex.liquidbounce.utils.MoveUtils;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.FloatValue;
import org.jetbrains.annotations.NotNull;


public class HypixelLowHop
extends SpeedMode {

    @NotNull
    public float Runspeed;

    public HypixelLowHop() {
        super("HypixelLowHop");
    }

    public float getRunspeed() {
        return this.Runspeed;
    }

    public void setRunspeed(float f) {
        this.Runspeed = f;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        ClientUtils.INSTANCE.displayChatMessage("retarded code from FoodByte! lol WhiteZhiJun#1337");
    }

    @Override
    public void onPreMotion() {
        if (MoveUtils.isMoving() && MinecraftInstance.mc.thePlayer.onGround) {
            setRunspeed(1.2);
            MinecraftInstance.mc.thePlayer.motionY = 0.31999998688697817;
        }
        MoveUtils.strafe(MoveUtils.getBaseMoveSpeed() * 0.90151 * (double)this.getRunspeed());
        if ((double)this.getRunspeed() > 1) {
            this.Runspeed -= 0.05;
        }
    }
}
