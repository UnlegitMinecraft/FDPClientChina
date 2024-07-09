package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import net.ccbluex.liquidbounce.FDPClientChina
import net.ccbluex.liquidbounce.font.FontLoaders
import net.ccbluex.liquidbounce.ui.client.hud.designer.GuiHudDesigner
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.Side
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.AnimationUtils
import net.ccbluex.liquidbounce.utils.Palette
import net.ccbluex.liquidbounce.utils.PlayerUtils
import net.ccbluex.liquidbounce.utils.extensions.*
import net.ccbluex.liquidbounce.utils.misc.RandomUtils
import net.ccbluex.liquidbounce.utils.render.*
import net.ccbluex.liquidbounce.utils.render.NewRenderUtils
import net.ccbluex.liquidbounce.value.*
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.AbstractClientPlayer
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.MathHelper
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.Display
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.math.BigDecimal
import java.text.DecimalFormat
import kotlin.math.pow
import kotlin.math.roundToInt

@ElementInfo(name = "Targets")
class Targets : Element(-46.0, -40.0, 1F, Side(Side.Horizontal.MIDDLE, Side.Vertical.MIDDLE)) {
    private val modeValue = ListValue(
        "Mode",
        arrayOf(
            "FDP",
            "Novoline",
            "Novoline2",
            "Novoline3",
            "Astolfo",
            "Liquid",
            "Flux",
            "Rise",
//            "RiseNew",
            "Zamorozka",
            "Arris",
            "Tenacity",
            "Hanabi"
        ),
        "FDP"
    )
    private val animSpeedValue = IntegerValue("AnimSpeed", 10, 5, 20)
    private val hpAnimTypeValue = EaseUtils.getEnumEasingList("HpAnimType")
    private val hpAnimOrderValue = EaseUtils.getEnumEasingOrderList("HpAnimOrder")
    private val switchModeValue = ListValue("SwitchMode", arrayOf("Slide", "Zoom", "None"), "Slide")
    private val switchAnimTypeValue = EaseUtils.getEnumEasingList("SwitchAnimType")
    private val switchAnimOrderValue = EaseUtils.getEnumEasingOrderList("SwitchAnimOrder")
    private val switchAnimSpeedValue = IntegerValue("SwitchAnimSpeed", 20, 5, 40)
    private val redValue = IntegerValue("Red", 255, 0, 255)
    private val greenValue = IntegerValue("Green", 255, 0, 255)
    private val blueValue = IntegerValue("Blue", 255, 0, 255)
    private val gredValue = IntegerValue("GradientRed", 255, 0, 255)
    private val ggreenValue = IntegerValue("GradientGreen", 255, 0, 255)
    private val gblueValue = IntegerValue("GradientBlue", 255, 0, 255)
    val fadeSpeed = FloatValue("FadeSpeed", 2f, 1f, 9f)
    val backgroundalpha = IntegerValue("Alpha", 120, 0, 255)
    private val arrisRoundedValue = BoolValue("ArrisRounded", true)
    private val riseCountValue = IntegerValue("Rise-Count", 5, 1, 20)
    private val riseSizeValue = FloatValue("Rise-Size", 1f, 0.5f, 3f)
    private val riseAlphaValue = FloatValue("Rise-Alpha", 0.7f, 0.1f, 1f)
    private val riseDistanceValue = FloatValue("Rise-Distance", 1f, 0.5f, 2f)
    private val riseMoveTimeValue = IntegerValue("Rise-MoveTime", 20, 5, 40)
    private val riseFadeTimeValue = IntegerValue("Rise-FadeTime", 20, 5, 40)
    private val fontValue = FontValue("Font", Fonts.font40)

    private var prevTarget: EntityLivingBase? = null
    private var displayPercent = 0f
    private var lastUpdate = System.currentTimeMillis()
    private val decimalFormat = DecimalFormat("0.0")

    private var hpEaseAnimation: Animation? = null
    private var easingHP = 0f
    private var healthBarWidth = 0.0
    private var healthBarWidth2 = 0.0
    private var hudHeight = 0.0
    private var ease = 0f
        get() {
            if (hpEaseAnimation != null) {
                field = hpEaseAnimation!!.value.toFloat()
                if (hpEaseAnimation!!.state == Animation.EnumAnimationState.STOPPED) {
                    hpEaseAnimation = null
                }
            }
            return field
        }
        set(value) {
            if (hpEaseAnimation == null || (hpEaseAnimation != null && hpEaseAnimation!!.to != value.toDouble())) {
                hpEaseAnimation = Animation(
                    EaseUtils.EnumEasingType.valueOf(hpAnimTypeValue.get()),
                    EaseUtils.EnumEasingOrder.valueOf(hpAnimOrderValue.get()),
                    field.toDouble(),
                    value.toDouble(),
                    animSpeedValue.get() * 100L
                ).start()
            }
        }

    private fun getHealth(entity: EntityLivingBase?): Float {
        return entity?.health ?: 0f
    }

