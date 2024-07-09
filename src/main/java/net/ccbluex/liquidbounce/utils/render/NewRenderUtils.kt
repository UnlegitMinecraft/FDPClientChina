package net.ccbluex.liquidbounce.utils.render

import net.ccbluex.liquidbounce.FDPClientChina
import net.ccbluex.liquidbounce.features.module.modules.client.HUD
import net.ccbluex.liquidbounce.ui.RenderLeave
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11.*
import java.awt.Color

object NewRenderUtils {
    @JvmStatic
    private val glCapMap: MutableMap<Int, Boolean> = HashMap()
    @JvmStatic
    var deltaTime = 0
    @JvmStatic
    private val DISPLAY_LISTS_2D = IntArray(4)
    @JvmStatic
    fun drawShadowWithCustomAlpha(x: Float, y: Float, width: Float, height: Float, alpha: Float) {
        if(FDPClientChina.RENDERLEAVE== RenderLeave.LOW) return
        if(!HUD.shadowValue.get()) return
        drawTexturedRectWithCustomAlpha(x - 9, y - 9, 9f, 9f, "paneltopleft", alpha)
        drawTexturedRectWithCustomAlpha(x - 9, y + height, 9f, 9f, "panelbottomleft", alpha)
        drawTexturedRectWithCustomAlpha(x + width, y + height, 9f, 9f, "panelbottomright", alpha)
        drawTexturedRectWithCustomAlpha(x + width, y - 9, 9f, 9f, "paneltopright", alpha)
        drawTexturedRectWithCustomAlpha(x - 9, y, 9f, height, "panelleft", alpha)
        drawTexturedRectWithCustomAlpha(x + width, y, 9f, height, "panelright", alpha)
        drawTexturedRectWithCustomAlpha(x, y - 9, width, 9f, "paneltop", alpha)
        drawTexturedRectWithCustomAlpha(x, y + height, width, 9f, "panelbottom", alpha)
    }
//    @JvmStatic
//    fun drawShadowWithCustomColor(x: Float, y: Float, width: Float, height: Float, alpha: Float) {
//        if(LiquidBounce.RENDERLEAVE== RenderLeave.LOW) return
//        val customColor = Color(HUD.redValue.get(), HUD.greenValue.get(), HUD.blueValue.get(), 255)
//        val customColor1 = Color(HUD.gredValue.get(), HUD.ggreenValue.get(), HUD.gblueValue.get(), 255)
//        val counter1 = intArrayOf(50)
//        val counter2 = intArrayOf(80)
//        counter1[0] += 1
//        counter2[0] += 1
//        counter1[0] = counter1[0].coerceIn(0, 50)
//        counter2[0] = counter2[0].coerceIn(0, 80)
//        RenderUtils.drawfloatGradientSideways(
//            x - 9, y - 9, 9f, 9f, Palette.fade2(customColor, counter1[0], 50).rgb,
//            Palette.fade2(customColor1, counter2[0], 50).rgb
//        )
//        RenderUtils.drawfloatGradientSideways(
//            x - 9, y + height, 9f, 9f, Palette.fade2(customColor, counter1[0], 50).rgb,
//            Palette.fade2(customColor1, counter2[0], 50).rgb
//        )
//        RenderUtils.drawfloatGradientSideways(
//            x + width, y + height, 9f, 9f, Palette.fade2(customColor, counter1[0], 50).rgb,
//            Palette.fade2(customColor1, counter2[0], 50).rgb
//        )
//        RenderUtils.drawfloatGradientSideways(
//            x + width, y - 9, 9f, 9f, Palette.fade2(customColor, counter1[0], 50).rgb,
//            Palette.fade2(customColor1, counter2[0], 50).rgb
//        )
//        RenderUtils.drawfloatGradientSideways(
//            x - 9, y, 9f, height, Palette.fade2(customColor, counter1[0], 50).rgb,
//            Palette.fade2(customColor1, counter2[0], 50).rgb
//        )
//        RenderUtils.drawfloatGradientSideways(
//            x + width, y, 9f, height, Palette.fade2(customColor, counter1[0], 50).rgb,
//            Palette.fade2(customColor1, counter2[0], 50).rgb
//        )
//        RenderUtils.drawfloatGradientSideways(
//            x, y - 9, width, 9f, Palette.fade2(customColor, counter1[0], 50).rgb,
//            Palette.fade2(customColor1, counter2[0], 50).rgb
//        )
//        RenderUtils.drawfloatGradientSideways(
//            x, y + height, width, 9f, Palette.fade2(customColor, counter1[0], 50).rgb,
//            Palette.fade2(customColor1, counter2[0], 50).rgb
//        )
//    }
    @JvmStatic
    fun drawTexturedRectWithCustomAlpha(x: Float, y: Float, width: Float, height: Float, image: String, alpha: Float) {
        glPushMatrix()
        val enableBlend = glIsEnabled(GL_BLEND)
        val disableAlpha = !glIsEnabled(GL_ALPHA_TEST)
        if (!enableBlend) glEnable(GL_BLEND)
        if (!disableAlpha) glDisable(GL_ALPHA_TEST)
        GlStateManager.color(1f, 1f, 1f, alpha)
        Minecraft.getMinecraft().textureManager.bindTexture(ResourceLocation("fdpclient/shadow/$image.png"))
        drawModalRectWithCustomSizedTexture(
            x,
            y,
            0F,
            0F,
            width,
            height,
            width,
            height
        )
        if (!enableBlend) glDisable(GL_BLEND)
        if (!disableAlpha) glEnable(GL_ALPHA_TEST)
        GlStateManager.resetColor()
        glPopMatrix()
    }
    @JvmStatic
    fun drawTexturedRect(x: Float, y: Float, width: Float, height: Float, image: String) {
        glPushMatrix()
        val enableBlend = glIsEnabled(GL_BLEND)
        val disableAlpha = !glIsEnabled(GL_ALPHA_TEST)
        if (!enableBlend) glEnable(GL_BLEND)
        if (!disableAlpha) glDisable(GL_ALPHA_TEST)
        Minecraft.getMinecraft().textureManager.bindTexture(ResourceLocation("fdpclient/shadow/$image.png"))
        GlStateManager.color(1f, 1f, 1f, 1f)
        drawModalRectWithCustomSizedTexture(
            x,
            y,
            0F,
            0F,
            width,
            height,
            width,
            height
        )
        if (!enableBlend) glDisable(GL_BLEND)
        if (!disableAlpha) glEnable(GL_ALPHA_TEST)
        glPopMatrix()
    }
    @JvmStatic
    fun drawModalRectWithCustomSizedTexture(
        x: Float,
        y: Float,
        u: Float,
        v: Float,
        width: Float,
        height: Float,
        textureWidth: Float,
        textureHeight: Float
    ) {
        val f = 1.0f / textureWidth
        val f1 = 1.0f / textureHeight
        val tessellator = Tessellator.getInstance()
        val worldrenderer = tessellator.worldRenderer
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX)
        worldrenderer.pos(x.toDouble(), (y + height).toDouble(), 0.0).tex(
            (u * f).toDouble(),
            ((v + height.toFloat()) * f1).toDouble()
        ).endVertex()
        worldrenderer.pos((x + width).toDouble(), (y + height).toDouble(), 0.0).tex(
            ((u + width) * f).toDouble(),
            ((v + height) * f1).toDouble()
        ).endVertex()
        worldrenderer.pos((x + width).toDouble(), y.toDouble(), 0.0).tex(
            ((u + width) * f).toDouble(),
            (v * f1).toDouble()
        ).endVertex()
        worldrenderer.pos(x.toDouble(), y.toDouble(), 0.0).tex((u * f).toDouble(), (v * f1).toDouble()).endVertex()
        tessellator.draw()
    }
    @JvmStatic
    fun drawCircle(x: Float, y: Float, radius: Float, start: Int, end: Int) {
        GlStateManager.enableBlend()
        GlStateManager.disableTexture2D()
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO)
        glEnable(GL_LINE_SMOOTH)
        glLineWidth(2f)
        glBegin(GL_LINE_STRIP)
        var i = end.toFloat()
        while (i >= start) {
            val c = getGradientOffset(
                Color(HUD.redValue.get(), HUD.greenValue.get(), HUD.blueValue.get()),
                Color(HUD.gredValue.get(), HUD.ggreenValue.get(), HUD.gblueValue.get(), 1),
                (Math.abs(System.currentTimeMillis() / 360.0 + (i * 34 / 360) * 56 / 100) / 10)
            ).rgb
            val f2 = (c shr 24 and 255).toFloat() / 255.0f
            val f22 = (c shr 16 and 255).toFloat() / 255.0f
            val f3 = (c shr 8 and 255).toFloat() / 255.0f
            val f4 = (c and 255).toFloat() / 255.0f
            GlStateManager.color(f22, f3, f4, f2)
            glVertex2f(
                (x + Math.cos(i * Math.PI / 180) * (radius * 1.001f)).toFloat(),
                (y + Math.sin(i * Math.PI / 180) * (radius * 1.001f)).toFloat()
            )
            i -= 360f / 90.0f
        }
        glEnd()
        glDisable(GL_LINE_SMOOTH)
        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()
    }

    @JvmStatic
    fun drawFilledCircle(xx: Int, yy: Int, radius: Float, color: Color) {
        val sections = 50
        val dAngle = 2 * Math.PI / sections
        var x: Float
        var y: Float
        glPushAttrib(GL_ENABLE_BIT)
        GLUtils.glEnable(GL_BLEND)
        GLUtils.glDisable(GL_TEXTURE_2D)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        GLUtils.glEnable(GL_LINE_SMOOTH)
        glBegin(GL_TRIANGLE_FAN)
        for (i in 0 until sections) {
            x = (radius * Math.sin(i * dAngle)).toFloat()
            y = (radius * Math.cos(i * dAngle)).toFloat()
            glColor4f(color.red / 255f, color.green / 255f, color.blue / 255f, color.alpha / 255f)
            glVertex2f(xx + x, yy + y)
        }
        GlStateManager.color(0f, 0f, 0f)
        glEnd()
        glPopAttrib()
    }

    @JvmStatic
    fun getGradientOffset(color1: Color, color2: Color, gident: Double): Color {
        var gident = gident
        if (gident > 1.0) {
            val f1 = gident % 1.0
            val f2 = gident.toInt()
            gident = if (f2 % 2 == 0) f1 else 1.0 - f1
        }
        val f3 = 1.0 - gident
        val f4 = (color1.red.toDouble() * f3 + color2.red.toDouble() * gident).toInt()
        val f5 = (color1.green.toDouble() * f3 + color2.green.toDouble() * gident).toInt()
        val f6 = (color1.blue.toDouble() * f3 + color2.blue.toDouble() * gident).toInt()
        return Color(f4, f5, f6)
    }
}