/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/UnlegitMinecraft/FDPClientChina/
 */
package net.ccbluex.liquidbounce.launch.data.legacyui

import com.google.common.collect.Lists
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.module.modules.client.HUD.genshinImpactAnim
import net.ccbluex.liquidbounce.font.FontLoaders
import net.ccbluex.liquidbounce.ui.btn.TestBtn
import net.ccbluex.liquidbounce.ui.client.GuiBackground
import net.ccbluex.liquidbounce.ui.client.altmanager.GuiAltManager
import net.ccbluex.liquidbounce.ui.i18n.LanguageManager
import net.ccbluex.liquidbounce.utils.misc.MiscUtils
import net.ccbluex.liquidbounce.utils.misc.RandomUtils.nextInt
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.*
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.resources.I18n
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.client.GuiModList
import org.apache.commons.io.Charsets
import org.lwjgl.opengl.GLContext
import java.awt.Color
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*


class GuiMainMenu : GuiScreen(), GuiYesNoCallback {
    private val girl: Int
    private var openGLWarning1: String
    private var openGLWarning2: String? = null
    private var openGLWarningLink: String? = null
    private var splashText = "missingno"
    var drawed = false;
    var updateCounter = 0f
    var clicked = false;
    var displayed = false;

    init {
        var bufferedreader: BufferedReader? = null
        try {
            val list: MutableList<String> = Lists.newArrayList()
            if (!list.isEmpty()) {
                do {
                    splashText = list[RANDOM.nextInt(list.size / 2)]
                } while (splashText.hashCode() == 125780783)
            }
        } catch (ignored: IOException) {
        } finally {
            if (bufferedreader != null) {
                try {
                    bufferedreader.close()
                } catch (ignored: IOException) {
                }
            }
        }
        girl = nextInt(0, 9)
        updateCounter = RANDOM.nextFloat()
        openGLWarning1 = ""
        if (!GLContext.getCapabilities().OpenGL20 && !OpenGlHelper.areShadersSupported()) {
            openGLWarning1 = I18n.format("title.oldgl1")
            openGLWarning2 = I18n.format("title.oldgl2")
            openGLWarningLink = "https://help.mojang.com/customer/portal/articles/325948?ref=game"
        }
    }

    fun drawBtns() {
        this.buttonList.add(
                TestBtn(
                        1,
                        (this.width / 2) - (130 / 2),
                        this.height / 2 - 20,
                        130,
                        23,
                        I18n.format("menu.singleplayer"),
                        null,
                        2,
                        Color(20, 20, 20, 130)
                )
        )

        this.buttonList.add(
                TestBtn(
                        2,
                        (this.width / 2) - (130 / 2),
                        this.height / 2 + 10,
                        130,
                        23,
                        I18n.format("menu.multiplayer"),
                        null,
                        2,
                        Color(20, 20, 20, 130)
                )
        )

        this.buttonList.add(
                TestBtn(
                        100,
                        (this.width / 2) - (130 / 2),
                        this.height / 2 + 40,
                        130,
                        23,
                        LanguageManager.get("ui.altmanager"),
                        null,
                        2,
                        Color(20, 20, 20, 130)
                )
        )

        this.buttonList.add(
                TestBtn(
                        103,
                        (this.width / 2) - (130 / 2),
                        this.height / 2 + 70,
                        130,
                        23,
                        LanguageManager.get("ui.mods"),
                        null,
                        2,
                        Color(20, 20, 20, 130)
                )
        )


        this.buttonList.add(
                TestBtn(
                        4,
                        this.width - 35,
                        10,
                        25,
                        25,
                        I18n.format("menu.quit"),
                        ResourceLocation("fdpclient/imgs/icon/quit.png"),
                        2,
                        Color(20, 20, 20, 130)
                )
        )

        this.buttonList.add(
                TestBtn(
                        0,
                        this.width - 65,
                        10,
                        25,
                        25,
                        I18n.format("menu.options"),
                        ResourceLocation("fdpclient/imgs/icon/setting.png"),
                        2,
                        Color(20, 20, 20, 130)
                )
        )


        this.buttonList.add(
                TestBtn(
                        104,
                        this.width - 95,
                        10,
                        25,
                        25,
                        I18n.format("ui.background"),
                        ResourceLocation("fdpclient/imgs/icon/wallpaper.png"),
                        2,
                        Color(20, 20, 20, 130)
                )
        )

        this.buttonList.add(
                TestBtn(
                        102,
                        this.width - 125,
                        10,
                        25,
                        25,
                        "Announcement",
                        ResourceLocation("fdpclient/imgs/icon/announcement.png"),
                        2,
                        Color(20, 20, 20, 130)
                )
        )

        this.buttonList.add(
                TestBtn(
                        514, this.width - 155, 10, 25, 25, "Discord", ResourceLocation("fdpclient/imgs/icon/discord.png"), 2,
                        Color(20, 20, 20, 130)
                )
        )

        this.buttonList.add(
                TestBtn(
                        114, this.width - 185, 10, 25, 25, "Website", ResourceLocation("fdpclient/imgs/icon/website.png"), 2,
                        Color(20, 20, 20, 130)
                )
        )


        this.buttonList.add(
                TestBtn(
                        191, 20, 10, 25, 25, "Change exterior", ResourceLocation("fdpclient/imgs/icon/moon-night.png"), 2,
                        Color(20, 20, 20, 130)
                )
        )

        drawed = true;
    }

