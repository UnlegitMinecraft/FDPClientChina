package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.matrix;

import net.ccbluex.liquidbounce.event.PacketEvent;
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode;
import net.ccbluex.liquidbounce.utils.MovementUtils;
import net.ccbluex.liquidbounce.value.FloatValue;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import org.jetbrains.annotations.NotNull;

public class MatrixLatest
        extends SpeedMode {
    /**
     * obf in brain
     */
    
    @NotNull
    public FloatValue JumpTimer;
    @NotNull
    public FloatValue DownTimer;
    @NotNull
    public FloatValue LaunchSpeed;
    @NotNull
    public FloatValue AddSpeed;
    @NotNull
    public FloatValue RunTime;
    @NotNull
    public FloatValue MadY;
    public long hurt = 0;
    public boolean wasTimer = false;

    public MatrixLatest() {
        super("MatrixLatest");
        this.JumpTimer = new FloatValue("Jump Timer", 1.25f, 0.1f, 2.0f);
        this.DownTimer = new FloatValue("Down Timer",0.8f, 0.1f, 2.0f);
        this.LaunchSpeed = new FloatValue("Launch Speed", 0.3f, 0.1f, 1.5f);
        this.AddSpeed = new FloatValue("Add Speed", 0.3f, 0.1f, 1.5f);
        this.MadY = new FloatValue("Mad Y", 0.2f4, 0.1f, 1.0f);
        this.RunTime = new FloatValue("Stay Mad", 3000f, 0f, 10000f);
    }

    @Override
    public void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof S19PacketEntityStatus && ((S19PacketEntityStatus)((Object)event.getPacket())).getOpCode() == ((int)1064409185L ^ 0x3F719863) && MatrixLatest.mc.thePlayer.equals(((S19PacketEntityStatus)((Object)event.getPacket())).getEntity(MatrixLatest.mc.theWorld))) {
            this.hurt = System.currentTimeMillis();
        }
    }

    @Override
    public void onPreMotion() {
        if (this.hurt == 0) {
            if (MovementUtils.INSTANCE.isMoving()) {
                MatrixLatest.mc.timer.timerSpeed = MatrixLatest.mc.thePlayer.motionY > 0.0 ? ((Float)this.DownTimer.get()).floatValue() : ((Float)this.JumpTimer.get()).floatValue();
                if (MatrixLatest.mc.thePlayer.onGround) {
                    MatrixLatest.mc.thePlayer.jump();
                    MovementUtils.INSTANCE.strafe();
                }
            } else {
                MatrixLatest.mc.thePlayer.motionX = 0.0;
                MatrixLatest.mc.thePlayer.motionZ = 0.0;
            }
        } else {
            long passedTime = System.currentTimeMillis() - this.hurt;
            if ((float)passedTime > ((Float)this.RunTime.get()).floatValue()) {
                this.hurt = 0;
            }
            if (this.wasTimer) {
                MatrixLatest.mc.timer.timerSpeed = 1;
                this.wasTimer = false;
            } else {
                MatrixLatest.mc.timer.timerSpeed = MatrixLatest.mc.thePlayer.motionY > 0.0 ? ((Float)this.DownTimer.get()).floatValue() : ((Float)this.JumpTimer.get()).floatValue();
            }
            if (MovementUtils.INSTANCE.isMoving() && MatrixLatest.mc.thePlayer.onGround) {
                MatrixLatest.mc.thePlayer.jump();
                MatrixLatest.mc.timer.timerSpeed = ((Float)this.JumpTimer.get()).floatValue() + 0.2;
                this.wasTimer = true;
                MovementUtils.INSTANCE.strafe(((Float)this.LaunchSpeed.get()).floatValue() + (1 - (float)(passedTime / 3000)) * ((Float)this.AddSpeed.get()).floatValue());
                MatrixLatest.mc.thePlayer.motionY = ((Float)this.MadY.get()).floatValue();
            }
        }
    }
}
