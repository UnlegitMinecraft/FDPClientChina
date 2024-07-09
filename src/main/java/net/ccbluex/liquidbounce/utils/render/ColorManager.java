package net.ccbluex.liquidbounce.utils.render;

import net.minecraft.client.Minecraft;

import java.awt.*;

public class ColorManager {
    public static int getRainbow(int speed, int offset) {
        float hue = (System.currentTimeMillis() + (long)offset) % (long)speed;
        return Color.getHSBColor((hue /= (float)speed), 0.8f, 1.0f).getRGB();
    }

    public static int rainbow(int delay) {
        double rainbowState = Math.ceil((double) (System.currentTimeMillis() + (long) delay) / 20.0D);
        rainbowState %= 360.0D;
        return Color.getHSBColor((float) (rainbowState / 360.0D), 0.8F, 0.7F).brighter().getRGB();
    }


    public static Color getChromaRainbow(double x, double y) {
        float v = 2000.0f;
        return new Color(Color.HSBtoRGB(((float)(((double)System.currentTimeMillis() - x * 10.0 * 1 - y * 10.0 * 1) % (double)v) / v), 0.8f, 0.8f));
    }

    public static int getRainbow2(int speed, int offset) {
        float hue = (System.currentTimeMillis() + (long)offset) % (long)speed;
        return Color.getHSBColor(hue /= (float)speed, 0.8f, 0.8f).getRGB();
    }

    public static int fluxRainbow(int delay, long timeOffset,float sa) {
        double rainbowState = Math.ceil((System.currentTimeMillis() + timeOffset) / 8 + delay / 20.0D);
        rainbowState %= 360.0;
        return Color.getHSBColor((float) (rainbowState / 360f), sa, 1).getRGB();
    }

    public static int rainbowTick=0;
    public static int astolfoRainbow(int delay, int offset, int index) {
        //if (++rainbowTick > 50) {
        //    rainbowTick = 0;
        //}
        Color rainbow = new Color(Color.HSBtoRGB(
                (float) ((double) Minecraft.getMinecraft().thePlayer.ticksExisted / 50.0 + Math.sin((double) rainbowTick / 50.0 * 1.6))
                        % 1.0f,
                0.5f, 1.0f));
        return rainbow.getRGB();
    }


    public static Color rainbow(long time, float count, float fade) {
        float hue = ((float)time + (1.0f + count) * 2.0E8f) / 1.0E10f % 1.0f;
        long color = Long.parseLong(Integer.toHexString(Color.HSBtoRGB(hue, 1.0f, 1.0f)), 16);
        Color c = new Color((int)color);
        return new Color((float)c.getRed() / 255.0f * fade, (float)c.getGreen() / 255.0f * fade, (float)c.getBlue() / 255.0f * fade, (float)c.getAlpha() / 255.0f);
    }
}