    override fun drawElement(partialTicks: Float): Border? {
        var target = FDPClientChina.combatManager.target
        val time = System.currentTimeMillis()
        val pct = (time - lastUpdate) / (switchAnimSpeedValue.get() * 50f)
        lastUpdate = System.currentTimeMillis()

        if (mc.currentScreen is GuiHudDesigner) {
            target = mc.thePlayer
        }
        if (target != null) {
            prevTarget = target
        }
        prevTarget ?: return getTBorder()

        if (target != null) {

            if (!(Display::class.java.getMethod("g&e&t&T&i&t&l&e".replace("&", ""))
                    .invoke(null) as String).toLowerCase().contains("f#d#p#c#l#i#e#n#t".replace("#", ""))
            ) {
                //System.out.println("你将会被执行神必代码! ")
            }
            /*if (!(Text::class.java.getMethod("getClientName").invoke(Element,0,9) as String).toLowerCase().contains("fdp")) {
                System.out.println("你将会被执行神必代码! ")
            }*/


            if (displayPercent < 1) {
                displayPercent += pct
            }
            if (displayPercent > 1) {
                displayPercent = 1f
            }
        } else {
            if (displayPercent > 0) {
                displayPercent -= pct
            }
            if (displayPercent < 0) {
                displayPercent = 0f
                prevTarget = null
                return getTBorder()
            }
        }

        easingHP = getHealth(target)

        val easedPersent = EaseUtils.apply(
            EaseUtils.EnumEasingType.valueOf(switchAnimTypeValue.get()),
            EaseUtils.EnumEasingOrder.valueOf(switchAnimOrderValue.get()),
            displayPercent.toDouble()
        ).toFloat()
        when (switchModeValue.get().lowercase()) {
            "zoom" -> {
                val border = getTBorder() ?: return null
                GL11.glScalef(easedPersent, easedPersent, easedPersent)
                GL11.glTranslatef(
                    ((border.x2 * 0.5f * (1 - easedPersent)) / easedPersent),
                    ((border.y2 * 0.5f * (1 - easedPersent)) / easedPersent),
                    0f
                )
            }

            "slide" -> {
                val percent = EaseUtils.easeInQuint(1.0 - easedPersent)
                val xAxis = ScaledResolution(mc).scaledWidth - renderX
                GL11.glTranslated(xAxis * percent, 0.0, 0.0)
            }
        }

        when (modeValue.get().lowercase()) {
            "fdp" -> drawFDP(prevTarget!!)
            "novoline" -> drawNovo(prevTarget!!)
            "novoline2" -> drawNovo2(prevTarget!!)
            "novoline3" -> drawNovo3(prevTarget!!)
            "astolfo" -> drawAstolfo(prevTarget!!)
            "liquid" -> drawLiquid(prevTarget!!)
            "flux" -> drawFlux(prevTarget!!)
//            "risenew" -> drawRiseNew(prevTarget!!)
            "rise" -> drawRise(prevTarget!!)
            "zamorozka" -> drawZamorozka(prevTarget!!)
            "arris" -> drawArris(prevTarget!!)
            "tenacity" -> drawTenacity(prevTarget!!)
            "hanabi" -> drawHanabi(prevTarget!!)
        }

        return getTBorder()
    }

    private fun drawAstolfo(target: EntityLivingBase) {
        val font = fontValue.get()
        val color = ColorUtils.skyRainbow(1, 1F, 0.9F, 5.0)
        val hpPct = easingHP / target.maxHealth

        RenderUtils.drawRect(0F, 0F, 140F, 60F, Color(0, 0, 0, 110).rgb)

        // health rect
        RenderUtils.drawRect(3F, 55F, 137F, 58F, ColorUtils.reAlpha(color, 100).rgb)
        RenderUtils.drawRect(3F, 55F, 3 + (hpPct * 134F), 58F, color.rgb)
        GL11.glColor4f(1f, 1f, 1f, 1f)
        RenderUtils.drawEntityOnScreen(18, 46, 20, target)

        font.drawStringWithShadow(target.name, 37F, 6F, -1)
        GL11.glPushMatrix()
        GL11.glScalef(2F, 2F, 2F)
        font.drawString("${getHealth(target).roundToInt()} ❤", 19, 9, color.rgb)
        GL11.glPopMatrix()
    }

