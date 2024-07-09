package com.guimc.fuckpcl.utils;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.win32.StdCallLibrary;

public class WindowUtils {
    public interface User32 extends StdCallLibrary {
        User32 INSTANCE = (User32) Native.loadLibrary("user32", User32.class);
        boolean EnumWindows(WinUser.WNDENUMPROC lpEnumFunc, Pointer arg);
        int GetWindowTextA(HWND hWnd, byte[] lpString, int nMaxCount);
    }

    public static boolean isWindows() {
        String OS = System.getProperty("os.name").toLowerCase();
        return OS.contains("windows");
    }

    public static Boolean findWindow(String Text) {
        final User32 user32 = User32.INSTANCE;
        final Boolean[] windowFunded = new Boolean[1];

        windowFunded[0] = false;

        user32.EnumWindows((hwnd, arg1) -> {
            byte[] windowText = new byte[512];
            user32.GetWindowTextA(hwnd, windowText, 512);
            String wText = Native.toString(windowText);
            if(wText.contains(Text)) {
                windowFunded[0] = true;
            }
            return true;
        }, null);

        return windowFunded[0];
    }
}