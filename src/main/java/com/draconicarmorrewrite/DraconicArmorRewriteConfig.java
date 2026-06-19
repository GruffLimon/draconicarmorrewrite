package com.draconicarmorrewrite;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.loading.FMLPaths;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.List;

public class DraconicArmorRewriteConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec CLIENT_SPEC;

    public static final ForgeConfigSpec.BooleanValue USE_CLASSIC_MODELS;

    private static boolean shouldVal = false;
    private static boolean initialized = false;

    static {
        BUILDER.push("General");
        USE_CLASSIC_MODELS = BUILDER
                .comment("Set to true to use the classic Draconic Evolution 1.12.2 armor 3D models.")
                .define("useClassicModels", true);
        BUILDER.pop();

        CLIENT_SPEC = BUILDER.build();
    }

    public static boolean useClassicModels() {
        if (initialized) return shouldVal;
        try {
            shouldVal = USE_CLASSIC_MODELS.get();
            initialized = true;
            return shouldVal;
        } catch (IllegalStateException e) {
            // Not loaded yet
        }
        try {
            Path configPath = FMLPaths.CONFIGDIR.get().resolve("draconicarmorrewrite-client.toml");
            if (Files.exists(configPath)) {
                List<String> lines = Files.readAllLines(configPath);
                for (String line : lines) {
                    String trimmed = line.trim();
                    if (trimmed.startsWith("useClassicModels")) {
                        String[] parts = trimmed.split("=");
                        if (parts.length > 1) {
                            shouldVal = parts[1].trim().equalsIgnoreCase("true");
                            initialized = true;
                            return shouldVal;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

