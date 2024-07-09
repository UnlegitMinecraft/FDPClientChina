package net.ccbluex.liquidbounce.ui.client.hud;

import cn.hanabi.gui.cloudmusic.ui.MusicOverlayRenderer;
import net.ccbluex.liquidbounce.FDPClientChina;
import net.ccbluex.liquidbounce.features.module.modules.client.HUD;
import net.ccbluex.liquidbounce.font.FontLoaders;
import net.ccbluex.liquidbounce.ui.client.GuiSelectPerformance;
import net.ccbluex.liquidbounce.utils.render.BlurUtils;
import net.ccbluex.liquidbounce.utils.render.ColorManager;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class Hotbar {
    public static void render(ScaledResolution sr, int itemX, float partialTicks) {
        GL11.glPushMatrix();
        if (HUD.INSTANCE.getHotbarRect().get())
            RenderUtils.drawRect(0, sr.getScaledHeight() - 23, sr.getScaledWidth(), sr.getScaledHeight(), new Color(0, 0, 0, 100));
        if (HUD.INSTANCE.getHotbarRect().get())
            RenderUtils.drawRect(itemX, sr.getScaledHeight() - 23, itemX + 22, sr.getScaledHeight(), new Color(0, 0, 0, 100));
        GL11.glPopMatrix();
        if (HUD.INSTANCE.getHotbarBlur().get() && !GuiSelectPerformance.offblur) BlurUtils.INSTANCE.draw(
                0f,
                (sr.getScaledHeight() - 23),
                sr.getScaledWidth(),
                sr.getScaledHeight(),
                10f
        );

        RenderUtils.drawRect(itemX, sr.getScaledHeight() - 23, itemX + 22, sr.getScaledHeight() - 21, new Color(0, 165, 255));
        RenderUtils.drawRect(0, sr.getScaledHeight() - 23, 2, sr.getScaledHeight(), ColorManager.astolfoRainbow(0, 0, 0));
        FontLoaders.C16.DisplayFonts(FontLoaders.C14, FDPClientChina.CLIENT_NAME + " " + FDPClientChina.CLIENT_VERSION, 7, sr.getScaledHeight() - 18, new Color(255, 255, 255).getRGB());

        FontLoaders.C16.DisplayFonts(FontLoaders.C14, "Language：" + Minecraft.getMinecraft().gameSettings.language + " | " + FDPClientChina.INSTANCE.getRENDERLEAVE() + " PERFORMANCE", 7, sr.getScaledHeight() - 10, new Color(255, 255, 255).getRGB());
        if (HUD.INSTANCE.getMusicDisplay().get()) MusicOverlayRenderer.INSTANCE.renderOverlay();
    }

    public static void drawGuiBackground(double s) {
        if (s <= 0.1 || s > 1.0) return;
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        GL11.glPushMatrix();
        if (!HUD.INSTANCE.getBlurValue().get() && !GuiSelectPerformance.offblur)
            BlurUtils.INSTANCE.draw(0, 0, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(), (float) (70.0 * s));
        RenderUtils.drawGradientSidewaysV(-4 * scaledResolution.getScaledHeight(), scaledResolution.getScaledHeight() / 2, scaledResolution.getScaledWidth() * 4, scaledResolution.getScaledHeight() + 150 + (2 * scaledResolution.getScaledHeight() * (1 - s)), new Color(0, 0, 0, 0).getRGB(), new Color(0, 165, 255, (int) (255 * s)).getRGB());
        FontLoaders.F24.drawCenteredString(FDPClientChina.CLIENT_NAME + " CHINA", scaledResolution.getScaledWidth() / 2, scaledResolution.getScaledHeight() - 40.0, new Color(255, 255, 255, (int) (255 * s)).getRGB());
        GL11.glPopMatrix();
    }
}
