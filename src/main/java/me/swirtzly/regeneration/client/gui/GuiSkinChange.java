package me.swirtzly.regeneration.client.gui;

import me.swirtzly.regeneration.RegenerationMod;
import me.swirtzly.regeneration.client.gui.parts.BlankContainer;
import me.swirtzly.regeneration.client.gui.parts.InventoryTabRegeneration;
import me.swirtzly.regeneration.client.image.ImageDownloadAlt;
import me.swirtzly.regeneration.client.skinhandling.SkinChangingHandler;
import me.swirtzly.regeneration.common.capability.RegenCap;
import me.swirtzly.regeneration.network.MessageNextSkin;
import me.swirtzly.regeneration.network.NetworkHandler;
import me.swirtzly.regeneration.util.ClientUtil;
import me.swirtzly.regeneration.util.FileUtil;
import micdoodle8.mods.galacticraft.api.client.tabs.TabRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.config.GuiButtonExt;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static me.swirtzly.regeneration.util.ClientUtil.playerModelAlex;
import static me.swirtzly.regeneration.util.ClientUtil.playerModelSteve;
import static me.swirtzly.regeneration.util.RenderUtil.drawModelToGui;

public class GuiSkinChange extends ContainerScreen {

    public static final int ID = 3;
    private static final ResourceLocation background = new ResourceLocation(RegenerationMod.MODID, "textures/gui/customizer_background_small.png");
    public static boolean isAlex = true;
    private static ResourceLocation PLAYER_TEXTURE = Minecraft.getInstance().player.getLocationSkin();
    private static SkinChangingHandler.EnumChoices choices = RegenCap.get(Minecraft.getInstance().player).getPreferredModel();
    private static List<File> skins = FileUtil.listAllSkins(choices);
    private static int position = 0;


    public GuiSkinChange() {
        super(new BlankContainer());
        xSize = 176;
        ySize = 186;

        choices = RegenCap.get(Minecraft.getInstance().player).getPreferredModel();
        skins = FileUtil.listAllSkins(choices);
        if (skins.size() > 0) {
            PLAYER_TEXTURE = SkinChangingHandler.createGuiTexture(skins.get(position));
        } else try {
            throw new Exception("NO SKINS COULD BE FOUND.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateModels() {
        try {
            isAlex = ImageDownloadAlt.isAlexSkin(ImageIO.read(skins.get(position)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        choices = isAlex ? SkinChangingHandler.EnumChoices.ALEX : SkinChangingHandler.EnumChoices.STEVE;
        PLAYER_TEXTURE = SkinChangingHandler.createGuiTexture(skins.get(position));
    }

    @Override
    public void initGui() {
        super.initGui();
        TabRegistry.updateTabValues(guiLeft, guiTop, InventoryTabRegeneration.class);
        TabRegistry.addTabsToList(this.buttonList);
        int cx = (width - xSize) / 2;
        int cy = (height - ySize) / 2;
        final int btnW = 68, btnH = 17;
        position = 0;

        GuiButtonExt btnNext = new GuiButtonExt(44, cx + 20, cy + 80, 20, 20, new TranslationTextComponent("regeneration.gui.previous").getFormattedText());
        GuiButtonExt btnPrevious = new GuiButtonExt(55, cx + 130, cy + 80, 20, 20, new TranslationTextComponent("regeneration.gui.next").getFormattedText());
        GuiButtonExt btnBack = new GuiButtonExt(66, cx + 20, cy + 145, btnW, btnH, new TranslationTextComponent("regeneration.gui.back").getFormattedText());
        GuiButtonExt btnOpenFolder = new GuiButtonExt(77, cx + 90, cy + 145, btnW, btnH, new TranslationTextComponent("regeneration.gui.open_folder").getFormattedText());
        GuiButtonExt btnSave = new GuiButtonExt(88, cx + 90, cy + 127, btnW, btnH, new TranslationTextComponent("regeneration.gui.save").getFormattedText());
        GuiButtonExt btnResetSkin = new GuiButtonExt(100, cx + 20, cy + 127, btnW, btnH, new TranslationTextComponent("regeneration.gui.reset_skin").getFormattedText());

        addButton(btnNext);
        addButton(btnPrevious);
        addButton(btnOpenFolder);
        addButton(btnBack);
        addButton(btnSave);
        addButton(btnResetSkin);

        updateModels();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        Minecraft.getInstance().getTextureManager().bindTexture(background);
        blit(guiLeft, guiTop, 0, 0, xSize, ySize);
        GlStateManager.pushMatrix();
        Minecraft.getInstance().getTextureManager().bindTexture(PLAYER_TEXTURE);
        playerModelAlex.isChild = false;
        playerModelSteve.isChild = false;
        float rotation = Minecraft.getInstance().player.ticksExisted * 2;
        switch (choices) {
            case ALEX:
                drawModelToGui(playerModelAlex, width / 2, height / 2 - 45, 1.0f, rotation);
                break;
            case STEVE:
                drawModelToGui(playerModelSteve, width / 2, height / 2 - 45, 1.0f, rotation);
                break;
            case EITHER:
                drawModelToGui(playerModelAlex, width / 2 - 40, height / 2 - 45, 1.0f, rotation);
                drawModelToGui(playerModelSteve, width / 2 + 40, height / 2 - 45, 1.0f, rotation);
                break;
        }
        GlStateManager.popMatrix();

        drawCenteredString(Minecraft.getInstance().fontRenderer, new TranslationTextComponent("regeneration.gui.next_incarnation").getUnformattedText(), width / 2, height / 2 - 80, Color.WHITE.getRGB());

        String skinName = skins.get(position).getName();
        skinName = skinName.substring(0, 1).toUpperCase() + skinName.substring(1);
        drawCenteredString(Minecraft.getInstance().fontRenderer, skinName.replaceAll(".png", ""), width / 2, height / 2 + 15, Color.WHITE.getRGB());

    }


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(Button button) throws IOException {
        super.actionPerformed(button);
        skins = FileUtil.listAllSkins(choices);
        updateModels();

        switch (button.id) {
            case 66:
                Minecraft.getInstance().player.openGui(RegenerationMod.INSTANCE, GuiPreferences.ID, Minecraft.getInstance().world, 0, 0, 0);
                break;

            case 55:
                //Next
                if (!PLAYER_TEXTURE.equals(Minecraft.getInstance().player.getLocationSkin())) {
                    if (position >= skins.size() - 1) {
                        position = 0;
                    } else {
                        position++;
                    }
                    PLAYER_TEXTURE = SkinChangingHandler.createGuiTexture(skins.get(position));
                    updateModels();
                }
                break;

            case 44:
                //Previous
                if (!PLAYER_TEXTURE.equals(Minecraft.getInstance().player.getLocationSkin())) {
                    if (position > 0) {
                        position--;
                    } else {
                        position = skins.size() - 1;
                    }
                    PLAYER_TEXTURE = SkinChangingHandler.createGuiTexture(skins.get(position));
                    updateModels();
                }
                break;

            case 88:
                updateModels();
                NetworkHandler.INSTANCE.sendToServer(new MessageNextSkin(SkinChangingHandler.imageToPixelData(skins.get(position)), ImageDownloadAlt.isAlexSkin(ImageIO.read(skins.get(position)))));
                break;
            case 100:
                ClientUtil.sendSkinResetPacket();
                break;

            case 77:
                try {
                    Desktop.getDesktop().open(SkinChangingHandler.SKIN_DIRECTORY);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Minecraft.getInstance().displayGuiScreen(null);
                break;
        }
    }

}