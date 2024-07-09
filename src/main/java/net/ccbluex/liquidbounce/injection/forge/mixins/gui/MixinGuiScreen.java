/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/UnlegitMinecraft/FDPClientChina/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.gui;

import net.ccbluex.liquidbounce.FDPClientChina;
import net.ccbluex.liquidbounce.features.module.modules.client.HUD;
import net.ccbluex.liquidbounce.features.special.GradientBackground;
import net.ccbluex.liquidbounce.font.FontLoaders;
import net.ccbluex.liquidbounce.ui.client.GuiBackground;
import net.ccbluex.liquidbounce.ui.client.GuiSelectPerformance;
import net.ccbluex.liquidbounce.utils.render.BlurUtils;
import net.ccbluex.liquidbounce.utils.render.ParticleUtils;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.Collections;
import java.util.List;

@Mixin(GuiScreen.class)
public abstract class MixinGuiScreen {

    @Shadow
    public Minecraft mc;
    @Shadow
    public int width;
    @Shadow
    public int height;
    @Shadow
    protected List<GuiButton> buttonList;
    @Shadow
    protected FontRenderer fontRendererObj;


    @Shadow
    public abstract void updateScreen();

    @Shadow
    public abstract void handleComponentHover(IChatComponent component, int x, int y);

    @Shadow
    protected abstract void drawHoveringText(List<String> textLines, int x, int y);

    @Shadow
    protected abstract void actionPerformed(GuiButton p_actionPerformed_1_);

