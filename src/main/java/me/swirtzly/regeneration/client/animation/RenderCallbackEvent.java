package me.swirtzly.regeneration.client.animation;

import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;

public class RenderCallbackEvent extends LivingEvent {

    protected LivingRenderer<LivingEntity> renderer;

    public RenderCallbackEvent(LivingEntity entity, LivingRenderer<LivingEntity> renderer) {
        super(entity);
        this.renderer = renderer;
    }

    public LivingRenderer<LivingEntity> getRenderer() {
        return renderer;
    }
	
}
