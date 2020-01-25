package me.swirtzly.regeneration.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import me.swirtzly.regeneration.RegenerationMod;
import me.swirtzly.regeneration.client.gui.parts.HIJContainer;
import me.swirtzly.regeneration.common.tiles.TileEntityHandInJar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class GuiHij extends ContainerScreen {
    public static final int ID = 77;
    private static final ResourceLocation TEXTURE = new ResourceLocation(RegenerationMod.MODID, "textures/gui/hij.png");
    private final IInventory playerInventory;
    private final IInventory handInventory;
    private final TileEntityHandInJar jar;
    private float mousePosx;
    private float mousePosY;

    public GuiHij(IInventory playerInv, IInventory handInv, TileEntityHandInJar jar) {
        super(new HIJContainer(playerInv, handInv, Minecraft.getInstance().player));
        this.playerInventory = playerInv;
        this.handInventory = handInv;
        this.allowUserInput = false;
        this.ySize = 133;
        this.jar = jar;
    }

    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        minecraft.fontRenderer.drawString(this.handInventory.getDisplayName().getUnformattedText(), 8, 6, Color.BLACK.getRGB());
        minecraft.fontRenderer.drawString("Residual Energy: " + jar.getLindosAmont(), 8, this.ySize - 96 + 2, Color.BLACK.getRGB());
    }

    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.blit(i, j, 0, 0, this.xSize, this.ySize);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        renderBackground();
        this.mousePosx = (float) mouseX;
        this.mousePosY = (float) mouseY;
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
}