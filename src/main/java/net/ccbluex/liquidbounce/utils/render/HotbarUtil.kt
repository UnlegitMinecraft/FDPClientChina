package net.ccbluex.liquidbounce.utils.render

import net.ccbluex.liquidbounce.utils.misc.Translate
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager

class HotbarUtil {
    val translate = Translate(0.0f, 0.0f)
    var size = 1.0f

    fun renderHotbarItem(index: Int, xPos: Float, yPos: Float, partialTicks: Float,fonts: FontRenderer) {

        val itemStack = Minecraft.getMinecraft().thePlayer.inventory.mainInventory[index]
        if (itemStack != null) {
            val lvt_7_1_ = itemStack.animationsToGo.toFloat() - partialTicks
            if (lvt_7_1_ > 0.0f) {
                GlStateManager.pushMatrix()
                val lvt_8_1_ = 1.0f + lvt_7_1_ / 5.0f
                GlStateManager.translate((xPos + 8), (yPos + 12), 0.0f)
                GlStateManager.scale(1.0f / lvt_8_1_, (lvt_8_1_ + 1.0f) / 2.0f, 1.0f)
                GlStateManager.translate((-(xPos + 8)), (-(yPos + 12)), 0.0f)
            }
            RenderUtils.drawTexturedRect(
                xPos - 2,
                yPos - -3,
                20f,
                20f,
                "hotbar",
                ScaledResolution(Minecraft.getMinecraft())
            )
            RenderUtils.drawTexturedRect(
                xPos - 2,
                yPos - -3,
                20f,
                20f,
                "hotbar",
                ScaledResolution(Minecraft.getMinecraft())
            )

            //RenderUtils.drawTexturedRect(xPos - 7, yPos -7,30f,30f,"hotbar",  ScaledResolution(Minecraft.getMinecraft()));
            //RenderUtils.drawTexturedRect(xPos - 7, yPos -7,30f,30f,"hotbar",  ScaledResolution(Minecraft.getMinecraft()));
            Minecraft.getMinecraft().renderItem.renderItemAndEffectIntoGUI(itemStack, xPos.toInt(), (yPos).toInt())
            if (lvt_7_1_ > 0.0f) {
                GlStateManager.popMatrix()
            }
            Minecraft.getMinecraft().renderItem.renderItemOverlays(fonts, itemStack, xPos.toInt(),
                yPos.toInt()
            )
        }
    }
}