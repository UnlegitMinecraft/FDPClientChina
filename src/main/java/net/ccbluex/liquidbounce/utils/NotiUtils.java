package net.ccbluex.liquidbounce.utils;

import com.guimc.fuckpcl.utils.WindowUtils;

import java.awt.*;

public class NotiUtils {
    public static void ShowSystemNotification(String title, String text, TrayIcon.MessageType type, Long delay)
            throws AWTException {
        if(!WindowUtils.isWindows()) return;
        if (SystemTray.isSupported()) {
            TrayIcon trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().createImage("icon.png"), title);
            trayIcon.setImageAutoSize(true);
            SystemTray.getSystemTray().add(trayIcon);
            trayIcon.displayMessage(title, text, type);
            new Thread(() -> {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                SystemTray.getSystemTray().remove(trayIcon);
            }).start();
        }
    }
}