    /* For modification, please keep "Designed by XiGua" */
    override fun initGui() {
        val defaultHeight = (this.height / 3.5).toInt()
        //我急了，写破防了，写了7个小时没写好
//        Thread {
//            if (LiquidBounce.CLIENTTEXT.contains("Waiting") || LiquidBounce.CLIENTTEXT.contains("Oops")) {
//                try {
//                    LiquidBounce.CLIENTTEXT = HttpUtils.get("http://fdpclient.club/changelogs")
//                } catch (e: Exception) {
//                    try {
//                        LiquidBounce.CLIENTTEXT = HttpUtils.get("http://fdpclient.club/changelogs")
//                    } catch (e: Exception) {
//                        LiquidBounce.CLIENTTEXT = "Oops.. :(\$Can't get information!#Try reopen the main menu\$140\$80"
//                    }
//                }
//            }
//        }.start()

        drawBtns()
        //this.buttonList.add(TestBtn(102, this.width - 95, 10, 25, 25, LanguageManager.get("ui.background"), ResourceLocation("fdpclient/imgs/icon/wallpaper.png"), 2,
        //    Color(20, 20, 20, 130)))


        /*
        this.buttonList.add(GuiButton(1, this.width / 2 - 50, defaultHeight, 100, 20, I18n.format("menu.singleplayer")))
        this.buttonList.add(GuiButton(2, this.width / 2 - 50, defaultHeight + 24, 100, 20, I18n.format("menu.multiplayer")))
        this.buttonList.add(GuiButton(100, this.width / 2 - 50, defaultHeight + 24 * 2, 100, 20, "%ui.altmanager%"))
        this.buttonList.add(GuiButton(103, this.width / 2 - 50, defaultHeight + 24 * 3, 100, 20, "%ui.mods%"))
        this.buttonList.add(GuiButton(102, this.width / 2 - 50, defaultHeight + 24 * 4, 100, 20, "%ui.background%"))
        this.buttonList.add(GuiButton(0, this.width / 2 - 50, defaultHeight + 24 * 5, 100, 20, I18n.format("menu.options")))
        this.buttonList.add(GuiButton(4, this.width / 2 - 50, defaultHeight + 24 * 6, 100, 20, I18n.format("menu.quit")))
        */
        super.initGui()

    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawBackground(0)
        val defaultHeight = (this.height).toFloat()
        val defaultWidth = (this.width).toFloat()
        //RenderUtils.drawCircle(defaultWidth/2,defaultHeight/2 + 60F, 150F,Color(0,0,0,100).rgb);
        val i = 0;
        val defaultHeight1 = (this.height).toDouble()
        val defaultWidth1 = (this.width).toDouble()

        val imageWidth = arrayOf(230, 250, 400)
        val imageHeight = arrayOf(312, 353, 422, 305, 242)

        var imageX: Int
        var imageY: Int

        when (girl) {
            0 -> {
                imageX = imageWidth[0]
                imageY = imageHeight[0]
            }

            1 -> {
                imageX = imageWidth[0]
                imageY = imageHeight[0]
            }

            2 -> {
                imageX = imageWidth[0]
                imageY = imageHeight[0]
            }

            3 -> {
                imageX = imageWidth[0]
                imageY = imageHeight[0]
            }

            4 -> {
                imageX = imageWidth[0]
                imageY = imageHeight[0]
            }

            else -> {
                imageX = 0
                imageY = 0
            }
        }

        RenderUtils.drawmage(ResourceLocation("fdpclient/girls/$girl.png"), width - imageX / 3, height - imageY / 3, imageX / 3, imageY / 3, 0.5f)
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)



