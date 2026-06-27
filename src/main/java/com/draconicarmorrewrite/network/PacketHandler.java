package com.draconicarmorrewrite.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("draconicarmorrewrite", "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int id = 0;

    public static void init() {
        CHANNEL.registerMessage(id++, UpgradableArmorSettingsPacket.class,
                UpgradableArmorSettingsPacket::encode,
                UpgradableArmorSettingsPacket::decode,
                UpgradableArmorSettingsPacket::handle);
        CHANNEL.registerMessage(id++, ArmorConfigActionPacket.class,
                ArmorConfigActionPacket::encode,
                ArmorConfigActionPacket::decode,
                ArmorConfigActionPacket::handle);
        CHANNEL.registerMessage(id++, ArmorShieldTogglePacket.class,
                ArmorShieldTogglePacket::encode,
                ArmorShieldTogglePacket::decode,
                ArmorShieldTogglePacket::handle);
        CHANNEL.registerMessage(id++, PacketShieldHit.class,
                PacketShieldHit::encode,
                PacketShieldHit::decode,
                PacketShieldHit::handle);
    }
}
