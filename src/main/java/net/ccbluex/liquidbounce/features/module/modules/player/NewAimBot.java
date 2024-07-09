package net.ccbluex.liquidbounce.features.module.modules.player;

import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.Render3DEvent;
import net.ccbluex.liquidbounce.event.UpdateEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.utils.IRenderManager;
import net.ccbluex.liquidbounce.utils.Rotation;
import net.ccbluex.liquidbounce.utils.RotationUtils;
import net.ccbluex.liquidbounce.utils.WorldUtil;
import net.ccbluex.liquidbounce.utils.render.RenderUtil;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.FloatValue;
import net.ccbluex.liquidbounce.value.IntegerValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBook;
import net.minecraft.item.ItemSword;
import net.minecraft.util.Vec3;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@ModuleInfo(name = "NewAimBot", category = ModuleCategory.PLAYER)
public class NewAimBot extends Module {

    public static BoolValue players = new BoolValue("Players", false);
    public static BoolValue headshot = new BoolValue("Only-Head", false);
    public static IntegerValue range = new IntegerValue("Range", 100, 1, 500);
    public static FloatValue deviation = new FloatValue("Pre-Attack", 1.5f, 0f, 10f);

    private Vec3 aimed;

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        List<EntityLivingBase> list;

        final List<EntityLivingBase> targets = WorldUtil.getLivingEntities().stream()
                .filter(this::isValid)
                .sorted(Comparator.comparing(e -> mc.thePlayer.getDistanceToEntity(e)))
                .collect(Collectors.toList());

        list = new ArrayList<>();
        list.addAll(targets.stream().filter((entity) -> entity instanceof EntityGiantZombie || entity instanceof EntityWither).collect(Collectors.toList()));
        list.addAll(targets.stream().filter((entity) -> !(entity instanceof EntityGiantZombie || entity instanceof EntityWither)).collect(Collectors.toList()));


        if (list.size() <= 0)
            return;

        this.aimed = this.getFixedLocation(list.get(0), deviation.get(), headshot.get());

        final float[] rotations = RotationUtils.getRotationToLocation(this.aimed);

        RotationUtils.setTargetRotation(new Rotation((rotations[0]),(rotations[1])));

        mc.thePlayer.rotationYawHead = rotations[0];

        if (!(mc.thePlayer.getHeldItem().getItem() instanceof ItemSword || mc.thePlayer.getHeldItem().getItem() instanceof ItemBook))
            mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());
    }

    @EventTarget
    public void onRender3D(final Render3DEvent event) {
        if (this.aimed == null)
            return;

        double posX = this.aimed.xCoord - ((IRenderManager) mc.getRenderManager()).getRenderPosX();
        double posY = this.aimed.yCoord - ((IRenderManager) mc.getRenderManager()).getRenderPosY();
        double posZ = this.aimed.zCoord - ((IRenderManager) mc.getRenderManager()).getRenderPosZ();

//        final BlockPos belowBlockPos = new BlockPos(posX - 0.5, posY - 0.5, posZ - 0.5);

        RenderUtil.drawBlockESP(posX - 0.5, posY - 0.5, posZ - 0.5, new Color(255, 0, 0, 100).getRGB(), new Color(0xFFE900).getRGB(), 0.4f, 0.1f);
    }

    private Vec3 getFixedLocation(final EntityLivingBase entity, final float velocity, final boolean head) {
        double x = entity.posX + ((entity.posX - entity.lastTickPosX) * velocity);
        double y = entity.posY + ((entity.posY - entity.lastTickPosY) * (velocity * 0.3)) + (head ? entity.getEyeHeight() : 1.0);
        double z = entity.posZ + ((entity.posZ - entity.lastTickPosZ) * velocity);

        return new Vec3(x, y, z);
    }

    private boolean isValid(final EntityLivingBase entity) {
        if (!(entity instanceof EntityZombie || entity instanceof EntitySilverfish || entity instanceof EntityWither || entity instanceof EntityGhast || entity instanceof EntitySpider || entity instanceof EntityGiantZombie || entity instanceof EntitySkeleton || entity instanceof EntityGolem || entity instanceof EntityEndermite || entity instanceof EntityWitch || entity instanceof EntityBlaze || entity instanceof EntitySlime || entity instanceof EntityCreeper || entity instanceof EntityWolf || entity instanceof EntityPlayer && players.get()))
            return false;

        if (entity.isDead || entity.getHealth() <= 0)
            return false;

        if (mc.thePlayer.getDistanceToEntity(entity) > range.get())
            return false;

        return canEntityBeSeenFixed(entity);
    }

    public static boolean canEntityBeSeenFixed(Entity entityIn) {
        return mc.thePlayer.worldObj.rayTraceBlocks(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ),
                new Vec3(entityIn.posX, entityIn.posY + (double) entityIn.getEyeHeight(), entityIn.posZ)) == null
                || mc.thePlayer.worldObj.rayTraceBlocks(
                new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ),
                new Vec3(entityIn.posX, entityIn.posY, entityIn.posZ)) == null;
    }
}
