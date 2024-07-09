/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/UnlegitMinecraft/FDPClientChina/
 */
package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import net.ccbluex.liquidbounce.FDPClientChina
import net.ccbluex.liquidbounce.font.CFontRenderer
import net.ccbluex.liquidbounce.font.FontLoaders
import net.ccbluex.liquidbounce.ui.client.GuiSelectPerformance
import net.ccbluex.liquidbounce.ui.client.hud.designer.GuiHudDesigner
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.Side
import net.ccbluex.liquidbounce.utils.render.BlurUtils
import net.ccbluex.liquidbounce.utils.render.EaseUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.utils.render.NewRenderUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.max


/**
 * CustomHUD Notification element
 */
@ElementInfo(name = "Notifications", blur = true)
class Notifications(
    x: Double = 0.0,
    y: Double = 0.0,
    scale: Float = 1F,
    side: Side = Side(Side.Horizontal.RIGHT, Side.Vertical.DOWN)
) : Element(x, y, scale, side) {

    private val backGroundAlphaValue = IntegerValue("BackGroundAlpha", 235, 0, 255)

    private val TitleShadow = BoolValue("Title Shadow", false)
    private val MotionBlur = BoolValue("Motion blur", false)
    private val ContentShadow = BoolValue("Content Shadow", true)
    //private val fontValue = FontValue("Font", Fonts.font35)

    /**
     * Example notification for CustomHUD designer
     */
    private val exampleNotification = Notification("Notification", "This is an example notification.", NotifyType.INFO)

    /**
     * Draw element
     */
    override fun drawElement(partialTicks: Float): Border? {
        // bypass java.util.ConcurrentModificationException
        FDPClientChina.hud.notifications.map { it }.forEachIndexed { index, notify ->
            GL11.glPushMatrix()

            if (notify.drawNotification(index, FontLoaders.C16, backGroundAlphaValue.get(), blurValue.get(), this.renderX.toFloat(), this.renderY.toFloat(), scale,ContentShadow.get(),TitleShadow.get(),MotionBlur.get())) {
                FDPClientChina.hud.notifications.remove(notify)
            }

            GL11.glPopMatrix()
        }

        if (mc.currentScreen is GuiHudDesigner) {
            if (!FDPClientChina.hud.notifications.contains(exampleNotification)) {
                FDPClientChina.hud.addNotification(exampleNotification)
            }

            exampleNotification.fadeState = FadeState.STAY
            exampleNotification.displayTime = System.currentTimeMillis()
//            exampleNotification.x = exampleNotification.textLength + 8F

            return Border(-exampleNotification.width.toFloat(), -exampleNotification.height.toFloat(), 0F, 0F)
        }

        return null
    }

    override fun drawBoarderBlur(blurRadius: Float) {}
}

class Notification(
    val title: String,
    val content: String,
    val type: NotifyType,
    val time: Int = 1500,
    val animeTime: Int = 500
) {
    var width = 100
    val height = 25

    var fadeState = FadeState.IN
    var nowY = -height
    var displayTime = System.currentTimeMillis()
    var animeXTime = System.currentTimeMillis()
    var animeYTime = System.currentTimeMillis()

    /**
     * Draw notification
     */
    fun drawNotification(index: Int, font: CFontRenderer, alpha: Int, blurRadius: Float, x: Float, y: Float, scale: Float,ContentShadow: Boolean,TitleShadow: Boolean,MotionBlur: Boolean): Boolean {
        this.width = 100.coerceAtLeast(FontLoaders.C12.DisplayFontWidths(font,content)
            .coerceAtLeast(font.getStringWidth(title)) + 15)
        val realY = -(index+1) * height
        val nowTime = System.currentTimeMillis()
        var transY = nowY.toDouble()

        // Y-Axis Animation
        if (nowY != realY) {
            var pct = (nowTime - animeYTime) / animeTime.toDouble()
            if (pct> 1) {
                nowY = realY
                pct = 1.0
            } else {
                pct = EaseUtils.easeOutExpo(pct)
            }
            transY += (realY - nowY) * pct
        } else {
            animeYTime = nowTime
        }

        // X-Axis Animation
        var pct = (nowTime - animeXTime) / animeTime.toDouble()
        when (fadeState) {
            FadeState.IN -> {
                if (pct> 1) {
                    fadeState = FadeState.STAY
                    animeXTime = nowTime
                    pct = 1.0
                }
                pct = EaseUtils.easeOutExpo(pct)
            }

            FadeState.STAY -> {
                pct = 1.0
                if ((nowTime - animeXTime)> time) {
                    fadeState = FadeState.OUT
                    animeXTime = nowTime
                }
            }

            FadeState.OUT -> {
                if (pct> 1) {
                    fadeState = FadeState.END
                    animeXTime = nowTime
                    pct = 1.0
                }
                pct = 1 - EaseUtils.easeInExpo(pct)
            }

            FadeState.END -> {
                return true
            }
        }
        val transX = width - (width * pct) - width
        GL11.glTranslated(transX, transY, 0.0)
        if (blurRadius != 0f) {
            if(!GuiSelectPerformance.offblur) BlurUtils.draw(4 + (x + transX).toFloat() * scale, (y + transY).toFloat() * scale, (width * scale) , (height.toFloat()-5f) * scale, blurRadius)
        }

        // draw notify
//        GL11.glPushMatrix()
//        GL11.glEnable(GL11.GL_SCISSOR_TEST)
//        GL11.glScissor(width-(width*pct).toFloat(),0F, width.toFloat(),height.toFloat())
        var colors=Color(type.renderColor.red,type.renderColor.green,type.renderColor.blue,alpha/3);

        NewRenderUtils.drawShadowWithCustomAlpha(2f,
            0F,
            width.toFloat() + 5f,
            height.toFloat() - 5f, 250f)

        RenderUtils.drawRect(
            2.0,
            0.0,
            4.0,
            height.toFloat() - 5.0,
            colors.rgb,
        )
        RenderUtils.drawRect(
            3F,
            0F,
            width.toFloat() + 5f,
            height.toFloat() - 5f,
            Color(0,0,0,150)
        )
        RenderUtils.drawGradientSidewaysH(
            3.0,
            0.0,
            20.0,
            height.toFloat() - 5.0,
            colors.rgb,
            Color(0,0,0,0).rgb
        )
        RenderUtils.drawRect(2f, height.toFloat()-6f, max(width - width * ((nowTime - displayTime) / (animeTime * 2F + time))+5f, 0F), height.toFloat()-5f ,Color(52, 97, 237).rgb)

        FontLoaders.C12.DisplayFont2(FontLoaders.C12,title, 4F, 3F, Color(245,245,245).rgb,TitleShadow)
        font.DisplayFont2(font,content, 4F, 10F, Color(255,255,255).rgb,ContentShadow)
        return false
    }
}

enum class NotifyType(var renderColor: Color) {
    SUCCESS(Color(0x36D399)),
    ERROR(Color(0xF87272)),
    WARNING(Color(0xFBBD23)),
    INFO(Color(0xF2F2F2));
}

enum class FadeState { IN, STAY, OUT, END }
