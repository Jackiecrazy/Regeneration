package me.swirtzly.regeneration.client.gui.parts;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;

public class BlankContainer extends Container {

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return false;
    }
	
}