    @Inject(method = "drawWorldBackground", at = @At("HEAD"), cancellable = true)
    private void drawWorldBackground(final CallbackInfo callbackInfo) {
        try {
            final HUD hud = FDPClientChina.moduleManager.getModule(HUD.class);
            if (hud.getInventoryParticle().get() && mc.thePlayer != null) {
                ParticleUtils.drawParticles(Mouse.getX() * width / mc.displayWidth, height - Mouse.getY() * height / mc.displayHeight - 1);
            }

            if (mc.thePlayer != null) {
                int defaultHeight1 = (this.height);
                int defaultWidth1 = (this.width);
                GL11.glPushMatrix();
                if (HUD.INSTANCE.getGenshinImpactAnim().get())
                    RenderUtils.drawImage(FDPClientChina.INSTANCE.getLumine(), 10, defaultHeight1 - (int) (0.2 * defaultWidth1), (int) (0.2 * defaultWidth1), (int) (0.2 * defaultWidth1));
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                FontLoaders.F30.DisplayFont2(FontLoaders.F30, FDPClientChina.CLIENT_NAME, defaultWidth1 - 12f - FontLoaders.F14.DisplayFontWidths(FontLoaders.F14, FDPClientChina.CLIENT_VERSION) - FontLoaders.F30.DisplayFontWidths(FontLoaders.F30, FDPClientChina.CLIENT_NAME), defaultHeight1 - 23.5f, new Color(255, 255, 255, 140).getRGB(), true);
                FontLoaders.F30.DisplayFont2(FontLoaders.F14, FDPClientChina.CLIENT_VERSION, defaultWidth1 - 10f - FontLoaders.F14.DisplayFontWidths(FontLoaders.F14, FDPClientChina.CLIENT_VERSION), defaultHeight1 - 15f, new Color(255, 255, 255, 140).getRGB(), true);
                GL11.glPopMatrix();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Inject(method = "drawWorldBackground", at = @At("HEAD"), cancellable = true)
    private void drawWorldBackground2(final CallbackInfo callbackInfo) {
        try {
            if (mc.thePlayer != null) {
                callbackInfo.cancel();
                if (HUD.INSTANCE.getBlurValue().get() && !GuiSelectPerformance.offblur)
                    BlurUtils.INSTANCE.draw(0, 0, this.width, this.height, 50);
                int defaultHeight1 = (this.height);
                int defaultWidth1 = (this.width);
                GL11.glPushMatrix();
                if (HUD.INSTANCE.getGenshinImpactAnim().get())
                    RenderUtils.drawImage(FDPClientChina.INSTANCE.getLumine(), 10, defaultHeight1 - (int) (0.2 * defaultWidth1), (int) (0.2 * defaultWidth1), (int) (0.2 * defaultWidth1));
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                FontLoaders.F30.DisplayFont2(FontLoaders.F30, FDPClientChina.CLIENT_NAME, defaultWidth1 - 12f - FontLoaders.F14.DisplayFontWidths(FontLoaders.F14, FDPClientChina.CLIENT_VERSION) - FontLoaders.F30.DisplayFontWidths(FontLoaders.F30, FDPClientChina.CLIENT_NAME), defaultHeight1 - 23.5f, new Color(255, 255, 255, 140).getRGB(), true);
                FontLoaders.F30.DisplayFont2(FontLoaders.F14, FDPClientChina.CLIENT_VERSION, defaultWidth1 - 10f - FontLoaders.F14.DisplayFontWidths(FontLoaders.F14, FDPClientChina.CLIENT_VERSION), defaultHeight1 - 15f, new Color(255, 255, 255, 140).getRGB(), true);
                GL11.glPopMatrix();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
/*
    @Inject(method = "displayCrashReport", at = @At("HEAD"))
    private void displayCrashReport(CrashReport crashReport, CallbackInfo ci) {
        if (!WindowUtils.isWindows()) return;
        try {
            File file = new File("./", "FDPCrashLogs.txt");
            if (!file.exists()) {
                file.createNewFile();
            } else {
                file.delete();
                file.createNewFile();
            }
            FileWriter fileWritter = new FileWriter(file.getName(), true);
            fileWritter.write("##########################FDP CRASH REPORT##########################\r\n\r\n" +
                    "If this problem persists, please send this file to the FDPClient developers! Website (where you can join the discord server): http://FDPClient.Club/\r\nThis file will be saved in \".minecraft/FDPCrashLogs.txt\"" +
                    "\r\n\r\n" +
                    " | 在没有错误日志的情况下诊断任何问题无异于闭眼开车!  --Apache官方文档\r\n" +
                    " | Troubleshooting any problem without the error log is like driving with your eyes closed.\r\n" +
                    " | From Apache official documentation Getting Started chapter\r\n" +
                    "   - INFO:\r\n" +
                    "   |   Version: " + LiquidBounce.CLIENT_VERSION + "\r\n" +
                    "   |   Time: " + System.currentTimeMillis() + "\r\n" +
                    "   |   OS: " + Util.getOSType() + "\r\n" +
                    "\r\n##########################FDP CRASH REPORT##########################\r\n" + crashReport.getCompleteReport());
            fileWritter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File file1 = new File("./", "FDPCrashLogs.txt");
        ;
        String s = file1.getAbsolutePath();

        if (Util.getOSType() == Util.EnumOS.OSX) {
            try {
                Runtime.getRuntime().exec(new String[]{"/usr/bin/open", s});
                return;
            } catch (IOException ioexception1) {
                ioexception1.printStackTrace();
            }
        } else if (Util.getOSType() == Util.EnumOS.WINDOWS) {
            String s1 = String.format("cmd.exe /C start \"Open file\" \"%s\"", new Object[]{s});

            try {
                Runtime.getRuntime().exec(s1);
                return;
            } catch (IOException ioexception) {
                ioexception.printStackTrace();
            }
        }

        boolean flag = false;

        try {
            Class<?> oclass = Class.forName("java.awt.Desktop");
            Object object = oclass.getMethod("getDesktop", new Class[0]).invoke((Object) null, new Object[0]);
            oclass.getMethod("browse", new Class[]{URI.class}).invoke(object, new Object[]{file1.toURI()});
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            flag = true;
        }

        if (flag) {
            Sys.openURL("file://" + s);
        }
    }*/

    @Inject(method = "initGui", at = @At("RETURN"))
    private void initGui(CallbackInfo callbackInfo) {
        /*try {
            Field fastRender = GameSettings.class.getDeclaredField("ofFastRender");
            if(fastRender.getBoolean((Object) Minecraft.getMinecraft().gameSettings)){
                try {
                    Field fastRenderField = null;
                    try {
                        final Field declaredField = GameSettings.class.getDeclaredField("ofFastRender");

                        if (!declaredField.isAccessible()) {
                            declaredField.setAccessible(true);
                        }

                        fastRenderField = declaredField;
                    } catch (NoSuchFieldException e) {
                    }
                    try {
                        if (fastRenderField != null) {
                            if (!fastRenderField.isAccessible()) {
                                fastRenderField.setAccessible(true);
                            }

                            fastRenderField.setBoolean(Minecraft.getMinecraft().gameSettings, false);
                        }
                    } catch (IllegalAccessException ignored) {
                    }
                    NotiUtils.ShowSystemNotification("FDPCNClient", "FastRender快速渲染与FDPCNClient不兼容，已关闭!", TrayIcon.MessageType.WARNING, 5000L);
                } catch (Exception e) {
                    try {
                        NotiUtils.ShowSystemNotification("FDPCNClient", "FDPCN Unsupported FastRender", TrayIcon.MessageType.WARNING, 5000L);
                    } catch (Exception es) {
                        es.printStackTrace();
                    }
                }
            }
        } catch (Exception var1) {
            var1.printStackTrace();
        }*/
    }

    @ModifyVariable(method = "sendChatMessage(Ljava/lang/String;)V", at = @At("HEAD"))
    private String sendChatMessage(String p_sendChatMessage_1_) {
        if (p_sendChatMessage_1_.length() > 100) {
            return p_sendChatMessage_1_.substring(0, 100);
        }
        return p_sendChatMessage_1_;
    }

    @Inject(method = "drawDefaultBackground", at = @At("HEAD"), cancellable = true)
    private void drawDefaultBackground(final CallbackInfo callbackInfo) {
        if (mc.currentScreen instanceof GuiContainer) {
            callbackInfo.cancel();
        }
    }

    /**
     * @author CCBlueX
     */
    @Inject(method = "drawBackground", at = @At("RETURN"), cancellable = true)
    private void drawClientBackground(final CallbackInfo callbackInfo) {
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        if (GuiBackground.Companion.getEnabled()) {
            if (FDPClientChina.INSTANCE.getBackground() == null) {
                GradientBackground.INSTANCE.draw(width, height);
            } else {
                mc.getTextureManager().bindTexture(FDPClientChina.INSTANCE.getBackground());
                Gui.drawModalRectWithCustomSizedTexture(0, 0, 0f, 0f, width, height, width, height);
            }
            if (GuiBackground.Companion.getBlur() && !GuiSelectPerformance.offblur) {
                BlurUtils.INSTANCE.draw(0, 0, mc.displayWidth, mc.displayHeight, 50);
            }
            GlStateManager.resetColor();
            if (GuiBackground.Companion.getParticles())
                ParticleUtils.drawParticles(Mouse.getX() * width / mc.displayWidth, height - Mouse.getY() * height / mc.displayHeight - 1);

        }
    }

    @Inject(method = "drawBackground", at = @At("RETURN"))
    private void drawParticles(final CallbackInfo callbackInfo) {
        if (GuiBackground.Companion.getParticles())
            ParticleUtils.drawParticles(Mouse.getX() * width / mc.displayWidth, height - Mouse.getY() * height / mc.displayHeight - 1);
    }

    @Inject(method = "handleComponentHover", at = @At("HEAD"))
    private void handleHoverOverComponent(IChatComponent component, int x, int y, final CallbackInfo callbackInfo) {
        if (component == null || component.getChatStyle().getChatClickEvent() == null)
            return;

        final ChatStyle chatStyle = component.getChatStyle();

        final ClickEvent clickEvent = chatStyle.getChatClickEvent();
        final HoverEvent hoverEvent = chatStyle.getChatHoverEvent();

        drawHoveringText(Collections.singletonList("§c§l" + clickEvent.getAction().getCanonicalName().toUpperCase() + ": §a" + clickEvent.getValue()), x, y - (hoverEvent != null ? 17 : 0));
    }

    @Inject(method = "drawHoveringText*", at = @At("HEAD"))
    private void drawHoveringText(CallbackInfo ci) {
        // idk why this make font renderer works
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enableRescaleNormal();
    }
}