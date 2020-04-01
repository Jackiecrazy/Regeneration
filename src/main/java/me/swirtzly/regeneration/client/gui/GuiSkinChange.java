package me.swirtzly.regeneration.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import me.swirtzly.regeneration.RegenerationMod;
import me.swirtzly.regeneration.client.gui.parts.BlankContainer;
import me.swirtzly.regeneration.client.gui.parts.FileButton;
import me.swirtzly.regeneration.client.gui.parts.InventoryTabRegeneration;
import me.swirtzly.regeneration.client.image.ImageDownloadAlt;
import me.swirtzly.regeneration.client.skinhandling.SkinChangingHandler;
import me.swirtzly.regeneration.common.capability.CapabilityRegeneration;
import me.swirtzly.regeneration.network.MessageNextSkin;
import me.swirtzly.regeneration.network.NetworkHandler;
import me.swirtzly.regeneration.util.ClientUtil;
import me.swirtzly.regeneration.util.FileUtil;
import micdoodle8.mods.galacticraft.api.client.tabs.TabRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import org.lwjgl.input.Mouse;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static me.swirtzly.regeneration.util.ClientUtil.playerModelAlex;
import static me.swirtzly.regeneration.util.ClientUtil.playerModelSteve;
import static me.swirtzly.regeneration.util.RenderUtil.drawModelToGui;

public class GuiSkinChange extends ContainerScreen {

    public static final int ID = 3;
    private static final ResourceLocation background = new ResourceLocation(RegenerationMod.MODID, "textures/gui/customizer_background_small.png");
    public static boolean isAlex = true;
    private static ResourceLocation PLAYER_TEXTURE = Minecraft.getInstance().player.getLocationSkin();
    private static SkinChangingHandler.EnumChoices choices = CapabilityRegeneration.getForPlayer(Minecraft.getInstance().player).getPreferredModel();
    private static List<File> skins = FileUtil.listAllSkins(choices);
    public int posX;
    public int posY;
    private ArrayList<Button> scrollButtonList = new ArrayList<>();
    private int scrollbarChangeReq;
    private TextFieldWidget textFieldValue;
    private String skinName = skins.get(0).getName();
    private String skinData = "";
    private GuiButtonExt btnBack, btnOpenFolder, btnSave, btnResetSkin;
    private int scrollbarPosY;
    private int scrollbarIndex;
    private int scrollbarChange;
    private boolean isScrollPressed;

