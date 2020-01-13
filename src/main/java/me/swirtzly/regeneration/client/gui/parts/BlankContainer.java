package me.swirtzly.regeneration.client.gui.parts;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;

import javax.annotation.Nullable;

public class BlankContainer extends Container {

    protected BlankContainer(@Nullable ContainerType<?> p_i50105_1_, int p_i50105_2_) {
        super(p_i50105_1_, p_i50105_2_);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return false;
    }

}
