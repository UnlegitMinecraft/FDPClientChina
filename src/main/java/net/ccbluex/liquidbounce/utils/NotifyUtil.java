package net.ccbluex.liquidbounce.utils;

import net.ccbluex.liquidbounce.FDPClientChina;

import javax.imageio.ImageIO;
import java.awt.*;
import java.util.Objects;

public class NotifyUtil {

    public static void start() {
        final boolean windows = System.getProperties().getProperty("os.name").toLowerCase().contains("windows");
        TrayIcon trayIcon = null;
        if (windows) {
            if (SystemTray.isSupported()) {
                try {
                    trayIcon = new TrayIcon(ImageIO.read(Objects.requireNonNull(NotifyUtil.class.getResourceAsStream("/assets/minecraft/fdpclient/misc/icon.png"))));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                trayIcon.setImageAutoSize(true);
                trayIcon.setToolTip(FDPClientChina.CLIENT_NAME + " " + FDPClientChina.CLIENT_VERSION);
                try {
                    SystemTray.getSystemTray().add(trayIcon);
                } catch (AWTException var7) {
                    ClientUtils.INSTANCE.logError("Unable to add tray icon.");
                }
                trayIcon.displayMessage(FDPClientChina.CLIENT_NAME, "Thank you for using " + FDPClientChina.CLIENT_NAME, TrayIcon.MessageType.NONE);
            }
        }
    }

}