    public GuiSkinChange() {
        super(new BlankContainer());
        xSize = 176;
        ySize = 186;
        choices = CapabilityRegeneration.getForPlayer(Minecraft.getInstance().player).getPreferredModel();
        skins = FileUtil.listAllSkins(choices);
        if (skins.size() > 0) {
            PLAYER_TEXTURE = SkinChangingHandler.createGuiTexture(skins.get(0));
        } else
            try {
                throw new Exception("NO SKINS COULD BE FOUND.");
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    public static void tickModels(File file) {
        try {
            isAlex = ImageDownloadAlt.isAlexSkin(ImageIO.read(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void keyTyped(char eventChar, int eventKey) throws IOException {
        if (textFieldValue.textboxKeyTyped(eventChar, eventKey) && !textFieldValue.getText().isEmpty()) {
            this.scrollButtonList.clear();
            for (File skin : FileUtil.similarWords(textFieldValue.getText(), FileUtil.listAllSkins(choices))) {
                FileButton BUTTON = new FileButton(scrollButtonList.size() + 3, posX + 8, posY + 7 + (24 * (scrollButtonList.size())), skin.getName().replaceAll(".png", ""));
                BUTTON.setFile(skin);
                this.scrollButtonList.add(BUTTON);
                tickButtonsList();
            }
        } else {
            if (textFieldValue.getText().isEmpty()) {
                if (textFieldValue.textboxKeyTyped(eventChar, eventKey)) {
                    this.scrollButtonList.clear();
                    for (File skin : FileUtil.listAllSkins(choices)) {
                        FileButton BUTTON = new FileButton(scrollButtonList.size() + 3, posX + 8, posY + 7 + (24 * (scrollButtonList.size())), skin.getName().replaceAll(".png", ""));
                        BUTTON.setFile(skin);
                        this.scrollButtonList.add(BUTTON);
                        tickButtonsList();
                    }
                }
            }
            super.keyTyped(eventChar, eventKey);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        Minecraft.getInstance().getTextureManager().bindTexture(background);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        GlStateManager.pushMatrix();
        Minecraft.getInstance().getTextureManager().bindTexture(PLAYER_TEXTURE);
        playerModelAlex.isChild = false;
        playerModelSteve.isChild = false;
        float rotation = Minecraft.getInstance().player.ticksExisted * 2;
        if (isAlex) {
            drawModelToGui(playerModelAlex, width / 2, height / 2 - 45, 1.0f, rotation);
        } else {
            drawModelToGui(playerModelSteve, width / 2, height / 2 - 45, 1.0f, rotation);
        }
        GlStateManager.popMatrix();

        drawCenteredString(Minecraft.getInstance().fontRenderer, new TranslationTextComponent("regeneration.gui.next_incarnation").getUnformattedText(), width / 2, height / 2 - 80, Color.WHITE.getRGB());
        drawCenteredString(Minecraft.getInstance().fontRenderer, skinName, width / 2, height / 2 + 15, Color.WHITE.getRGB());
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        textFieldValue.drawTextBox();
    }

    @Override
    public void tickScreen() {
        super.tickScreen();
        textFieldValue.tickCursorCounter();
    }

    @Override
    protected void actionPerformed(Button button) throws IOException {
        super.actionPerformed(button);
        switch (button.id) {
            case 66:
                Minecraft.getInstance().player.openGui(RegenerationMod.INSTANCE, GuiPreferences.ID, Minecraft.getInstance().world, 0, 0, 0);
                break;
            case 88:
                NetworkHandler.INSTANCE.sendToServer(new MessageNextSkin(skinData, isAlex));
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

        if (button instanceof FileButton) {
            FileButton buttontick = (FileButton) button;
            PLAYER_TEXTURE = SkinChangingHandler.createGuiTexture(buttontick.getFile());
            tickModels(buttontick.getFile());
            skinName = buttontick.getFile().getName().substring(0, 1).toUpperCase() + buttontick.getFile().getName().substring(1).replaceAll(".png", "");
            btnSave.enabled = true;
            skinData = SkinChangingHandler.imageToPixelData(buttontick.getFile());
        }
    }

    @Override
    public void initGui() {

        int cx = (width - xSize) / 2;
        int cy = (height - ySize) / 2;

        this.scrollButtonList.clear();
        for (File skin : skins) {
            FileButton BUTTON = new FileButton(scrollButtonList.size() + 3, posX + 8, posY + 7 + (24 * (scrollButtonList.size())), skin.getName().replaceAll(".png", ""));
            BUTTON.setFile(skin);
            this.scrollButtonList.add(BUTTON);
        }
        textFieldValue = new TextFieldWidget(10, this.mc.fontRenderer, cx - 220, cy + 15, 200, 20);
        textFieldValue.setMaxStringLength(10000);
        textFieldValue.setText("");
        textFieldValue.setEnabled(true);
        textFieldValue.setFocused(true);
        textFieldValue.setEnableBackgroundDrawing(true);

        super.initGui();
        TabRegistry.tickTabValues(guiLeft, guiTop, InventoryTabRegeneration.class);
        TabRegistry.addTabsToList(this.buttonList);
        final int btnW = 68, btnH = 17;

        btnBack = new GuiButtonExt(66, cx + 20, cy + 145, btnW, btnH, new TranslationTextComponent("regeneration.gui.back").getFormattedText());
        btnOpenFolder = new GuiButtonExt(77, cx + 90, cy + 145, btnW, btnH, new TranslationTextComponent("regeneration.gui.open_folder").getFormattedText());
        btnSave = new GuiButtonExt(88, cx + 90, cy + 127, btnW, btnH, new TranslationTextComponent("regeneration.gui.save").getFormattedText());
        btnResetSkin = new GuiButtonExt(100, cx + 20, cy + 127, btnW, btnH, new TranslationTextComponent("regeneration.gui.reset_skin").getFormattedText());

        btnSave.enabled = false;

        skinName = skinName.substring(0, 1).toUpperCase() + skinName.substring(1).replaceAll(".png", "");

        this.tickButtonsList();
    }

    public boolean needsScrollbar() {
        return this.scrollButtonList.size() > 8;
    }

    public void tickButtonsList() {
        this.buttonList.clear();
        addButton(btnOpenFolder);
        addButton(btnBack);
        addButton(btnSave);
        addButton(btnResetSkin);
        int id = 1000;
        int cx = (width - xSize) / 2;
        int cy = (height - ySize) / 2;
        for (int i = this.scrollbarIndex; i < this.scrollbarIndex + 5 && i < this.scrollButtonList.size(); i++) {
            FileButton but = (FileButton) this.scrollButtonList.get(i);
            FileButton BUTTON = new FileButton(id, cx - 220, cy + 45 + (24 * (i - this.scrollbarIndex)), but.displayString);
            BUTTON.setFile(but.getFile());
            this.buttonList.add(BUTTON);
            id++;
        }
    }

    public void tickScrollPositon(int change) {
        if (change != 0 && this.needsScrollbar()) {
            this.scrollbarPosY = change;
            if (this.scrollbarPosY > 85) this.scrollbarPosY = 85;
            if (this.scrollbarPosY < 00) this.scrollbarPosY = 00;
            this.tickScrollIndex();
        }
    }

    public void tickScrollIndex() {
        if (this.scrollbarPosY <= this.scrollbarChangeReq) {
            this.scrollbarIndex = 0;
            this.scrollbarChange = this.scrollbarPosY;

            this.tickButtonsList();
        } else if (this.scrollbarPosY - this.scrollbarChange >= this.scrollbarChangeReq) {
            this.scrollbarIndex++;
            this.scrollbarChange = this.scrollbarPosY;
            this.tickButtonsList();
        } else if (this.scrollbarPosY - this.scrollbarChange <= (-1 * this.scrollbarChangeReq)) {
            this.scrollbarIndex--;
            this.scrollbarChange = this.scrollbarPosY;

            if (this.scrollbarIndex < 0) this.scrollbarIndex = 0;

            this.tickButtonsList();
        }
    }

    private boolean isMouseOverArea(int mouseX, int mouseY, int posX, int posY, int width, int height) {
        return (mouseX >= posX && mouseX < posX + width && mouseY >= posY && mouseY < posY + height);
    }

    @Override
    public void handleMouseInput() {
        try {
            super.handleMouseInput();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int wheelState;
        if ((wheelState = Mouse.getEventDWheel()) != 0)
            this.tickScrollPositon(this.scrollbarPosY - (wheelState / 10));
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {

        this.textFieldValue.mouseClicked(mouseX, mouseY, button);

        try {
            super.mouseClicked(mouseX, mouseY, button);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int posX = (this.width - this.xSize) / 2;
        int posY = (this.height - this.ySize) / 2;

        if (button != 0 || !this.needsScrollbar()) return;

        if (isMouseOverArea(mouseX, mouseY, posX + 154, posY + 7 + this.scrollbarPosY, 12, 15))
            this.isScrollPressed = true;

    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY, int button, long timeSince) {
        super.mouseClickMove(mouseX, mouseY, button, timeSince);

        int posX = (this.width - this.xSize) / 2;
        int posY = (this.height - this.ySize) / 2;

        if (Mouse.isButtonDown(0) && this.isScrollPressed) {
            this.tickScrollPositon(mouseY - posY);

            if (!Mouse.isButtonDown(0)) this.isScrollPressed = false;
        }
    }
	
}