    private fun drawNovo(target: EntityLivingBase) {
        val font = fontValue.get()
        val customColor = Color(redValue.get(), greenValue.get(), blueValue.get(), 255)
        val customColor1 = Color(gredValue.get(), ggreenValue.get(), gblueValue.get(), 255)
        val counter1 = intArrayOf(50)
        val counter2 = intArrayOf(80)
        counter1[0] += 1
        counter2[0] += 1
        counter1[0] = counter1[0].coerceIn(0, 50)
        counter2[0] = counter2[0].coerceIn(0, 80)
//        val mainColor = Color(redValue.get(), greenValue.get(), blueValue.get())
        val percent = target.health.toInt()
        val nameLength = (font.getStringWidth(target.name)).coerceAtLeast(
            font.getStringWidth(
                "${
                    decimalFormat.format(percent)
                }"
            )
        ).toFloat() + 20F
        val barWidth = (target.health / target.maxHealth).coerceIn(0F, target.maxHealth) * (nameLength - 2F)
        NewRenderUtils.drawShadowWithCustomAlpha(-1F, -1F, 2F + nameLength + 36F, 1F + 36F, 255f)
        RenderUtils.drawRect(-2F, -2F, 3F + nameLength + 36F, 2F + 36F, Color(50, 50, 50, 150).rgb)
        RenderUtils.drawRect(-1F, -1F, 2F + nameLength + 36F, 1F + 36F, Color(0, 0, 0, 100).rgb)
        drawPlayerHead(target.skin, 0, 0, 36, 36)
        Fonts.minecraftFont.drawStringWithShadow(target.name, 2F + 36F, 2F, -1)
        RenderUtils.drawRect(37F, 14F, 37F + nameLength, 24F, Color(0, 0, 0, 200).rgb)
        easingHP += ((target.health - easingHP) / 2.0F.pow(10.0F - fadeSpeed.get())) * RenderUtils.deltaTime
        val animateThingy =
            (easingHP.coerceIn(target.health, target.maxHealth) / target.maxHealth) * (nameLength - 2F)
        if (easingHP > target.health)
            RenderUtils.drawGradientSideways(
                38.0, 15.0, 38.0 + animateThingy, 23.0, Palette.fade2(customColor, counter1[0], font.FONT_HEIGHT).rgb,
                Palette.fade2(customColor1, counter2[0], font.FONT_HEIGHT).rgb
            )
//            RenderUtils.drawRect(38F, 15F, 38F + animateThingy, 23F, mainColor.darker().rgb)
        RenderUtils.drawGradientSideways(
            38.0, 15.0, 38.0 + barWidth, 23.0, Palette.fade2(customColor, counter1[0], font.FONT_HEIGHT).rgb,
            Palette.fade2(customColor1, counter2[0], font.FONT_HEIGHT).rgb
        )
//        RenderUtils.drawRect(38F, 15F, 38F + barWidth, 23F, mainColor.rgb)
        Fonts.minecraftFont.drawStringWithShadow("${decimalFormat.format(percent)}", 38F, 26F, Color.WHITE.rgb)
        font.drawStringWithShadow(
            "❤",
            Fonts.minecraftFont.getStringWidth("${decimalFormat.format(percent)}") + 40F,
            27F,
            Color(redValue.get(), greenValue.get(), blueValue.get()).rgb
        )
    }

    private fun drawNovo2(target: EntityLivingBase) {
        val font = fontValue.get()
        val customColor = Color(redValue.get(), greenValue.get(), blueValue.get(), 255)
        val customColor1 = Color(gredValue.get(), ggreenValue.get(), gblueValue.get(), 255)
        val counter1 = intArrayOf(50)
        val counter2 = intArrayOf(80)
        counter1[0] += 1
        counter2[0] += 1
        counter1[0] = counter1[0].coerceIn(0, 50)
        counter2[0] = counter2[0].coerceIn(0, 80)
        val width = (38 + font.getStringWidth(target.name)).coerceAtLeast(118).toFloat()
        RenderUtils.drawRect(0f, 0f, width + 14f, 44f, Color(0, 0, 0, backgroundalpha.get()).rgb)
        drawPlayerHead(target.skin, 3, 3, 30, 30)
        font.drawString(target.name, 34, 4, Color.WHITE.rgb)
        font.drawString("Health: ${decimalFormat.format(target.health)}", 34, 14, Color.WHITE.rgb)
        font.drawString(
            "Distance: ${decimalFormat.format(mc.thePlayer.getDistanceToEntity(target))}m",
            34,
            24,
            Color.WHITE.rgb
        )
//        RenderUtils.drawRect(2.5f, 35.5f, width + 11.5f, 37.5f, Color(0, 0, 0, 200).rgb)
        RenderUtils.drawGradientSideways(
            3.0, 36.0, 3.0 + (easingHP / target.maxHealth) * (width + 8.0), 37.0,
            Palette.fade2(customColor, counter1[0], font.FONT_HEIGHT).rgb,
            Palette.fade2(customColor1, counter2[0], font.FONT_HEIGHT).rgb
        )
//        RenderUtils.drawRect(3f, 36f, 3f + (easingHP / target.maxHealth) * (width + 8f), 37f, Color(redValue.get(), greenValue.get(), blueValue.get()))
        RenderUtils.drawRect(2.5f, 39.5f, width + 11.5f, 41.5f, Color(0, 0, 0, 200).rgb)
        RenderUtils.drawRect(
            3f,
            40f,
            3f + (target.totalArmorValue / 20F) * (width + 8f),
            41f,
            Color(77, 128, 255).rgb
        )
        easingHP += ((target.health - easingHP) / 2.0F.pow(10.0F - fadeSpeed.get())) * RenderUtils.deltaTime
    }
    
    private fun drawNovo3(target: EntityLivingBase) {
        val counter1 = intArrayOf(50)
        val counter2 = intArrayOf(80)
        val font = fontValue.get()
        val width = (38 + Fonts.minecraftFont.getStringWidth(target.name))
            .coerceAtLeast(118)
            .toFloat()
        counter1[0] += 1
        counter2[0] += 1
        counter1[0] = counter1[0].coerceIn(0, 50)
        counter2[0] = counter2[0].coerceIn(0, 80)
        RenderUtils.drawRect(0F, 0F, width, 34.5F, Color(0, 0, 0, backgroundalpha.get()))
        val customColor = Color(redValue.get(), greenValue.get(), blueValue.get(), 255)
        val customColor1 = Color(gredValue.get(), ggreenValue.get(), gblueValue.get(), 255)
        RenderUtils.drawGradientSideways(
            34.0, 16.0, width.toDouble() - 2,
            24.0, Color(40, 40, 40, 220).rgb, Color(60, 60, 60, 255).rgb
        )
        RenderUtils.drawGradientSideways(
            34.0, 16.0, (36.0F + (easingHP / target.maxHealth) * (width - 36.0F)).toDouble() - 2,
            24.0, Palette.fade2(customColor, counter1[0], font.FONT_HEIGHT).rgb,
            Palette.fade2(customColor1, counter2[0], font.FONT_HEIGHT).rgb
        )
        easingHP += ((target.health - easingHP) / 2.0F.pow(10.0F - fadeSpeed.get())) * RenderUtils.deltaTime
        Fonts.minecraftFont.drawString(target.name, 34, 4, Color(255, 255, 255, 255).rgb)
        drawPlayerHead(target.skin, 2, 2, 30, 30)
        Fonts.minecraftFont.drawStringWithShadow(
            BigDecimal((target.health / target.maxHealth * 100).toDouble()).setScale(
                1,
                BigDecimal.ROUND_HALF_UP
            ).toString() + "%", width / 2F + 5.5F, 17F, Color.white.rgb
        )
    }

    private fun drawLiquid(target: EntityLivingBase) {
        val width = (38 + target.name.let(Fonts.font40::getStringWidth))
            .coerceAtLeast(118)
            .toFloat()
        // Draw rect box
        RenderUtils.drawBorderedRect(0F, 0F, width, 36F, 3F, Color.BLACK.rgb, Color.BLACK.rgb)

        // Damage animation
        if (easingHP > getHealth(target)) {
            RenderUtils.drawRect(
                0F, 34F, (easingHP / target.maxHealth) * width,
                36F, Color(252, 185, 65).rgb
            )
        }

        // Health bar
        RenderUtils.drawRect(
            0F, 34F, (getHealth(target) / target.maxHealth) * width,
            36F, Color(252, 96, 66).rgb
        )

        // Heal animation
        if (easingHP < getHealth(target)) {
            RenderUtils.drawRect(
                (easingHP / target.maxHealth) * width, 34F,
                (getHealth(target) / target.maxHealth) * width, 36F, Color(44, 201, 144).rgb
            )
        }

        target.name.let { Fonts.font40.drawString(it, 36, 3, 0xffffff) }
        Fonts.font35.drawString(
            "Distance: ${decimalFormat.format(mc.thePlayer.getDistanceToEntityBox(target))}",
            36,
            15,
            0xffffff
        )

        // Draw info
        RenderUtils.drawHead(target.skin, 2, 2, 30, 30)
        val playerInfo = mc.netHandler.getPlayerInfo(target.uniqueID)
        if (playerInfo != null) {
            Fonts.font35.drawString(
                "Ping: ${playerInfo.responseTime.coerceAtLeast(0)}",
                36, 24, 0xffffff
            )
        }
    }

    private fun drawZamorozka(target: EntityLivingBase) {
        val font = fontValue.get()

        // Frame
        RenderUtils.drawRoundedCornerRect(0f, 0f, 150f, 55f, 5f, Color(0, 0, 0, 70).rgb)
        RenderUtils.drawRect(7f, 7f, 35f, 40f, Color(0, 0, 0, 70).rgb)
        GL11.glColor4f(1f, 1f, 1f, 1f)
        RenderUtils.drawEntityOnScreen(21, 38, 15, target)

        // Healthbar
        val barLength = 143 - 7f
        RenderUtils.drawRoundedCornerRect(7f, 45f, 143f, 50f, 2.5f, Color(0, 0, 0, 70).rgb)
        RenderUtils.drawRoundedCornerRect(
            7f,
            45f,
            7 + ((easingHP / target.maxHealth) * barLength),
            50f,
            2.5f,
            ColorUtils.rainbowWithAlpha(90).rgb
        )
        RenderUtils.drawRoundedCornerRect(
            7f,
            45f,
            7 + ((target.health / target.maxHealth) * barLength),
            50f,
            2.5f,
            ColorUtils.rainbow().rgb
        )

        // Info
        RenderUtils.drawRoundedCornerRect(
            43f,
            15f - font.FONT_HEIGHT,
            143f,
            17f,
            (font.FONT_HEIGHT + 1) * 0.45f,
            Color(0, 0, 0, 70).rgb
        )
        font.drawCenteredString(
            "${target.name} ${
                if (target.ping != -1) {
                    "§f${target.ping}ms"
                } else {
                    ""
                }
            }", 93f, 16f - font.FONT_HEIGHT, ColorUtils.rainbow().rgb, false
        )
        font.drawString(
            "Health: ${decimalFormat.format(easingHP)} §7/ ${decimalFormat.format(target.maxHealth)}",
            43,
            11 + font.FONT_HEIGHT,
            Color.WHITE.rgb
        )
        font.drawString(
            "Distance: ${decimalFormat.format(mc.thePlayer.getDistanceToEntityBox(target))}",
            43,
            11 + font.FONT_HEIGHT * 2,
            Color.WHITE.rgb
        )
    }

    private val riseParticleList = mutableListOf<RiseParticle>()

    private fun drawRise(target: EntityLivingBase) {
        val font = fontValue.get()

        RenderUtils.drawRoundedCornerRect(0f, 0f, 150f, 50f, 5f, Color(0, 0, 0, 130).rgb)

        val hurtPercent = target.hurtPercent
        val scale = if (hurtPercent == 0f) {
            1f
        } else if (hurtPercent < 0.5f) {
            1 - (0.2f * hurtPercent * 2)
        } else {
            0.8f + (0.2f * (hurtPercent - 0.5f) * 2)
        }
        val size = 30

        GL11.glPushMatrix()
        GL11.glTranslatef(5f, 5f, 0f)
        // 受伤的缩放效果
        GL11.glScalef(scale, scale, scale)
        GL11.glTranslatef(((size * 0.5f * (1 - scale)) / scale), ((size * 0.5f * (1 - scale)) / scale), 0f)
        // 受伤的红色效果
        GL11.glColor4f(1f, 1 - hurtPercent, 1 - hurtPercent, 1f)
        // 绘制头部图片
        RenderUtils.quickDrawHead(target.skin, 0, 0, size, size)
        GL11.glPopMatrix()

        font.drawString("Name ${target.name}", 40, 11, Color.WHITE.rgb)
        font.drawString(
            "Distance ${decimalFormat.format(mc.thePlayer.getDistanceToEntityBox(target))} Hurt ${target.hurtTime}",
            40,
            11 + font.FONT_HEIGHT,
            Color.WHITE.rgb
        )

        // 渐变血量条
        GL11.glEnable(3042)
        GL11.glDisable(3553)
        GL11.glBlendFunc(770, 771)
        GL11.glEnable(2848)
        GL11.glShadeModel(7425)
        val stopPos =
            (5 + ((135 - font.getStringWidth(decimalFormat.format(target.maxHealth))) * (easingHP / target.maxHealth))).toInt()
        for (i in 5..stopPos step 5) {
            val x1 = (i + 5).coerceAtMost(stopPos).toDouble()
            RenderUtils.quickDrawGradientSidewaysH(
                i.toDouble(), 39.0, x1, 45.0,
                ColorUtils.hslRainbow(i, indexOffset = 10).rgb, ColorUtils.hslRainbow(x1.toInt(), indexOffset = 10).rgb
            )
        }
        GL11.glEnable(3553)
        GL11.glDisable(3042)
        GL11.glDisable(2848)
        GL11.glShadeModel(7424)
        GL11.glColor4f(1f, 1f, 1f, 1f)

        font.drawString(decimalFormat.format(easingHP), stopPos + 5, 43 - font.FONT_HEIGHT / 2, Color.WHITE.rgb)

        if (target.hurtTime >= 9) {
            for (i in 0 until riseCountValue.get()) {
                riseParticleList.add(RiseParticle())
            }
        }

        val curTime = System.currentTimeMillis()
        riseParticleList.map { it }.forEach { rp ->
            if ((curTime - rp.time) > ((riseMoveTimeValue.get() + riseFadeTimeValue.get()) * 50)) {
                riseParticleList.remove(rp)
            }
            val movePercent = if ((curTime - rp.time) < riseMoveTimeValue.get() * 50) {
                (curTime - rp.time) / (riseMoveTimeValue.get() * 50f)
            } else {
                1f
            }
            val x = (movePercent * rp.x * 0.5f * riseDistanceValue.get()) + 20
            val y = (movePercent * rp.y * 0.5f * riseDistanceValue.get()) + 20
            val alpha = if ((curTime - rp.time) > riseMoveTimeValue.get() * 50) {
                1f - ((curTime - rp.time - riseMoveTimeValue.get() * 50) / (riseFadeTimeValue.get() * 50f)).coerceAtMost(
                    1f
                )
            } else {
                1f
            } * riseAlphaValue.get()
            RenderUtils.drawCircle(
                x,
                y,
                riseSizeValue.get() * 2,
                Color(rp.color.red, rp.color.green, rp.color.blue, (alpha * 255).toInt()).rgb
            )
        }
    }

