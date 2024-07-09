package net.ccbluex.liquidbounce.ui.client;

import net.ccbluex.liquidbounce.FDPClientChina;
import net.ccbluex.liquidbounce.font.FontLoaders;
import net.ccbluex.liquidbounce.injection.access.StaticStorage;
import net.ccbluex.liquidbounce.launch.data.legacyui.GuiMainMenu;
import net.ccbluex.liquidbounce.ui.RenderLeave;
import net.ccbluex.liquidbounce.utils.render.BlurUtils;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.utils.timer.MSTimer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;

public class GuiSelectPerformance extends GuiScreen {
    public MSTimer msTimer = new MSTimer();
    public MSTimer click = new MSTimer();
    public int α = 255;
    public static boolean offblur=false;

    @Override
    public void initGui() {
        try {
            if (System.getProperty("blurdisable").contains("true")) {
                offblur = true;
            }
        }catch (Exception e){
            offblur=false;
        }
        StaticStorage.scaledResolution = new ScaledResolution(mc);
        if (FDPClientChina.INSTANCE.getRENDERLEAVESELECTED() == 1) {
            mc.displayGuiScreen(new GuiMainMenu());
        }
        α = 255;
        click.reset();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if(α>=1000) return;
        if (!click.hasTimePassed(200)) super.mouseClicked(mouseX, mouseY, mouseButton);
        else {
            int h = (StaticStorage.scaledResolution.getScaledHeight() / 5);
            if (mouseY >= (1.5 * h) && mouseY <= (2.5 * h)) {
                FDPClientChina.INSTANCE.setRENDERLEAVE(RenderLeave.HIGH);
                α = 1255;
            }
            if (mouseY >= (2.5 * h) && mouseY <= (3.5 * h)) {
                FDPClientChina.INSTANCE.setRENDERLEAVE(RenderLeave.NORMAL);
                α = 1255;
            }
            if (mouseY >= (3.5 * h) && mouseY <= (4.5 * h)) {
                FDPClientChina.INSTANCE.setRENDERLEAVE(RenderLeave.LOW);
                α = 1255;
            }
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (α < 1000) {
            if (α >= 0 && msTimer.hasTimePassed(1)) {
                α = α - 1;
                click.reset();
                msTimer.reset();
            }
            double bfb = (255.0 - α) / 255.0;
            GL11.glPushMatrix();
            int i = 0;
            if ((255 * bfb) < 255) {
                click.reset();
                i = (int) (255 * bfb);
            } else {
                i = 255;
            }
            if (1 != 255)
                RenderUtils.drawImage(new ResourceLocation("fdpclient/misc/splash.png"), 0, 0, StaticStorage.scaledResolution.getScaledWidth(), StaticStorage.scaledResolution.getScaledHeight());
            if (1 != 255 && !offblur)
                BlurUtils.INSTANCE.draw((float) 0, (float) 0, (float) StaticStorage.scaledResolution.getScaledWidth(), (float) StaticStorage.scaledResolution.getScaledHeight(), (float) (50.0f * bfb));
            GL11.glPopMatrix();
            RenderUtils.drawRect(0, 0, StaticStorage.scaledResolution.getScaledWidth(), StaticStorage.scaledResolution.getScaledHeight(), new Color(81, 174, 204, i));
            RenderUtils.drawGradientSidewaysH(-100, 0, StaticStorage.scaledResolution.getScaledWidth(), StaticStorage.scaledResolution.getScaledHeight(), new Color(0, 165, 255, i).getRGB(), new Color(0, 165, 255, 0).getRGB());
            FontLoaders.F50.DisplayFonts(FontLoaders.F40, "Select Your Performance Level", 30, 30, new Color(255, 255, 255, i).getRGB());

            int h = (StaticStorage.scaledResolution.getScaledHeight() / 5);
            int b = i;
            if (i >= 200)
                b = i - 55;
            FontLoaders.F20.DisplayFonts(FontLoaders.F20, "High | 高性能-完整的视觉(推荐)", 30, (2 * h), new Color(255, 255, 255, b + ((mouseY >= (1.5 * h) && mouseY <= (2.5 * h)) ? 0 : 55)).getRGB());
            FontLoaders.F20.DisplayFonts(FontLoaders.F20, "Normal | 中性能-将会禁用模糊(Tips: ESP中的Jello,OUTLINE,GLOW模式会大幅度降低帧率)", 30, (3 * h), new Color(255, 255, 255, b + ((mouseY >= (2.5 * h) && mouseY <= (3.5 * h)) ? 0 : 55)).getRGB());
            FontLoaders.F20.DisplayFonts(FontLoaders.F20, "Low | 低性能-将会禁用模糊和阴影并停止使用部分视觉(禁用模糊状态下可能会出现离奇的渲染错误)", 30, (4 * h), new Color(255, 255, 255, b + ((mouseY >= (3.5 * h) && mouseY <= (4.5 * h)) ? 0 : 55)).getRGB());
        } else {
            if (α > 1000 && msTimer.hasTimePassed(1)) {
                α = α - 1;
                msTimer.reset();
            } else {
                if (α == 1000) {
                    if (FDPClientChina.INSTANCE.getRENDERLEAVESELECTED() == 3) {
                        mc.displayGuiScreen(null);
                        FDPClientChina.INSTANCE.setRENDERLEAVESELECTED(1);
                    } else {
                        FDPClientChina.INSTANCE.setRENDERLEAVESELECTED(1);
                        mc.displayGuiScreen(new GuiMainMenu());
                    }
                }
            }
            double bfb = (255.0 - (α - 1000)) / 255.0;
            int i = 0;
            if ((255 * bfb) < 255) {
                i = (int) (255 * bfb);
            } else {
                i = 255;
            }
            int h = (StaticStorage.scaledResolution.getScaledHeight() / 5);
            i = 255 - i;
            drawBackground(0);
            RenderUtils.drawRect(0, 0, StaticStorage.scaledResolution.getScaledWidth(), StaticStorage.scaledResolution.getScaledHeight(), new Color(81, 174, 204, i));
            RenderUtils.drawGradientSidewaysH(-100, 0, StaticStorage.scaledResolution.getScaledWidth(), StaticStorage.scaledResolution.getScaledHeight(), new Color(0, 165, 255, i).getRGB(), new Color(0, 165, 255, 0).getRGB());
            FontLoaders.F50.DisplayFonts(FontLoaders.F40, "Select Your Performance Level", 30, 30, new Color(255, 255, 255, i).getRGB());
            FontLoaders.F20.DisplayFonts(FontLoaders.F20, "High | 高性能-完整的视觉(推荐)", 30, (2 * h), new Color(255, 255, 255, i).getRGB());
            FontLoaders.F20.DisplayFonts(FontLoaders.F20, "Normal | 中性能-将会禁用模糊(Tips: ESP中的Jello,OUTLINE,GLOW模式会大幅度降低帧率)", 30, (3 * h), new Color(255, 255, 255, i).getRGB());
            FontLoaders.F20.DisplayFonts(FontLoaders.F20, "Low | 低性能-将会禁用模糊和阴影并停止使用部分视觉(禁用模糊状态下可能会出现离奇的渲染错误)", 30, (4 * h), new Color(255, 255, 255, i).getRGB());


        }
    }
}
