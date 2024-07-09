/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/UnlegitMinecraft/FDPClientChina/
 */
package net.ccbluex.liquidbounce.utils;

import net.ccbluex.liquidbounce.FDPClientChina;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.entity.Entity;

public final class ServerUtils extends MinecraftInstance {

    public static ServerData serverData;

    public static void connectToLastServer() {
        if(serverData == null)
            return;

        mc.displayGuiScreen(new GuiConnecting(new GuiMultiplayer(FDPClientChina.mainMenu), mc, serverData));
    }

    public static String getRemoteIp() {
        String serverIp = "Idling";

        if (mc.isIntegratedServerRunning()) {
            serverIp = "SinglePlayer";
        } else if (mc.theWorld != null && mc.theWorld.isRemote) {
            final ServerData serverData = mc.getCurrentServerData();
            if(serverData != null)
                serverIp = serverData.serverIP;
        }

        return serverIp;
    }

    public static boolean isHypixelLobby() {
        if (mc.theWorld == null) return false;

        String target = "CLICK TO PLAY";
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity.getName().startsWith("§e§l")) {
                if (entity.getName().equals("§e§l" + target)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isHypixelDomain(String s1) {
        int chars = 0;
        String str = "www.hypixel.net";

        for (char c : str.toCharArray()) {
            if (s1.contains(String.valueOf(c))) chars++;
        }

        return chars == str.length();
    }
}