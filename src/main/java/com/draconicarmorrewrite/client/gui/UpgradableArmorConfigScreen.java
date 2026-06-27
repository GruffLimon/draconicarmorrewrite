package com.draconicarmorrewrite.client.gui;

import com.draconicarmorrewrite.network.PacketHandler;
import com.draconicarmorrewrite.network.UpgradableArmorSettingsPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class UpgradableArmorConfigScreen extends Screen {
    private final ItemStack armorStack;
    private double speed = 1.0;
    private double jump = 1.0;
    private boolean flight = true;

    private Button speedBtn;
    private Button jumpBtn;
    private Button flightBtn;

    public UpgradableArmorConfigScreen(ItemStack armorStack) {
        super(Component.translatable("gui.draconicarmorrewrite.title"));
        this.armorStack = armorStack;

        CompoundTag settings = armorStack.getTagElement("DESettings");
        if (settings != null) {
            if (settings.contains("speed_mult")) {
                this.speed = settings.getDouble("speed_mult");
            }
            if (settings.contains("jump_mult")) {
                this.jump = settings.getDouble("jump_mult");
            }
            if (settings.contains("flight_enabled")) {
                this.flight = settings.getBoolean("flight_enabled");
            }
        }
    }

    @Override
    protected void init() {
        int x = (this.width - 220) / 2;
        int y = (this.height - 180) / 2;

        // Speed minus
        this.addRenderableWidget(Button.builder(Component.literal("-"), btn -> {
            speed = Math.max(0.0, speed - 0.1);
            updateButtonLabels();
        }).bounds(x + 10, y + 45, 30, 20).build());

        // Speed text/status button
        speedBtn = Button.builder(Component.literal(""), btn -> {})
                .bounds(x + 45, y + 45, 130, 20)
                .build();
        speedBtn.active = false;
        this.addRenderableWidget(speedBtn);

        // Speed plus
        this.addRenderableWidget(Button.builder(Component.literal("+"), btn -> {
            speed = Math.min(1.0, speed + 0.1);
            updateButtonLabels();
        }).bounds(x + 180, y + 45, 30, 20).build());

        // Jump minus
        this.addRenderableWidget(Button.builder(Component.literal("-"), btn -> {
            jump = Math.max(0.0, jump - 0.1);
            updateButtonLabels();
        }).bounds(x + 10, y + 85, 30, 20).build());

        // Jump status
        jumpBtn = Button.builder(Component.literal(""), btn -> {})
                .bounds(x + 45, y + 85, 130, 20)
                .build();
        jumpBtn.active = false;
        this.addRenderableWidget(jumpBtn);

        // Jump plus
        this.addRenderableWidget(Button.builder(Component.literal("+"), btn -> {
            jump = Math.min(1.0, jump + 0.1);
            updateButtonLabels();
        }).bounds(x + 180, y + 85, 30, 20).build());

        // Flight toggle
        flightBtn = Button.builder(Component.literal(""), btn -> {
            flight = !flight;
            updateButtonLabels();
        }).bounds(x + 10, y + 125, 200, 20).build();
        this.addRenderableWidget(flightBtn);

        updateButtonLabels();
    }

    private void updateButtonLabels() {
        if (speedBtn != null) {
            speedBtn.setMessage(Component.literal("Speed: " + (int) (speed * 100) + "%"));
        }
        if (jumpBtn != null) {
            jumpBtn.setMessage(Component.literal("Jump: " + (int) (jump * 100) + "%"));
        }
        if (flightBtn != null) {
            flightBtn.setMessage(Component.literal("Flight: " + (flight ? "Enabled" : "Disabled")));
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        int x = (this.width - 220) / 2;
        int y = (this.height - 180) / 2;

        // Background card
        graphics.fill(x, y, x + 220, y + 160, 0xD0101010);
        int borderCol = 0xFF4A4A4A;
        graphics.fill(x, y, x + 220, y + 1, borderCol);             // Top border
        graphics.fill(x, y + 159, x + 220, y + 160, borderCol);     // Bottom border
        graphics.fill(x, y, x + 1, y + 160, borderCol);             // Left border
        graphics.fill(x + 219, y, x + 220, y + 160, borderCol);     // Right border

        // Title
        graphics.drawCenteredString(this.font, this.title, this.width / 2, y + 15, 0xFFFFFFFF);

        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public void onClose() {
        PacketHandler.CHANNEL.sendToServer(new UpgradableArmorSettingsPacket(speed, jump, flight));
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
