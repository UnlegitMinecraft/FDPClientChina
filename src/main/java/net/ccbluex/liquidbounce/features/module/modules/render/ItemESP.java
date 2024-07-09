/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/UnlegitMinecraft/FDPClientChina/
 */
package net.ccbluex.liquidbounce.features.module.modules.render;

import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.Render2DEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.utils.RenderUtil;
import net.ccbluex.liquidbounce.utils.render.ScaleUtils;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Slot;
import net.minecraft.item.*;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.List;

import static java.awt.Color.BLACK;
import static net.minecraft.enchantment.Enchantment.*;
import static net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel;
import static net.minecraft.init.Items.skull;
import static net.minecraft.util.EnumChatFormatting.*;

@ModuleInfo(name = "ItemESP", category = ModuleCategory.RENDER)
public class ItemESP extends Module {

    public BoolValue names = new BoolValue("Names", true);
    private final IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
    private final FloatBuffer modelView = GLAllocation.createDirectFloatBuffer(16);
    private final FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);
    private final FloatBuffer vector = GLAllocation.createDirectFloatBuffer(4);

    @EventTarget
    public void onRender(Render2DEvent er) {
        for (Entity o : mc.theWorld.getLoadedEntityList()) {
            if (o instanceof EntityItem) {
                IBakedModel ibakedmodel = mc.getRenderItem().getItemModelMesher().getItemModel(((EntityItem) o).getEntityItem());
                float f1 = MathHelper.sin(((float) ((EntityItem) o).getAge() + er.getPartialTicks()) / 10.0F + ((EntityItem) o).hoverStart) * 0.1F + 0.1F;
                float f2 = ibakedmodel.getItemCameraTransforms().getTransform(ItemCameraTransforms.TransformType.GROUND).scale.y;
                final double x = RenderUtil.interpolate(o.posX, o.lastTickPosX, er.getPartialTicks()), // @off
                        y = RenderUtil.interpolate(o.posY + f1, o.lastTickPosY + f1, er.getPartialTicks()),
                        z = RenderUtil.interpolate(o.posZ, o.lastTickPosZ, er.getPartialTicks()),
                        width = o.width / 1.4,
                        height = o.height + 0.2; // @on

                AxisAlignedBB aabb = new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width);
                List<Vector3d> vectors = Arrays.asList(new Vector3d(aabb.minX, aabb.minY, aabb.minZ),
                        new Vector3d(aabb.minX, aabb.maxY, aabb.minZ), new Vector3d(aabb.maxX, aabb.minY, aabb.minZ),
                        new Vector3d(aabb.maxX, aabb.maxY, aabb.minZ), new Vector3d(aabb.minX, aabb.minY, aabb.maxZ),
                        new Vector3d(aabb.minX, aabb.maxY, aabb.maxZ), new Vector3d(aabb.maxX, aabb.minY, aabb.maxZ),
                        new Vector3d(aabb.maxX, aabb.maxY, aabb.maxZ));

                mc.entityRenderer.setupCameraTransform(er.getPartialTicks(), 0);

                Vector4d position = null;

                for (Vector3d vector : vectors) {
                    vector = project2D(er.getResolution(), vector.x - mc.getRenderManager().viewerPosX,
                            vector.y - mc.getRenderManager().viewerPosY,
                            vector.z - mc.getRenderManager().viewerPosZ);

                    if (vector != null && vector.z >= 0.0 && vector.z < 1.0) {
                        if (position == null) {
                            position = new Vector4d(vector.x, vector.y, vector.z, 0.0);
                        }

                        position.x = Math.min(vector.x, position.x);
                        position.y = Math.min(vector.y, position.y);
                        position.z = Math.max(vector.x, position.z);
                        position.w = Math.max(vector.y, position.w);
                    }
                }

                mc.entityRenderer.setupOverlayRendering();

                if (position != null) {
                    double posX = position.x, posY = position.y, endPosX = position.z, endPosY = position.w;

                    // region BOX
                    RenderUtil.drawCornerBox(posX, posY, endPosX, endPosY, isItemSpecial((EntityItem) o) ? 4 : 3, BLACK);
                    RenderUtil.drawCornerBox(posX, posY, endPosX, endPosY, isItemSpecial((EntityItem) o) ? 2 : 1, getItemColor((EntityItem) o));
                    // endregion

                    // region name
                    if (names.get()) {
                        float amp = 1;
                        switch (mc.gameSettings.guiScale) {
                            case 0:
                                amp = 0.5F;
                                break;
                            case 1:
                                amp = 2.0F;
                                break;
                            case 3:
                                amp = 0.6666666666666667F;
                        }

                        double[] positions = ScaleUtils.getScaledMouseCoordinates(mc, posX, posY);
                        double[] positionsEnd = ScaleUtils.getScaledMouseCoordinates(mc, endPosX, endPosY);
                        double[] scaledPositions = new double[]{positions[0] * 2, positions[1] * 2, positionsEnd[0] * 2, positionsEnd[1] * 2};

                        GL11.glPushMatrix();
                        GL11.glScalef(0.5f * amp, 0.5f * amp, 0.5f * amp);
                        double _width = Math.abs(scaledPositions[2] - scaledPositions[0]);
                        float v = (float) (mc.fontRendererObj.FONT_HEIGHT * 2) - mc.fontRendererObj.FONT_HEIGHT / 2f;

                        mc.fontRendererObj.drawStringWithShadow(((EntityItem) o).getEntityItem().getDisplayName(),
                                (float) (scaledPositions[0] + _width / 2 -
                                        mc.fontRendererObj.getStringWidth(((EntityItem) o).getEntityItem().getDisplayName()) / 2),
                                (float) positionsEnd[1] * 2 + v,
                                getItemColor((EntityItem) o).brighter().getRGB());
                        GL11.glPopMatrix();
                        // endregion
                    }
                }
            }
        }
    }

    private boolean isItemSpecial(EntityItem o) {
        boolean special = o.getEntityItem().getItem() instanceof ItemArmor ||
                o.getEntityItem().getItem() == skull //
                        && !o.getEntityItem().getDisplayName().equalsIgnoreCase("Zombie Head") //
                        && !o.getEntityItem().getDisplayName().equalsIgnoreCase("Creeper Head") //
                        && !o.getEntityItem().getDisplayName().equalsIgnoreCase("Skeleton Skull") //
                        && !o.getEntityItem().getDisplayName().equalsIgnoreCase("Wither Skeleton Skull") //
                        && !o.getEntityItem().getDisplayName().equalsIgnoreCase(GREEN + "Frog's Hat")
                || o.getEntityItem().getItem() instanceof ItemAppleGold;


        if (o.getEntityItem().getItem() instanceof ItemArmor) {

            for (int type = 1; type < 5; type++) {
                String strType = "";

                switch (type) {
                    case 1:
                        strType = "helmet";
                        break;
                    case 2:
                        strType = "chestplate";
                        break;
                    case 3:
                        strType = "leggings";
                        break;
                    case 4:
                        strType = "boots";
                        break;
                }


                if (getSlotFromPlayerContainer(4 + type).getHasStack()) {
                    ItemStack is = getSlotFromPlayerContainer(4 + type).getStack();
                    if (is.getItem().getUnlocalizedName().contains(strType) && o.getEntityItem().getItem().getUnlocalizedName().contains(strType)) {
                        return getProtection(o.getEntityItem()) > getProtection(getSlotFromPlayerContainer(4 + type).getStack());
                    }
                }
            }
            return !hasItem(o.getEntityItem());
        } else if (o.getEntityItem().getItem() instanceof ItemPickaxe) {
            for (int i = 9; i < 45; i++) {
                if (getSlotFromPlayerContainer(i).getHasStack()) {
                    if (getSlotFromPlayerContainer(i).getStack().getItem() instanceof ItemPickaxe) {
                        return getToolEffect(o.getEntityItem()) > getToolEffect(getSlotFromPlayerContainer(i).getStack());
                    }
                }
            }
            return !hasItem(o.getEntityItem());
        } else if (o.getEntityItem().getItem() instanceof ItemSpade) {
            for (int i = 9; i < 45; i++) {
                if (getSlotFromPlayerContainer(i).getHasStack()) {
                    if (getSlotFromPlayerContainer(i).getStack().getItem() instanceof ItemSpade) {
                        return getToolEffect(o.getEntityItem()) > getToolEffect(getSlotFromPlayerContainer(i).getStack());
                    }
                }
            }
            return !hasItem(o.getEntityItem());
        } else if (o.getEntityItem().getItem() instanceof ItemAxe) {
            for (int i = 9; i < 45; i++) {
                if (getSlotFromPlayerContainer(i).getHasStack()) {
                    if (getSlotFromPlayerContainer(i).getStack().getItem() instanceof ItemAxe) {
                        return getToolEffect(o.getEntityItem()) > getToolEffect(getSlotFromPlayerContainer(i).getStack());
                    }
                }
            }
            return !hasItem(o.getEntityItem());
        }
        return special;
    }

    private float getProtection(ItemStack stack) {
        float protection = 0;

        if (stack.getItem() instanceof ItemArmor) {
            ItemArmor armor = (ItemArmor) stack.getItem();

            protection += (float) (armor.damageReduceAmount + (100 - armor.damageReduceAmount) * getEnchantmentLevel(Enchantment.protection.effectId, stack) * 0.0075D);
            protection += (float) (getEnchantmentLevel(blastProtection.effectId, stack) / 100d);
            protection += (float) (getEnchantmentLevel(fireProtection.effectId, stack) / 100d);
            protection += (float) (getEnchantmentLevel(thorns.effectId, stack) / 100d);
            protection += (float) (getEnchantmentLevel(unbreaking.effectId, stack) / 50d);
            protection += (float) (getEnchantmentLevel(projectileProtection.effectId, stack) / 100d);
        }

        return protection;
    }

    private float getToolEffect(ItemStack stack) {
        final Item item = stack.getItem();

        if (!(item instanceof ItemTool)) {
            return 0;
        }

        final String name = item.getUnlocalizedName();
        final ItemTool tool = (ItemTool) item;
        float value;

        if (item instanceof ItemPickaxe) {
            value = tool.getStrVsBlock(stack, Blocks.stone);
            if (name.toLowerCase().contains("gold")) value -= 5;
        } else if (item instanceof ItemSpade) {
            value = tool.getStrVsBlock(stack, Blocks.dirt);
            if (name.toLowerCase().contains("gold")) value -= 5;
        } else if (item instanceof ItemAxe) {
            value = tool.getStrVsBlock(stack, Blocks.log);
            if (name.toLowerCase().contains("gold")) value -= 5;
        } else return 1f;

        value += (float) (EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, stack) * 0.0075D);
        value += (float) (EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack) / 100.0D);

        return value;
    }


    private boolean hasItem(ItemStack is) {
        for (int i = 0; i < 3; i++) {
            if (mc.thePlayer.inventory.armorInventory[i] != null) {
                if (mc.thePlayer.inventory.armorInventory[i].getItem() == is.getItem()) {
                    return true;
                }
            }
        }
        for (int i = 9; i < 45; i++) {
            if (getSlotFromPlayerContainer(i).getHasStack()) {
                final ItemStack is1 = getSlotFromPlayerContainer(i).getStack();
                if (is.getItem() == is1.getItem()) {
                    return true;
                }
            }
        }
        return false;
    }


    private Color getItemColor(EntityItem o) {

        String displayName = o.getEntityItem().getDisplayName();
        if (displayName.equalsIgnoreCase(GOLD + "Excalibur") || displayName.equalsIgnoreCase("aDragon Sword")
                || displayName.equalsIgnoreCase(GREEN + "Cornucopia")
                || displayName.equalsIgnoreCase(RED + "Bloodlust") || displayName.equalsIgnoreCase(RED + "Artemis' Bow")
                || displayName.equalsIgnoreCase(GREEN + "Miner's Blessing") || displayName.equalsIgnoreCase(GOLD + "Axe of Perun")
                || displayName.equalsIgnoreCase(GOLD + "Cornucopia")) {

            return new Color(0xFF8A8AFF);
        }

        if (!isItemSpecial(o)) {
            return new Color(255, 255, 255);
        }

        if (o.getEntityItem().getItem() instanceof ItemArmor) {
            return new Color(75, 189, 193);
        } else if (o.getEntityItem().getItem() instanceof ItemAppleGold) {
            return new Color(255, 199, 71);
        } else if (o.getEntityItem().getItem() instanceof ItemSkull && isItemSpecial(o)) {
            return new Color(255, 199, 71);
        } else if (o.getEntityItem().getItem() instanceof ItemSword) {
            return new Color(255, 117, 117);
        } else if (o.getEntityItem().getItem() instanceof ItemPickaxe) {
            return new Color(130, 219, 82);
        } else if (o.getEntityItem().getItem() instanceof ItemSpade) {
            return new Color(130, 219, 82);
        } else if (o.getEntityItem().getItem() instanceof ItemAxe) {
            return new Color(130, 219, 82);
        } else if (o.getEntityItem().getItem() instanceof ItemRecord) {
            return new Color(255, 117, 117);
        } else {
            return new Color(255, 255, 255);
        }
    }

    private Vector3d project2D(ScaledResolution scaledResolution, double x, double y, double z) {
        GL11.glGetFloat(2982, this.modelView);
        GL11.glGetFloat(2983, this.projection);
        GL11.glGetInteger(2978, this.viewport);

        if (GLU.gluProject((float) x, (float) y, (float) z, this.modelView, this.projection, this.viewport,
                this.vector)) {
            return new Vector3d(this.vector.get(0) / scaledResolution.getScaleFactor(),
                    (Display.getHeight() - this.vector.get(1)) / scaledResolution.getScaleFactor(), this.vector.get(2));
        }

        return null;
    }

    public Slot getSlotFromPlayerContainer(int slot) {
        return mc.thePlayer.inventoryContainer.getSlot(slot);
    }
}