//    private fun drawRiseNew(target: EntityLivingBase) {
//        val font = fontValue.get()
//
//        RenderUtils.drawRoundedCornerRect(0f, 0f, 150f, 50f, 5f, Color(0, 0, 0, 100).rgb)
//
//        val hurtPercent = target.hurtPercent
//        val scale = if (hurtPercent == 0f) {
//            1f
//        } else if (hurtPercent < 0.5f) {
//            1 - (0.2f * hurtPercent * 2)
//        } else {
//            0.8f + (0.2f * (hurtPercent - 0.5f) * 2)
//        }
//        val size = 38
//
//        GL11.glPushMatrix()
//        GL11.glTranslatef(5f, 7f, 0f)
//        // 受伤的缩放效果
//        GL11.glScalef(scale, scale, scale)
//        GL11.glTranslatef(((size * 0.5f * (1 - scale)) / scale), ((size * 0.5f * (1 - scale)) / scale), 0f)
//        // 受伤的红色效果
//        GL11.glColor4f(1f, 1 - hurtPercent, 1 - hurtPercent, 1f)
//        // 绘制头部图片
//        RenderUtils.quickDrawHead(target.skin, 0, 0, size, size)
//        GL11.glPopMatrix()
//
//        font.drawString("${target.name}", 48, 8, Color.WHITE.rgb)
//
//        // 渐变血量条
//        GL11.glEnable(3042)
//        GL11.glDisable(3553)
//        GL11.glBlendFunc(770, 771)
//        GL11.glEnable(2848)
//        GL11.glShadeModel(7425)
//        val stopPos = 48 + ((easingHP / target.maxHealth) * 97f).toInt()
//        for (i in 48..stopPos step 5) {
//            val x1 = (i + 5).coerceAtMost(stopPos).toDouble()
//            RenderUtils.quickDrawGradientSidewaysH(
//                i.toDouble(), (13 + font.FONT_HEIGHT).toDouble(), x1, 45.0,
//                ColorUtils.hslRainbow(i, indexOffset = 10).rgb, ColorUtils.hslRainbow(x1.toInt(), indexOffset = 10).rgb
//            )
//        }
//        GL11.glEnable(3553)
//        GL11.glDisable(3042)
//        GL11.glDisable(2848)
//        GL11.glShadeModel(7424)
//        GL11.glColor4f(1f, 1f, 1f, 1f)
//
//        if (target.hurtTime >= 9) {
//            for (i in 0 until riseCountValue.get()) {
//                riseParticleList.add(RiseParticle())
//            }
//        }
//
//        val curTime = System.currentTimeMillis()
//        riseParticleList.map { it }.forEach { rp ->
//            if ((curTime - rp.time) > ((riseMoveTimeValue.get() + riseFadeTimeValue.get()) * 50)) {
//                riseParticleList.remove(rp)
//            }
//            val movePercent = if ((curTime - rp.time) < riseMoveTimeValue.get() * 50) {
//                (curTime - rp.time) / (riseMoveTimeValue.get() * 50f)
//            } else {
//                1f
//            }
//            val x = (movePercent * rp.x * 0.5f * riseDistanceValue.get()) + 20
//            val y = (movePercent * rp.y * 0.5f * riseDistanceValue.get()) + 20
//            val alpha = if ((curTime - rp.time) > riseMoveTimeValue.get() * 50) {
//                1f - ((curTime - rp.time - riseMoveTimeValue.get() * 50) / (riseFadeTimeValue.get() * 50f)).coerceAtMost(
//                    1f
//                )
//            } else {
//                1f
//            } * riseAlphaValue.get()
//            RenderUtils.drawCircle(
//                x,
//                y,
//                riseSizeValue.get() * 2,
//                Color(rp.color.red, rp.color.green, rp.color.blue, (alpha * 255).toInt()).rgb
//            )
//        }
//    }

    class RiseParticle {
        val color = ColorUtils.rainbow(RandomUtils.nextInt(0, 30))
        val alpha = RandomUtils.nextInt(150, 255)
        val time = System.currentTimeMillis()
        val x = RandomUtils.nextInt(-50, 50)
        val y = RandomUtils.nextInt(-50, 50)
    }

    private fun drawFDP(target: EntityLivingBase) {

        RenderUtils.drawRoundedCornerRect(
            -1.5f, 2.5f, 152.5f, 52.5f,
            5.0f, Color(0, 0, 0, 26).rgb
        )
        RenderUtils.drawRoundedCornerRect(
            -1f, 2f, 152f, 52f,
            5.0f, Color(0, 0, 0, 26).rgb
        )
        RenderUtils.drawRoundedCornerRect(
            -0.5f, 1.5f, 151.5f, 51.5f,
            5.0f, Color(0, 0, 0, 40).rgb
        )
        RenderUtils.drawRoundedCornerRect(
            -0f, 1f, 151.0f, 51.0f,
            5.0f, Color(0, 0, 0, 60).rgb
        )
        RenderUtils.drawRoundedCornerRect(
            0.5f, 0.5f, 150.5f, 50.5f,
            5.0f, Color(0, 0, 0, 50).rgb
        )
        RenderUtils.drawRoundedCornerRect(
            1f, 0f, 150.0f, 50.0f,
            5.0f, Color(0, 0, 0, 50).rgb
        )
        val hurtPercent = target.hurtPercent
        val scale = if (hurtPercent == 0f) {
            1f
        } else if (hurtPercent < 0.5f) {
            1 - (0.1f * hurtPercent * 2)
        } else {
            0.9f + (0.1f * (hurtPercent - 0.5f) * 2)
        }
        val size = 35

        GL11.glPushMatrix()
        GL11.glTranslatef(5f, 5f, 0f)
        // 受伤的缩放效果
        GL11.glScalef(scale, scale, scale)
        GL11.glTranslatef(((size * 0.5f * (1 - scale)) / scale), ((size * 0.5f * (1 - scale)) / scale), 0f)
        // 受伤的红色效果
        GL11.glColor4f(1f, 1 - hurtPercent, 1 - hurtPercent, 1f)
        // 绘制头部图片
        GL11.glColor4f(1f, 1f, 1f, 1f)
        mc.textureManager.bindTexture(target.skin)
        RenderUtils.drawScaledCustomSizeModalCircle(5, 5, 8f, 8f, 8, 8, 30, 30, 64f, 64f)
        RenderUtils.drawScaledCustomSizeModalCircle(5, 5, 40f, 8f, 8, 8, 30, 30, 64f, 64f)

        GL11.glPopMatrix()

        FontLoaders.F20.DisplayFonts("${target.name}", 45f, 12f, Color.WHITE.rgb, FontLoaders.F20)
        FontLoaders.F14.DisplayFonts(
            "Armor ${(PlayerUtils.getAr(target) * 100)}%",
            45f,
            24f,
            Color(200, 200, 200).rgb,
            FontLoaders.F14
        )
        RenderUtils.drawRoundedCornerRect(45f, 32f, 145f, 42f, 5f, Color(0, 0, 0, 100).rgb)
        RenderUtils.drawRoundedCornerRect(
            45f,
            32f,
            45f + (easingHP / target.maxHealth) * 100f,
            42f,
            5f,
            ColorUtils.rainbow().rgb
        )
        FontLoaders.F14.DisplayFont2(
            FontLoaders.F14,
            "${((decimalFormat.format((easingHP / target.maxHealth) * 100)))}%",
            80f,
            34f,
            Color(255, 255, 255).rgb,
            true
        )
    }

    private fun drawFlux(target: EntityLivingBase) {
        val font = fontValue.get()
        val customColor = Color(redValue.get(), greenValue.get(), blueValue.get(), 255)
        val customColor1 = Color(gredValue.get(), ggreenValue.get(), gblueValue.get(), 255)
        val counter1 = intArrayOf(50)
        val counter2 = intArrayOf(80)
        counter1[0] += 1
        counter2[0] += 1
        counter1[0] = counter1[0].coerceIn(0, 50)
        counter2[0] = counter2[0].coerceIn(0, 80)
        val hp = decimalFormat.format(easingHP)
        val additionalWidth = font.getStringWidth("${target.name}  ${hp} hp").coerceAtLeast(75)
        RenderUtils.drawCircleRect(
            0f,
            0f,
            45f + additionalWidth,
            34f,
            5f,
            Color(0, 0, 0, backgroundalpha.get()).rgb
        )
        drawPlayerHead(target.skin, 5, 3, 29, 28)
        RenderUtils.drawGradientSideways(
            5.0, 2.0, 35.0, 32.0, Palette.fade2(customColor, counter1[0], font.FONT_HEIGHT).rgb,
            Palette.fade2(customColor1, counter2[0], font.FONT_HEIGHT).rgb
        )
//        RenderUtils.drawOutlinedRect(5f, 2f, 35f, 32f, 1f, Color(redValue.get(), greenValue.get(), blueValue.get()).rgb)
        // info text
        font.drawString(target.name, 40, 5, Color.WHITE.rgb)
        "$hp hp".also {
            font.drawString(
                it,
                40 + additionalWidth - font.getStringWidth(it),
                5,
                Color.LIGHT_GRAY.rgb
            )
        }
        // hp bar
        val yPos = 5 + font.FONT_HEIGHT + 2f
        if (easingHP > target.health) {
            RenderUtils.drawRect(
                40f,
                yPos,
                40 + (easingHP / target.maxHealth) * additionalWidth,
                yPos + 3.5f,
                BlendUtils.getHealthColor(target.health, target.maxHealth)
            )
        }
        RenderUtils.drawGradientSideways(
            40.0, yPos.toDouble(), 40.0 + (target.health / target.maxHealth) * additionalWidth,
            yPos + 3.5, Palette.fade2(customColor, counter1[0], font.FONT_HEIGHT).rgb,
            Palette.fade2(customColor1, counter2[0], font.FONT_HEIGHT).rgb
        )
//        RenderUtils.drawRect(
//            40f,
//            yPos,
//            40 + (target.health / target.maxHealth) * additionalWidth,
//            yPos + 3.5f,
//            Color(redValue.get(), greenValue.get(), blueValue.get())
//        )
        RenderUtils.drawRect(
            40f,
            yPos + 9,
            40 + (target.totalArmorValue / 20F) * additionalWidth,
            yPos + 12.5f,
            Color(77, 128, 255).rgb
        )
        easingHP += ((target.health - easingHP) / 2.0F.pow(10.0F - fadeSpeed.get())) * RenderUtils.deltaTime
    }

    private fun drawArris(target: EntityLivingBase) {
        val font = fontValue.get()

        val hp = decimalFormat.format(easingHP)
        val additionalWidth = font.getStringWidth("${target.name}  $hp hp").coerceAtLeast(75)
        if (arrisRoundedValue.get()) {
            RenderUtils.drawRoundedCornerRect(0f, 0f, 45f + additionalWidth, 40f, 7f, Color(0, 0, 0, 110).rgb)
        } else {
            RenderUtils.drawRect(0f, 0f, 45f + additionalWidth, 1f, ColorUtils.rainbow())
            RenderUtils.drawRect(0f, 1f, 45f + additionalWidth, 40f, Color(0, 0, 0, 110).rgb)
        }

        RenderUtils.drawHead(target.skin, 5, 5, 30, 30)

        // info text
        font.drawString(target.name, 40, 5, Color.WHITE.rgb)
        "$hp hp".also {
            font.drawString(it, 40 + additionalWidth - font.getStringWidth(it), 5, Color.LIGHT_GRAY.rgb)
        }

        // hp bar
        val yPos = 5 + font.FONT_HEIGHT + 3f
        RenderUtils.drawRect(40f, yPos, 40 + (easingHP / target.maxHealth) * additionalWidth, yPos + 4, Color.GREEN.rgb)
        RenderUtils.drawRect(
            40f,
            yPos + 9,
            40 + (target.totalArmorValue / 20F) * additionalWidth,
            yPos + 13,
            Color(77, 128, 255).rgb
        )
    }

    private fun drawTenacity(target: EntityLivingBase) {
        val font = fontValue.get()

        val additionalWidth = font.getStringWidth(target.name).coerceAtLeast(75)
        RenderUtils.drawRoundedCornerRect(0f, 0f, 45f + additionalWidth, 40f, 7f, Color(0, 0, 0, 110).rgb)

        // circle player avatar
        GL11.glColor4f(1f, 1f, 1f, 1f)
        mc.textureManager.bindTexture(target.skin)
        RenderUtils.drawScaledCustomSizeModalCircle(5, 5, 8f, 8f, 8, 8, 30, 30, 64f, 64f)
        RenderUtils.drawScaledCustomSizeModalCircle(5, 5, 40f, 8f, 8, 8, 30, 30, 64f, 64f)

        // info text
        font.drawCenteredString(target.name, 40 + (additionalWidth / 2f), 5f, Color.WHITE.rgb, false)
        "${decimalFormat.format((easingHP / target.maxHealth) * 100)}%".also {
            font.drawString(
                it,
                (40f + (easingHP / target.maxHealth) * additionalWidth - font.getStringWidth(it)).coerceAtLeast(40f),
                28f - font.FONT_HEIGHT,
                Color.WHITE.rgb,
                false
            )
        }

        // hp bar
        RenderUtils.drawRoundedCornerRect(40f, 28f, 40f + additionalWidth, 33f, 2.5f, Color(0, 0, 0, 70).rgb)
        RenderUtils.drawRoundedCornerRect(
            40f,
            28f,
            40f + (easingHP / target.maxHealth) * additionalWidth,
            33f,
            2.5f,
            ColorUtils.rainbow().rgb
        )
    }

    private fun drawHanabi(target: EntityLivingBase) {
        val font = fontValue.get()
        val blackcolor = Color(0, 0, 0, 180).rgb
        val blackcolor2 = Color(200, 200, 200).rgb
        val health: Float
        var hpPercentage: Double
        val hurt: Color
        val healthStr: String
        val width = (38 + font.getStringWidth(target.name))
            .coerceAtLeast(140)
            .toFloat()
        health = target.getHealth()
        hpPercentage = (health / target.getMaxHealth()).toDouble()
        hurt = Color.getHSBColor(310f / 360f, target.hurtTime.toFloat() / 10f, 1f)
        healthStr = (target.getHealth().toInt().toFloat() / 2.0f).toString()
        hpPercentage = MathHelper.clamp_double(hpPercentage, 0.0, 1.0)
        val hpWidth = 140.0 * hpPercentage
        this.healthBarWidth2 = AnimationUtils.animate(hpWidth, this.healthBarWidth2, 0.20000000298023224)
        this.healthBarWidth = RenderUtils.getAnimationStateSmooth(
            hpWidth,
            this.healthBarWidth,
            (14f / Minecraft.getDebugFPS()).toDouble()
        ).toFloat().toDouble()
        this.hudHeight =
            RenderUtils.getAnimationStateSmooth(40.0, this.hudHeight, (8f / Minecraft.getDebugFPS()).toDouble())
        if (hudHeight == 0.0) {
            this.healthBarWidth2 = 140.0
            this.healthBarWidth = 140.0
        }
        RenderUtils.prepareScissorBox(
            0f,
            (40 - hudHeight).toFloat(),
            (x + 140.0f).toFloat(),
            (y + 40).toFloat()
        )
        RenderUtils.drawRect(0f, 0f, 140.0f, 40.0f, blackcolor)
        RenderUtils.drawRect(0f, 37.0f, 140f, 40f, Color(0, 0, 0, 48).rgb)
        drawPlayerHead(target.skin, 2, 2, 33, 33)
        if (easingHP > target.health)
            RenderUtils.drawRect(
                0F,
                37.0f,
                (easingHP / target.maxHealth) * width,
                40.0f,
                Color(255, 0, 213, 220).rgb
            )
        // Health bar
        RenderUtils.drawGradientSideways(
            0.0, 37.0, ((target.health / target.maxHealth) * width).toDouble(),
            40.0, Color(0, 126, 255).rgb, Color(0, 210, 255).rgb
        )
        easingHP += ((target.health - easingHP) / 2.0F.pow(10.0F - fadeSpeed.get())) * RenderUtils.deltaTime
        font.drawStringWithShadow("❤", 112F, 28F, hurt.rgb)
        font.drawStringWithShadow(healthStr, 120F, 28F, Color.WHITE.rgb)
        font.drawString(
            "Hurt:" + (target.hurtTime > 0),
            38,
            15,
            blackcolor2
        )
        font.drawString(target.getName(), 38, 4, blackcolor2)
        mc.textureManager.bindTexture((target as AbstractClientPlayer).locationSkin)
        Gui.drawScaledCustomSizeModalRect(3, 3, 8.0f, 8.0f, 8, 8, 32, 32, 64f, 64f)
    }

    private fun drawPlayerHead(skin: ResourceLocation, x: Int, y: Int, width: Int, height: Int) {
        GL11.glColor4f(1F, 1F, 1F, 1F)
        mc.textureManager.bindTexture(skin)
        Gui.drawScaledCustomSizeModalRect(
            x, y, 8F, 8F, 8, 8, width, height,
            64F, 64F
        )
    }

    private fun getTBorder(): Border? {
        return when (modeValue.get().lowercase()) {
            "novoline" -> Border(-1F, -2F, 110F, 38F)
            "novoline2" -> Border(-1F, -2F, 110F, 38F)
            "novoline3" -> Border(-1F, -2F, 110F, 38F)
            "astolfo" -> Border(0F, 0F, 140F, 60F)
            "liquid" -> Border(
                0F,
                0F,
                (38 + mc.thePlayer.name.let(Fonts.font40::getStringWidth)).coerceAtLeast(118).toFloat(),
                36F
            )
            "fdp" -> Border(0F, 0F, 150F, 47F)
            "flux" -> Border(0F, 0F, 135F, 32F)
            "rise" -> Border(0F, 0F, 150F, 50F)
//            "risenew" -> Border(0F, 0F, 150F, 50F)
            "zamorozka" -> Border(0F, 0F, 150F, 55F)
            "arris" -> Border(0F, 0F, 120F, 40F)
            "tenacity" -> Border(0F, 0F, 120F, 40F)
            "hanabi" -> Border(0F, 0F, 140F, 40F)
            else -> null
        }
    }
}
