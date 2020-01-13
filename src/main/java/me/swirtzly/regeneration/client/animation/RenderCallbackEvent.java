package me.swirtzly.regeneration.client.animation;

import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;

public class RenderCallbackEvent extends LivingEvent {

    protected LivingRenderer renderer;

    public RenderCallbackEvent(LivingEntity entity, LivingRenderer renderer) {
        super(entity);
        this.renderer = renderer;
    }

    public LivingRenderer getRenderer() {
        return renderer;
    }

}
