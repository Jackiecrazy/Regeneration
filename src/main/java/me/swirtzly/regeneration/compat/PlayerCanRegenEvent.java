package me.swirtzly.regeneration.compat;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * Created by Swirtzly
 * on 01/01/2020 @ 15:11
 */
public class PlayerCanRegenEvent extends PlayerEvent {
    public PlayerCanRegenEvent(PlayerEntity player) {
        super(player);
    }
}