        if (genshinImpactAnim.get()) RenderUtils.drawImage(
                LiquidBounce.venti,
                defaultWidth1.toInt() - (0.6 * defaultWidth1).toInt(),
                defaultHeight1.toInt() - (0.3 * defaultWidth1).toInt(),
                (0.6 * defaultWidth1).toInt(),
                (0.3 * defaultWidth1).toInt()
        )
        FontLoaders.F40.drawCenteredString(
                LiquidBounce.CLIENT_NAME,
                this.width.toDouble() / 2,
                this.height.toDouble() / 2 - 60,
                Color(255, 255, 255, 200).rgb
        )

        FontLoaders.F16.drawString(
                "Made by UnlegitMinecraft",
                10f,
                this.height - 15f,
                Color(255, 255, 255, 170).rgb
        )
        FontLoaders.F16.drawString(
                "FDPClientChina",
                10f,
                this.height - 25f,
                Color(255, 255, 255, 170).rgb
        )
        var versionMsg =
                "Version: " + LiquidBounce.CLIENT_VERSION + if (LiquidBounce.VERSIONTYPE.contains("Release")) " | Release" else " | " + LiquidBounce.VERSIONTYPE
        FontLoaders.F16.drawString(
                versionMsg,
                this.width - FontLoaders.F16.getStringWidth(versionMsg) - 10F,
                this.height - 15f,
                Color(255, 255, 255, 170).rgb
        )
        try {
            if (!displayed) {
                var back = Layer.draw(
                        defaultWidth.toInt(),
                        defaultHeight1.toInt(),
                        LiquidBounce.CLIENTTEXT.split("$")[2].toFloat(),
                        LiquidBounce.CLIENTTEXT.split("$")[3].toFloat(),
                        LiquidBounce.CLIENTTEXT.split("$")[0],
                        LiquidBounce.CLIENTTEXT.split("$")[1].replace("%VERSION%", LiquidBounce.CLIENT_VERSION),
                        255,
                        mouseX,
                        mouseY,
                        clicked
                )
                if (back == 1) {
                    drawed = false;
                    buttonList.removeAll(buttonList)
                } else if (back == 2) {
                    displayed = true
                    drawBtns()
                }
                if (drawed && back != 1) {
                    //drawBtns()
                }
                clicked = false;
            } else {
                if (!drawed) {
                    drawBtns()
                }
            }
        } catch (e: Exception) {
            //e.printStackTrace()
        }
        super.drawScreen(mouseX, mouseY, partialTicks)
    }


    override fun mouseClicked(p_mouseClicked_1_: Int, i2: Int, i3: Int) {
        clicked = true;
        super.mouseClicked(p_mouseClicked_1_, i2, i3)
    }

    override fun actionPerformed(button: GuiButton) {
        when (button.id) {
            0 -> mc.displayGuiScreen(GuiOptions(this, mc.gameSettings))
            1 -> mc.displayGuiScreen(GuiSelectWorld(this))
            2 -> mc.displayGuiScreen(GuiMultiplayer(this))
            4 -> mc.shutdown()
            100 -> mc.displayGuiScreen(GuiAltManager(this))
            102 -> MiscUtils.showURL("https://github.com/UnlegitMinecraft/FDPClientChina/releases")
            103 -> mc.displayGuiScreen(GuiModList(this))
            104 -> mc.displayGuiScreen(GuiBackground(this))
            514 -> MiscUtils.showURL("https://${LiquidBounce.CLIENT_WEBSITE}/discord.html")
            114 -> MiscUtils.showURL("https://${LiquidBounce.CLIENT_WEBSITE}")
            191 -> LiquidBounce.Darkmode = !LiquidBounce.Darkmode
        }
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {}

    companion object {
        private val RANDOM = Random()
    }
}



