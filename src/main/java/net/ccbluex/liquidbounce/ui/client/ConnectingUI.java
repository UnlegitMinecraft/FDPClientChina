package net.ccbluex.liquidbounce.ui.client;

import net.ccbluex.liquidbounce.utils.ServerUtils;
import net.ccbluex.liquidbounce.utils.extensions.RendererExtensionKt;
import net.ccbluex.liquidbounce.utils.render.BlurUtils;
import net.ccbluex.liquidbounce.utils.render.NewRenderUtils;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.I18n;

import java.awt.*;
import java.util.List;

public class ConnectingUI {
    public static void initButton(List<GuiButton> buttonList){
        Minecraft mc=Minecraft.getMinecraft();
        ScaledResolution scaledResolution=new ScaledResolution(mc);
        double width=scaledResolution.getScaledWidth();
        double height=scaledResolution.getScaledHeight();
        buttonList.add(new GuiButton(0, (int) (width / 2 - 120), (int) (height / 2 + 30),240,25, I18n.format("gui.cancel", new Object[0])));
    }
    public static void draw(double width,double height){
        Minecraft mc=Minecraft.getMinecraft();
        ScaledResolution scaledResolution=new ScaledResolution(mc);
        width=scaledResolution.getScaledWidth();
        height=scaledResolution.getScaledHeight();
        NewRenderUtils.drawShadowWithCustomAlpha((float) ((width / 2) - 120f), (float) ((height / 2) - 50f), 240f,105f,255);
        if(!GuiSelectPerformance.offblur) BlurUtils.INSTANCE.draw((float) ((width / 2) - 120f), (float) ((height / 2) - 50f), 240f,105f,15f);
        Gui.drawRect((int) ((width / 2) - 120), (int) ((height / 2) + 55), (int) ((width / 2) + 120), (int) ((height / 2) - 50), new Color(0, 0, 0, 130).getRGB());
        //Gui.drawRect((int) ((width / 2)-160), (int) ((height / 2)-80), (int) ((width / 2)+160), (int) ((height / 2)-50),new Color(0,0,0,120).getRGB());
        RenderUtils.drawLoadingCircle((float) (width / 2), (float) (height / 2)-20);

        String ip = "Unknown";

        final ServerData serverData = mc.getCurrentServerData();
        if(serverData != null)
            ip = serverData.serverIP;
        if(ServerUtils.serverData != null)
            ip = ServerUtils.serverData.serverIP;
        String step;
        try {
            if (mc.getNetHandler() == null) {
                step = I18n.format("connect.connecting", new Object[0]);
            } else {
                step = I18n.format("connect.authorizing", new Object[0]);
            }
        }catch (Exception e){
            step="正在连接服务器...";
        }
        RendererExtensionKt.drawCenteredString(mc.fontRendererObj, "Connecting to", (float) (width / 2), (float) (height / 2 + 8), 0xFFFFFF, false);
        //FontLoaders.F18.drawString("FDPCN Client", (float) (width / 2)-FontLoaders.F18.getStringWidth("FDPCN Client"), (float) (height / 2 - 70), 0xFFFFFF);
        RendererExtensionKt.drawCenteredString(mc.fontRendererObj,ip, (float) (width / 2), (float) (height / 2 + 18), 0x5281FB,false);


    }
}
