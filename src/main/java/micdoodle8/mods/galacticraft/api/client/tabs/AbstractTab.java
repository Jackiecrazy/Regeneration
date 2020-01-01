package micdoodle8.mods.galacticraft.api.client.tabs;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.screen.Screen;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;

public abstract class AbstractTab extends Button {
    public int potionOffsetLast;
    protected ItemRenderer itemRender;
    ResourceLocation texture = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
    ItemStack renderStack;

    public AbstractTab(int id, int posX, int posY, ItemStack renderStack) {
        super(id, posX, posY, 28, 32, "");
        this.renderStack = renderStack;
        itemRender = FMLClientHandler.instance().getClient().getRenderItem();
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        int newPotionOffset = TabRegistry.getPotionOffsetNEI();
        Screen screen = FMLClientHandler.instance().getClient().currentScreen;
        if (screen instanceof InventoryScreen) {
            newPotionOffset += TabRegistry.getRecipeBookOffset((InventoryScreen) screen) - TabRegistry.recipeBookOffset;
        }
        if (newPotionOffset != potionOffsetLast) {
            x += newPotionOffset - potionOffsetLast;
            potionOffsetLast = newPotionOffset;
        }
        if (visible) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            int yTexPos = enabled ? 3 : 32;
            int ySize = enabled ? 25 : 32;
            int xOffset = id == 2 ? 0 : 1;
            int yPos = y + (enabled ? 3 : 0);

            mc.renderEngine.bindTexture(texture);
            this.blit(x, yPos, xOffset * 28, yTexPos, 28, ySize);

            RenderHelper.enableGUIStandardItemLighting();
            zLevel = 100.0F;
            itemRender.zLevel = 100.0F;
            GlStateManager.enableLighting();
            GlStateManager.enableRescaleNormal();
            itemRender.renderItemAndEffectIntoGUI(renderStack, x + 6, y + 8);
            itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, renderStack, x + 6, y + 8, null);
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();
            itemRender.zLevel = 0.0F;
            zLevel = 0.0F;
            RenderHelper.disableStandardItemLighting();
        }
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        boolean inWindow = enabled && visible && mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;

        if (inWindow) {
            onTabClicked();
        }

        return inWindow;
    }

    public abstract void onTabClicked();

    public abstract boolean shouldAddToList();

}
