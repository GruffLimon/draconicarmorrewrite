package com.draconicarmorrewrite.client.gui;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Properties;
import net.minecraftforge.fml.loading.FMLPaths;

public final class HudSettings {
    private static final Path FILE = FMLPaths.CONFIGDIR.get().resolve("draconicarmorrewrite_hud.properties");
    private static final String SETTINGS_VERSION = "4";
    private static boolean loaded;

    public static int shieldX;
    public static int shieldY;
    public static int shieldScale;
    public static int shieldFadeMode;
    public static boolean shieldRotated;
    public static boolean shieldNumeric;
    public static boolean shieldHidden;

    public static int toolX;
    public static int toolY;
    public static int toolScale;
    public static int toolFadeMode;
    public static boolean toolHidden;

    private HudSettings() {
    }

    public static void load() {
        if (loaded) {
            return;
        }
        loaded = true;
        
        // Defaults
        shieldX = -1;
        shieldY = -1;
        shieldScale = 100;
        shieldFadeMode = 0;
        shieldRotated = false;
        shieldNumeric = true;
        shieldHidden = false;

        toolX = -1;
        toolY = -1;
        toolScale = 100;
        toolFadeMode = 0;
        toolHidden = false;

        if (!Files.exists(FILE, new LinkOption[0])) {
            return;
        }
        Properties props = new Properties();
        try (InputStream stream = Files.newInputStream(FILE, new OpenOption[0])) {
            props.load(stream);
            shieldX = readInt(props, "shieldX", shieldX);
            shieldY = readInt(props, "shieldY", shieldY);
            shieldScale = clamp(readInt(props, "shieldScale", shieldScale), 30, 300);
            shieldFadeMode = clamp(readInt(props, "shieldFadeMode", shieldFadeMode), 0, 4);
            shieldRotated = Boolean.parseBoolean(props.getProperty("shieldRotated", Boolean.toString(shieldRotated)));
            shieldNumeric = Boolean.parseBoolean(props.getProperty("shieldNumeric", Boolean.toString(shieldNumeric)));
            shieldHidden = Boolean.parseBoolean(props.getProperty("shieldHidden", Boolean.toString(shieldHidden)));

            toolX = readInt(props, "toolX", toolX);
            toolY = readInt(props, "toolY", toolY);
            toolScale = clamp(readInt(props, "toolScale", toolScale), 30, 300);
            toolFadeMode = clamp(readInt(props, "toolFadeMode", toolFadeMode), 0, 4);
            toolHidden = Boolean.parseBoolean(props.getProperty("toolHidden", Boolean.toString(toolHidden)));
        } catch (IOException e) {
            // ignore
        }
    }

    public static void save() {
        loaded = true;
        Properties props = new Properties();
        props.setProperty("settingsVersion", SETTINGS_VERSION);
        props.setProperty("shieldX", Integer.toString(shieldX));
        props.setProperty("shieldY", Integer.toString(shieldY));
        props.setProperty("shieldScale", Integer.toString(shieldScale));
        props.setProperty("shieldFadeMode", Integer.toString(shieldFadeMode));
        props.setProperty("shieldRotated", Boolean.toString(shieldRotated));
        props.setProperty("shieldNumeric", Boolean.toString(shieldNumeric));
        props.setProperty("shieldHidden", Boolean.toString(shieldHidden));

        props.setProperty("toolX", Integer.toString(toolX));
        props.setProperty("toolY", Integer.toString(toolY));
        props.setProperty("toolScale", Integer.toString(toolScale));
        props.setProperty("toolFadeMode", Integer.toString(toolFadeMode));
        props.setProperty("toolHidden", Boolean.toString(toolHidden));
        try {
            Files.createDirectories(FILE.getParent(), new FileAttribute[0]);
            try (OutputStream stream = Files.newOutputStream(FILE, new OpenOption[0])) {
                props.store(stream, "Draconic Armor Rewrite HUD settings");
            }
        } catch (IOException e) {
            // ignore
        }
    }

    public static void moveShieldTo(int x, int y, int screenWidth, int screenHeight) {
        shieldX = clamp(Math.round((float)x * 1000.0f / (float)Math.max(1, screenWidth)), 0, 1000);
        shieldY = clamp(Math.round((float)y * 1000.0f / (float)Math.max(1, screenHeight)), 0, 1000);
    }

    public static void moveToolTo(int x, int y, int screenWidth, int screenHeight) {
        toolX = clamp(Math.round((float)x * 1000.0f / (float)Math.max(1, screenWidth)), 0, 1000);
        toolY = clamp(Math.round((float)y * 1000.0f / (float)Math.max(1, screenHeight)), 0, 1000);
    }

    public static int shieldLeft(int screenWidth) {
        return shieldX < 0 ? 5 : Math.round((float)(screenWidth * shieldX) / 1000.0f);
    }

    public static int shieldTop(int screenHeight) {
        return shieldY < 0 ? screenHeight - 122 : Math.round((float)(screenHeight * shieldY) / 1000.0f);
    }

    public static int toolLeft(int screenWidth) {
        return toolX < 0 ? Math.max(0, screenWidth - 192) : Math.round((float)(screenWidth * toolX) / 1000.0f);
    }

    public static int toolTop(int screenHeight) {
        return toolY < 0 ? Math.max(0, screenHeight - 104) : Math.round((float)(screenHeight * toolY) / 1000.0f);
    }

    public static float shieldScale() {
        return (float)shieldScale / 100.0f;
    }

    public static float toolScale() {
        return (float)toolScale / 100.0f;
    }

    public static void adjustShieldScale(int amount) {
        shieldScale = clamp(shieldScale + amount, 30, 300);
    }

    public static void adjustToolScale(int amount) {
        toolScale = clamp(toolScale + amount, 30, 300);
    }

    private static int readInt(Properties props, String key, int fallback) {
        try {
            return Integer.parseInt(props.getProperty(key, Integer.toString(fallback)));
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
