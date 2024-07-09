package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.render.ColorUtils
import net.ccbluex.liquidbounce.utils.render.HotbarUtil
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.FontValue
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import java.awt.Color

@ElementInfo(name = "Hotbar")//By Fu917
class Hotbar(x: Double = 40.0, y: Double = 100.0) : Element(x, y) {
    private val itemrenderY = FloatValue("itemrenderY",80f, -500f, 500f)
    private val fonts = FontValue("Font", Fonts.font35)


    val slotlist = mutableListOf<HotbarUtil>()

    private var lastSlot = -1

    init {
        for (i in 0..8) {
            val slot = HotbarUtil()
            slotlist.add(slot)
        }
    }

    override fun drawElement(partialTicks: Float): Border {
        GlStateManager.pushMatrix()
        GlStateManager.enableRescaleNormal()
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)


        slotlist.forEachIndexed { index, hotbarutil ->

            val hover = index == mc.thePlayer.inventory.currentItem && mc.thePlayer.inventory.mainInventory[index] != null
            val scale = hotbarutil.translate.x
            val positionX = (index * 25 / scale) - 5
            val currentitem = mc.thePlayer.inventory.mainInventory[mc.thePlayer.inventory.currentItem]

            hotbarutil.size = if (hover) 1.5f else 1.0f
            hotbarutil.translate.translate(hotbarutil.size, 0f, 2.0)

            if (hover) {
                GlStateManager.pushMatrix()
                GlStateManager.scale(scale - 0.5f, scale - 0.5f, scale - 0.5f)

                try {
                    val list = currentitem.getTooltip(mc.thePlayer, mc.gameSettings.advancedItemTooltips)
                    val infolist : ArrayList<String> = ArrayList()

                    for(i in 0 until list.size) {
                        if (!infolist.contains(list[i]) && list[i].length > 2 ) {
                            infolist.add(list[i])
                        }
                    }
                    var posy = -13f
                    infolist.forEachIndexed{index , it ->
                        val font = if(ColorUtils.stripColor(infolist[index]) == currentitem.displayName) fonts.get() else fonts.get()
                        font.drawString(infolist[index], positionX * 1.5f,(-(8.5f * infolist.size) + posy)+itemrenderY.get(),if(ColorUtils.stripColor(infolist[index]) == currentitem.displayName) -1 else Color(175 ,175 ,175).rgb, true)
                        posy += font.FONT_HEIGHT + 2f
                    }
                    infolist.clear()
                } catch (e : Exception) {
                    e.printStackTrace()
                }
                GlStateManager.popMatrix()
            }
            GlStateManager.pushMatrix()
            GlStateManager.scale(scale, scale, scale)
            RenderHelper.enableGUIStandardItemLighting()
            hotbarutil.renderHotbarItem(index, positionX, -10f, mc.timer.renderPartialTicks,fonts.get())
            RenderHelper.disableStandardItemLighting()
            GlStateManager.popMatrix()
        }

        GlStateManager.disableRescaleNormal()
        GlStateManager.disableBlend()
        GlStateManager.popMatrix()
        return Border(-5f, -10f, 210f, 5f)

    }
}